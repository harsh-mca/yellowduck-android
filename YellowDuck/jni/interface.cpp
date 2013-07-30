/**
 Interface to communicate with the Chess Engine
**/

#include <jni.h>
#include <unistd.h>

extern "C" int gnucap_main(int argc, const char *argv[]);

//start the engine
extern "C" JNIEXPORT jint JNICALL Java_name_w_yellowduck_activities_experience_ElectricScene_callGnuCap(JNIEnv *pEnv, jobject pObj, jstring fileName) {
    char *szFileName = (char *)pEnv->GetStringUTFChars(fileName, NULL);

    const char *argv[3];
    char param1[10], param2[10];
    strcpy(param1, "");
    strcpy(param2, "-b");
    argv[0]=param1;
    argv[1]=param2;
    argv[2]=szFileName;
    return gnucap_main(3, argv);
}
