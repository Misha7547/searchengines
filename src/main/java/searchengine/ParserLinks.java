package searchengine;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Connection;
import searchengine.config.ParserConfig;
import searchengine.model.Site;
import searchengine.services.EntityService;
import org.jsoup.Connection;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;


@Setter
@Getter
public class ParserLinks extends RecursiveAction {
    private final String url;
    private final Set<String> linksSet;
    private final Site site;
    private int codeResponse;
    private ParserConfig parserConfig;
    private EntityService entityService;

    public ParserLinks(String url, Site site, Set<String> linksSet) {
        this.url = url;
        this.linksSet = linksSet;
        this.site = site;
    }

    public int getCodeResponse() {
        return codeResponse;
    }


    @Override
    protected void compute() {

            List<ParserLinks> tasks = new ArrayList<>();
            if (linksSet.add(url)) {
                try {
                    Document document = getDocument(url);
                    entityService.addEntitiesToDB(document, url, codeResponse, site, 0);
                    Elements resultLinks = document.select("a[href]");
                    if (!(resultLinks == null || resultLinks.size() == 0)) {
                        List<String> linksChildren = new ArrayList<>();
                        for (Element resultLink : resultLinks) {
                            String absLink = resultLink.attr("abs:href");
                            if ((!linksChildren.contains(absLink)) && absLink.startsWith(url)
                                    && !(absLink.contains("#")) && absLink.length() > url.length()) {
                                linksChildren.add(absLink);
                            }
                        }
                        for (String childLink : linksChildren) {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                            }
                            ParserLinks task = new ParserLinks(childLink, site, linksSet);
                            task.setParserConfig(parserConfig);
                            task.setEntityService(entityService);
                            task.fork();
                            tasks.add(task);
                        }
                    }
                    for (ParserLinks task : tasks) {
                        task.join();
                    }

                } catch (NullPointerException | ParserConfigurationException | IOException | SQLException ex) {
                    entityService.updateLastError(site, url + " - " + ex.getMessage());
                }
            }
    }

    public Document getDocument(String url) throws ParserConfigurationException, SQLException, IOException {

        Document doc = null;

        try {
            Connection connection = (Connection) Jsoup
                    .connect(url)
                    .userAgent(parserConfig.getUseRagent())
                    .referrer(parserConfig.getReferrer())
                    .timeout(parserConfig.getTimeout());

            doc = connection.get();
            codeResponse = connection.response().statusCode();
        } catch (HttpStatusException | SocketTimeoutException e) {
            codeResponse = 404;
            System.out.println(e.getLocalizedMessage());
        } catch (UnsupportedMimeTypeException e) {
            codeResponse = 404;
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            codeResponse = 404;
        }
        return doc;
    }


}
