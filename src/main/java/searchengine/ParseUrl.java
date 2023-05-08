package searchengine;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.model.Page;
import searchengine.services.EntityService;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;



public class ParseUrl {


    public static void parsWeb(String url, EntityService entityService)
            throws IOException, SQLException, ParserConfigurationException, InterruptedException {

        CopyOnWriteArrayList<String> links =new CopyOnWriteArrayList<>();
        links.add(url);
        while (!links.isEmpty()){
            for(String link:links){
                Document document = Jsoup.connect(link)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
                Elements postUrl = document.select("a");
                for (Element post : postUrl) {
                    String linksChildren = post.attr("abs:href");
                    if(!link.contains(linksChildren)) {
                        try {
                            Page page = new Page();
                            links.add(linksChildren);
                            String path = linksChildren;
                            int code = urlCode(linksChildren);
                            entityService.getPage(page,path,code,"Привет");
                            Thread.sleep(500);
                        } catch (Exception e){
                            System.out.println("Ошибка в парсе ");
                        }

                    }
                }
            }
        }
    }

    public static int urlCode(String url) throws ParserConfigurationException, SQLException, IOException {
        int code;
        try {
            Connection.Response response =  Jsoup.connect(url).execute();
            code = response.statusCode();
        } catch (HttpStatusException | SocketTimeoutException e){
            code =404;
        } catch (UnsupportedMimeTypeException e){
            code = 404;
        } catch (IOException e){
            code =404;
        }
        return code;
    }
}
