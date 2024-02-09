package searchengine.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.ResultSearch;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.SiteRepository;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Data
public class SearchService {

    @Autowired
    Lemmatisator lemmatisator;
    @Autowired
    private final LemmaRepository lemmaRepository;
    @Autowired
    private final IndexRepository indexRepository;

    public Object search (String query,String siteUrl) throws IOException {

        HashMap<String, Integer> wordsMap =lemmatisator.lemmatisator(query);
        List<Lemma> listLemmas = (List<Lemma>) lemmaRepository.findAll();
        List<Lemma> listSortedLemma = new ArrayList<>();

        for (String key : wordsMap.keySet()) {
            if (wordsMap.get(key) > 8 ){
                wordsMap.remove(wordsMap.get(key));
            }
            searchLemma(key, listLemmas,listSortedLemma);
        }

        List<Index> listIndexAll = (List<Index>) indexRepository.findAll();
        List<Index> listIndex = new ArrayList<>();

        for (int i = 0; i < listSortedLemma.size(); i++ ){
            if(i > 0 && listIndex.size() == 0){
                break;
            } else
            searchIndex(listSortedLemma.get(i),listIndexAll,listIndex);
        }

        if( siteUrl != null){
            List <Index> numberIndex =new ArrayList<>();
            for( Index index :listIndex){
                int j = 0;
                if(index.getPageId().getSiteId().getUrl().equals(siteUrl)){
                    j++;
                    System.out.println(index.getId());
                }
                if (j == 0)
                    numberIndex.add(index);
            }
            for (Index j : numberIndex){
                listIndex.remove(j);
            }
        }
        Object resultSearch = null;

        if(listIndex.size() == 0){

        } else {

            HashMap <Index, Integer> absoluteRelevanceList = new HashMap<>();
            for ( Index index:listIndex){
                absoluteRelevance(index,listIndexAll, listSortedLemma,absoluteRelevanceList);
            }

            HashMap <Index, Double> relativeRelevanceList = new HashMap<>();
            double max = Collections.max(absoluteRelevanceList.values());
            double relativeRelevances;

            for ( Index index: absoluteRelevanceList.keySet()){
            relativeRelevances = (double) absoluteRelevanceList.get(index)  /  max;
            relativeRelevanceList.put(index,relativeRelevances);
            }

            List < Map.Entry<Index,Double>> list = new LinkedList<Map.Entry<Index, Double>>(relativeRelevanceList.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<Index, Double>>() {
                @Override
                public int compare(Map.Entry<Index, Double> o1, Map.Entry<Index, Double> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            resultSearch = createSearchResult(list,query);
        }
        return resultSearch ;
    }

    public void searchLemma (String key, List <Lemma> listLemmas, List<Lemma> listSortedLemma){

        for (Lemma lemma : listLemmas){
            if (key.equals(lemma.getLemma())){
                listSortedLemma.add(lemma);
            }
        }
        Collections.sort(listSortedLemma, new Comparator<Lemma>() {
            @Override
            public int compare(Lemma o1, Lemma o2) {
                return o1.getFrequency() - o2.getFrequency();
            }
        });
    }

    public void searchIndex (Lemma lemma, List<Index> listIndexAll, List<Index> listIndex){

        if (listIndex.size() == 0 ){
            for (Index index: listIndexAll){
                if(lemma.getId() == index.getLemmaId().getId())
                    listIndex.add(index);
            }
        } else {
            List <Index> numberIndex =new ArrayList<>();
            for (int i = 0; i < listIndex.size(); i++){
                int j = 0;
                for (Index indexs: listIndexAll){
                    if(indexs.getPageId() == listIndex.get(i).getPageId()
                            && lemma.getLemma().equals(indexs.getLemmaId().getLemma()))
                        j++;
                }
                if (j == 0)
                numberIndex.add(listIndex.get(i));
            }
            for (Index j : numberIndex){
                listIndex.remove(j);
            }
        }
    }

    public void absoluteRelevance (Index index, List<Index> listIndexAll,
                                   List<Lemma> listSortedLemma, HashMap <Index, Integer> absoluteRelevanceList){

        int g = 0;
        for(Lemma lemma: listSortedLemma){
            for( Index indexs: listIndexAll){
                if(indexs.getPageId().getId() == index.getPageId().getId()
                        && indexs.getLemmaId().getLemma().equals(lemma.getLemma()))
                g = g+indexs.getRank();
            }
        }
        absoluteRelevanceList.put(index,g);
    }

    private List<ResultSearch> createSearchResult(List < Map.Entry<Index,Double>> list, String query  ) throws IOException {

        List<ResultSearch> searchResult = new ArrayList<>();

        for (Map.Entry<Index, Double> index: list){

            ResultSearch resultSearch = new ResultSearch();
            resultSearch.setSite(index.getKey().getPageId().getSiteId().getUrl());
            resultSearch.setSiteName(index.getKey().getPageId().getSiteId().getName().toString());
            resultSearch.setUri(index.getKey().getPageId().getPath());
            Document document = Jsoup.connect(index.getKey().getPageId().getPath()).get();
            resultSearch.setTitle(document.title());
            resultSearch.setSnippet(lemmatisator.getSnippet(index.getKey().getPageId().getContent(), query));
            resultSearch.setRelevance(index.getValue());
            searchResult.add(resultSearch);
        }
        return searchResult;
    }
}
