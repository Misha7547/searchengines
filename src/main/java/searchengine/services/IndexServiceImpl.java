package searchengine.services;

import com.sun.xml.txw2.Document;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.ParserLinks;
import searchengine.config.ParserConfig;
import searchengine.model.Page;
import searchengine.model.Site;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class IndexServiceImpl  implements IndexService{
    @Autowired
    ParserConfig parserConfig;

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


