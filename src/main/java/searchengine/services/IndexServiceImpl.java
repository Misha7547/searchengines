package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndexServiceImpl  implements IndexService{
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private SiteRepository siteRepository;


    @Override
    public Object startIndexing() {

        return null;
    }

}
