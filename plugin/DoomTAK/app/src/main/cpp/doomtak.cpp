#include <jni.h>
#include "com_atakmap_android_doomtak_plugin_DoomTakTool.h"
#include "PureDOOM.h"

JNIEXPORT jstring JNICALL Java_com_atakmap_android_doomtak_plugin_DoomTakTool_myNativeMethod
  (JNIEnv *env, jclass clazz)
{
    doom_init(0, nullptr, 0);

    return env->NewStringUTF("Hello DoomTAK!");
}
