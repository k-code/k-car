/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class pro_kornev_kcar_protocol_Protocol */

#ifndef _Included_pro_kornev_kcar_protocol_Protocol
#define _Included_pro_kornev_kcar_protocol_Protocol
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     pro_kornev_kcar_protocol_Protocol
 * Method:    toByteArray
 * Signature: (Lpro/kornev/kcar/protocol/Data;[B)I
 */
JNIEXPORT jint JNICALL Java_pro_kornev_kcar_protocol_Protocol_toByteArray
  (JNIEnv *, jclass, jobject, jbyteArray);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol
 * Method:    fromByteArray
 * Signature: ([BI)Lpro/kornev/kcar/protocol/Data;
 */
JNIEXPORT jobject JNICALL Java_pro_kornev_kcar_protocol_Protocol_fromByteArray
  (JNIEnv *, jclass, jbyteArray, jint);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol
 * Method:    getVersion
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_getVersion
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol
 * Method:    getMaxLength
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_pro_kornev_kcar_protocol_Protocol_getMaxLength
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol
 * Method:    byteType
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_byteType
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol
 * Method:    intType
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_intType
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol
 * Method:    arrayType
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_arrayType
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
/* Header for class pro_kornev_kcar_protocol_Protocol_Cmd */

#ifndef _Included_pro_kornev_kcar_protocol_Protocol_Cmd
#define _Included_pro_kornev_kcar_protocol_Protocol_Cmd
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    reservedFirst
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_reservedFirst
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    reservedLast
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_reservedLast
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    copFirst
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_copFirst
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    copLast
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_copLast
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    autoFirst
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_autoFirst
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    autoLast
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_autoLast
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    error
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_error
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    ping
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_ping
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    camState
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_camState
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    camImg
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_camImg
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    camFps
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_camFps
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    camQuality
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_camQuality
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    camFlash
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_camFlash
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    camSizeList
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_camSizeList
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    camSizeSet
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_camSizeSet
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    sensLight
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_sensLight
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    sensAxis
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_sensAxis
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    sensMagnetic
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_sensMagnetic
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    sensGps
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_sensGps
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    autoTriggerLed
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_autoTriggerLed
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    autoUsReq
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_autoUsReq
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    autoUsRes
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_autoUsRes
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    autoLMS
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_autoLMS
  (JNIEnv *, jclass);

/*
 * Class:     pro_kornev_kcar_protocol_Protocol_Cmd
 * Method:    autoRMS
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_pro_kornev_kcar_protocol_Protocol_00024Cmd_autoRMS
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
