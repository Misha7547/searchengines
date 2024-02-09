package searchengine.dto.statistics;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class ResultSearch {

    private String site;

    private String siteName;

    private String uri;

    private String title;

    private String snippet;

    private double relevance;
}
