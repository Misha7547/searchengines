package searchengine.model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@Data
public class ListPage {
    private List<Page> listPage;
}
