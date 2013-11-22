#include "jniprotocol.h"
#include "protocol.h"
#include <stdlib.h>

JNIEXPORT jint JNICALL Java_pro_kornev_kcar_protocol_Protocol_toByteArray(JNIEnv *env, jclass jc, jobject jdata, jbyteArray jbuf) {
    // Crate Data class
    jclass clazz = env->FindClass("pro/kornev/kcar/protocol/Data");
    jfieldID fid = env->GetFieldID(clazz, "id", "I");
    jfieldID fcmd = env->GetFieldID(clazz, "cmd", "B");
    jfieldID ftype = env->GetFieldID(clazz, "type", "B");
    jfieldID fbData = env->GetFieldID(clazz, "bData", "B");
    jfieldID fiData = env->GetFieldID(clazz, "iData", "I");
    jfieldID faSize = env->GetFieldID(clazz, "aSize", "I");
    jfieldID faData = env->GetFieldID(clazz, "aData", "[B");

    // Extract data from Data object
    jint id = env->GetIntField(jdata, fid);
    jbyte cmd = env->GetByteField(jdata, fcmd);
    jbyte type = env->GetByteField(jdata, ftype);
    jbyte bData = env->GetByteField(jdata, fbData);
    jint iData = env->GetIntField(jdata, fiData);
    jint aSize = env->GetIntField(jdata, faSize);
    jbyteArray aData = static_cast<jbyteArray>( env->GetObjectField( jdata, faData ) );

    // Create and fill POTOCOL_data
    PROTOCOL_data data;
    data.id = (unsigned int)id;
    data.cmd = (unsigned char)cmd;
    data.type = (unsigned char)type;
    data.bData = (unsigned char)bData;
    data.iData = (unsigned int)iData;
    data.aSize = (unsigned int)aSize;

    if (type == DATA_TYPE_ARRAY) {
        data.aData = (unsigned char *)malloc(sizeof(unsigned char)*aSize);
        jbyte *jaData = (jbyte *)malloc(sizeof(jbyte)*aSize);
        env->GetByteArrayRegion(aData, 0, aSize, jaData);
        for (int i=0; i<data.aSize; i++) {
            data.aData[i] = (unsigned char)jaData[i];
        }
    }

    // Convert data to array
    unsigned char b[PROTOCOL_MAX_FRAME_SIZE];
    unsigned int len = PROTOCOL_toByteArray(data, b);

    jbyte *jb = (jbyte *)malloc(sizeof(jbyte)*len);

    for (int i=0; i<len; i++) {
        jb[i] = b[i];
    }

    //jbyteArray buf = env->NewByteArray(len);  // allocate
    env->SetByteArrayRegion(jbuf, 0, len, jb);  // copy

    free(jb);

    return len;
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
    env->SetByteField(jdata, env->GetFieldID(jdc, "cmd", "B" ), (jbyte)data.cmd);
    env->SetByteField(jdata, env->GetFieldID(jdc, "type", "B" ), (jbyte)data.type);
    env->SetByteField(jdata, env->GetFieldID(jdc, "bData", "B" ), (jbyte)data.bData);
    env->SetIntField(jdata, env->GetFieldID(jdc, "iData", "I" ), (jint)data.iData);
    env->SetIntField(jdata, env->GetFieldID(jdc, "aSize", "I" ), (jint)data.aSize);

    jbyteArray jaData = env->NewByteArray(data.aSize);

    jbyte *aData = (jbyte *)malloc(sizeof(jbyte)*data.aSize);

    for (int i=0; i<data.aSize; i++) {
        aData[i] = data.aData[i];
    }

    //jbyteArray buf = env->NewByteArray(len);  // allocate
    env->SetByteArrayRegion(jaData, 0, data.aSize, aData);  // copy


    env->SetObjectField(jdata, env->GetFieldID(jdc, "aData", "[B" ), (jbyteArray)jaData);

    return jdata;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_getVersion(JNIEnv *env, jclass jc) {
    return (jbyte)PROTOCOL_VERSION;
}

JNIEXPORT jint JNICALL Java_pro_kornev_kcar_protocol_Protocol_getMaxLength(JNIEnv *env, jclass jc) {
    return (jint)PROTOCOL_MAX_FRAME_SIZE;
}
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_byteType(JNIEnv *e, jclass c) {
    return (jbyte)DATA_TYPE_CHAR;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_intType(JNIEnv *e, jclass c) {
    return (jbyte)DATA_TYPE_INT;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_arrayType(JNIEnv *e, jclass c) {
    return (jbyte)DATA_TYPE_ARRAY;
}

//Commands

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_reservedFirst(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_RESERVED_F;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_reservedLast(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_RESERVED_L;
}
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_copFirst(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_COM_COP_F;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_copLast(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_COM_COP_L;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_autoFirst(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_COM_AUTO_F;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_autoLast(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_COM_AUTO_L;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_error(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_ERROR;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_ping(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_PING;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_camFps(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_CAM_FPS;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_camQuality(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_CAM_QUALITY;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_camState(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_CAM_STATE;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_camImg(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_CAM_IMG;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_camFlash(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_CAM_FLASH;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_camSizeList(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_CAM_SIZE_LIST;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_camSizeSet(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_CAM_SIZE_SET;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_autoTriggerLed(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_TRIGGER_LED;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_autoUsReq(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_DISTANCE_REQ;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_autoUsRes(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_DISTANCE_RES;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_autoLMS(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_LMS;
}

JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_autoRMS(JNIEnv *e, jclass c) {
    return (jbyte)PROTOCOL_CMD_RMS;
}

