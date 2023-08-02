package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name="search_index")
public class Index {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "page_id")
    private int pageId;

    @Column(name = "lemma_id")
    private int lemmaId;

    private float rank;
}
