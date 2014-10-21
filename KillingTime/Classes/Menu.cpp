#include "Menu.h"
#include "HelloWorldScene.h"
#include "Constant.h"
#include "NativeUtils.h"
#include "PlayGameConstants.h"
USING_NS_CC;

CCScene* MenuLayer::scene()
{
	// 'scene' is an autorelease object
	CCScene *scene = CCScene::create();

	// 'layer' is an autorelease object
	MenuLayer *layer1 = MenuLayer::create();
	// add layer as a child to scene
	scene->addChild(layer1);
	// return the scene
	return scene;
}
void MenuLayer::gotoGamePlay(){
	this->scheduleOnce(schedule_selector(MenuLayer::fadeOut),0.2f);
	
}
void MenuLayer::fadeOut(float dt){
	CCDirector::sharedDirector()->replaceScene(CCTransitionFade::create(0.5f,HelloWorld::scene()));
}
void MenuLayer::playButtonCallback(CCObject* pSender)
{
	gotoGamePlay();

}

void MenuLayer::highScoreButtonCallback(CCObject* pSender)
{
	NativeUtils::showLeaderboard(LEADERBOARD_EASY_MODE);
	
}


void MenuLayer::onEnter(){
	/*bool bRet = false;*/
	CCLayer::onEnter();
	do 
	{
		CC_BREAK_IF(!CCLayer::init()  );
		this->setTouchEnabled(true);
		CCSize winSize = CCDirector::sharedDirector()->getWinSize();
		CCSize visibleSize = CCDirector::sharedDirector()->getVisibleSize();
		screenHeight = visibleSize.height;
		screenWidth = visibleSize.width;
		/*bRet = true;*/
		initSprite();
		drawGround();
		this->schedule(schedule_selector(MenuLayer::update));
		this->setKeypadEnabled(true);
	} while (0);

	/*return bRet;*/
}
void MenuLayer::update(float dt){
	ground->update(1);
}
void MenuLayer::initSprite(){
	playButton = CCMenuItemImage::create(
		RES_PLAY_BUTTON,
		RES_PLAY_BUTTON,
		this,
		menu_selector(MenuLayer::playButtonCallback));

	highScoreButton = CCMenuItemImage::create(
		RES_HIGHT_BUTTON,
		RES_HIGHT_BUTTON,
		this,
		menu_selector(MenuLayer::highScoreButtonCallback));

	playButton->setScaleX(BUTTON_WIDTH/playButton->getContentSize().width);
	playButton->setScaleY(BUTTON_HEIGH/playButton->getContentSize().height);
	highScoreButton->setScaleX(BUTTON_WIDTH/highScoreButton->getContentSize().width);
	highScoreButton->setScaleY(BUTTON_HEIGH/highScoreButton->getContentSize().height);
	playButton->setPosition(ccp(screenWidth/4, GROUND_HEIGHT+BUTTON_HEIGH/2+5));
	highScoreButton->setPosition(ccp(3*screenWidth/4, GROUND_HEIGHT+BUTTON_HEIGH/2+5));

	// create menu, it's an autorelease object
	pMenu = CCMenu::create(playButton,highScoreButton, NULL);
	pMenu->setPosition(CCPointZero);
	this->addChild(pMenu, 1);

	CCSize visibleSize = CCDirector::sharedDirector()->getVisibleSize();
	CCPoint origin = CCDirector::sharedDirector()->getVisibleOrigin();

	jumpFaceText = CCSprite::create(RES_JUMP_FACE);
	jumpFaceText->setPosition(ccp(screenWidth/2,TEXT_Y));
	

}
void MenuLayer::drawGround(){
	CCSize visibleSize = CCDirector::sharedDirector()->getVisibleSize();
	m_bgSprite = CCSprite::create(RES_BACKGROUND);
	m_bgSprite->setScaleX(visibleSize.width/m_bgSprite->getContentSize().width);
	m_bgSprite->setScaleY(visibleSize.height/m_bgSprite->getContentSize().height);
	m_bgSprite->setPosition(ccp(visibleSize.width/2,visibleSize.height/2));
	this->addChild(m_bgSprite);
	ground = new Ground();
	ground->addToScene(this,visibleSize);
	ground->setState(GROUND_MOVING);
	//this->addChild(jumpFaceText);
	addName();
}

void MenuLayer::addName(){
	CCString *nameStr = CCString::create("Killing Time");
	name = CCLabelTTF::create(nameStr->getCString(), RES_FONT_NAME, 45);
	name->setZOrder(getChildrenCount()); //font of all
	name->setColor(ccc3(255, 255, 255));
	name->CCNode::setPosition(screenWidth/2, TEXT_Y);

	this->addChild(name);
}
void MenuLayer::keyBackClicked(void){
	#if (CC_TARGET_PLATFORM == CC_PLATFORM_WINRT) || (CC_TARGET_PLATFORM == CC_PLATFORM_WP8)
	CCMessageBox("You pressed the close button. Windows Store Apps do not implement a close button.","Alert");
#else
	NativeUtils::exitConfirm();
#if (CC_TARGET_PLATFORM == CC_PLATFORM_IOS)
    exit(0);
#endif
#endif
}