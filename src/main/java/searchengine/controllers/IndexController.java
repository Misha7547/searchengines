package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.config.SitesList;
import searchengine.services.EntityService;
import searchengine.services.IndexService;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;


@RestController
@RequestMapping("/api")
public class IndexController {
     @Autowired
     IndexService indexService;
     @Autowired
     SitesList sitesList;
     @Autowired
    EntityService entityService;


    public IndexController (IndexService indexService){
        this.indexService = indexService;
    }

    @GetMapping("/startIndexing")
    public ResponseEntity start() {
        if (indexService.IsIndexingRun()) {
            return ResponseEntity.ok(indexService.startIndexing());
        }
        return ResponseEntity.badRequest().body("Индексация не запущена");
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity stop() throws SQLException, IOException, ParserConfigurationException, InterruptedException {
        if (!indexService.IsIndexingRun()) {
            return ResponseEntity.ok(indexService.stopIndexing());
        }
        return ResponseEntity.badRequest().body("Индексация не запущена");
    }

}
