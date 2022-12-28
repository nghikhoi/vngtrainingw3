package vng.training.w3.predictive;

import vng.training.w3.common.Trie;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static vng.training.w3.Utils.toCharacterArray;

public class TriePredictive implements IPredictiveText {

    private final Trie<Character, Boolean> trie = new Trie<>();

    public TriePredictive(List<String> words) {
        for (String word : words) {
            trie.put(toCharacterArray(word), true);
        }
    }

    private Trie<Character, Boolean> seekTo(String source) {
        Trie<Character, Boolean> node = trie;
        for (char c : source.toCharArray()) {
            node = node.getChild(c);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    @Override
    public List<String> predict(String source, int limit) {
        List<String> result = new LinkedList<>();

        for (int i = source.length() - 1; i >= 0 && result.size() < limit; i--) {
            String prefix = source.substring(0, i + 1);

            Trie<Character, Boolean> node = seekTo(prefix);
            if (node == null) {
                continue;
            }
            while (result.size() < limit) {
                List<List<Character>> seekKeys = node.seekKeys(limit - result.size());
                while (result.size() < limit && !seekKeys.isEmpty()) {
                    List<Character> seekKey = seekKeys.remove(0);
                    StringBuilder sb = new StringBuilder(prefix);
                    for (Character c : seekKey) {
                        sb.append(c);
                    }
                    result.add(sb.toString());
                }
            }
        }

        return result;
    }

}
