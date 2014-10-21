#ifndef __SPLASH_SCENE_H__
#define __SPLASH_SCENE_H__


#include "cocos2d.h"

USING_NS_CC;

class  Splash :public cocos2d::CCLayer
{
public:
	static cocos2d::CCScene* scene();
	void initSprite();
	void transition();
	void finishSplash(float dt);

private:
	CCLayerColor *backGround;
	CCSprite *logo; 
protected:
	  virtual bool init();  
public:
	CREATE_FUNC(Splash);
};

 


#endif // __SPLASH_SCENE_H__