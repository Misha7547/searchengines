package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name="search_lemma")
public class Lemma {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String lemma;

    private int frequency;

    @ManyToOne()
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    private Site siteByLemma;
}
