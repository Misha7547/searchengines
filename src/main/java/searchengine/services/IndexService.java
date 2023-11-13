package searchengine.services;

import searchengine.model.Site;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;

public interface IndexService {

    Object startIndexing ();

    Object stopIndexing() throws SQLException, IOException, ParserConfigurationException, InterruptedException;

    Boolean isIndexingRun();

    Object getIndexPage(String html) throws IOException;

    Boolean checkSite(String html);
}
