#include "jniprotocol.h"
#include "protocol.h"
#include <stdlib.h>

JNIEXPORT jbyteArray JNICALL Java_pro_kornev_kcar_protocol_Protocol_toByteArray(JNIEnv *env, jclass cl, jobject jdata) {
    // Crate Data class
    jclass clazz = env->FindClass("pro/kornev/kcar/protocol/Data");
    jfieldID fid = env->GetFieldID(clazz, "id", "I");
    jfieldID fcmd = env->GetFieldID(clazz, "cmd", "B");
    jfieldID ftype = env->GetFieldID(clazz, "type", "B");
    jfieldID fbData = env->GetFieldID(clazz, "bData", "B");
    jfieldID fiData = env->GetFieldID(clazz, "iData", "I");

    // Extract data from Data object
    jint id = env->GetIntField(jdata, fid);
    jbyte cmd = env->GetByteField(jdata, fcmd);
    jbyte type = env->GetByteField(jdata, ftype);
    jbyte bData = env->GetByteField(jdata, fbData);
    jint iData = env->GetIntField(jdata, fiData);

    // Create and fill POTOCOL_data
    PROTOCOL_data data;
    data.id = (unsigned int)id;
    data.cmd = (unsigned char)cmd;
    data.type = (unsigned char)type;
    data.bData = (unsigned char)bData;
    data.iData = (unsigned int)iData;

    // Convert data to array
    unsigned char b[PROTOCOL_MAX_FRAME_SIZE];
    unsigned int len = PROTOCOL_toByteArray(data, b);

    jbyte *jb = (jbyte *)malloc(sizeof(jbyte)*len);

    for (int i=0; i<len; i++) {
        jb[i] = b[i];
    }

    jbyteArray buf = env->NewByteArray(len);  // allocate
    env->SetByteArrayRegion(buf, 0, len, jb);  // copy

    free(jb);

    return buf;
}

JNIEXPORT jobject JNICALL Java_pro_kornev_kcar_protocol_Protocol_fromByteArray(JNIEnv *env, jclass jc, jbyteArray jbuf, jint jlen) {
    jclass jdc;
    jobject jdata;

    jdc =  env->FindClass("pro/kornev/kcar/protocol/Data");
    jdata = env->AllocObject( jdc );
    jbyte *jb = (jbyte *)malloc(jlen);

    env->GetByteArrayRegion(jbuf, 0, jlen, jb);

    unsigned char *buf = (unsigned char *)malloc(jlen);
    for (int i=0; i<jlen; i++) {
        buf[i] = 0xFF & jb[i];
    }

    PROTOCOL_data data = PROTOCOL_fromByteArray(buf, jlen);

    env->SetIntField(jdata, env->GetFieldID(jdc, "id", "I" ), (jint)data.id);
    env->SetByteField(jdata, env->GetFieldID(jdc, "cmd", "B" ), (jint)data.cmd);
    env->SetByteField(jdata, env->GetFieldID(jdc, "type", "B" ), (jint)data.type);
    env->SetByteField(jdata, env->GetFieldID(jdc, "bData", "B" ), (jint)data.bData);
    env->SetByteField(jdata, env->GetFieldID(jdc, "iData", "I" ), (jint)data.iData);

    return jdata;
}
