//
// Created by nghik on 12/28/2022.
//

#ifndef HASHTABLE_HASHTABLE_H
#define HASHTABLE_HASHTABLE_H

#include <jni.h>

typedef struct {
    jobject raw;
} htjni_item;

typedef struct {
    jobject raw;
} htjni_hash_table;

static int HTJNI_INITIAL_BASE_SIZE = 53;

static int HTJNI_PRIME_1 = 151;
static int HTJNI_PRIME_2 = 163;

static void init(JNIEnv* env);

static htjni_item htjni_new_item(JNIEnv* env, const char* k, const char* v);

htjni_hash_table htjni_new(JNIEnv* env);

void htjni_insert(JNIEnv* env, htjni_hash_table ht, const char* key, const char* value);
char* htjni_search(JNIEnv* env, htjni_hash_table ht, const char* key);
void htjni_delete(JNIEnv* env, htjni_hash_table h, const char* key);

#endif //HASHTABLE_HASHTABLE_H
