#include "doomtak.h"
#include <cstdio>
#include <cstdlib>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include "DOOM.h"
#include "doomdef.h"
#include "logger.h"

AAssetManager *gAssetManager = nullptr;

extern "C" {

JNIEXPORT void JNICALL
Java_com_atakmap_android_doomtak_DoomTakDropDownReceiver_initNativeLayer(
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
    if (result == nullptr) {
        return nullptr;
    }
    env->SetByteArrayRegion(result, 0, SCREENWIDTH * SCREENHEIGHT * channels,
                            (const jbyte *) framebuffer);
    return result;
}

JNIEXPORT void JNICALL
Java_com_atakmap_android_doomtak_DoomTakDropDownReceiver_mouseMove(
        JNIEnv *env, jobject obj, jint deltaX, jint deltaY) {
    doom_mouse_move(deltaX, deltaY);
}

JNIEXPORT void JNICALL
Java_com_atakmap_android_doomtak_DoomTakDropDownReceiver_keyDown(
        JNIEnv *env, jobject obj, jint key) {
    doom_key_down(static_cast<doom_key_t>(key));
}

JNIEXPORT void JNICALL
Java_com_atakmap_android_doomtak_DoomTakDropDownReceiver_keyUp(
        JNIEnv *env, jobject obj, jint key) {
    doom_key_up(static_cast<doom_key_t>(key));
}

JNIEXPORT void JNICALL
Java_com_atakmap_android_doomtak_DoomTakDropDownReceiver_joyButtonDown(
        JNIEnv *env, jobject obj, jint button) {
    doom_joy_button_down(static_cast<doom_joy_button_t >(button));
}

JNIEXPORT void JNICALL
Java_com_atakmap_android_doomtak_DoomTakDropDownReceiver_joyButtonUp(
        JNIEnv *env, jobject obj, jint button) {
    doom_joy_button_up(static_cast<doom_joy_button_t >(button));
}

JNIEXPORT void JNICALL
Java_com_atakmap_android_doomtak_DoomTakDropDownReceiver_joystick(
        JNIEnv *env, jobject obj, jint x, jint y) {
    doom_joystick(x, y);
}

JNIEXPORT void JNICALL
Java_com_atakmap_android_doomtak_DoomTakDropDownReceiver_pauseGame(
        JNIEnv *env, jobject obj, jboolean paused) {
    doom_pause_game(paused);
}

JNIEXPORT void JNICALL
Java_com_atakmap_android_doomtak_DoomTakDropDownReceiver_quitGame(
        JNIEnv *env, jobject obj) {
    doom_quit_game();
}

JNIEXPORT jshortArray JNICALL
Java_com_atakmap_android_doomtak_audio_DoomTakSoundPlayer_getSoundBuffer(
        JNIEnv *env, jobject obj, jint length) {
    int16_t *soundBuffer = doom_get_sound_buffer();
    jshortArray result = env->NewShortArray(length / sizeof(int16_t));
    if (result == nullptr) {
        return nullptr;
    }
    env->SetShortArrayRegion(result, 0, length / sizeof(int16_t), soundBuffer);
    return result;
}

JNIEXPORT int JNICALL
Java_com_atakmap_android_doomtak_audio_DoomTakMusicPlayer_tickMidi(
        JNIEnv *env, jobject obj) {
    return (jint) doom_tick_midi();
}
}
