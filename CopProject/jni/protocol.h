#ifndef __PROTOCOL_H
#define __PROTOCOL_H

// TODO : add protocol version to frame

// Max frame len 4-header, 4-length, 4-id, 1-command, 1-type, 4-max data size, 4-crc
#define PROTOCOL_MAX_FRAME_SIZE 22
#define PROTOCOL_VERSION 0x01

typedef unsigned char t_byte;
typedef unsigned int t_int;

typedef struct {
    t_int id;
    t_byte cmd;
    t_byte type;
    t_byte bData;
    t_int iData;
} PROTOCOL_data;

const PROTOCOL_data PROTOCOL_empty_data = {0,0,0,0,0};

extern PROTOCOL_data PROTOCOL_fromByteArray(t_byte *buf, t_int bufLen);
extern t_int PROTOCOL_toByteArray(PROTOCOL_data data, t_byte *buf);

#endif //__PROTOCOL_H


