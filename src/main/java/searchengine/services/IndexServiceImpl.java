package searchengine.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.ParseUrl;
import searchengine.config.Site;
import searchengine.config.SitesList;
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
    ForkJoinPool forkJoinPool = new ForkJoinPool();
    @Override
    public Object startIndexing() {
        isIndexingRun =true;
        entityService.deleteAllSite();
        entityService.deleteAllPages();

      for (Site list: sitesList.getSites()){
              CompletableFuture.runAsync(() -> {
                  try {
                      getSiteAndPage(list.getName(), list.getUrl(),isIndexingRun);
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
      }
        return isIndexingRun;
    }

    @Override
    public Object stopIndexing() throws SQLException, IOException, ParserConfigurationException, InterruptedException {
        isIndexingRun = false;
        forkJoinPool.shutdown();
        getSiteAndPage(null,null,isIndexingRun);
        return isIndexingRun;
    }

    @Override
    public Boolean IsIndexingRun() {
        return isIndexingRun;
    }

    public  void getSiteAndPage (String name, String url,Boolean isIndexingRun ) throws SQLException, IOException, ParserConfigurationException, InterruptedException {
        searchengine.model.Site site = new searchengine.model.Site();
        if (isIndexingRun == true) {
            entityService.findSiteByName(site, name);
            entityService.findSiteByUrl(site, url);
            entityService.updateSite(site, Status.INDEXING);
            int idSite = site.getId();
            entityService.Pars(url, entityService, idSite, site);
        } else  if (isIndexingRun ==false){
            entityService.updateSite(site,Status.FAILED);
            entityService.updateLastError(site,"Индексация остановлена пользователем");
        }

    }
}




