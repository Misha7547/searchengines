package searchengine.dto.statistics;


import lombok.Data;

import java.util.List;

@Data
public class DateResponse {

    private boolean result;

    private int count;

    private List <ResultSearch> data;
}
