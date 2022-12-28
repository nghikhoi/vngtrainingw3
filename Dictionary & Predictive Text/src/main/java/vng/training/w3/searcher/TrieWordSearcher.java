package vng.training.w3.searcher;

import vng.training.w3.common.Trie;

import java.util.Arrays;
import java.util.List;

import static vng.training.w3.Utils.toCharacterArray;

public class TrieWordSearcher extends WordSearcher {

    private final Trie<Character, Boolean> trie = new Trie<>();

    public TrieWordSearcher(List<String> words) {
        super(words);

        intialize(words);
    }

    private void intialize(List<String> words) {
        for (String word : words) {
            trie.put(toCharacterArray(word), true);
        }
    }

    @Override
    public boolean contains(String word) {
        return trie.get(List.of(word.toCharArray()).toArray(new Character[word.length()])) != null;
    }

}
