package searchengine.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import searchengine.model.*;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.SiteRepository;
import searchengine.repository.PageRepository;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RecursiveAction;

@Service
@RequiredArgsConstructor
@Data
public class ParseUrl extends RecursiveAction {

    private String url;
    private PageRepository pageRepository;
    private SiteRepository siteRepository;
    private IndexRepository indexRepository;
    private LemmaRepository lemmaRepository;
    private String name;
    private Site site;
    private boolean indexRun;

    public ParseUrl(String url, PageRepository pageRepository, SiteRepository siteRepository,
                    IndexRepository indexRepository, LemmaRepository lemmaRepository, String name, Site site) {
        this.url = url;
        this.pageRepository = pageRepository;
        this.siteRepository = siteRepository;
        this.indexRepository = indexRepository;
        this.lemmaRepository = lemmaRepository;
        this.name = name;
        this.site = site;
    }

    @SneakyThrows
    @Override
    protected void compute() {

        if (indexRun) {
            CopyOnWriteArrayList<String> links = new CopyOnWriteArrayList<>();
            links.add(url);
            while (!links.isEmpty()) {
                for (String link : links) {
                    Document document = Jsoup.connect(link)
                            .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                            .referrer("http://www.google.com")
                            .get();
                    Elements postUrl = document.select("a");
                    parsElement(postUrl, link, links, document);
                }
            }
        } else join();
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

    private void setPage(String path, int code, String content) throws IOException {
        Lemmatisator lemmatisator = new Lemmatisator();
        Page page = new Page();
        try {
            page.setSiteId(site);
            page.setPath(path);
            page.setCode(code);
            page.setContent(content);
            pageRepository.save(page);
            Thread.sleep(500);
            Map<String, Integer> wordsMap;
            String clearTegs = lemmatisator.clearingTags(path);
            wordsMap = lemmatisator.lemmatisator(clearTegs);
            for (String key : wordsMap.keySet()) {
                setLemma(key, page, wordsMap.get(key));
            }
        } catch (Exception e) {
            site.setName(name);
            site.setUrl(url);
            site.setStatus(Status.FAILED);
            site.setLastError(" Ошибка в парсинге");
            site.setStatusTime(new Timestamp(System.currentTimeMillis()));
            siteRepository.save(site);
        }
    }

    private void indexSet(Lemma lemma, Page page, int i) {
        Index index = new Index();
        index.setRank(i);
        index.setLemmaId(lemma);
        index.setPageId(page);
        indexRepository.save(index);
    }

    private void setLemma(String key, Page page, int i) {
        List<Lemma> listLemmas = (List<Lemma>) lemmaRepository.findAll();
        boolean check = true;
        if (listLemmas.isEmpty()) {
            Lemma lemma = new Lemma();
            lemma.setSiteByLemma(site);
            lemma.setLemma(key);
            lemma.setFrequency(1);
            lemmaRepository.save(lemma);
            indexSet(lemma, page, i);
        } else {
            for (Lemma lemma : listLemmas) {
                if (key.equals(lemma.getLemma())) {
                    Lemma lemmaSave = lemmaRepository.findById(lemma.getId()).orElseThrow();
                    lemmaSave.setFrequency(lemma.getFrequency() + 1);
                    lemmaRepository.save(lemmaSave);
                    indexSet(lemmaSave, page, i);
                    check = false;
                    break;
                }
            }
            if (check) {
                Lemma lemmas = new Lemma();
                lemmas.setSiteByLemma(site);
                lemmas.setLemma(key);
                lemmas.setFrequency(1);
                lemmaRepository.save(lemmas);
                indexSet(lemmas, page, i);
            }
        }
    }

    private void parsElement(Elements postUrl, String link, CopyOnWriteArrayList<String> links, Document document)
            throws IOException {

        for (Element post : postUrl) {
            String linksChildren = post.attr("abs:href");
            if (!link.contains(linksChildren)
                    && !linksChildren.contains(url)) {
                links.add(linksChildren);
                String path = linksChildren.replaceAll(url, " ");
                int code = urlCode(linksChildren);
                setPage(path, code, String.valueOf(document));
                if (!indexRun) join();
            }
        }
    }
}
