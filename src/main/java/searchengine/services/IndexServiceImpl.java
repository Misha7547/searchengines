package searchengine.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.model.Page;
import searchengine.model.Site;

@Service
@RequiredArgsConstructor
public class IndexServiceImpl  implements IndexService{
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private SiteRepository siteRepository;

private Page page;
private Site site;

    private Boolean isIndexingRun = false;
    @Override
    public Object startIndexing() {
        isIndexingRun = true;
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
