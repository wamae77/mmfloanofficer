#include <jni.h>
#include <cstring>

extern "C" {
JNIEXPORT jstring JNICALL
Java_com_deefrent_rnd_jiboostfieldapp_BaseApp_getBaseURL(JNIEnv *env, jobject thiz) {
    //UAT URL
    return env->NewStringUTF( "https://jiboostapi.wearedeefrent.org/api/");
    //DEV Test URL
    //return env->NewStringUTF("https://test-api.ekenya.co.ke/moneymart-api/api/");
    //PRODUCTION URL
    //return env->NewStringUTF("https://moneymart-api.ekenya.co.ke:17080/moneymart-cbs-api/api/");
}
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_deefrent_rnd_common_utils_Constants_getPINNERURL(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF("jiboost.wearedeefrent.org");
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_deefrent_rnd_common_utils_Constants_getPINNERCERT(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF("sha256/qtKUIytKjQx6WeERsF8ffuTH2+LFxqi8LntoUjfCdME=");
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_deefrent_rnd_common_utils_Constants_getFingerPrintURL(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF("https://fingerprint.moneymart-test.awsekenya.com");
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_gne_pm_PM_powerOn(JNIEnv *env, jclass clazz) {
    // TODO: implement powerOn()
}