#include "Splash.h"
#include "Constant.h"
#include "Menu.h"

USING_NS_CC;

CCScene* Splash::scene()
{
	// 'scene' is an autorelease object
	CCScene *scene = CCScene::create();

	// 'layer' is an autorelease object
	Splash *layer = Splash::create();

	// add layer as a child to scene
	scene->addChild(layer);

	// return the scene
	return scene;
}
bool Splash::init(){
	if ( !CCLayer::init() )
	{
		return false;
	}
	initSprite();
	this->addChild(backGround);
	this->addChild(logo);
	transition();
	return true;
}
void Splash::transition(){
	this->scheduleOnce(schedule_selector(Splash::finishSplash),1.0f);
}
void Splash::finishSplash(float dt){
	CCDirector::sharedDirector()->replaceScene(CCTransitionFade::create(1.0f,MenuLayer::scene()));
}
void Splash::initSprite(){
	CCSize visibleSize = CCDirector::sharedDirector()->getVisibleSize();
	CCPoint origin = CCDirector::sharedDirector()->getVisibleOrigin();
	backGround = new CCLayerColor();
	backGround->setScaleX(visibleSize.width);
	backGround->setScaleY(visibleSize.height);
	backGround->initWithColor(ccc4(255,0,0,0));
	logo = CCSprite::create(RES_LOGO);
	logo->setPosition(ccp(visibleSize.width/2,visibleSize.height/2));
}
