#include "dlna_jni.h"

#define CLASSPATHNAME "com/sdmc/dlna/service/Callbacks"

JavaVM *g_jvm;
jclass Callbacks;

static CallbackMethod methods[] = {
		{"updateDevList", "([Lcom/sdmc/dlna/service/DevInfo;)V", listUpdateCallback, NULL},
		{"play", "()I", playCallBack, NULL},
		{"playImage", "(ILjava/lang/String;)I", playImageCallBack, NULL},
		{"stop", "()I", stopCallBack, NULL},
		{"pause", "()I", pauseCallBack, NULL},
		{"setSpeed", "(I)I", setSpeedCallBack, NULL},
		{"speedUp", "()I", speedUpCallBack, NULL},
		{"speedDown", "()I", speedDownCallBack, NULL},
		{"setVolume", "(I)I", setVolumeCallBack, NULL},
		{"volumeUp", "()I", volumeUpCallBack, NULL},
		{"volumeDown", "()I", volumeDownCallBack, NULL},
		{"searchFileList", "([Lcom/sdmc/dlna/service/FileNode;II)I", searchFileListCallBack, NULL},
		{"searchPosition", "(I)Lcom/sdmc/dlna/service/PositionInfo;", searchPositionCallBack, NULL},
		{"getPosition", "(Lcom/sdmc/dlna/service/PositionInfo;)I", getPositionCallBack, NULL},
		{"httpStatusChange", "(I)I", httpStatusChangeCallBack, NULL},
		{"waitPlay", "()I", waitPlayCallBack, NULL},
};

/* 初始化回调 */
void initCallback(JNIEnv * env) {
	int len = sizeof(methods) / sizeof(CallbackMethod);
	LOGI("Size = %d\n", len);
	int i = 0;
	jclass cls = (*env)->FindClass(env, CLASSPATHNAME);
	if (NULL == cls) {
		LOGE("Class Pointer is NULL");
		return;
	}
    Callbacks = (*env)->NewGlobalRef(env, cls);
	for (i = 0; i < len; i++) {
		methods[i].methodInJNI = (*env)->GetStaticMethodID(env, cls, methods[i].methodNameInJava, methods[i].methodSignature);
		if (NULL == methods[i].methodInJNI) {
			LOGE("MethodID Pointer is NULL");
			return;
		}
	}
}

void regCallback() {
	DLNA_Need_CallBack* cb = DLNA_GetCallbackHandle();
	cb->_DevListUpdateCallback = methods[UPDATE_DEV_LIST].callbackMethodInC;
	cb->_DevPlayCallBack = methods[PLAY].callbackMethodInC;
	cb->_DevStopCallBack = methods[STOP].callbackMethodInC;
	cb->_DevPauseCallBack = methods[PAUSE].callbackMethodInC;
	cb->_DevSetSpeedCallBack = methods[SET_SPEED].callbackMethodInC;
	cb->_DevIncreaseSpeedCallBack = methods[SPEED_UP].callbackMethodInC;
	cb->_DevDecreaseSpeedCallBack = methods[SPEED_DOWN].callbackMethodInC;
	cb->_DevSetVolumeCallBack = methods[SET_VOLUME].callbackMethodInC;
	cb->_DevIncreaseVolumeCallBack = methods[VOLUME_UP].callbackMethodInC;
	cb->_DevDecreaseVolumeCallBack = methods[VOLUME_DOWN].callbackMethodInC;
	cb->_DevPicPlayCallBack = methods[PLAY_IMAGE].callbackMethodInC;
	cb->_Ctr_SearchFileListCallBack = methods[SEARCH_FILE_LIST].callbackMethodInC;
	cb->_Ctr_SearchPositionInfoCallBack = methods[SEARCH_POSITION].callbackMethodInC;
	cb->_Ctr_GetPositionInfoCallBack = methods[GET_POSITION].callbackMethodInC;
	cb->_Net_HttpStatusChange = methods[HTTP_CHANGED].callbackMethodInC;
	cb->_DevWaitPlay = methods[WAIT_PLAY].callbackMethodInC;
}

