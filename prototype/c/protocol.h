#ifndef __PROTOCOL_H
#define __PROTOCOL_H

#include <stdlib.h>

typedef unsigned char uchar;
typedef unsigned int uint;

typedef struct {
    uint id;
    uchar cmd;
    uchar type;
    uchar bData;
    uint iData;
} PROTOCOL_data;

PROTOCOL_data PROTOCOL_fromByteArray(uchar *buf, uint bufLen);
unsigned int PROTOCOL_toByteArray(PROTOCOL_data data, unsigned char *buf);

#endif //__PROTOCOL_H


