package searchengine.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.model.Status;
import searchengine.services.PageRepository;
import searchengine.services.SiteRepository;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Data
public class ParseUrl {

    public void parsWeb(int idSite, String url, PageRepository pageRepository, SiteRepository siteRepository, String name)
            throws IOException {

        Site site = new Site();
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
                        try {
                            setPage(idSite, path, code, String.valueOf(document), pageRepository);
                            Thread.sleep(500);
                        } catch (Exception e) {
                            site.setName(name);
                            site.setUrl(url);
                            site.setStatus(Status.FAILED);
                            site.setLastError(" Ошибка в парсинге");
                            site.setStatusTime(new Timestamp(System.currentTimeMillis()));
                            siteRepository.save(site);
                        }
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

    public void setPage(int idSite, String path, int code, String content, PageRepository pageRepository) {
        Page page = new Page();
        page.setId(idSite);
        page.setPath(path);
        page.setCode(code);
        page.setContent(content);
        pageRepository.save(page);
    }
}
