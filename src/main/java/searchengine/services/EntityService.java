package searchengine.services;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.ParseUrl;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.model.Status;


import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
@Getter
@Setter
@Service
@Data
public class EntityService {

    private boolean indexingRun;
    private boolean indexingStop;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private PageRepository pageRepository;

    private static ParseUrl parseUrl = new ParseUrl();

    private Site site;
    private Page page;

    public void deleteAllPages() {
        pageRepository.deleteAll();
    }

    public void deleteAllSite() {
        siteRepository.deleteAll();
    }

    public Site updateSite(Site site, Status status) {
        try {
            site.setStatus(status);
            site.setStatusTime(new Timestamp(System.currentTimeMillis()));
        } catch (Exception e){
            System.out.println(" ошибка 2 ");
        }
        return siteRepository.save(site);
    }

    public Site updateLastError(Site site, String errorMessage) {
        site.setLastError(errorMessage);
        site.setStatusTime(new Timestamp(System.currentTimeMillis()));
        return siteRepository.save(site);
    }

    public Site findSiteByName(Site site, String name) {
        try {
            site.setName(name);
        }catch (Exception e){
            System.out.println("Ошибка 3");
        }
        return siteRepository.save(site);
    }

    public Site findSiteByUrl( Site site,String siteUrl) {
        try {
            site.setUrl(siteUrl);
        } catch (Exception e) {
            System.out.println(" Ошибка 4 ");
        }

        return siteRepository.save(site);
    }

    public void Pars (String url, EntityService entityService) throws SQLException, IOException, ParserConfigurationException, InterruptedException {
        parseUrl.parsWeb(url, entityService);
    }

   public Page getPage (Page page, String path, int code, String content){
        try {
            page.setPath(path);
            page.setCode(code);
            page.setContent(content);

        } catch (Exception e){
            System.out.println("Ошибка три ");
        }
       return pageRepository.save(page);
   }
}


