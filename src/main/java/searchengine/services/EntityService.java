package searchengine.services;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.model.Site;
import searchengine.model.Status;


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

//    public void deleteAllPages() {
//        pageRepository.deleteAll();
//    }

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



//    public Page getPageByPath(Page page, Site site) {
//        page.setSiteId(site.getId());
//        return pageRepository.save(page);
//    }


//    public synchronized Page addPageToDB(String path, int code, String content, int pageId) {
//        Page page = new Page();
//        if (!(pageId == 0)) {
//            page.setId(pageId);
//        }
//        page.setPath(path);
//        page.setCode(code);
//        page.setContent(content);
//        pageRepository.save(page);
//        return page;
//    }

    public void addEntitiesToDB(Document doc, String url, int code, Site site, int pageId) throws SQLException, IOException {

        }

}


