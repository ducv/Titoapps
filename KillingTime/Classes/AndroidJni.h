#ifndef __ANDROID_ADMOB_JNI_H__
#define __ANDROID_ADMOB_JNI_H__
#include <jni.h>
extern "C"
{
    extern void showAdmobJNI();
    extern void hideAdmobJNI();
    extern void setAdmobVisibleJNI(int number);
    extern void setVisibleAdmobJNI(bool visible);
    extern void setAdmobVisibilityJNI(const char* name);
	 extern void showWeb();
	
	
}
#endif