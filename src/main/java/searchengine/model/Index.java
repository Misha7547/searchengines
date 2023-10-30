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

    @ManyToOne()
    @JoinColumn(name = "page_id", referencedColumnName = "id")
    private Page pageId;

    @ManyToOne()
    @JoinColumn(name = "lemma_id", referencedColumnName = "id")
    private Lemma lemmaId;

//    private float rank;
}
