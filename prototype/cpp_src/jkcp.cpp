#include <iostream>
#include "jkcp.h"
#include "protocol.h"

class JData {
    public:
        jint id;
        jboolean cmd;
        jboolean type;
        jboolean bData;
        jint iData;
};

JNIEXPORT jint JNICALL Java_pro_kornev_kcar_prototype_Protocol_toByteArray(JNIEnv *env, jclass cl, jobject jdata, jbooleanArray buf) {
    JData *d = (JData*) jdata;

    PROTOCOL_data data;
    data.id = d->id;
    data.cmd = d->cmd;
    data.type = d->type;
    data.bData = d->bData;
    data.iData = d->iData;

    unsigned char *b = (unsigned char *)malloc(0);
    unsigned int len = PROTOCOL_toByteArray(data, b);

    jboolean *jb = (jboolean *)malloc(sizeof(jboolean)*len);

    for (int i=0; i<len; i++) {
        jb[i] = b[i];
    }

    buf = env->NewBooleanArray(len);  // allocate
    env->SetBooleanArrayRegion(buf, 0, len, jb);  // copy
/**/

    return len;
}

JNIEXPORT jobject JNICALL Java_pro_kornev_kcar_prototype_Protocol_fromByteArray(JNIEnv *a, jclass b, jcharArray c, jint d) {
    return 0;
}