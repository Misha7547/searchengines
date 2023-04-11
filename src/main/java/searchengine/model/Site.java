package searchengine.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="Search_site")
public class Site
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "status_time")
    private Date statusTime;
    @Type(type = "text")
    @Column(name = "last_error")
    private String lastError;
    private String url;
    private String name;

}
