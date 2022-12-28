package vng.training.w3.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Trie<K, V> {

    private final HashMap<K, Trie<K, V>> children = new HashMap<>();
    private V value;

    public Set<K> getKeys() {
        return children.keySet();
    }

    public Trie<K, V> getChild(K key) {
        return children.get(key);
    }

    public void put(K[] keys, V value) {
        Trie<K, V> node = this;
        for (K key : keys) {
            node = node.children.computeIfAbsent(key, k -> new Trie<>());
        }
        node.value = value;
    }

    public V get(K[] keys) {
        Trie<K, V> node = this;
        for (K key : keys) {
            node = node.children.get(key);
            if (node == null) {
                return null;
            }
        }
        return node.value;
    }

    public List<List<K>> seekKeys(int limit) {
        List<List<K>> result = new LinkedList<>();

        int remaining = limit;
        for (K key : getKeys()) {
            Trie<K, V> node = getChild(key);

            for (List<K> seekKey : node.seekKeys(remaining)) {
                if (seekKey.isEmpty()) {
                    continue;
                }
                List<K> fullKey = new LinkedList<>();
                fullKey.add(key);
                fullKey.addAll(seekKey);

                result.add(fullKey);
                remaining--;
                if (remaining == 0) {
                    return result;
                }
            }

            if (node.value != null) {
                result.add(List.of(key));
                remaining--;
                if (remaining == 0) {
                    return result;
                }
            }
        }

        return result;
    }

}
