#include <jni.h>
#include "com_atakmap_android_doomtak_plugin_DoomTakTool.h"

JNIEXPORT jstring JNICALL Java_com_atakmap_android_doomtak_plugin_DoomTakTool_myNativeMethod
  (JNIEnv *env, jclass clazz)
{
    return env->NewStringUTF("Hello DoomTAK!");
}
