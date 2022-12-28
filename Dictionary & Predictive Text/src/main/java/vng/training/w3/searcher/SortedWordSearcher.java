package vng.training.w3.searcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortedWordSearcher extends WordSearcher {

    public SortedWordSearcher(List<String> words) {
        super(new ArrayList<>(words.size()));
        initialize(words);
    }

    private void initialize(List<String> words) {
        getWords().addAll(words);
        Collections.sort(getWords());
    }

    @Override
    public boolean contains(String word) {
        return Collections.binarySearch(getWords(), word) >= 0;
    }

}
