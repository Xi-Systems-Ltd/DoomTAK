LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)


LOCAL_CFLAGS=-O3 -std=c++11 -D__GXX_EXPERIMENTAL_CXX0X__
LOCAL_CPPFLAGS := -std=c++11
LOCAL_MODULE := doomtak
LOCAL_SRC_FILES := doomtak.cpp

LOCAL_C_INCLUDES += ${LOCAL_PATH}/../../../build/generated/jni
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)


