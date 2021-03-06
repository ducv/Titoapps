LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := cocos2dcpp_shared

LOCAL_MODULE_FILENAME := libjumpface

LOCAL_SRC_FILES := hellocpp/main.cpp \
                   ../../Classes/AppDelegate.cpp \
                   ../../Classes/HelloWorldScene.cpp\
					../../Classes/Bird.cpp\
					../../Classes/Ground.cpp\
					../../Classes/Walk.cpp\
					../../Classes/Menu.cpp\
					../../Classes/ScoreBoard.cpp\
					../../Classes/AndroidJni.cpp\
					../../Classes/NativeUtils.cpp\
					../../Classes/JniHelpers.cpp\
					../../Classes/Splash.cpp

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../Classes
					

LOCAL_WHOLE_STATIC_LIBRARIES += cocos2dx_static
LOCAL_WHOLE_STATIC_LIBRARIES += cocosdenshion_static
LOCAL_WHOLE_STATIC_LIBRARIES += cocos_extension_static
LOCAL_WHOLE_STATIC_LIBRARIES += cocos_curl_static


include $(BUILD_SHARED_LIBRARY)

$(call import-module,cocos2dx)
$(call import-module,CocosDenshion/android)
$(call import-module,extensions)
$(call import-module,cocos2dx/platform/third_party/android/prebuilt/libcurl)

