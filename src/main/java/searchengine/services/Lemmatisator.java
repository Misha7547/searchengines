package searchengine.services;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Repository
public class Lemmatisator {

    private HashMap<String, Integer> wordsMap;

    private final LuceneMorphology luceneMorph = new RussianLuceneMorphology();

    private static final String REGEXP_TEXT = "\\s*(\\s|\\?|\\||»|«|\\*|,|!|\\.)\\s*";

    private static final String REGEXP_WORD = "[а-яА-ЯёЁ]+";

    public Lemmatisator() throws IOException {
    }

    public HashMap<String, Integer> Lemmatisator(String offer) throws IOException {

        wordsMap = new HashMap<>();
        String text = offer.trim();
        String[] words = text.toLowerCase().split(REGEXP_TEXT);

        for (String word : words) {
            if (wordCheck(word)) {
                List<String> wordBaseForms = luceneMorph.getNormalForms(word);
                wordBaseForms.forEach(w -> {
                    wordsMap.put(w, wordsMap.getOrDefault(w, 0) + 1);
                });
            }
        }
        return wordsMap;
    }

    public boolean wordCheck(String word) {

        if (word.matches(REGEXP_WORD)) {
            List<String> wordBaseForms =
                    luceneMorph.getMorphInfo(word);
            if ((!wordBaseForms.get(0).endsWith("ПРЕДЛ") && (!wordBaseForms.get(0).endsWith("СОЮЗ")) &&
                    (!wordBaseForms.get(0).endsWith("ЧАСТ")) && (!wordBaseForms.get(0).endsWith("МЕЖД")))) {
                return true;
            }
        }
        return false;
    }

    public String clearingTags ( String html){
        String textHtml = Jsoup.parse(html).text();
        return textHtml;
    }
}
