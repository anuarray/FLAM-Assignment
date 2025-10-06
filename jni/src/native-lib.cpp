#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_edgeviewer_MainActivity_stringFromJNI(
	JNIEnv* env,
	jobject /* this */) {
	std::string hello = "NDK ready";
	return env->NewStringUTF(hello.c_str());
}
