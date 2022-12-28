package vng.training.w3;

import java.io.File;

public class HashtableJNI implements IHashtable {

    public int baseSize, size, count;
    public HashtableJNIItem[] items;

    static {
        String jniName = "hashtable";
        String path = new File("build\\libs\\$name\\shared\\$name.dll".replace("$name", jniName)).getAbsolutePath();
        System.load(path);
    }

    public HashtableJNI(int baseSize) {
        this.baseSize = baseSize;
    }

    public static native HashtableJNI newHashtable();

    public native void insert(String key, String value);

    public native String search(String key);

    public native void delete(String key);

}
