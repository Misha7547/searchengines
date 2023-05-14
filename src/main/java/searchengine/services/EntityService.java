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
            site.setStatus(status);
            site.setStatusTime(new Timestamp(System.currentTimeMillis()));
        return siteRepository.save(site);
    }

    public Site updateLastError(Site site, String errorMessage) {
        site.setLastError(errorMessage);
        site.setStatusTime(new Timestamp(System.currentTimeMillis()));
        return siteRepository.save(site);
    }

    public Site findSiteByName(Site site, String name) {
            site.setName(name);
        return siteRepository.save(site);
    }

    public Site findSiteByUrl( Site site,String siteUrl) {
            site.setUrl(siteUrl);
        return siteRepository.save(site);
    }

    public void Pars (String url, EntityService entityService, int idSite, Site site)
            throws SQLException, IOException, ParserConfigurationException, InterruptedException {
        parseUrl.parsWeb(url, entityService, idSite, site);
    }

   public Page getPage (Page page, String path, int code, String content, int idSite){
            page.setId(idSite);
            page.setPath(path);
            page.setCode(code);
            page.setContent(content);
       return pageRepository.save(page);
   }
}


