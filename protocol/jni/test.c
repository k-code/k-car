#include <stdio.h>
#include <stdlib.h>
#include "protocol.h"

#define _DEBUG

int main() {
    PROTOCOL_data data;
    
    printf("fill data\n");
    data.id = 1;
    data.cmd = 2;
    data.type = 0;
    data.bData = 3;
    
    unsigned char buf[PROTOCOL_MAX_FRAME_SIZE];
    
    printf("data to byte buffer\n");
    long len = PROTOCOL_toByteArray(data, buf);
    
    for (long i=0; i<len; i++) {
        printf("%.2X ", buf[i]);
    }
    printf("\n");
    
    printf("fill data\n");
    data.id = 1;
    data.cmd = 2;
    data.type = 1;
    data.iData = -2147483648;
    
    printf("data to byte buffer\n");
    len = PROTOCOL_toByteArray(data, buf);
    
    for (long i=0; i<len; i++) {
        printf("%.2X ", buf[i]);
    }
    printf("\n");

    unsigned char newBuf[] = {0x42, 0x00, 0xF0, 0xAA, 0xAA, 0xAA, 0xAA, 0x00, 0x00, 0x00, 0x13, 0x00, 0x00, 0x00, 0x01, 0x02, 0x01, 0x80, 0x00, 0x00, 0x00, 0xB5, 0x12, 0xFF};
  
    printf("convert buf to data\n");
    PROTOCOL_data newData = PROTOCOL_fromByteArray(newBuf, sizeof(newBuf));
    printf("id %d\n", newData.id);
    printf("cmd %d\n", newData.cmd);
    printf("type %d\n", newData.type);
    printf("iData %d\n", newData.iData);
    
    
    printf("end\n");
}
