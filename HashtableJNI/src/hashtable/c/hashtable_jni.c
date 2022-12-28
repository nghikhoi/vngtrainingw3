#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <stdbool.h>

#include "hashtable_jni.h"
#include "prime.h"

jclass itemClass;
jmethodID itemConstructor;
jfieldID item_key;
jfieldID item_value;

jobject DELETED_ITEM;

jclass tableClass;
jmethodID tableConstructor;
jfieldID table_base_size;
jfieldID table_size;
jfieldID table_count;
jfieldID table_items;

bool isInit = false;

static void init(JNIEnv *env) {
    if (isInit) {
        return;
    }
    jthrowable exc;

    itemClass = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "vng/training/w3/HashtableJNIItem"));
    if ((exc = (*env)->ExceptionOccurred(env))) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        printf("Error: %s", "Cannot find HashtableJNIItem class");
        return;
    }

    itemConstructor = (*env)->GetMethodID(env, itemClass, "<init>", "(Ljava/lang/String;Ljava/lang/String;)V");
    if ((exc = (*env)->ExceptionOccurred(env))) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        printf("Error: %s", "Cannot find HashtableJNIItem constructor");
        return;
    }

    item_key = (*env)->GetFieldID(env, itemClass, "key", "Ljava/lang/String;");
    if ((exc = (*env)->ExceptionOccurred(env))) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        printf("Error: %s", "Cannot find HashtableJNIItem.key");
        return;
    }

    item_value = (*env)->GetFieldID(env, itemClass, "value", "Ljava/lang/String;");
    if ((exc = (*env)->ExceptionOccurred(env))) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        printf("Error: %s", "Cannot find HashtableJNIItem.value");
        return;
    }

    jfieldID deleted_item = (*env)->GetStaticFieldID(env, itemClass, "DELETED_ITEM", "Lvng/training/w3/HashtableJNIItem;");
    if ((exc = (*env)->ExceptionOccurred(env))) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        printf("Error: %s", "Cannot find HashtableJNIItem.DELETED_ITEM");
        return;
    }

    DELETED_ITEM = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, itemClass, deleted_item));
    if ((exc = (*env)->ExceptionOccurred(env))) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        printf("Error: %s", "Cannot find HashtableJNIItem.DELETED_ITEM");
        return;
    }

    tableClass = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "vng/training/w3/HashtableJNI"));
    if ((exc = (*env)->ExceptionOccurred(env))) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        printf("Error: %s", "Cannot find HashtableJNI class");
        return;
    }

    tableConstructor = (*env)->GetMethodID(env, tableClass, "<init>", "(I)V");
    if ((exc = (*env)->ExceptionOccurred(env))) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        printf("Error: %s", "Cannot find HashtableJNI constructor");
        return;
    }

    table_base_size = (*env)->GetFieldID(env, tableClass, "baseSize", "I");
    if ((exc = (*env)->ExceptionOccurred(env))) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        printf("Error: %s", "Cannot find HashtableJNI.baseSize");
        return;
    }

    table_size = (*env)->GetFieldID(env, tableClass, "size", "I");
    if ((exc = (*env)->ExceptionOccurred(env))) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        printf("Error: %s", "Cannot find HashtableJNI.size");
        return;
    }

    table_count = (*env)->GetFieldID(env, tableClass, "count", "I");
    if ((exc = (*env)->ExceptionOccurred(env))) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        printf("Error: %s", "Cannot find HashtableJNI.count");
        return;
    }

    table_items = (*env)->GetFieldID(env, tableClass, "items", "[Lvng/training/w3/HashtableJNIItem;");
    if ((exc = (*env)->ExceptionOccurred(env))) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        printf("Error: %s", "Cannot find HashtableJNI.items");
        return;
    }

    isInit = true;
}

static int htjni_hash(const char *s, const int a, const int m) {
    long hash = 0;
    const int len_s = strlen(s);
    for (int i = 0; i < len_s; i++) {
        hash += (long) pow(a, len_s - (i + 1)) * s[i];
        hash = hash % m;
    }
    return (int) hash;
}

static int htjni_get_hash(const char *s, const int num_buckets, const int attempt) {
    const int hash_a = htjni_hash(s, HTJNI_PRIME_1, num_buckets);
    const int hash_b = htjni_hash(s, HTJNI_PRIME_2, num_buckets);
    return (hash_a + (attempt * (hash_b + 1))) % num_buckets;
}

static htjni_item htjni_new_item(JNIEnv *env, const char *k, const char *v) {
    jstring key = (*env)->NewStringUTF(env, k);
    jstring value = (*env)->NewStringUTF(env, v);
    jobject object = (*env)->NewObject(env, itemClass, itemConstructor, key, value);

    htjni_item result;
    result.raw = object;
    return result;
}

