package vng.training.w3;

import java.util.Objects;

public class HashtableJNIItem {

    public static final HashtableJNIItem DELETED_ITEM = new HashtableJNIItem(null, null);

    public String key, value;

    public HashtableJNIItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashtableJNIItem that = (HashtableJNIItem) o;
        return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

}
