//
// Created by pete.herniman on 04/10/2024.
//

#ifndef DOOMTAK_LOGGER_H
#define DOOMTAK_LOGGER_H

#include <android/log.h>

#define LOG_TAG "DoomTAKNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)

#endif //DOOMTAK_LOGGER_H
