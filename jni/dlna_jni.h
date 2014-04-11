#ifndef __DLNA_JNI_H__
#define __DLNA_JNI_H__

#ifdef __c_plus_plus__
extern "C" {
#endif

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <jni.h>
#include <android/log.h>
#include "dlna_api.h"

//#define DEBUG

#define TAG "DLNA_JNI"

#ifdef DEBUG
#define LOGI(args, ...) __android_log_print(ANDROID_LOG_INFO, TAG, args, ##__VA_ARGS__)
#define LOGD(args, ...) __android_log_print(ANDROID_LOG_DEBUG, TAG, args, ##__VA_ARGS__)
#define LOGE(args, ...) __android_log_print(ANDROID_LOG_ERROR, TAG, args, ##__VA_ARGS__)
#else
#define LOGI(args, ...) do{}while(0)
#define LOGD(args, ...) do{}while(0)
#define LOGE(args, ...) do{}while(0)
#endif

// 回调结构
typedef struct {
	char * methodNameInJava;
	char * methodSignature;
	void * callbackMethodInC;
	jmethodID methodInJNI;
} CallbackMethod;

// 回调枚举
enum {
	UPDATE_DEV_LIST = 0x0,
	PLAY,
	PLAY_IMAGE,
	STOP,
	PAUSE,
	SET_SPEED,
	SPEED_UP,
	SPEED_DOWN,
	SET_VOLUME,
	VOLUME_UP,
	VOLUME_DOWN,
	SEARCH_FILE_LIST,
	SEARCH_POSITION,
	GET_POSITION,
	HTTP_CHANGED,
	WAIT_PLAY,
};

// 回调声明
void listUpdateCallback(DevList *pFrist, int count);
int playCallBack(void);
int playImageCallBack(int type, char *path);
int stopCallBack();
int pauseCallBack();
int setSpeedCallBack(int speed);
int speedUpCallBack();
int speedDownCallBack();
int setVolumeCallBack(int volume);
int volumeUpCallBack();
int volumeDownCallBack();
int searchFileListCallBack(pFSNode pFrist, int ResultNum, int TotalNum);
int searchPositionCallBack(int InstanceID, pDlna_PositionInfo preInfo);
int getPositionCallBack(pDlna_PositionInfo preInfo);
int httpStatusChangeCallBack(HTTPINITSTATUS status);
int waitPlayCallBack();

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    setUserName
 * Signature: (Ljava/lang/String;Ljava/lang/String;)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_setUserName(JNIEnv * env, jclass clz, jstring username, jstring udn);

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    initUpnp
 * Signature: ()I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_initUpnp(JNIEnv * env, jclass clz);

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    play
 * Signature: (ILjava/lang/String;)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_play(JNIEnv * env, jclass clz, jint devnum, jstring path);

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    stop
 * Signature: (I)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_stop(JNIEnv * env, jclass clz, jint devnum);

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    pause
 * Signature: (I)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_pause(JNIEnv * env, jclass clz, jint devnum);

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    setSpeed
 * Signature: (II)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_setSpeed(JNIEnv * env, jclass clz, jint devnum, jint speed);

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    speedUp
 * Signature: (I)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_speedUp(JNIEnv * env, jclass clz, jint devnum);

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    speedDown
 * Signature: (I)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_speedDown(JNIEnv * env, jclass clz, jint devnum);

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    setVolume
 * Signature: (II)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_setVolume(JNIEnv * env, jclass clz, jint devnum, jint volume);

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    volumeUp
 * Signature: (I)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_volumeUp(JNIEnv * env, jclass clz, jint devnum);

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    volumeDown
 * Signature: (I)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_volumeDown(JNIEnv * env, jclass clz, jint devnum);

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    playPic
 * Signature: (ILjava/lang/String;)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_playPic(JNIEnv * env, jclass clz, jint devnum, jstring path);

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    search
 * Signature: (ILjava/lang/String;)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_search(JNIEnv * env, jclass clz, jint devnum, jint objID, jstring flag, jstring filter, jint startIndex, jint requestedCount, jstring sortCriteria);

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    getPosition
 * Signature: (II)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_getPosition(JNIEnv * env, jclass clz, jint devnum, jint InstanceID);

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    setShare
 * Signature: (III)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_setShare(JNIEnv * env, jclass clz, jint switch1, jint switch2, jint switch3);

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    reinit
 * Signature: ()I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_reinit(JNIEnv * env, jclass clz);

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    reinitFs
 * Signature: ()I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_reinitFs(JNIEnv * env, jclass clz);

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    getMediaMsg
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;
 */
jobjectArray Java_com_sdmc_dlna_service_NativeAccess_getMediaMsg(JNIEnv * env, jclass clz, jstring filePath, jstring artist, jstring title);




/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    createFileNodS
 * Signature: [BLjava/util/ArrayList;)I;
 */
jint Java_com_sdmc_dlna_service_NativeAccess_createFileNodS(JNIEnv * env, jclass clz, jbyteArray byte_arr, jobject list_string);
#ifdef __c_plus_plus__
}
#endif

#endif
