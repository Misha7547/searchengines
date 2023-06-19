package searchengine.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.Status;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
@Data
public class IndexServiceImpl implements IndexService {

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    PageRepository pageRepository;

    private Boolean isIndexingRun = true;
    @Autowired
    SitesList sitesList;

    ForkJoinPool forkJoinPool = new ForkJoinPool();

    @Override
    public Object startIndexing() {
        isIndexingRun = true;
        pageRepository.deleteAll();
        siteRepository.deleteAll();

        for (Site list : sitesList.getSites()) {
            CompletableFuture.runAsync(() -> {
                try {
                    getSiteAndPage(list.getName(), list.getUrl(), isIndexingRun);
                } catch (SQLException | IOException | ParserConfigurationException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, ForkJoinPool.commonPool());
        }
        return isIndexingRun;
    }

    @Override
    public Object stopIndexing()
            throws SQLException, IOException, ParserConfigurationException, InterruptedException {
        isIndexingRun = false;
        forkJoinPool.shutdown();
        getSiteAndPage(null, null, isIndexingRun);
        return isIndexingRun;
    }

    @Override
    public Boolean isIndexingRun() {
        return isIndexingRun;
    }

    public void getSiteAndPage(String name, String url, Boolean isIndexingRun)
            throws SQLException, IOException, ParserConfigurationException, InterruptedException {
        searchengine.model.Site site = new searchengine.model.Site();
        ParseUrl parseUrl = new ParseUrl();
        if (isIndexingRun) {
            site.setName(name);
            site.setUrl(url);
            site.setStatus(Status.INDEXING);
            site.setStatusTime(new Timestamp(System.currentTimeMillis()));
            int idSite = site.getId();
            siteRepository.save(site);
            parseUrl.parsWeb(idSite, url, pageRepository, siteRepository, name);
        } else if (!isIndexingRun) {
            site.setName(name);
            site.setUrl(url);
            site.setStatus(Status.FAILED);
            site.setLastError("Индексация остановлена пользователем");
            site.setStatusTime(new Timestamp(System.currentTimeMillis()));
            siteRepository.save(site);
        }
    }
}




