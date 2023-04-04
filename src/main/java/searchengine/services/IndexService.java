package searchengine.services;

public interface IndexService {

    Object startIndexing ();

    Object stopIndexing();

    Boolean IsIndexingRun();
}
