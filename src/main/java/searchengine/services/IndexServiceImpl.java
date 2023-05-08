package searchengine.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.ParseUrl;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.Page;
import searchengine.model.Status;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
@Data
public class IndexServiceImpl  implements IndexService{
    @Autowired
    private EntityService entityService;

    private Boolean isIndexingRun = true;
    @Autowired
    SitesList sitesList;

    ParseUrl parseUrl = new ParseUrl();

    @Override
    public Object startIndexing() {

        entityService.deleteAllSite();

      for (Site list: sitesList.getSites()){
          try {
              CompletableFuture.runAsync(() -> {
                  try {
                      getSiteAndPage(list.getName(), list.getUrl());
                  } catch (SQLException e) {
                      throw new RuntimeException(e);
                  } catch (IOException e) {
                      throw new RuntimeException(e);
                  } catch (ParserConfigurationException e) {
                      throw new RuntimeException(e);
                  } catch (InterruptedException e) {
                      throw new RuntimeException(e);
                  }
              }, ForkJoinPool.commonPool());
          } catch (Exception e){
              System.out.println("Ошибка один");
          }
      }
        return isIndexingRun;
    }

    @Override
    public Object stopIndexing() {
        isIndexingRun = false;
        return null;
    }

    @Override
    public Boolean IsIndexingRun() {
        return isIndexingRun;
    }

    public  void getSiteAndPage (String name, String url) throws SQLException, IOException, ParserConfigurationException, InterruptedException {
        try {
            searchengine.model.Site site = new searchengine.model.Site();
            Page page  = new Page();
            entityService.findSiteByName(site,name);
            entityService.findSiteByUrl(site,url);
            entityService.updateSite(site,Status.INDEXING);
            int idSite = site.getId();
            entityService.Pars(url,entityService);
        } catch (Exception e){
            System.out.println(" Ошибка 2 ");
        }

    }
}




