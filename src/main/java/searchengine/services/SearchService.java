package searchengine.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Data
public class SearchService {

    @Autowired
    Lemmatisator lemmatisator;

    public Object search (String query) throws IOException {
        HashMap<String, Integer> wordsMap = new HashMap<>();
        wordsMap =lemmatisator.lemmatisator(query);
        for (String key : wordsMap.keySet()) {

        }
        return wordsMap;
    }
}
