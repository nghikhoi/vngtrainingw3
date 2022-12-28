package vng.training.w3;

import java.util.Objects;

public class HashtableItem {
    public static final HashtableItem DELETED_ITEM = new HashtableItem(null, null);

    public String key, value;

    public HashtableItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashtableItem that = (HashtableItem) o;
        return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

}
