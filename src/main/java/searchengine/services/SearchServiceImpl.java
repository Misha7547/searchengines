package searchengine.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.DateResponse;
import searchengine.dto.statistics.ResultSearch;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;


import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Data
public class SearchServiceImpl implements SearchService {


    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final Lemmatisator lemmatisator;

    public Object search(String query, String siteUrl) throws IOException {

        Map<String, Integer> wordsMap = lemmatisator.lemmatisator(query);
        List<Lemma> listLemmas = (List<Lemma>) lemmaRepository.findAll();
        List<Lemma> listSortedLemma = new ArrayList<>();
        List<Index> listIndexAll = (List<Index>) indexRepository.findAll();
        List<Index> listIndex = new ArrayList<>();
        Object resultSearch;

        for (String key : wordsMap.keySet()) {
            if (wordsMap.get(key) > 8) {
                wordsMap.remove(key);
            }
            searchLemma(key, listLemmas, listSortedLemma);
        }

        for (int i = 0; i < listSortedLemma.size(); i++) {
            if (i > 0 && listIndex.isEmpty()) {
                break;
            } else
                searchIndex(listSortedLemma.get(i), listIndexAll, listIndex);
        }

        checkSite(siteUrl, listIndex);

        if (listIndex.isEmpty()) {
            ResultSearch result = new ResultSearch();
            resultSearch = getDataResponse(listIndex, (List<ResultSearch>) result);
        } else {

            HashMap<Index, Integer> absoluteRelevanceList = new HashMap<>();
            HashMap<Index, Double> relativeRelevanceList = new HashMap<>();

            for (Index index : listIndex) {
                absoluteRelevance(index, listIndexAll, listSortedLemma, absoluteRelevanceList);
            }

            double max = Collections.max(absoluteRelevanceList.values());
            countRelative(absoluteRelevanceList, relativeRelevanceList, max);
            List<Map.Entry<Index, Double>> list = new LinkedList<>(relativeRelevanceList.entrySet());
            sort(list);
            resultSearch = createSearchResult(list, query, listIndex);
        }
        return resultSearch;
    }

    private void searchLemma(String key, List<Lemma> listLemmas, List<Lemma> listSortedLemma) {

        for (Lemma lemma : listLemmas) {
            if (key.equals(lemma.getLemma())) {
                listSortedLemma.add(lemma);
            }
        }
        listSortedLemma.sort((o1, o2) -> o1.getFrequency() - o2.getFrequency());
    }

    private void searchIndex(Lemma lemma, List<Index> listIndexAll, List<Index> listIndex) {

        if (listIndex.isEmpty()) {
            getIndexOne(lemma, listIndexAll, listIndex);
        } else {
            List<Index> numberIndex = new ArrayList<>();
            for (int i = 0; i < listIndex.size(); i++) {
                int j = deleteIndex(i, lemma, listIndexAll, listIndex);
                if (j == 0)
                    numberIndex.add(listIndex.get(i));
            }
            for (Index j : numberIndex) {
                listIndex.remove(j);
            }
        }
    }

    private void absoluteRelevance(Index index, List<Index> listIndexAll,
                                   List<Lemma> listSortedLemma, Map<Index, Integer> absoluteRelevanceList) {
        int g = 0;
        for (Lemma lemma : listSortedLemma) {
            for (Index indexs : listIndexAll) {
                if (indexs.getPageId().getId() == index.getPageId().getId()
                        && indexs.getLemmaId().getLemma().equals(lemma.getLemma()))
                    g = g + indexs.getRank();
            }
        }
        absoluteRelevanceList.put(index, g);
    }

    private DateResponse createSearchResult(List<Map.Entry<Index, Double>> list,
                                            String query, List<Index> listIndex) throws IOException {
        List<ResultSearch> searchResult = new ArrayList<>();
        for (Map.Entry<Index, Double> index : list) {

            ResultSearch resultSearch = new ResultSearch();
            resultSearch.setSite(index.getKey().getPageId().getSiteId().getUrl());
            resultSearch.setSiteName(index.getKey().getPageId().getSiteId().getName());
            resultSearch.setUri(index.getKey().getPageId().getPath());
            Document document = Jsoup.connect(index.getKey().getPageId().getPath()).get();
            resultSearch.setTitle(document.title());
            resultSearch.setSnippet(lemmatisator.getSnippet(index.getKey().getPageId().getContent(), query));
            resultSearch.setRelevance(index.getValue());
            searchResult.add(resultSearch);
        }
        return getDataResponse(listIndex, searchResult);
    }

    private void countRelative(Map<Index, Integer> absoluteRelevanceList,
                               Map<Index, Double> relativeRelevanceList, double max) {
        double relativeRelevances;

        for (Index index : absoluteRelevanceList.keySet()) {
            relativeRelevances = (double) absoluteRelevanceList.get(index) / max;
            relativeRelevanceList.put(index, relativeRelevances);
        }
    }

    private void sort(List<Map.Entry<Index, Double>> list) {

        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
    }

    private DateResponse getDataResponse(List<Index> listIndex, List<ResultSearch> searchResult) {

        DateResponse dateResponse = new DateResponse();
        dateResponse.setResult(true);
        dateResponse.setCount(listIndex.size());
        dateResponse.setData(searchResult);
        return dateResponse;
    }

    private void checkSite(String siteUrl, List<Index> listIndex) {

        if (siteUrl != null) {
            List<Index> numberIndex = new ArrayList<>();
            for (Index index : listIndex) {
                int j = 0;
                if (index.getPageId().getSiteId().getUrl().equals(siteUrl))
                    j++;
                if (j == 0)
                    numberIndex.add(index);
            }
            for (Index j : numberIndex) {
                listIndex.remove(j);
            }
        }
    }

    private void getIndexOne(Lemma lemma, List<Index> listIndexAll, List<Index> listIndex) {

        for (Index index : listIndexAll) {
            if (lemma.getId() == index.getLemmaId().getId())
                listIndex.add(index);
        }
    }

    private int deleteIndex(int i, Lemma lemma, List<Index> listIndexAll, List<Index> listIndex) {
        int j = 0;
        for (Index indexs : listIndexAll) {
            if (indexs.getPageId() == listIndex.get(i).getPageId()
                    && lemma.getLemma().equals(indexs.getLemmaId().getLemma()))
                j++;
        }
        return j;
    }
}