static JNINativeMethod nativeMethods[] = {
		{"setUserName", "(Ljava/lang/String;Ljava/lang/String;)I", (void *)Java_com_sdmc_dlna_service_NativeAccess_setUserName},
		{"initUpnp", "()I", (void *)Java_com_sdmc_dlna_service_NativeAccess_initUpnp},
		{"play", "(ILjava/lang/String;)I", (void *)Java_com_sdmc_dlna_service_NativeAccess_play},
		{"stop", "(I)I", (void *)Java_com_sdmc_dlna_service_NativeAccess_stop},
		{"pause", "(I)I", (void *)Java_com_sdmc_dlna_service_NativeAccess_pause},
		{"setSpeed", "(II)I", (void *)Java_com_sdmc_dlna_service_NativeAccess_setSpeed},
		{"speedUp", "(I)I", (void *)Java_com_sdmc_dlna_service_NativeAccess_speedUp},
		{"speedDown", "(I)I", (void *)Java_com_sdmc_dlna_service_NativeAccess_speedDown},
		{"setVolume", "(II)I", (void *)Java_com_sdmc_dlna_service_NativeAccess_setVolume},
		{"volumeUp", "(I)I", (void *)Java_com_sdmc_dlna_service_NativeAccess_volumeUp},
		{"volumeDown", "(I)I", (void *)Java_com_sdmc_dlna_service_NativeAccess_volumeDown},
		{"playPic", "(ILjava/lang/String;)I", (void *)Java_com_sdmc_dlna_service_NativeAccess_playPic},
		{"search", "(IILjava/lang/String;Ljava/lang/String;IILjava/lang/String;)I", (void *)Java_com_sdmc_dlna_service_NativeAccess_search},
		{"getPosition", "(II)I", (void *)Java_com_sdmc_dlna_service_NativeAccess_getPosition},
		{"setShare", "(III)I", (void *)Java_com_sdmc_dlna_service_NativeAccess_setShare},
		{"reinit", "()I", (void *)Java_com_sdmc_dlna_service_NativeAccess_reinit},
		{"reinitFs", "()I", (void *)Java_com_sdmc_dlna_service_NativeAccess_reinitFs},
		{"getMediaMsg", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;", (void *)Java_com_sdmc_dlna_service_NativeAccess_getMediaMsg},
		{"createFileNodS","([BLjava/util/ArrayList;)I",(void *)Java_com_sdmc_dlna_service_NativeAccess_createFileNodS},
};


jclass DevInfo_clazz;
jclass FileNode_clazz;
jclass PositionInfo_clazz;

int jniRegisterNativeMethods(JNIEnv* env, const char* className, const JNINativeMethod* nativeMethods, int numMethods) {
    jclass clazz;
	 
	clazz = (*env)->FindClass(env, "com/sdmc/dlna/service/FileNode");
	if (NULL == clazz) {
		LOGE("Class Pointer is NULL");
		return -1;
	}
    	FileNode_clazz = (*env)->NewGlobalRef(env, clazz);
	LOGE("listUpdateCallback Now!FileNode __%d__ env is %p,FileNode_clazz is %p",__LINE__,env,FileNode_clazz);
	
	clazz = (*env)->FindClass(env, "com/sdmc/dlna/service/DevInfo");
	if (NULL == clazz) {
		LOGE("Class Pointer is NULL");
		return -1;
	}
    	DevInfo_clazz = (*env)->NewGlobalRef(env, clazz);
	LOGE("listUpdateCallback Now!DevInfo __%d__ env is %p,DevInfo_clazz is %p",__LINE__,env,DevInfo_clazz);

	clazz = (*env)->FindClass(env, "com/sdmc/dlna/service/PositionInfo");
		if (NULL == clazz) {
			LOGE("Class Pointer is NULL");
			return -1;
		}
		PositionInfo_clazz = (*env)->NewGlobalRef(env, clazz);
		LOGE("listUpdateCallback Now!PositionInfo_clazz __%d__ env is %p,PositionInfo_clazz is %p",__LINE__,env,PositionInfo_clazz);
	 
	  LOGI("__1__Registering %s natives\n", className);
    clazz = (*env)->FindClass(env, className);
	 LOGI("__2__Registering %s clazz is %p\n", className,clazz);
	 
	 
	 
    if (clazz == NULL) {
        LOGE("__2__Native registration unable to find class '%s'\n", className);
        return -1;
    }
    if ((*env)->RegisterNatives(env, clazz, nativeMethods, numMethods) < 0) {
        LOGE("__3__RegisterNatives failed for '%s'\n", className);
        return -1;
    }
    return 0;
}

int registerNativeMethod(JNIEnv *env) {
    const char* const thePathOfJavaClass = "com/sdmc/dlna/service/NativeAccess";
    return jniRegisterNativeMethods(env, thePathOfJavaClass , nativeMethods, sizeof(nativeMethods) / sizeof(nativeMethods[0]));
}

char * getCStrByJStr(JNIEnv * env, jstring jstr) {
	return (char*) (*env)->GetStringUTFChars(env, jstr, NULL);
}

/* This function will be call when the library first be loaded */
jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    LOGI("JNI_OnLoad Start!");
    JNIEnv* env = NULL;
    g_jvm = vm;
    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		LOGE("GetEnv failed!");
		return -1;
	}
    registerNativeMethod(env);
    initCallback(env);
	
    return JNI_VERSION_1_4;
}