static htjni_hash_table htjni_new_sized(JNIEnv *env, const int base_size) {
    htjni_hash_table ht;
    ht.raw = (*env)->NewObject(env, tableClass, tableConstructor, base_size);

    (*env)->SetIntField(env, ht.raw, table_base_size, base_size);
    (*env)->SetIntField(env, ht.raw, table_size, next_prime(base_size));
    (*env)->SetIntField(env, ht.raw, table_count, 0);

    int size = (*env)->GetIntField(env, ht.raw, table_size);
    (*env)->SetObjectField(env, ht.raw, table_items, (*env)->NewObjectArray(env, size, itemClass, NULL));

    return ht;
}

htjni_hash_table htjni_new(JNIEnv *env) {
    return htjni_new_sized(env, HTJNI_INITIAL_BASE_SIZE);
}

static void htjni_resize(JNIEnv *env, htjni_hash_table ht, const int base_size) {
    if (base_size < HTJNI_INITIAL_BASE_SIZE) {
        return;
    }
    htjni_hash_table new_ht = htjni_new_sized(env, base_size);

    int size = (*env)->GetIntField(env, ht.raw, table_size);
    jobjectArray items = (*env)->GetObjectField(env, ht.raw, table_items);
    for (int i = 0; i < size; i++) {
        htjni_item item;
        jobject object = (*env)->GetObjectArrayElement(env, items, i);
        item.raw = object;

        if (item.raw != NULL && !(*env)->IsSameObject(env, item.raw, DELETED_ITEM)) {
            jobject keyObj = (*env)->GetObjectField(env, item.raw, item_key);
            jobject valueObj = (*env)->GetObjectField(env, item.raw, item_value);
            const char* key = keyObj == NULL ? "" : (*env)->GetStringUTFChars(env, keyObj, NULL);
            const char* value = valueObj == NULL ? "" : (*env)->GetStringUTFChars(env, valueObj, NULL);
            htjni_insert(env, new_ht, key, value);
        }
    }

    (*env)->SetIntField(env, ht.raw, table_base_size, (*env)->GetIntField(env, new_ht.raw, table_base_size));
    (*env)->SetIntField(env, ht.raw, table_size, (*env)->GetIntField(env, new_ht.raw, table_size));
    (*env)->SetIntField(env, ht.raw, table_count, (*env)->GetIntField(env, new_ht.raw, table_count));
    (*env)->SetObjectField(env, ht.raw, table_items, (*env)->GetObjectField(env, new_ht.raw, table_items));
}

static void htjni_resize_up(JNIEnv *env, htjni_hash_table ht) {
    int base_size = (*env)->GetIntField(env, ht.raw, table_base_size);
    const int new_size = base_size * 2;
    htjni_resize(env, ht, new_size);
}

static void htjni_resize_down(JNIEnv *env, htjni_hash_table ht) {
    int base_size = (*env)->GetIntField(env, ht.raw, table_base_size);
    const int new_size = base_size / 2;
    htjni_resize(env, ht, new_size);
}

void htjni_insert(JNIEnv *env, htjni_hash_table ht, const char *key, const char *value) {
    int size = (*env)->GetIntField(env, ht.raw, table_size);
    int count = (*env)->GetIntField(env, ht.raw, table_count);
    const int load = count * 100 / size;
    if (load > 70) {
        htjni_resize_up(env, ht);
    }
    htjni_item item = htjni_new_item(env, key, value);

    int index;
    {
        size = (*env)->GetIntField(env, ht.raw, table_size);
        jobject keyObj = (*env)->GetObjectField(env, item.raw, item_key);
        char *key = keyObj == NULL ? "" : (*env)->GetStringUTFChars(env, keyObj, NULL);
        index = htjni_get_hash(key, size, 0);
    }

    jobjectArray items = (*env)->GetObjectField(env, ht.raw, table_items);

    htjni_item cur_item;
    {
        jobject object = (*env)->GetObjectArrayElement(env, items, index);
        cur_item.raw = object;
    }

    int i = 1;
    while (cur_item.raw != NULL) {
        if (!(*env)->IsSameObject(env, cur_item.raw, DELETED_ITEM)) {
            jobject curKeyObj = (*env)->GetObjectField(env, cur_item.raw, item_key);
            char *cur_key = curKeyObj == NULL ? "" : (*env)->GetStringUTFChars(env, curKeyObj, NULL);
            if (strcmp(cur_key, key) == 0) {
                (*env)->SetObjectArrayElement(env, items, index, item.raw);
                return;
            }
        }
        int size = (*env)->GetIntField(env, ht.raw, table_size);
        jobject keyObj = (*env)->GetObjectField(env, cur_item.raw, item_key);
        char *key = keyObj == NULL ? "" : (*env)->GetStringUTFChars(env, keyObj, NULL);
        index = htjni_get_hash(key, size, i);
        cur_item.raw = (*env)->GetObjectArrayElement(env, items, index);
        i++;
    }

    (*env)->SetObjectArrayElement(env, items, index, item.raw);
    (*env)->SetIntField(env, ht.raw, table_count, count + 1);
}

