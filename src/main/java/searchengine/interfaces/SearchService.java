package searchengine.interfaces;

import java.io.IOException;

public interface SearchService {

    Object search (String query,String siteUrl) throws IOException;
}
