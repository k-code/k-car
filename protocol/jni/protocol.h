#ifndef __PROTOCOL_H
#define __PROTOCOL_H

#define PROTOCOL_MAX_FRAME_SIZE 102400
#define PROTOCOL_VERSION 0x01

#define DATA_TYPE_CHAR 0x00
#define DATA_TYPE_INT 0x01
#define DATA_TYPE_ARRAY 0x02

typedef unsigned char t_byte;
typedef unsigned int t_int;

typedef struct {
    t_int id;
    t_byte cmd;
    t_byte type;
    t_byte bData;
    t_int iData;
    t_int aSize;
    t_byte* aData;
} PROTOCOL_data;

#define PROTOCOL_empty_data {0,-1,0,0,0,0,0}

extern PROTOCOL_data PROTOCOL_fromByteArray(t_byte *buf, t_int bufLen);
extern t_int PROTOCOL_toByteArray(PROTOCOL_data data, t_byte *buf);

#endif //__PROTOCOL_H


