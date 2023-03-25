package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import searchengine.model.Page;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IndexServiceImpl  implements IndexService{
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private SiteRepository siteRepository;
    @Override
    public void startIndexing() {

    }

}
