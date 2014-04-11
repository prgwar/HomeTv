#ifndef __DLNA_SOCKET_H__
#define __DLNA_SOCKET_H__



#ifdef _c_plus_plus_
	extern "C"{
#endif

#include "upnp_type.h"

struct _my_in_addr
{
	unsigned int s_addr;
};

struct socketaddr_in
{
	unsigned short int sin_family;
	unsigned short sin_port;
	struct _my_in_addr sin_addr;
	unsigned char sin_zero[8];
};


enum
{
	HTTP_INIT_WAIT = 0,
	HTTP_INIT_SUCCESS = 1
};
typedef int HTTPINITSTATUS;

int CheckNetWorkStatus(void);

int RecvSocketMsg(char *msg,int len);

int MakeTheCourseSocket(dlna_init_handle *phand);


#ifdef _c_plus_plus_
		}
#endif

#endif