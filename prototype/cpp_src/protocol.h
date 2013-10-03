#ifndef __PROTOCOL_H
#define __PROTOCOL_H

#include <stdlib.h>

typedef unsigned char t_byte;
typedef unsigned int t_int;

typedef struct {
    t_int id;
    t_byte cmd;
    t_byte type;
    t_byte bData;
    t_int iData;
} PROTOCOL_data;

PROTOCOL_data PROTOCOL_fromByteArray(t_byte *buf, t_int bufLen);
t_int PROTOCOL_toByteArray(PROTOCOL_data data, t_byte *buf);

#endif //__PROTOCOL_H


