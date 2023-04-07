package searchengine.services;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Site;

import java.util.List;

@Repository
public interface SiteRepository extends CrudRepository <Site,Integer> {

    Site findByName(String siteName);

    Site findByUrl(String siteUrl);

    List<Site> findAll();
}
