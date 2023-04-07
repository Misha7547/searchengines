package searchengine.services;

import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import searchengine.model.Page;
import searchengine.model.Site;


import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

public class EntityService {
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private PageRepository pageRepository;

    public void deleteAllPages() {
        pageRepository.deleteAll();
    }
    public void deleteAllSite() {
        siteRepository.deleteAll();
    }

//    public Site updateSite(Site site, StatusIndexing status) {
//        site.setStatus(status);
//        site.setStatusTime(new Timestamp(System.currentTimeMillis()));
//        return siteRepository.save(site);
//    }


    public Site updateLastError(Site site, String errorMessage) {
        site.setLastError(errorMessage);
        site.setStatusTime(new Timestamp(System.currentTimeMillis()));
        return siteRepository.save(site);
    }



//    public Site findSiteByName(Site site) {
//        return siteRepository.findById(site.getName());
//    }



    public Site findSiteByUrl(String siteUrl) {
        return siteRepository.findByUrl(siteUrl);
    }

    public Iterable<Site> findSites() {
        return siteRepository.findAll();
    }




//    public synchronized Page addPageToDB(String path, int code, String content, Site site, int pageId) {
//        Page page = new Page();
//        if (!(pageId == 0)) {
//            page.setId(pageId);
//        }
//        page.setPath(path);
//        page.setCode(code);
//        page.setContent(content);
//        page.setSiteByPage(site);
//        pageRepository.save(page);
//        return page;
//    }
public void addEntitiesToDB(Document doc, String url, int code, Site site, int pageId) throws SQLException, IOException {}
}


