package searchengine.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.ParserConfig;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.model.Site;
import searchengine.model.Status;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Data
public class IndexServiceImpl  implements IndexService{
    @Autowired
    ParserConfig parserConfig;
    @Autowired
    private EntityService entityService;

    Site site;

    StatisticsData statisticsData;

    private Boolean isIndexingRun = true;
    @Override
    public Object startIndexing() {

        ArrayList<DetailedStatisticsItem> name = new ArrayList<>(statisticsData.getDetailed());
        for (DetailedStatisticsItem names: name){
            entityService.findSiteByName(site,names.getName());
            entityService.findSiteByUrl(site,names.getUrl());
            entityService.updateSite(site, Status.valueOf("axx"));
            entityService.updateLastError(site,"пиздец");
            return names;
        }

        return null;
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
}




