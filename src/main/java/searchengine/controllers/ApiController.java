package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.IndexService;
import searchengine.services.StatisticsService;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final IndexService indexService;


    public ApiController(StatisticsService statisticsService, IndexService indexService) {
        this.statisticsService = statisticsService;
        this.indexService = indexService;
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
}
