LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS += -lm -llog -L$(SYSROOT)/usr/lib -llog
LOCAL_LDLIBS += -L$(LOCAL_PATH) -liod

LOCAL_MODULE    := sdmc_dlna_jni
LOCAL_SRC_FILES := dlna_jni.c



include $(BUILD_SHARED_LIBRARY)