#ifndef __UPNP_TYPE_H__
#define __UPNP_TYPE_H__


#ifdef __c_plus_plus__
	extern "C"{
#endif


struct _Dlna_init_handle
{
	int (*_reinit)(void);
};
typedef struct _Dlna_init_handle dlna_init_handle;



#define JNIOK 1

//Number of Can be identified DevType.
#define TV_DEV_TYPECOUNT 2

//Number of services.

#define TV_SERVER_SER_COUNT 2 
#define TV_RENDER_SER_COUNT 3

#define MAX_TV_SERVICE_SERVCOUNT TV_RENDER_SER_COUNT

//Max value length
#define TV_MAX_VAL_LEN 5

//Max actions
#define TV_MAXACTIONS 13

/* This should be the maximum VARCOUNT from above */
#define TV_MAXVARS TV_PICTURE_VARCOUNT

/*
***************************************************
*	Track:			the trackid,can always set 1;
*	TrackDuration: 	the video/music total play_times;set as 3:12:25(hour:min:second)
*	TrackMetaData: 	Can always set "";
*	TrackURI:		play video/music's path. http://10.10.121.12:49532/like.mp3 
*	RelTime:			already play times, set as 3:12:20(hour:min:second)
*	AbsTime:			Can always set "NOT_IMPLEMENTED".
*	RelCount:		Can always set -1.
*	AbsCount:		Can always set -1.
***************************************************
*/
struct _dlna_PositionInfo
{
	int Track;
	char TrackDuration[20];
	char TrackMetaData[20];
	char TrackURI[255];
	char RelTime[20];
	char AbsTime[20];
	int RelCount;
	int AbsCount;
};
typedef struct _dlna_PositionInfo Dlna_PositionInfo;
typedef struct _dlna_PositionInfo *pDlna_PositionInfo;

//Index of control service
enum
{
	TV_SERVICE_DIRECTORY = 0,
	TV_SERVICE_MANAGER
	
};

enum
{
	TV_RENDER_CONTROL = 0,
	TV_RENDER_AVTRANSPORT,
	TV_RENDER_CONTMANAGER
};


enum
{
	DEV_TYPE_UNSEARCH = -1,
	DEV_TYPE_MEDIASERVER = 0,
	DEV_TYPE_MEDIARENDERER = 1
};
typedef int TV_DEV_TYPE;

struct _DevList
{
	int devnum;//设备编号
	TV_DEV_TYPE type;
	char username[255];//用户名
	char UDN[255];//UDN
};
typedef struct _DevList DevList;
typedef struct _DevList *pDevList;


#ifdef __c_plus_plus__
	}
#endif


#endif
