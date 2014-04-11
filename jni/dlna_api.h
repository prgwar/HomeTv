#ifndef __DLNA_API_H__
#define __DLNA_API_H__


#ifdef __c_plus_plus__
	extern "C"{
#endif

#include "sai_type.h"
#include "fs_type.h"
#include "upnp_type.h"





/*
*************************************************************
*Function:	DLNA_Init
*Used:		Init dlna C's Mode.	
*IN:
*
*OUT:
* 
*************************************************************
*/
SAI_BOOL DLNA_Init(void);

/*
*************************************************************
*Function:	DLNA_FS_ReInit
*Used:		ReInit the file system if change any file.	
*IN:
*
*OUT:
* 
*************************************************************
*/
SAI_BOOL DLNA_FS_ReInit(void);


/*
*************************************************************
*Function:	DLNA_ReCreate_FileNode
*Used:		rebuild filenode.	
*IN:
*   int count:						FS_FileNode count.
*   LPFS_FileNode pFrist:			the frist handle of FS_FileNode.
*OUT:
*   int:				Always return 0.
*************************************************************
*/
SAI_BOOL DLNA_ReCreate_FileNode(int count, LPFS_FileNode pFrist);


/*
*************************************************************
*Function:	DLNA_FS_GetMediaMsg
*Used:		Get Search File Media Msg.	
*IN:
*	filepath:	Search File Path, if path == NULL || *path == 0x00, search the last file meadia msg.
*	artist:	The artist Msg.
*	title:	The title Msg.
*OUT:
* 
*************************************************************
*/
SAI_BOOL DLNA_FS_GetMediaMsg(char *filepath,char *artist, char * title);

/*
*************************************************************
*Function:	DLNA_SetSearchStatus
*Used:		Set File Share Status.	
*IN:
*	Video:	0.close 1.open.
*	Music:	0.close 1.open.
*	Image:	0.close 1.open.
*OUT:
* 
*************************************************************
*/
SAI_BOOL DLNA_SetSearchStatus(char Video,char Music,char Image);


/*
*************************************************************
*Function:	DLNA_KillAll
*Used:		Kill the thread.	
*IN:
*
*OUT:
* 
*************************************************************
*/
SAI_BOOL DLNA_KillAll(void);

/*
*************************************************************
*Function:	Dlna_SetUserName
*Used:		Set username & the udn num.	
*IN:
*	username:	The Dev username.
*	udn:			The Dev udn.
*OUT:
* 
*************************************************************
*/
SAI_BOOL Dlna_SetUserName(char *username,char *udn);


/*
*************************************************************
*Function:	DLNA_Ctr_SendPlay
*Used:		Set Dev Play.	
*IN:
*   devnum:	Dev Number.
*   path:		Play File's path.
*OUT:
*   int:		Always return 0.
*************************************************************
*/
int DLNA_Ctr_SendPlay(int devnum,char *path);

/*
*************************************************************
*Function:	DLNA_Ctr_SendStop
*Used:		Stop Dev Play Video or Music.(Can't Stop Play Pic)	
*IN:
*   devnum:	Dev Number.
*OUT:
*   int:		Always return 0.
*************************************************************
*/
int DLNA_Ctr_SendStop(int devnum);

/*
*************************************************************
*Function:	DLNA_Ctr_SendPause
*Used:		Set Dev Play Pause.(Can't Used Pic)	
*IN:
*   devnum:	Dev Number.
*OUT:
*   int:		Always return 0.
*************************************************************
*/
int DLNA_Ctr_SendPause( int devnum );

/*
*************************************************************
*Function:	DLNA_Ctr_SendSetSpeed
*Used:		Set Dev Play's Speed.(Video or Music,Can't Play Pic)	
*IN:
*   devnum:	Dev Number.
*   speed:	Play Speed.
*OUT:
*   int:		Always return 0.
*************************************************************
*/
int DLNA_Ctr_SendSetSpeed(int devnum,int speed);

/*
*************************************************************
*Function:	DLNA_Ctr_SendIncreaseSpeed
*Used:		Increase Dev Play Speed.(Can't Play Pic)	
*IN:
*   devnum:	Dev Number.
*OUT:
*   int:		Always return 0.
*************************************************************
*/
int DLNA_Ctr_SendIncreaseSpeed(int devnum);

/*
*************************************************************
*Function:	DLNA_Ctr_SendDecreaseSpeed
*Used:		Decrease Dev Play Speed.(Can't Play Pic)	
*IN:
*   devnum:	Dev Number.
*OUT:
*   int:		Always return 0.
*************************************************************
*/
int DLNA_Ctr_SendDecreaseSpeed(int devnum);

/*
*************************************************************
*Function:	DLNA_Ctr_SendSetVolume
*Used:		Set Dev Play's Volume.( Video or Music.Can't Play Pic)	
*IN:
*   devnum:	Dev Number.
*   Volume:	The Set Player Volume.
*OUT:
*   int:		Always return 0.
*************************************************************
*/
int DLNA_Ctr_SendSetVolume(int devnum,int Volume);

/*
*************************************************************
*Function:	DLNA_Ctr_SendIncreaseVolume
*Used:		Increase Dev Play's Volume.( Video or Music.Can't Play Pic)	
*IN:
*   devnum:	Dev Number.
*   path:		Play File's path.
*OUT:
*   int:		Always return 0.
*************************************************************
*/
int DLNA_Ctr_SendIncreaseVolume(int devnum);

/*
*************************************************************
*Function:	DLNA_Ctr_SendDecreaseVolume
*Used:		Decrease Dev Play's Volume.( Video or Music.Can't Play Pic)	
*IN:
*   devnum:	Dev Number.
*   path:		Play File's path.
*OUT:
*   int:		Always return 0.
*************************************************************
*/
int DLNA_Ctr_SendDecreaseVolume(int devnum);

/*
*************************************************************
*Function:	DLNA_Ctr_SendPicPlay
*Used:		Set Dev Play Pic.
*IN:
*   devnum:	Dev Number.
*   path:		Play File's path.
*OUT:
*   int:		Always return 0.
*************************************************************
*/
int DLNA_Ctr_SendPicPlay(int devnum,char *path);

/*
*************************************************************
*Function:	DLNA_Ctr_SendBrowse
*Used:		GetBrowseNodeMsg.	
*IN:
*   devnum:			Dev Number.
*   ObjectID:			Search Node ID.
*   BrowseFlag:		Always Set As "BrowseDirectChildren".
*   Filter:				Always Set As "*".
*   StartingIndex:		Always Set As 0.
*   RequestedCount:	Want Get child Node Num.
*   SortCriteria:		Always Set As "*".
*OUT:
*   int:				Always return 0.
*************************************************************
*/
int DLNA_Ctr_SendBrowse(int devnum, int ObjectID, char * BrowseFlag, char * Filter, int StartingIndex, int RequestedCount, char * SortCriteria);

/*
*************************************************************
*Function:	DLNA_Ctr_SendGetPositionInfo
*Used:		GetBrowseNodeMsg.	
*IN:
*   devnum:			Dev Number.
*   InstanceID:		Can always set 0.
*OUT:
*   int:				Always return 0.
*************************************************************
*/
int DLNA_Ctr_SendGetPositionInfo(int devnum,int InstanceID);


struct _DLNA_Need_CallBack
{
	int (*_DevListUpdateCallback)(pDevList pFrist,int count);

	/*Action Function*/
	//音视频播放回调
	int (*_DevPlayCallBack)(void);

	//播放停止回调
	int (*_DevStopCallBack)(void);

	//播放暂停回调
	int (*_DevPauseCallBack)(void);

	//设置播放速度回调，传入预设值speed
	int (*_DevSetSpeedCallBack)(int speed);

	//增加播放速度回调
	int (*_DevIncreaseSpeedCallBack)(void);

	//减少播放速度回调
	int (*_DevDecreaseSpeedCallBack)(void);

	//设置音量回调，传入预设值volume
	int (*_DevSetVolumeCallBack)(int Volume);

	//增加音量回调
	int (*_DevIncreaseVolumeCallBack)(void);

	//减少音量回调
	int (*_DevDecreaseVolumeCallBack)(void);

	//播放回调，传入path和type
	int (*_DevPicPlayCallBack)(int type,char *path);

	/*Search File List*/
	//查询文件接口回调
	int (*_Ctr_SearchFileListCallBack)(pFSNode pFrist,int ResultNum,int TotalNum);


	//查询当前播放情况回调
	int (*_Ctr_SearchPositionInfoCallBack)(int InstanceID,pDlna_PositionInfo preInfo);

	//得到当前播放情况回调
	int (*_Ctr_GetPositionInfoCallBack)(pDlna_PositionInfo preInfo);

	//通知底层http服务状态改变
	int (*_Net_HttpStatusChange)(HTTPINITSTATUS status);

	//通知有播放请求
	int (*_DevWaitPlay)(void);
};
typedef struct _DLNA_Need_CallBack DLNA_Need_CallBack;


/*
*************************************************************
*Function:	DLNA_GetCallbackHandle
*Used:		Get C's CallBack Handle To Set CallBack.	
*IN:
*OUT:
*   DLNA_Need_CallBack *:	The finger of C's Callback handle.
*************************************************************
*/
DLNA_Need_CallBack *DLNA_GetCallbackHandle(void);


#ifdef __c_plus_plus__
	}
#endif


#endif
