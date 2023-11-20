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


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final SitesList sites;
    @Autowired
    private final SiteRepository siteRepository;
    @Autowired
    private final PageRepository pageRepository;
    @Autowired
    private final LemmaRepository lemmaRepository;

    @Override
    public StatisticsResponse getStatistics() {

        TotalStatistics total = new TotalStatistics();
        total.setSites(sites.getSites().size());
        total.setIndexing(true);

        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        List<Site> sitesList = sites.getSites();
        for(int i = 0; i < sitesList.size(); i++) {
            Site site = sitesList.get(i);
            List<Page> pageList = pageList(site.getUrl());
            List<Lemma> lemmaList = lemmas(site.getUrl());
            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setName(site.getName());
            item.setUrl(site.getUrl());
            int pages = pageList.size();
            int lemmas = lemmaList.size();
            item.setPages(pages);
            item.setLemmas(lemmas);
            item.setStatus(statusSite(site.getUrl()));
            item.setError(errorSite(site.getUrl()));
            item.setStatusTime(System.currentTimeMillis() - statusTimeSite(site.getUrl()));
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

    public List<Page> pageList(String urlSite){
        List<Page> listPages = (List<Page>) pageRepository.findAll();
        List<Page> listPage = new ArrayList<>();
                int i = idSite(urlSite);
                for (Page page: listPages){
                    int x = page.getSiteId().getId();
                    if(x == i){
                        listPage.add(page);
                    }
                }
        return listPage;
    }

    public List<Lemma> lemmas(String urlSite){
        List<Lemma> listLemmas = (List<Lemma>) lemmaRepository.findAll();
        List<Lemma> listLemma = new ArrayList<>();
        int i = idSite(urlSite);
        for (Lemma lemma:listLemmas){
            int x = lemma.getSiteByLemma().getId();
            if(x == i){
                listLemma.add(lemma);
            }
        }
        return listLemma;
    }

    public String statusSite (String urlSite){
        String statusSite = null;
        List<searchengine.model.Site> listsites = (List<searchengine.model.Site>) siteRepository.findAll();
        for (searchengine.model.Site site: listsites){
            if(urlSite.contains(site.getUrl())){
                statusSite = site.getStatus().toString();
            }
        }
        return statusSite;
    }

    public String errorSite (String urlSite){
        String errorSite = null;
        List<searchengine.model.Site> listsites = (List<searchengine.model.Site>) siteRepository.findAll();
        for (searchengine.model.Site site: listsites){
            if(urlSite.contains(site.getUrl())){
                errorSite = site.getLastError();
            }
        }
        return errorSite;
    }

    public long statusTimeSite (String urlSite){
        long dateMillis = 0;
        Date date;
        List<searchengine.model.Site> listsites = (List<searchengine.model.Site>) siteRepository.findAll();
        for (searchengine.model.Site site: listsites){
            if(urlSite.contains(site.getUrl())){
                date = site.getStatusTime();
                dateMillis = date.getTime();
            }
        }
        return dateMillis;
    }

    public int  idSite(String urlSite){
        int i = 0;
        List<searchengine.model.Site> listsites = (List<searchengine.model.Site>) siteRepository.findAll();
        for (searchengine.model.Site site: listsites){
            if(urlSite.contains(site.getUrl())){
               i = site.getId();
            }
        }
      return i;
    }
}
