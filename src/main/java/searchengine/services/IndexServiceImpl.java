package searchengine.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.ResultParseIndex;
import searchengine.interfaces.IndexService;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.repository.*;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.Status;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
@Data
public class IndexServiceImpl implements IndexService {

    @Autowired
    SiteRepository siteRepository;
    @Autowired
    PageRepository pageRepository;
    @Autowired
    LemmaRepository lemmaRepository;
    @Autowired
    IndexRepository indexRepository;
    @Autowired
    SitesList sitesList;
    @Autowired
    Lemmatisator lemmatisator;
    private Document document;
    private Boolean isIndexingRun = true;
    ForkJoinPool forkJoinPool = new ForkJoinPool();
    private Boolean checkSite;
    ParseUrl parseUrl;

    @Override
    public Object startIndexing(){
        isIndexingRun = true;
        pageRepository.deleteAll();
        siteRepository.deleteAll();
        lemmaRepository.deleteAll();
        indexRepository.deleteAll();

        ResultParseIndex resultParseIndex = new ResultParseIndex();
        resultParseIndex.setResult(isIndexingRun);

        for (Site list : sitesList.getSites()) {
            CompletableFuture.runAsync(() -> {
                try {
                    getSiteAndPage(list.getName(), list.getUrl(), isIndexingRun);
                } catch (SQLException | IOException | ParserConfigurationException | InterruptedException e) {
                    e.getMessage();
                }
            }, ForkJoinPool.commonPool());
        }

        return resultParseIndex;
    }

    @Override
    public Object stopIndexing()
            throws SQLException, IOException, ParserConfigurationException, InterruptedException {
        isIndexingRun = false;
        parseUrl.setIndexRun(isIndexingRun);
        getSiteAndPage(null, null, isIndexingRun);
        ResultParseIndex resultParseIndex = new ResultParseIndex();
        resultParseIndex.setResult(true);
        return resultParseIndex;
    }

    @Override
    public Boolean isIndexingRun() {
        return isIndexingRun;
    }

    private void getSiteAndPage(String name, String url, Boolean isIndexingRun)
            throws SQLException, IOException, ParserConfigurationException, InterruptedException {
        searchengine.model.Site site = new searchengine.model.Site();
        if (Boolean.TRUE.equals(isIndexingRun)) {
            site.setName(name);
            site.setUrl(url);
            site.setStatus(Status.INDEXING);
            site.setStatusTime(new Timestamp(System.currentTimeMillis()));
            siteRepository.save(site);
            parseUrl = new ParseUrl(url, pageRepository, siteRepository, indexRepository, lemmaRepository,name,site);
            parseUrl.setIndexRun(isIndexingRun);
            parseUrl.fork();
        } else {
            Iterable<searchengine.model.Site> list = siteRepository.findAll();
            for (searchengine.model.Site sites: list){
                searchengine.model.Site siteSave = siteRepository.findById(sites.getId()).orElseThrow();
                siteSave.setStatus(Status.FAILED);
                siteSave.setLastError("Индексация остановлена пользователем");
                siteSave.setStatusTime(new Timestamp(System.currentTimeMillis()));
                siteRepository.save(siteSave);
            }
        }
    }

    @Override
    public Object getIndexPage(String html) throws IOException {
        Page page = new Page();
        searchengine.model.Site site = new searchengine.model.Site();
        String contents = String.valueOf(document = Jsoup.connect(html).get());
        site.setName(html);
        page.setSiteId(site);
        page.setPath(html);
        page.setCode(urlCode(html));
        page.setContent(contents);
        Map<String, Integer> wordsMap;
        String clearTegs = lemmatisator.clearingTags(html);
        wordsMap = lemmatisator.lemmatisator(clearTegs);
        for (String key : wordsMap.keySet()) {
            Lemma lemma = new Lemma();
            Index index = new Index();
            lemma.setSiteByLemma(site);
            lemma.setLemma(key);
            lemma.setFrequency(wordsMap.get(key));
            index.setLemmaId(lemma);
            index.setPageId(page);
            index.setRank(wordsMap.get(key));
            lemmaRepository.save(lemma);
            indexRepository.save(index);
        }
        pageRepository.save(page);
        return null;
    }

    private static int urlCode(String url) {
        int code;
        try {
            Connection.Response response = Jsoup.connect(url).execute();
            code = response.statusCode();
        } catch (IOException e) {
            code = 404;
        }
        return code;
    }

    @Override
    public Boolean checkSite(String html){

        for (Site list : sitesList.getSites()){
            checkSite = html.contains((CharSequence) list);
        }
        return checkSite;
    }
}






