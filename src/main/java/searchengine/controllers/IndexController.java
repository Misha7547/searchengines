package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.services.IndexService;

@RestController
@RequestMapping("/api")
public class IndexController {

    private final IndexService indexService;

    public IndexController (IndexService indexService){
        this.indexService = indexService;
    }

    @GetMapping("/startIndexing")
    public ResponseEntity start() {
       // if (entityService.isIndexingRun()) {
            return ResponseEntity.ok(indexService.startIndexing());
       // }
       // return ResponseEntity.badRequest().body("Индексация не запущена");
    }
}
