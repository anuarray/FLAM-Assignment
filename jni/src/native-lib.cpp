#include <jni.h>
#include <vector>
#include "edge_processor.h"

static jbyteArray toJByteArray(JNIEnv* env, const ImageBuffer& buf) {
	jbyteArray arr = env->NewByteArray(static_cast<jsize>(buf.bytes.size()));
	if (!arr) return nullptr;
	env->SetByteArrayRegion(arr, 0, static_cast<jsize>(buf.bytes.size()), reinterpret_cast<const jbyte*>(buf.bytes.data()));
	return arr;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_example_edgeviewer_NativeBridge_processRgba(
	JNIEnv* env,
	jclass /*clazz*/,
	jbyteArray rgba,
	jint width,
	jint height,
	jint stride,
	jint mode) {
	jboolean isCopy = JNI_FALSE;
	jbyte* data = env->GetByteArrayElements(rgba, &isCopy);
	if (!data) return nullptr;
	ImageView view{ reinterpret_cast<const uint8_t*>(data), width, height, stride, 4 };
	ImageBuffer out = processFrameRGBA(view, static_cast<EdgeMode>(mode));
	env->ReleaseByteArrayElements(rgba, data, JNI_ABORT);
	return toJByteArray(env, out);
}
