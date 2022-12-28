package vng.training.w3;

import java.io.File;

public class Main {

    public static native void init();

    static {
        String jniName = "hashtable";
        String path = new File("build\\libs\\$name\\shared\\$name.dll".replace("$name", jniName)).getAbsolutePath();
        System.load(path);
    }

    public static void main(String[] args) {
        init();
        IHashtable hashtable = HashtableJNI.newHashtable();
        IHashtable hashtable2 = Hashtable.newTable();

        test(hashtable);
        test(hashtable2);
    }

    private static void test(IHashtable hashtable) {
        hashtable.insert("key1", "value1");
        hashtable.insert("key2", "value2");
        hashtable.insert("key3", "value3");

        System.out.println("Value: " + hashtable.search("key1"));
        System.out.println("Value: " + hashtable.search("key2"));

        hashtable.delete("key2");

        System.out.println("Value: " + hashtable.search("key2"));
    }

}
