#include <iostream>
#include "jkcp.h"
#include "protocol.h"

JNIEXPORT jbyteArray JNICALL Java_pro_kornev_kcar_prototype_Protocol_toByteArray(JNIEnv *env, jclass cl, jobject jdata) {
    printf("crate Data class\n");
    jclass clazz = env->FindClass("Lpro/kornev/kcar/prototype/Data;");
    jfieldID fid = env->GetFieldID(clazz, "id", "I");
    jfieldID fcmd = env->GetFieldID(clazz, "cmd", "B");
    jfieldID ftype = env->GetFieldID(clazz, "type", "B");
    jfieldID fbData = env->GetFieldID(clazz, "bData", "B");
    jfieldID fiData = env->GetFieldID(clazz, "iData", "I");

    printf("Extract data from Data object\n");
    jint id = env->GetIntField(jdata, fid);
    jbyte cmd = env->GetIntField(jdata, fcmd);
    jbyte type = env->GetIntField(jdata, ftype);
    jbyte bData = env->GetIntField(jdata, fbData);
    jint iData = env->GetIntField(jdata, fiData);

    printf("id: %d\n", id);
    printf("cmd: %d\n", cmd);
    printf("type: %d\n", type);
    printf("bData: %d\n", bData);
    printf("iData: %d\n", iData);

    printf("Create and fill POTOCOL_data\n");
    PROTOCOL_data data;
    data.id = (unsigned int)id;
    data.cmd = (unsigned char)cmd;
    data.type = (unsigned char)type;
    data.bData = (unsigned char)bData;
    data.iData = (unsigned int)iData;

    printf("Convert data to array\n");
    unsigned char *b = (unsigned char *)malloc(0);
    unsigned int len = PROTOCOL_toByteArray(data, b);

    jbyte *jb = (jbyte *)malloc(sizeof(jbyte)*len);

    for (int i=0; i<len; i++) {
        printf("%.2x ", b[i]);
        jb[i] = b[i];
    }
    printf("\n");

    jbyteArray buf = env->NewByteArray(len);  // allocate
    env->SetByteArrayRegion(buf, 0, len, jb);  // copy*//*

    return buf;
}

JNIEXPORT jobject JNICALL Java_pro_kornev_kcar_prototype_Protocol_fromByteArray(JNIEnv *env, jclass jc, jbyteArray jbuf, jint jlen) {
    jclass jdc;
    jobject jdata;

    jdc =  env->FindClass("Lpro/kornev/kcar/prototype/Data;");
    jdata = env->AllocObject( jdc );
    //env->SetLongField( obj, env->GetFieldID( tempClass, "peer", "J" ), (jlong)eval);
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
