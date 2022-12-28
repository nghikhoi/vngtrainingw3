package vng.training.w3.searcher;

import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class HashWordSearcher extends WordSearcher {

    private final Set<String> hashSet = Sets.newHashSet();

    public HashWordSearcher(List<String> words) {
        super(words);
        initialize(words);
    }

    private void initialize(List<String> words) {
        hashSet.addAll(words);
    }

    @Override
    public boolean contains(String word) {
        return hashSet.contains(word);
    }

}
