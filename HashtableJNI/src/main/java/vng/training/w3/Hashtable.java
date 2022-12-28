package vng.training.w3;

import java.util.Objects;

import static vng.training.w3.PrimeUtils.nextPrime;

public class Hashtable implements IHashtable {

    public static final int DEFAULT_BASE_SIZE = 53;
    private static final int PRIME_1 = 151;
    private static final int PRIME_2 = 163;

    public int baseSize, size, count;
    public HashtableItem[] items;

    public Hashtable(int baseSize) {
        this.baseSize = baseSize;
    }

    private static int hash(String s, int a, int m) {
        long hash = 0;
        for (int i = 0; i < s.length(); i++) {
            hash += (long) Math.pow(a, s.length() - (i + 1)) * s.charAt(i);
            hash = hash % m;
        }
        return (int) hash;
    }

    private static int getHash(String s, int numBuckets, int attempt) {
        int hashA = hash(s, PRIME_1, numBuckets);
        int hashB = hash(s, PRIME_2, numBuckets);
        return (hashA + (attempt * (hashB + 1))) % numBuckets;
    }

    private static HashtableItem newItem(String key, String value) {
        return new HashtableItem(key, value);
    }

    private static Hashtable newSized(int baseSize) {
        Hashtable hashtable = new Hashtable(baseSize);
        hashtable.size = nextPrime(baseSize);
        hashtable.items = new HashtableItem[hashtable.size];
        return hashtable;
    }

    public static Hashtable newTable() {
        return newSized(DEFAULT_BASE_SIZE);
    }

    private static void resize(Hashtable hashtable, int baseSize) {
        Hashtable newHashtable = newSized(baseSize);
        for (int i = 0; i < hashtable.size; i++) {
            HashtableItem item = hashtable.items[i];
            if (item != null && item != HashtableItem.DELETED_ITEM) {
                newHashtable.insert(item.key, item.value);
            }
        }
        hashtable.baseSize = newHashtable.baseSize;
        hashtable.size = newHashtable.size;
        hashtable.count = newHashtable.count;
        hashtable.items = newHashtable.items;
    }

    private static void resizeUp(Hashtable hashtable) {
        int newBaseSize = hashtable.baseSize * 2;
        resize(hashtable, newBaseSize);
    }

    private static void resizeDown(Hashtable hashtable) {
        int newBaseSize = hashtable.baseSize / 2;
        resize(hashtable, newBaseSize);
    }

    @Override
    public void insert(String key, String value) {
        if (count * 100 / size > 70) {
            resizeUp(this);
        }
        HashtableItem item = newItem(key, value);
        int index = getHash(item.key, size, 0);
        HashtableItem curItem = items[index];
        int i = 1;
        while (curItem != null) {
            if (curItem == HashtableItem.DELETED_ITEM) {
                break;
            }
            if (curItem.key.equals(item.key)) {
                items[index] = item;
                return;
            }
            index = getHash(item.key, size, i);
            curItem = items[index];
            i++;
        }
        items[index] = item;
        count++;
    }

    @Override
    public String search(String key) {
        int index = getHash(key, size, 0);
        HashtableItem item = items[index];
        int i = 1;
        while (item != null) {
            if (!Objects.equals(item, HashtableItem.DELETED_ITEM)) {
                if (item.key.equals(key)) {
                    return item.value;
                }
            }

            index = getHash(key, size, i);
            item = items[index];
            i++;
        }
        return null;
    }

    @Override
    public void delete(String key) {
        if (count * 100 / size < 10) {
            resizeDown(this);
        }
        int index = getHash(key, size, 0);
        HashtableItem item = items[index];
        int i = 1;
        while (item != null) {
            if (item.key.equals(key)) {
                items[index] = HashtableItem.DELETED_ITEM;
            }
            index = getHash(key, size, i);
            item = items[index];
            i++;
        }
        count--;
    }

}
