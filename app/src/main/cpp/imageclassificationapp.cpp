#include <jni.h>
#include <android/log.h>

extern "C"{
JNIEXPORT void JNICALL
Java_com_example_imageclassificationapp_MainActivity_customPrint(JNIEnv *env, jobject thiz) {
    __android_log_print(ANDROID_LOG_INFO, "MyTag", "The value is %s", "hello world");
}
}