char *htjni_search(JNIEnv *env, htjni_hash_table ht, const char *key) {
    int size = (*env)->GetIntField(env, ht.raw, table_size);
    int index = htjni_get_hash(key, size, 0);

    jobjectArray items = (*env)->GetObjectField(env, ht.raw, table_items);
    htjni_item item;
    {
        jobject object = (*env)->GetObjectArrayElement(env, items, index);
        item.raw = object;
    }
    int i = 1;
    while (item.raw != NULL) {
        jobject keyObj = (*env)->GetObjectField(env, item.raw, item_key);
        jobject valueObj = (*env)->GetObjectField(env, item.raw, item_value);
        char* key = keyObj == NULL ? "" : (*env)->GetStringUTFChars(env, keyObj, NULL);
        char* value = valueObj == NULL ? "" : (*env)->GetStringUTFChars(env, valueObj, NULL);

        if (!(*env)->IsSameObject(env, item.raw, DELETED_ITEM)) {
            if (strcmp(key, key) == 0) {
                return value;
            }
        }
        if (strcmp(key, key) == 0) {
            return value;
        }
        index = htjni_get_hash(key, size, i);
        item.raw = (*env)->GetObjectArrayElement(env, items, index);
        i++;
    }
    return NULL;
}

void htjni_delete(JNIEnv *env, htjni_hash_table ht, const char *key) {
    int count = (*env)->GetIntField(env, ht.raw, table_count);
    int size = (*env)->GetIntField(env, ht.raw, table_size);
    const int load = count * 100 / size;
    if (load < 10) {
        htjni_resize_down(env, ht);
    }

    size = (*env)->GetIntField(env, ht.raw, table_size);
    int index = htjni_get_hash(key, size, 0);

    jobjectArray items = (*env)->GetObjectField(env, ht.raw, table_items);
    htjni_item item;
    {
        jobject object = (*env)->GetObjectArrayElement(env, items, index);
        item.raw = object;
    }

    int i = 1;
    while (item.raw != NULL) {
        if (!(*env)->IsSameObject(env, item.raw, DELETED_ITEM)) {
            jobject curKeyObj = (*env)->GetObjectField(env, item.raw, item_key);
            char *cur_key = curKeyObj == NULL ? "" : (*env)->GetStringUTFChars(env, curKeyObj, NULL);
            if (strcmp(cur_key, key) == 0) {
                (*env)->SetObjectArrayElement(env, items, index, DELETED_ITEM);
            }
        }
        size = (*env)->GetIntField(env, ht.raw, table_size);
        index = htjni_get_hash(key, size, i);
        item.raw = (*env)->GetObjectArrayElement(env, items, index);
        i++;
    }
    (*env)->SetIntField(env, ht.raw, table_count, count - 1);
}

JNIEXPORT void JNICALL Java_vng_training_w3_Main_init
        (JNIEnv *env, jclass clazz) {
    printf("Initializing hash table\n");
    init(env);
    printf("Initialized hash table\n");
}

JNIEXPORT jobject JNICALL Java_vng_training_w3_HashtableJNI_newHashtable
        (JNIEnv *env, jclass clazz) {
    init(env);
    htjni_hash_table ht = htjni_new(env);
    return ht.raw;
}

JNIEXPORT void JNICALL Java_vng_training_w3_HashtableJNI_insert
        (JNIEnv *env, jobject ht, jstring key, jstring value) {
    init(env);
    htjni_hash_table table;
    table.raw = ht;
    const char *key_str = (*env)->GetStringUTFChars(env, key, NULL);
    const char *value_str = (*env)->GetStringUTFChars(env, value, NULL);
    htjni_insert(env, table, key_str, value_str);
}

JNIEXPORT jstring JNICALL Java_vng_training_w3_HashtableJNI_search
        (JNIEnv *env, jobject ht, jstring key) {
    init(env);
    htjni_hash_table table;
    table.raw = ht;
    const char *key_str = (*env)->GetStringUTFChars(env, key, NULL);
    char *value = htjni_search(env, table, key_str);
    if (value == NULL) {
        return NULL;
    }
    return (*env)->NewStringUTF(env, value);
}

JNIEXPORT void JNICALL Java_vng_training_w3_HashtableJNI_delete
        (JNIEnv *env, jobject ht, jstring key) {
    init(env);
    htjni_hash_table table;
    table.raw = ht;
    const char *key_str = (*env)->GetStringUTFChars(env, key, NULL);
    htjni_delete(env, table, key_str);
}
