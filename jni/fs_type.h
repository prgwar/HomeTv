#ifndef __FS_TYPE_H__
#define __FS_TYPE_H__



#ifdef _c_plus_plus_
	extern "C"{
#endif


#include "sai_type.h"

extern char WEB_SER_DIR[];


#define MAX_TITLE_SIZE 255
#define MAX_PATH_SIZE  512


struct _NodeID
{
	uint32 Lv_One :3;
	uint32 Lv_Two :5;
	uint32 Lv_Three :24;
};
typedef struct _NodeID NodeID;
typedef struct _NodeID *pNodeID;

enum
{
	NODEIDFORVIDEO = 0x10000000,
	NODEIDFORMUSIC = 0x20000000,
	NODEIDFORIMAGE = 0x30000000
};

enum
{
	BrowseErr,
	BrowseMetadata,
	BrowseDirectChildren
};
typedef int DLNABROWSEFLAG;

/*
*******************************************
struct of the file node.
uint32 id				:the node's id;
uint32 restricted		:restricted;
uint32 parentID		:it's parentid;
uint32 childCount		:child count;
uint32 searchable		:searchable;

SAI_STRUCT_TYPE type:the priv's type;
SAI_HANDLE _priv		:private msg;

truct _FSNode *_parent:parent's node;
struct _FSNode *_child	:child 's frist node;
struct _FSNode *_last	:The last brother node point;
struct _FSNode *_next	:The next brother node point.
*******************************************
*/
struct _FSNode
{
	char8 title[MAX_TITLE_SIZE];
	uint32 id;
	uint32 restricted;
	uint32 parentID;
	uint32 childCount;
	uint32 searchable;

	SAI_STRUCT_TYPE privtype;
	SAI_HANDLE _priv;

	struct _FSNode *_parent;
	struct _FSNode *_child;
	struct _FSNode *_last;
	struct _FSNode *_next;
};
typedef struct _FSNode FSNode;
typedef struct _FSNode *pFSNode;


enum
{
	FS_UNKNOW,
	FS_VIDEO,
	FS_MUSIC,
	FS_IMAGE
};
typedef int FS_TYPE;

struct _Fs_FileNode_
{
	FS_TYPE type;
	char filepath[MAX_PATH_SIZE];
};
typedef struct _Fs_FileNode_ FS_FileNode;
typedef struct _Fs_FileNode_ *LPFS_FileNode;


pFSNode GetLastFSNode(pFSNode pParentNode);

pFSNode CreateFSNode(pFSNode pParentNode,pFSNode pBrotherNode);
SAI_BOOL DestoryFSNode(pFSNode pDestoryNode);

pFSNode GetSearchFSNodeFromID(pFSNode pRootNode,uint32 ObjectID);


#ifdef _c_plus_plus_
		}
#endif

#endif
