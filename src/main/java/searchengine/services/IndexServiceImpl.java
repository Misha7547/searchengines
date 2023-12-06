package searchengine.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import java.util.HashMap;
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

    private Boolean isIndexingRun = true;
    @Autowired
    SitesList sitesList;

    ForkJoinPool forkJoinPool = new ForkJoinPool();

    @Autowired
    Lemmatisator lemmatisator;
    private Document document;

    private Boolean checkSite;

    @Override
    public Boolean startIndexing() {
        isIndexingRun = true;
        pageRepository.deleteAll();
        siteRepository.deleteAll();
        lemmaRepository.deleteAll();
        indexRepository.deleteAll();

        for (Site list : sitesList.getSites()) {
            CompletableFuture.runAsync(() -> {
                try {
                    getSiteAndPage(list.getName(), list.getUrl(), isIndexingRun);
                } catch (SQLException | IOException | ParserConfigurationException | InterruptedException e) {
                    e.getMessage();
                }
            }, ForkJoinPool.commonPool());
        }
        return isIndexingRun;
    }

    @Override
    public Boolean stopIndexing()
            throws SQLException, IOException, ParserConfigurationException, InterruptedException {
        isIndexingRun = false;
        forkJoinPool.shutdown();
        getSiteAndPage(null, null, isIndexingRun);
        return isIndexingRun;
    }

    @Override
    public Boolean isIndexingRun() {
        return isIndexingRun;
    }


    public void getSiteAndPage(String name, String url, Boolean isIndexingRun)
            throws SQLException, IOException, ParserConfigurationException, InterruptedException {
        searchengine.model.Site site = new searchengine.model.Site();
        ParseUrl parseUrl = new ParseUrl();
        if (isIndexingRun) {
            site.setName(name);
            site.setUrl(url);
            site.setStatus(Status.INDEXING);
            site.setStatusTime(new Timestamp(System.currentTimeMillis()));
            siteRepository.save(site);
            parseUrl.parsWeb(url, pageRepository, siteRepository, indexRepository, lemmaRepository,name,site);
        } else {
            site.setName(name);
            site.setUrl(url);
            site.setStatus(Status.FAILED);
            site.setLastError("Индексация остановлена пользователем");
            site.setStatusTime(new Timestamp(System.currentTimeMillis()));
            siteRepository.save(site);
        }
    }

    @Override
    public Object getIndexPage(String html) throws IOException {
        Page page = new Page();
        searchengine.model.Site site = new searchengine.model.Site();
        site.setName(html);
        page.setSiteId(site);
        page.setPath(html);
        page.setCode(urlCode(html));
        page.setContent(String.valueOf(document = Jsoup.connect(html).get()));
        HashMap<String, Integer> wordsMap = new HashMap<>();
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

    public static int urlCode(String url) {
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
            if (html.contains((CharSequence) list)){
                checkSite = true;
            } else {
                checkSite = false;
            }
        }
        return checkSite;
    }

}




