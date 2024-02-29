package searchengine.services;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.tartarus.snowball.ext.RussianStemmer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class Lemmatisator {


    private final LuceneMorphology luceneMorph = new RussianLuceneMorphology();

    private static final String REGEXP_TEXT = "\\s*(\\s|\\?|\\||»|«|\\*|,|!|\\.)\\s*";

    private static final String REGEXP_WORD = "[а-яА-ЯёЁ]+";

    public Lemmatisator() throws IOException {
    }

    public Map<String, Integer> lemmatisator(String offer){
        HashMap<String, Integer> wordsMap;
        wordsMap = new HashMap<>();
        String text = offer.trim();
        String[] words = text.toLowerCase().split(REGEXP_TEXT);

        for (String word : words) {
            if (wordCheck(word)) {
                List<String> wordBaseForms = luceneMorph.getNormalForms(word);
                wordBaseForms.forEach(w -> wordsMap.put(w, wordsMap.getOrDefault(w, 0) + 1));
            }
        }
        return wordsMap;
    }

    private boolean wordCheck(String word) {

        if (word.matches(REGEXP_WORD)) {
            List<String> wordBaseForms =
                    luceneMorph.getMorphInfo(word);
            return !wordBaseForms.get(0).endsWith("ПРЕДЛ") && (!wordBaseForms.get(0).endsWith("СОЮЗ")) &&
                    (!wordBaseForms.get(0).endsWith("ЧАСТ")) && (!wordBaseForms.get(0).endsWith("МЕЖД"));
        }
        return false;
    }

    public String clearingTags ( String html) throws IOException {
        Document document = Jsoup.connect(html)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com")
                .get();
        return document.text();
    }

    public String getSnippet(String content, String textQuery) {

        String textForSearchQuery = Jsoup.parse(content).getElementsContainingOwnText(textQuery).text();
        Pattern pattern = Pattern.compile(textQuery);
        Matcher matcher = pattern.matcher(textForSearchQuery);
        String snippet;
        if (matcher.find()) {
            snippet = find(matcher,textForSearchQuery,textQuery);
        } else {
            textQuery = textQuery.trim();
            String[] words = textQuery.toLowerCase().split(REGEXP_TEXT);

            StringBuilder builderSnippet = new StringBuilder();
            RussianStemmer russianStemmer = new RussianStemmer();

            for (String word : words) {
                if (wordCheck(word)) {
                    russianStemmer.setCurrent(word);
                    if (russianStemmer.stem()) {
                        word = russianStemmer.getCurrent() + ".*?\\b";
                    }
                    String textForSearchWord = Jsoup.parse(content).getElementsMatchingOwnText(word).text();
                    pattern = Pattern.compile(word, Pattern.CASE_INSENSITIVE);
                    matcher = pattern.matcher(textForSearchWord);
                    if (matcher.find()) {
                        String snippetWord = findSnippet( matcher,textForSearchWord);
                        builderSnippet.append(snippetWord).append("...");
                    }
                }
            }
            snippet = builderSnippet.toString();
        }
        return snippet;
    }

    private String find (Matcher matcher,String textForSearchQuery, String textQuery){

        String snippet;
        int beginIndex = matcher.start() > 80 ?
                textForSearchQuery.lastIndexOf(' ', matcher.start() - 60) : 0;
        int endIndex = Math.min(beginIndex + matcher.end() + 160, textForSearchQuery.length());
        snippet = textForSearchQuery.substring(beginIndex, endIndex);
        snippet = StringUtils.replace(snippet, textQuery, "<b>" + textQuery + "</b>");
        return snippet;

    }

    private String findSnippet(Matcher matcher, String textForSearchWord ){

        int beginIndex = matcher.start() > 35 ?
                textForSearchWord.lastIndexOf(' ', matcher.start() - 15) : 0;
        int endIndex = Math.min(matcher.start() + 80, textForSearchWord.length());
        String result = matcher.group();
        String snippetWord = textForSearchWord.substring(beginIndex, endIndex);
        snippetWord = StringUtils.replace(snippetWord, result, "<b>" + result + "</b>");
        return snippetWord;

    }
}


