#include <stdio.h>
#include <stdlib.h>
#include "protocol.h"

int main() {
    PROTOCOL_data data;
    
    printf("fill data\n");
    data.id = 1;
    data.cmd = 2;
    data.type = 0;
    data.bData = 3;
    
    unsigned char *buf = malloc(0);
    
    printf("data to byte buffer\n");
    unsigned int len = PROTOCOL_toByteArray(data, buf);
    
    for (unsigned int i=0; i<len; i++) {
        printf("%.2X ", buf[i]);
    }
    printf("\n");
    
    printf("fill data\n");
    data.id = 1;
    data.cmd = 2;
    data.type = 1;
    data.iData = 3;
    
    printf("data to byte buffer\n");
    len = PROTOCOL_toByteArray(data, buf);
    
    for (unsigned int i=0; i<len; i++) {
        printf("%.2X ", buf[i]);
    }
    printf("\n");

    unsigned char newBuf[] = {0x42, 0x00, 0xF0, 0xAA, 0xAA, 0xAA, 0xAA, 0x00, 0x00, 0x00, 0x13, 0x00, 0x00, 0x00, 0x01, 0x02, 0x01, 0x00, 0x00, 0x00, 0x03, 0x17, 0x12, 0xFF};
  
    printf("convert buf to data\n");
    PROTOCOL_data newData = PROTOCOL_fromByteArray(newBuf, sizeof(newBuf));
    printf("id %d\n", newData.id);
    printf("cmd %d\n", newData.cmd);
    printf("type %d\n", newData.type);
    printf("iData %d\n", newData.iData);
    
    
    printf("end\n");
}
