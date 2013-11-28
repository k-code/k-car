#ifndef __PROTOCOL_H
#define __PROTOCOL_H

#define PROTOCOL_MAX_FRAME_SIZE     102400
#define PROTOCOL_VERSION            0x01

#define DATA_TYPE_CHAR              0x00
#define DATA_TYPE_INT               0x01
#define DATA_TYPE_ARRAY             0x02

// Commands categories
// Reserved for system commands
#define PROTOCOL_CMD_RESERVED_F     0
#define PROTOCOL_CMD_RESERVED_L     9
// Command for rend data between control and cop
#define PROTOCOL_CMD_COM_COP_F      10
#define PROTOCOL_CMD_COM_COP_L      49
// Command for rend data between control and autopilot
#define PROTOCOL_CMD_COM_AUTO_F     50
#define PROTOCOL_CMD_COM_AUTO_L     89

// Commands
// System
#define PROTOCOL_CMD_ERROR          0
#define PROTOCOL_CMD_PING           1

// For COP
#define PROTOCOL_CMD_CAM_STATE      10
#define PROTOCOL_CMD_CAM_IMG        11
#define PROTOCOL_CMD_CAM_FPS        12
#define PROTOCOL_CMD_CAM_QUALITY    13
#define PROTOCOL_CMD_CAM_FLASH      14
#define PROTOCOL_CMD_CAM_SIZE_LIST  15
#define PROTOCOL_CMD_CAM_SIZE_SET   16

// For autopilot
#define PROTOCOL_CMD_TRIGGER_LED    50
#define PROTOCOL_CMD_DISTANCE_REQ   51
#define PROTOCOL_CMD_DISTANCE_RES   52
#define PROTOCOL_CMD_LMS            53
#define PROTOCOL_CMD_RMS            54

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

#define PROTOCOL_empty_data {0,0,0,0,0,0,0}

extern PROTOCOL_data PROTOCOL_fromByteArray(t_byte *buf, t_int bufLen);
extern t_int PROTOCOL_toByteArray(PROTOCOL_data data, t_byte *buf);

#endif //__PROTOCOL_H


