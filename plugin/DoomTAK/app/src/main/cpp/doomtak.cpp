#include <jni.h>
#include <cstdio>
#include <cstdlib>
#include "com_atakmap_android_doomtak_plugin_DoomTakTool.h"
#include "DOOM.h"

JNIEXPORT jstring JNICALL Java_com_atakmap_android_doomtak_plugin_DoomTakTool_runDoom
        (JNIEnv *env, jclass clazz) {
    // Set the HOME variable to a suitable directory
    const char *homePath = ".";
    setenv("HOME", homePath, 1);
    const char *args[] = {nullptr, "-shdev"};
    doom_init(2, const_cast<char **>(args), 0);
    printf("Doom initialized\n");

    return env->NewStringUTF("Hello DoomTAK!");
}
