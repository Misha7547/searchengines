package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.interfaces.StatisticsService;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {


    @Autowired
    private final SiteRepository siteRepository;
    @Autowired
    private final PageRepository pageRepository;
    @Autowired
    private final LemmaRepository lemmaRepository;
    private final SitesList sites;
    private String statusSite = null;
    private String errorSite = null;
    private long dateMillis = 0;
    private int idI;

    @Override
    public StatisticsResponse getStatistics() {

        TotalStatistics total = new TotalStatistics();
        total.setSites(sites.getSites().size());
        total.setIndexing(true);

        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        List<Site> sitesList = sites.getSites();
        for(int i = 0; i < sitesList.size(); i++) {
            Site site = sitesList.get(i);
            infoSite(site.getUrl());
            List<Page> pageList = listSorting();
            List<Lemma> lemmaList = lemmaSorting();
            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setName(site.getName());
            item.setUrl(site.getUrl());
            int pages = pageList.size();
            int lemmas = lemmaList.size();
            item.setPages(pages);
            item.setLemmas(lemmas);
            item.setStatus(statusSite);
            item.setError(errorSite);
            item.setStatusTime(System.currentTimeMillis() - dateMillis);
            total.setPages(total.getPages() + pages);
            total.setLemmas(total.getLemmas() + lemmas);
            detailed.add(item);
        }

        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);
        response.setStatistics(data);
        response.setResult(true);
        return response;
    }

    private List<Page> listSorting(){
        List<Page> listPages = (List<Page>) pageRepository.findAll();
        List<Page> listPage = new ArrayList<>();
                int i = idI;
                for (Page page: listPages){
                    int x = page.getSiteId().getId();
                    if(x == i){
                        listPage.add(page);
                    }
                }
        return listPage;
    }

    private List<Lemma> lemmaSorting(){
        List<Lemma> listLemmas = (List<Lemma>) lemmaRepository.findAll();
        List<Lemma> listLemma = new ArrayList<>();
        int i = idI;
        for (Lemma lemma:listLemmas){
            int x = lemma.getSiteByLemma().getId();
            if(x == i){
                listLemma.add(lemma);
            }
        }
        return listLemma;
    }

    private void infoSite(String urlSite){
        Date date;
        List<searchengine.model.Site> listsites = (List<searchengine.model.Site>) siteRepository.findAll();
        for (searchengine.model.Site site: listsites){
            if(urlSite.contains(site.getUrl())){
               idI = site.getId();
               statusSite = site.getStatus().toString();
               errorSite = site.getLastError();
               date = site.getStatusTime();
               dateMillis = date.getTime();
            }
        }
    }
}