void JNI_OnUnload(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;
    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGE("GetEnv failed!");
        return;
    }
    if(Callbacks != NULL) {
    	(*env)->DeleteGlobalRef(env, Callbacks);
    }
    return;
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    setUserName
 * Signature: (Ljava/lang/String;Ljava/lang/String;)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_setUserName(JNIEnv * env, jclass clz, jstring username, jstring udn) {
	LOGE("Java_com_sdmc_dlna_service_NativeAccess_setUserName Now!!");
	return Dlna_SetUserName(getCStrByJStr(env, username), getCStrByJStr(env, udn));
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    initUpnp
 * Signature: ()I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_initUpnp(JNIEnv * env, jclass clz) {
	(*env)->GetJavaVM(env, &g_jvm);
	//registerNativeMethod(env);
	regCallback();
	int ret = DLNA_Init();
	if(ret != 0) {
		return ret;
	} else {
		return 0;
	}
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    play
 * Signature: (ILjava/lang/String;)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_play(JNIEnv * env, jclass clz, jint devnum, jstring path) {
	return DLNA_Ctr_SendPlay(devnum, getCStrByJStr(env, path));
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    stop
 * Signature: (I)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_stop(JNIEnv * env, jclass clz, jint devnum) {
	return DLNA_Ctr_SendStop(devnum);
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    pause
 * Signature: (I)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_pause(JNIEnv * env, jclass clz, jint devnum) {
	return DLNA_Ctr_SendPause(devnum);
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    setSpeed
 * Signature: (II)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_setSpeed(JNIEnv * env, jclass clz, jint devnum, jint speed) {
	return DLNA_Ctr_SendSetSpeed(devnum, speed);
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    speedUp
 * Signature: (I)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_speedUp(JNIEnv * env, jclass clz, jint devnum) {
	return DLNA_Ctr_SendIncreaseSpeed(devnum);
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    speedDown
 * Signature: (I)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_speedDown(JNIEnv * env, jclass clz, jint devnum) {
	return DLNA_Ctr_SendDecreaseSpeed(devnum);
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    setVolume
 * Signature: (II)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_setVolume(JNIEnv * env, jclass clz, jint devnum, jint volume) {
	return DLNA_Ctr_SendSetVolume(devnum, volume);
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    volumeUp
 * Signature: (I)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_volumeUp(JNIEnv * env, jclass clz, jint devnum) {
	return DLNA_Ctr_SendIncreaseVolume(devnum);
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    volumeDown
 * Signature: (I)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_volumeDown(JNIEnv * env, jclass clz, jint devnum) {
	return DLNA_Ctr_SendDecreaseVolume(devnum);
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    playPic
 * Signature: (ILjava/lang/String;)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_playPic(JNIEnv * env, jclass clz, jint devnum, jstring path) {
	return DLNA_Ctr_SendPicPlay(devnum, getCStrByJStr(env, path));
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    search
 * Signature: (ILjava/lang/String;)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_search(JNIEnv * env, jclass clz, jint devnum, jint objID, jstring flag, jstring filter, jint startIndex, jint requestedCount, jstring sortCriteria) {
	LOGE("Java_com_sdmc_dlna_service_NativeAccess_search come in!");
	return DLNA_Ctr_SendBrowse(devnum, objID, getCStrByJStr(env, flag), getCStrByJStr(env, filter), startIndex, requestedCount, getCStrByJStr(env, sortCriteria));
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    getPosition
 * Signature: (II)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_getPosition(JNIEnv * env, jclass clz, jint devnum, jint InstanceID) {
	return DLNA_Ctr_SendGetPositionInfo(devnum, InstanceID);
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    setShare
 * Signature: (III)I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_setShare(JNIEnv * env, jclass clz, jint switch1, jint switch2, jint switch3) {
	return DLNA_SetSearchStatus(switch1, switch2, switch3);
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    reinit
 * Signature: ()I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_reinit(JNIEnv * env, jclass clz) {
	return DLNA_KillAll();
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    reinitFs
 * Signature: ()I
 */
jint Java_com_sdmc_dlna_service_NativeAccess_reinitFs(JNIEnv * env, jclass clz) {
	return DLNA_FS_ReInit();
}

/*
 * Class:     com_sdmc_dlna_service_NativeAccess
 * Method:    getMediaMsg
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;
 */
jobjectArray Java_com_sdmc_dlna_service_NativeAccess_getMediaMsg(JNIEnv * env, jclass clz, jstring filePath, jstring artist, jstring title) {
	char art[255];
	char ttl[255];
	int ret = DLNA_FS_GetMediaMsg(getCStrByJStr(env, filePath), art, ttl);
	if (ret == 0) {
		jobjectArray objArray = (*env)->NewObjectArray(env, 2, (*env)->FindClass(env, "java/lang/String"), 0);

		jstring tmpStr = (*env)->NewStringUTF(env, art);
		(*env)->SetObjectArrayElement(env, objArray, 0, tmpStr);
		(*env)->DeleteLocalRef(env, tmpStr);

		tmpStr = (*env)->NewStringUTF(env, ttl);
		(*env)->SetObjectArrayElement(env, objArray, 1, tmpStr);
		(*env)->DeleteLocalRef(env, tmpStr);
		return objArray;
	} else {
		return NULL;
	}
}

jint Java_com_sdmc_dlna_service_NativeAccess_createFileNodS(JNIEnv * env, jclass clz, jbyteArray byte_arr, jobject list_string){
	LOGE("Java_com_sdmc_dlna_service_NativeAccess_createFileNodS Now!!");
	
	jbyte   *arr = (*env)->GetByteArrayElements(env, byte_arr, NULL);
	jsize   size = (*env)->GetArrayLength(env , byte_arr);
	
	jclass cls_arraylist_string = (*env)->GetObjectClass(env, list_string);
	jmethodID arraylist_get_string = (*env)->GetMethodID(env, cls_arraylist_string,"get","(I)Ljava/lang/Object;");
	
	
	LOGE("arrylist size %d\n", size);
	int i;
	char *ptype = arr;	
	char* pNodeFirst = (char *)malloc(sizeof(FS_FileNode)*size);
	
	if(!pNodeFirst){
		LOGE("malloc error");
	}
	
	memset(pNodeFirst, 0, sizeof(FS_FileNode)*size);	
	LPFS_FileNode pNode = (LPFS_FileNode)pNodeFirst;
	
	for(i = 0; i<size; i++){	
		jstring name = (jstring)(*env)->CallObjectMethod(env, list_string, arraylist_get_string, i);
		char *p = (*env)->GetStringUTFChars(env, name, NULL);
		
		pNode->type = *ptype;
		memcpy(pNode->filepath, p, strlen(p));
		
		LOGE("type = %d, name = %s", *ptype, p);
		ptype++;
		pNode++;
		
		(*env)->ReleaseStringUTFChars(env, name, p);
		(*env)->DeleteLocalRef(env, name);
	}
	
	DLNA_ReCreate_FileNode(size, (LPFS_FileNode)pNodeFirst);
	free(pNodeFirst);
	(*env)->ReleaseByteArrayElements(env, byte_arr, arr, 0);
	
	LOGE("Java_com_sdmc_dlna_service_NativeAccess_createFileNodS Leave!!");
	
}

// Callback Methods
/* 通用无参数的回调 */
int commonCallback0(int index) {
	JNIEnv *env = NULL;
	(*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL);
	if (env == NULL) {
		LOGE("Get ENV FAILED!");
		return -1;
	}
	int ret = (*env)->CallStaticIntMethod(env, Callbacks, methods[index].methodInJNI);

	(*g_jvm)->DetachCurrentThread(g_jvm);
	return ret;
}

/* 通用带一个字符串参数的回调 */
int commonCallback1(int index, char * path) {
	JNIEnv *env = NULL;
	(*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL);
	if (env == NULL) {
		LOGE("Get ENV FAILED!");
		return -1;
	}
	jstring jstrPath = (*env)->NewStringUTF(env, path);
	int ret = (*env)->CallStaticIntMethod(env, Callbacks, methods[index].methodInJNI, jstrPath);
	(*env)->DeleteLocalRef(env, jstrPath);

	(*g_jvm)->DetachCurrentThread(g_jvm);
	return ret;
}

/* 通用带一个整形参数的回调 */
int commonCallback2(int index, int value) {
	JNIEnv *env = NULL;
	(*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL);
	if (env == NULL) {
		LOGE("Get ENV FAILED!");
		return -1;
	}
	int ret = (*env)->CallStaticIntMethod(env, Callbacks, methods[index].methodInJNI, value);

	(*g_jvm)->DetachCurrentThread(g_jvm);
	return ret;
}

/* 通用带一个整形和一个字符串参数的回调 */
int commonCallback3(int index, int value, char * path) {
	JNIEnv *env = NULL;
	(*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL);
	if (env == NULL) {
		LOGE("Get ENV FAILED!");
		return -1;
	}
	jstring jstrPath =(*env)->NewStringUTF(env, path);
	int ret = (*env)->CallStaticIntMethod(env, Callbacks, methods[index].methodInJNI, value, jstrPath);
	(*env)->DeleteLocalRef(env, jstrPath);

	(*g_jvm)->DetachCurrentThread(g_jvm);
	return ret;
}
/*通知发现设备回调接口，传入设备信息枚举*/
void listUpdateCallback(DevList *pFrist, int count) {
	LOGE("listUpdateCallback Now!\n");
	JNIEnv *env = NULL;
	(*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL);
	if (env == NULL) {
		LOGE("Get ENV FAILED!");
		return;
	}

	
#if 0	
	clazz = (*env)->FindClass(env, "com/sdmc/dlna/service/DevInfo");
	LOGE("listUpdateCallback Now!DevInfo __%d__ env is %p,clazz is %p",__LINE__,env,clazz);
#endif
	
	jmethodID mid = (*env)->GetMethodID(env, DevInfo_clazz, "<init>", "(IILjava/lang/String;Ljava/lang/String;)V");


	jobject obj = NULL;
	jstring jstrName = NULL;
	jstring jstrUDN = NULL;
	jobjectArray objArray = (*env)->NewObjectArray(env, count, DevInfo_clazz, 0);
	int i = 0;
	for(i = 0; i < count; i++) {

		jstrName =(*env)->NewStringUTF(env, pFrist[i].username);

		jstrUDN =(*env)->NewStringUTF(env, pFrist[i].UDN);
		obj = (*env)->NewObject(env, DevInfo_clazz, mid, pFrist[i].devnum, pFrist[i].type, jstrName, jstrUDN);
		(*env)->SetObjectArrayElement(env, objArray, i, obj);
		

		(*env)->DeleteLocalRef(env, obj);
		(*env)->DeleteLocalRef(env, jstrUDN);
		(*env)->DeleteLocalRef(env, jstrName);
	}

	(*env)->CallStaticVoidMethod(env, Callbacks, methods[UPDATE_DEV_LIST].methodInJNI, objArray);

	(*env)->DeleteLocalRef(env, objArray);
//	(*env)->DeleteLocalRef(env, DevInfo_clazz);
	(*g_jvm)->DetachCurrentThread(g_jvm);

}

/*音视频播放回调，传入path*/
int playCallBack() {
	return commonCallback0(PLAY);
}

/*播放停止回调*/
int stopCallBack() {
	return commonCallback0(STOP);
}

/*播放暂停回调*/
int pauseCallBack() {
	LOGE("pauseCallBack Now!");
	return commonCallback0(PAUSE);
}

/*设置播放速度回调，传入预设值speed*/
int setSpeedCallBack(int speed) {
	return commonCallback2(SET_SPEED, speed);
}

/*增加播放速度回调*/
int speedUpCallBack() {
	return commonCallback0(SPEED_UP);
}

/*减少播放速度回调*/
int speedDownCallBack() {
	return commonCallback0(SPEED_DOWN);
}

/*设置音量回调，传入预设值volume*/
int setVolumeCallBack(int volume) {
	return commonCallback2(SET_VOLUME, volume);
}

/*增加音量回调*/
int volumeUpCallBack() {
	return commonCallback0(VOLUME_UP);
}

/*减少音量回调*/
int volumeDownCallBack() {
	return commonCallback0(VOLUME_DOWN);
}

/*图片播放回调，传入path*/
int playImageCallBack(int type, char *path) {
	return commonCallback3(PLAY_IMAGE, type, path);
}

/*获得文件夹目录结构的回调*/
int searchFileListCallBack(pFSNode pFrist, int ResultNum, int TotalNum) {
	LOGE("searchFileListCallBack Now!\n");
	JNIEnv *env = NULL;
	pFSNode pNode = pFrist;
	
	(*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL);
	if (env == NULL) {
		LOGE("Get ENV FAILED!");
		return -1;
	}

	jmethodID mid = (*env)->GetMethodID(env, FileNode_clazz, "<init>", "(Ljava/lang/String;IIIIIILjava/lang/String;)V");
	
	jobject obj = NULL;
	jstring jstrName = NULL;
	jstring jstrPath = NULL;
	jobjectArray objArray = (*env)->NewObjectArray(env, ResultNum, FileNode_clazz, 0);
	int i = 0;
	for(i = 0; i < ResultNum; i++) {
		//LOGE("searchFileListCallBack Now!_Call To Java i is %d,ResultNum is %d\n",i,ResultNum);
		jstrName =(*env)->NewStringUTF(env, pNode->title);
		LOGE("pNode->title Is %s,pNode->privtype is %d\n",pNode->title,pNode->privtype);
		//LOGE("searchFileListCallBack Now!_Call To Java i is %d,ResultNum is %d\n",i,ResultNum);
		if(SAI_STRUCT_CONTAIER != pNode->privtype) {
			LOGE("searchFileListCallBack Now!_Call To Java jstrPath is :%s\n",pNode->_priv);
			jstrPath =(*env)->NewStringUTF(env, pNode->_priv);		
		} else { 
			LOGE("searchFileListCallBack Now!_Call To Java CONTAIER\n");
			jstrPath =(*env)->NewStringUTF(env, "");
			
		}
		//LOGE("searchFileListCallBack Now!_Call To Java i is %d,ResultNum is %d\n",i,ResultNum);
		obj = (*env)->NewObject(env, FileNode_clazz, mid, jstrName, pNode->id, pNode->restricted, pNode->parentID, pNode->childCount, pNode->searchable, pNode->privtype, jstrPath);
		//LOGE("searchFileListCallBack Now!_Call To Java i is %d,ResultNum is %d\n",i,ResultNum);
		(*env)->SetObjectArrayElement(env, objArray, i, obj);
		(*env)->DeleteLocalRef(env, obj);
		(*env)->DeleteLocalRef(env, jstrPath);
		(*env)->DeleteLocalRef(env, jstrName);
		pNode = pNode->_next;
		
	}
	LOGE("searchFileListCallBack Now!_Call To Java Begin\n");
	int ret = (*env)->CallStaticIntMethod(env, Callbacks, methods[SEARCH_FILE_LIST].methodInJNI, objArray, ResultNum, TotalNum);
	LOGE("searchFileListCallBack Now!_Call To Java Over\n");
	
	(*env)->DeleteLocalRef(env, objArray);
//	(*env)->DeleteLocalRef(env, FileNode_clazz);
	(*g_jvm)->DetachCurrentThread(g_jvm);

	return ret;
}

int searchPositionCallBack(int InstanceID, pDlna_PositionInfo preInfo){
	LOGE("start searchPositionCallBack start");
	JNIEnv *env = NULL;
	(*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL);
	if (env == NULL) {
		LOGE("Get ENV FAILED!");
		return -1;
	}

	jobject obj = (*env)->CallStaticObjectMethod(env, Callbacks, methods[SEARCH_POSITION].methodInJNI, InstanceID);

	if(NULL == obj) {
		LOGE("Get Position Failed!");
		return -1;
	}

	jfieldID fieldID;

	fieldID = (*env)->GetFieldID(env, PositionInfo_clazz, "track", "I");
	jint track = (*env)->GetIntField(env, obj, fieldID);

	fieldID = (*env)->GetFieldID(env, PositionInfo_clazz, "trackDuration", "Ljava/lang/String;");
	jstring trackDuration = (*env)->GetObjectField(env, obj, fieldID);

	fieldID = (*env)->GetFieldID(env, PositionInfo_clazz, "trackMetaData", "Ljava/lang/String;");
	jstring trackMetaData = (*env)->GetObjectField(env, obj, fieldID);

	fieldID = (*env)->GetFieldID(env, PositionInfo_clazz, "trackURI", "Ljava/lang/String;");
	jstring trackURI = (*env)->GetObjectField(env, obj, fieldID);

	fieldID = (*env)->GetFieldID(env, PositionInfo_clazz, "relTime", "Ljava/lang/String;");
	jstring relTime = (*env)->GetObjectField(env, obj, fieldID);

	fieldID = (*env)->GetFieldID(env, PositionInfo_clazz, "absTime", "Ljava/lang/String;");
	jstring absTime = (*env)->GetObjectField(env, obj, fieldID);

	fieldID = (*env)->GetFieldID(env, PositionInfo_clazz, "relCount", "I");
	jint relCount = (*env)->GetIntField(env, obj, fieldID);

	fieldID = (*env)->GetFieldID(env, PositionInfo_clazz, "absCount", "I");
	jint absCount = (*env)->GetIntField(env, obj, fieldID);

	preInfo->Track = track;
	if(getCStrByJStr(env, trackDuration) != NULL) {
		memcpy(preInfo->TrackDuration, getCStrByJStr(env, trackDuration), 20);
	}
	if(getCStrByJStr(env, trackMetaData) != NULL) {
		memcpy(preInfo->TrackMetaData, getCStrByJStr(env, trackMetaData), 20);
	}
	if(getCStrByJStr(env, trackURI) != NULL) {
		LOGI("searchPositionCallBack trackURI %s \n", getCStrByJStr(env, trackURI));
		memcpy(preInfo->TrackURI, getCStrByJStr(env, trackURI), sizeof(trackURI));
	}
	if(getCStrByJStr(env, relTime) != NULL) {
		memcpy(preInfo->RelTime, getCStrByJStr(env, relTime), 20);
	}
	if(getCStrByJStr(env, absTime) != NULL) {
		memcpy(preInfo->AbsTime, getCStrByJStr(env, absTime), 20);
	}
	
	preInfo->RelCount = relCount;
	preInfo->AbsCount = absCount;

	(*env)->DeleteLocalRef(env, absTime);
	(*env)->DeleteLocalRef(env, relTime);
	(*env)->DeleteLocalRef(env, trackURI);
	(*env)->DeleteLocalRef(env, trackMetaData);
	(*env)->DeleteLocalRef(env, trackDuration);
	(*env)->DeleteLocalRef(env, obj);
	(*g_jvm)->DetachCurrentThread(g_jvm);
	LOGE("searchPositionCallBack over");
	return 0;
}
int getPositionCallBack(pDlna_PositionInfo preInfo){
	JNIEnv *env = NULL;
	(*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL);
	if (env == NULL) {
		LOGE("Get ENV FAILED!");
		return -1;
	}

	jmethodID mid = (*env)->GetMethodID(env, PositionInfo_clazz, "<init>", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;II)V");
	jstring trackDuration =(*env)->NewStringUTF(env, preInfo->TrackDuration);
	jstring trackMetaData =(*env)->NewStringUTF(env, preInfo->TrackMetaData);
	jstring trackURI =(*env)->NewStringUTF(env, preInfo->TrackURI);
	jstring relTime =(*env)->NewStringUTF(env, preInfo->RelTime);
	jstring absTime =(*env)->NewStringUTF(env, preInfo->AbsTime);
	jobject obj = (*env)->NewObject(env, PositionInfo_clazz, mid, preInfo->Track, trackDuration, trackMetaData, trackURI, relTime, absTime, preInfo->RelCount, preInfo->AbsCount);
	int ret = (*env)->CallStaticIntMethod(env, Callbacks, methods[GET_POSITION].methodInJNI, obj);

	(*env)->DeleteLocalRef(env, absTime);
	(*env)->DeleteLocalRef(env, relTime);
	(*env)->DeleteLocalRef(env, trackURI);
	(*env)->DeleteLocalRef(env, trackMetaData);
	(*env)->DeleteLocalRef(env, trackDuration);
	(*env)->DeleteLocalRef(env, obj);
	(*g_jvm)->DetachCurrentThread(g_jvm);
	return ret;
}

int httpStatusChangeCallBack(HTTPINITSTATUS status) {
	return commonCallback2(HTTP_CHANGED, status);
}

/*播放等待*/
int waitPlayCallBack() {
	return commonCallback0(WAIT_PLAY);
}
