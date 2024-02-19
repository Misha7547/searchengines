package searchengine.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.repository.IndexService;
import searchengine.repository.StatisticsService;
import searchengine.services.SearchService;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/api")
public class ApiController {


    private final StatisticsService statisticsService;
    private final IndexService indexService;
    private final SearchService searchService;


    public ApiController(StatisticsService statisticsService, IndexService indexService, SearchService searchService) {
        this.statisticsService = statisticsService;
        this.indexService = indexService;
        this.searchService = searchService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity start() {
        if (indexService.isIndexingRun()) {
            return ResponseEntity.ok(indexService.startIndexing());
        }
        return ResponseEntity.badRequest().body("Индексация не запущена");
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity stop() throws SQLException, IOException, ParserConfigurationException, InterruptedException {
        if (!indexService.isIndexingRun()) {
            return ResponseEntity.ok(indexService.stopIndexing());
        }
        return ResponseEntity.badRequest().body("Индексация не запущена");
    }

    @PostMapping("/indexPage")
    public ResponseEntity<Object> getPage(@RequestParam(name = "url") String url) throws IOException{
      if(indexService.checkSite(url)){
          return ResponseEntity.ok(indexService.getIndexPage(url));
      }
      return ResponseEntity.badRequest().body("Данная страница находится за пределами сайтов, \n" +
              "указанных в конфигурационном файле\n");
    }

    @GetMapping("/search")
    public ResponseEntity  <Object> search (@RequestParam(value = "query") String query,  @RequestParam(value = "site", required = false) String site) throws IOException {
        if (query != null){
            return ResponseEntity.ok(searchService.search(query,site));
        }
        return ResponseEntity.badRequest().body("Задан пустой поисковый запрос");
    }
}
