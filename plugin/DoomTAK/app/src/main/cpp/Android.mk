LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS := -O3 -D__GXX_EXPERIMENTAL_CXX0X__
LOCAL_CPPFLAGS := -std=c++11
LOCAL_MODULE := doomtak

# Include DoomTAK C++ code
LOCAL_SRC_FILES := doomtak.cpp

# Include all PureDOOM .c files from the DOOM directory
PUREDOOM_SRC_PATH := $(LOCAL_PATH)/DOOM
LOCAL_SRC_FILES += $(wildcard $(PUREDOOM_SRC_PATH)/*.c)

LOCAL_C_INCLUDES += $(PUREDOOM_SRC_PATH) ${LOCAL_PATH}/../../../build/generated/jni

LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)
