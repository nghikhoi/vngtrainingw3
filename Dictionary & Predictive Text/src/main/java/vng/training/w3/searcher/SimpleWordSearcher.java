package vng.training.w3.searcher;

import java.util.ArrayList;
import java.util.List;

public class SimpleWordSearcher extends WordSearcher {

    public SimpleWordSearcher(List<String> words) {
        super(new ArrayList<>(words.size()));

        initialize(words);
    }

    private void initialize(List<String> words) {
        getWords().addAll(words);
    }

    @Override
    public boolean contains(String word) {
        return getWords().contains(word);
    }

}
