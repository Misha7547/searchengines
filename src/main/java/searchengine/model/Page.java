package searchengine.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
@Setter
@Getter
@Entity
@Table(name="Search_page",
        indexes = {@Index(name = "Path_INDX", columnList = "path")})
public class Page
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "site_id")
    private int siteId;
    private String path;
    private int code;
    @Type(type = "text")
    private String content;

    public void setId() {
    }
}
