
#include "platform/android/jni/JniHelper.h"
#include "AndroidJni.h"

#include "cocos2d.h" 
#define CLASS_NAME "com/tito/crazy/jump/JumpFace"
using namespace cocos2d;

extern "C"
{
		
    void showAdmobJNI(){
		#if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
		 JniMethodInfo t;
		if (!JniHelper::getStaticMethodInfo(t, CLASS_NAME
						,"showAdmobJNI"
						,"()V"))
		{
			return;
		}
		t.env->CallStaticVoidMethod(t.classID,t.methodID);
		#endif

		
    }
    void hideAdmobJNI(){
		#if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
		JniMethodInfo t;
		if (!JniHelper::getStaticMethodInfo(t, CLASS_NAME
			,"hideAdmobJNI"
			,"()V"))
		{
			return;
		}
		t.env->CallStaticVoidMethod(t.classID,t.methodID);
		#endif
	
    }
    void setAdmobVisibleJNI(int number){
		 #if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
		cocos2d::JniMethodInfo t;
		if (cocos2d::JniHelper::getMethodInfo(t, CLASS_NAME
						,"setAdmobVisibleJNI"
						,"(I)V"))
		{
			t.env->CallStaticVoidMethod(t.classID,t.methodID,number);
		}else{
			return;
		}
		#endif
    }
    void setVisibleAdmobJNI(bool visible){
		 #if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
		cocos2d::JniMethodInfo t;
		if (cocos2d::JniHelper::getMethodInfo(t, CLASS_NAME
						,"setVisibleAdmobJNI"
						,"(Z)V"))
		{
			t.env->CallStaticVoidMethod(t.classID,t.methodID,visible);
		}else{
			return;
		}
		#endif
    }
    void setAdmobVisibilityJNI(const char* name){
		 #if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
       cocos2d::JniMethodInfo t;
        if (cocos2d::JniHelper::getMethodInfo(t, CLASS_NAME
                        ,"setAdmobVisibilityJNI"
                        ,"(Ljava/lang/String;)V"))
        {
            jstring StringArg1 = t.env->NewStringUTF(name);
            t.env->CallStaticVoidMethod(t.classID,t.methodID, StringArg1);
        }else{
			return;
		}
		#endif
    }
	
}
