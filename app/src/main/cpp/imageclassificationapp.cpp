#include <jni.h>
#include <android/log.h>

extern "C"{

JNIEXPORT void JNICALL
Java_com_example_imageclassificationapp_MainActivity_customPrint(JNIEnv *env, jobject thiz) {
    __android_log_print(ANDROID_LOG_INFO, "MyTag", "The value is %s", "hello world");
}

JNIEXPORT void JNICALL
Java_com_example_imageclassificationapp_PhotoCaputuringComponentKt_generateByteBuffer(JNIEnv *env,jclass clazz,
                                                                                      jobject byte_buffer,
                                                                                      jintArray rgb_values_array,
                                                                                      jint image_size) {

    jint* rgbArray = (env)->GetIntArrayElements(rgb_values_array,
                                                nullptr);


    float* bufferData = (float*) (env)->GetDirectBufferAddress(byte_buffer);


    int pixel = 0;

    int i = 0;
    while(i < image_size) {

        int j = 0;
        while (j < image_size) {
            int rgb = rgbArray[pixel];
            pixel++;


            float red = ((rgb >> 16) & 0xFF) * (1.0f / 1);
            float green = ((rgb >> 8) & 0xFF) * (1.0f / 1);
            float blue = (rgb & 0xFF) * (1.0f / 1);

            *bufferData++ = red;
            *bufferData++ = green;
            *bufferData++ = blue;
            j++;
        }
        i++;

    }

    (env)->ReleaseIntArrayElements(rgb_values_array, rgbArray, JNI_ABORT);
}

}
