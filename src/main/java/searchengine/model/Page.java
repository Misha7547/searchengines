package searchengine.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import javax.persistence.*;


@Setter
@Getter
@Entity
@Table(name="search_page")

//@Table(name="search_page",
//        indexes = {@Index(name = "path_INDX", columnList = "path")})
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne()
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    private Site siteId;
    private String path;
    private int code;
    @Type(type = "text")
    private String content;
}
