#include "doomtak.h"
#include <cstdio>
#include <cstdlib>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include "com_atakmap_android_doomtak_DoomTakGLRenderer.h"
#include "DOOM.h"
#include "doomdef.h"
#include "logger.h"

AAssetManager *gAssetManager = nullptr;

extern "C" {

JNIEXPORT void JNICALL
Java_com_atakmap_android_doomtak_DoomTakGLRenderer_initNativeLayer(
        JNIEnv *env, jobject obj, jobject assetManager) {
    gAssetManager = AAssetManager_fromJava(env, assetManager);
    // Set the HOME variable to a suitable directory
    const char *homePath = ".";
    setenv("HOME", homePath, 1);
    // Initialize the DOOM engine
    int argc = 1;
    char *argv[] = {"doom"};
    doom_init(argc, argv, 0);  // Initialize with no special flags
}

// This method will render the OpenGL content
JNIEXPORT void JNICALL
Java_com_atakmap_android_doomtak_DoomTakGLRenderer_doomUpdate(
        JNIEnv *env, jobject obj) {
    // Call the DOOM engine to update the game state
    doom_update();
}

JNIEXPORT jbyteArray JNICALL
Java_com_atakmap_android_doomtak_DoomTakGLRenderer_getFramebuffer(
        JNIEnv *env, jobject obj, jint channels) {
    const unsigned char *framebuffer = doom_get_framebuffer(channels);
    jbyteArray result = env->NewByteArray(SCREENWIDTH * SCREENHEIGHT * channels);
    env->SetByteArrayRegion(result, 0, SCREENWIDTH * SCREENHEIGHT * channels,
                            (const jbyte *) framebuffer);
    return result;
}
}
