package searchengine.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
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
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Data
public class ParseUrl {


    public void parsWeb(String url, PageRepository pageRepository, SiteRepository siteRepository, IndexRepository indexRepository,
                        LemmaRepository lemmaRepository, String name,Site site)
            throws IOException {

        CopyOnWriteArrayList<String> links = new CopyOnWriteArrayList<>();
        links.add(url);
        while (!links.isEmpty()) {
            for (String link : links) {
                Document document = Jsoup.connect(link)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
                Elements postUrl = document.select("a");
                for (Element post : postUrl) {
                    String linksChildren = post.attr("abs:href");
                    if (!link.contains(linksChildren)
                            && !linksChildren.contains(url)) {
                        links.add(linksChildren);
                        String path = linksChildren.replaceAll(url, " ");
                        int code = urlCode(linksChildren);
                        setPage(path, code, String.valueOf(document), pageRepository, url,
                                site, siteRepository, lemmaRepository, indexRepository, name);
                    }
                }
            }
        }
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

    public void setPage(String path, int code, String content, PageRepository pageRepository,
                        String url, Site site, SiteRepository siteRepository, LemmaRepository lemmaRepository,
                        IndexRepository indexRepository,String name) throws IOException {
        Lemmatisator lemmatisator  = new Lemmatisator();
        Page page = new Page();
        try {
            page.setSiteId(site);
            page.setPath(path);
            page.setCode(code);
            page.setContent(content);
            pageRepository.save(page);
            Thread.sleep(500);
            HashMap<String, Integer> wordsMap = new HashMap<>();
            String clearTegs = lemmatisator.clearingTags(path);
            wordsMap = lemmatisator.lemmatisator(clearTegs);
            for (String key : wordsMap.keySet()) {
                Lemma lemma = new Lemma();
                lemma.setSiteByLemma(site);
                lemma.setLemma(key);
                lemma.setFrequency(wordsMap.get(key));
                lemmaRepository.save(lemma);
                int i = wordsMap.get(key);
                indexSet(lemma,page,indexRepository, i);
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

    public IndexRepository indexSet(Lemma lemma, Page page, IndexRepository indexRepository, int i ){
        Index index = new Index();
        index.setRank(i);
        index.setLemmaId(lemma);
        index.setPageId(page);
        indexRepository.save(index);
        return indexRepository;
    }
}
