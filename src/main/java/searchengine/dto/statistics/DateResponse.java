package searchengine.dto.statistics;


import lombok.Data;

@Data
public class DateResponse {

    private boolean result;

    private int count;

    private ResultSearch resultSearch;
}
