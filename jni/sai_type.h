#ifndef __SAI_TYPE_H__
#define __SAI_TYPE_H__



#ifdef _c_plus_plus_
	extern "C"{
#endif

//#define NULL (1 == 0)


typedef char *Byte;

typedef char char8;
typedef unsigned int uchar8;
typedef short short8;
typedef unsigned short ushort8;
typedef int int32;
typedef unsigned int uint32;

typedef void *SAI_HANDLE;
typedef int SAI_BOOL;

typedef int HTTPINITSTATUS;

enum SAI_ERRCODE
{
	SAI_ERR_SUCCESS,
	SAI_ERR_POINTEMP
};
typedef int SAI_ERRCODE;


enum
{
	SAI_STRUCT_NULL,
	SAI_STRUCT_CONTAIER,
	SAI_STRUCT_RESOURCE,
	SAI_STRUCT_RESOURCE_VIDEO,
	SAI_STRUCT_RESOURCE_MUSIC,
	SAI_STRUCT_RESOURCE_PHOTO
};
typedef int SAI_STRUCT_TYPE;

SAI_HANDLE SAI_Malloc(int len);
int SAI_Free(SAI_HANDLE p);

#define ANDROID_PRINT
//#define LINUX_PRINT
//#define NO_PRINT

#ifdef ANDROID_PRINT
#include <android/log.h>

int __android_log_print(int prio, const char *tag,  const char *fmt, ...);

#undef LOG_FP
#define LOG_FP "fp"

#undef LOG_TEST
#define LOG_TEST "fp_test"

#define FP_Print(args, ...) __android_log_print(ANDROID_LOG_ERROR, LOG_FP, args, ##__VA_ARGS__)
#define FP_Test_Print(args, ...) __android_log_print(ANDROID_LOG_ERROR, LOG_TEST, args, ##__VA_ARGS__)
#define Sai_Print(args, ...) __android_log_print(ANDROID_LOG_ERROR, LOG_FP, args, ##__VA_ARGS__)
#define Sai_Test_Print(args, ...) __android_log_print(ANDROID_LOG_ERROR, LOG_TEST, args, ##__VA_ARGS__)
#endif



#ifdef _c_plus_plus_
		}
#endif

#endif
