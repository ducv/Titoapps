#include "HelloWorldScene.h"
#include "SimpleAudioEngine.h" 
#include "LocalStorage.h"
#include "Menu.h"
#include "NativeUtils.h"
#include "PlayGameConstants.h"

USING_NS_CC;

//const char* highScore = "high_score";

CCScene* HelloWorld::scene()
{
    // 'scene' is an autorelease object
    CCScene *scene = CCScene::create();
    
    // 'layer' is an autorelease object
    HelloWorld *layer = HelloWorld::create();

    // add layer as a child to scene
    scene->addChild(layer);

    // return the scene
    return scene;
}

// on "init" you need to initialize your instance
bool HelloWorld::init()
{
    //////////////////////////////
    // 1. super init first
    if ( !CCLayer::init() )
    {
        return false;
    }
    
    CCSize visibleSize = CCDirector::sharedDirector()->getVisibleSize();
	CCPoint origin = CCDirector::sharedDirector()->getVisibleOrigin();
	CocosDenshion::SimpleAudioEngine::sharedEngine()->preloadEffect(SOUND_HIT);
	CocosDenshion::SimpleAudioEngine::sharedEngine()->preloadEffect(SOUND_DIE);
	CocosDenshion::SimpleAudioEngine::sharedEngine()->preloadEffect(SOUND_WING);
	CocosDenshion::SimpleAudioEngine::sharedEngine()->preloadEffect(SOUND_SWOOSHING);
	CocosDenshion::SimpleAudioEngine::sharedEngine()->preloadEffect(SOUND_POINT);
	isSound = true;
	screenHeight = visibleSize.height;
	screenWidth = visibleSize.width;
	gameState = GAME_STATE_WATTING;
	restartNewGame();
	this->setTouchEnabled(true);
	this->schedule( schedule_selector(HelloWorld::update) ); 
	localStorageInit("score.db");
	this->setKeypadEnabled(true);
	

    return true;
}
void HelloWorld::keyBackClicked(void){
	#if (CC_TARGET_PLATFORM == CC_PLATFORM_WINRT) || (CC_TARGET_PLATFORM == CC_PLATFORM_WP8)
	CCMessageBox("You pressed the close button. Windows Store Apps do not implement a close button.","Alert");
#else
	CCDirector::sharedDirector()->replaceScene(CCTransitionFade::create(1.0f, MenuLayer::scene()));
#if (CC_TARGET_PLATFORM == CC_PLATFORM_IOS)
    exit(0);
#endif
#endif
}
void HelloWorld::initBird(){
	bird = new Bird(this);
	bird->setSprite(m_birdSprite);
	bird->setState(BIRD_READY);
	addChild( bird->getSprite() );
	bird->setScore(false);

}
void HelloWorld::initWalk(){
	for (int i = 0; i<3;i++)
	{
		walk[i] = new Walk();
		walk[i]->init();
		walk[i]->addToScene(this);
		walk[i]->setState(WALK_READY);
		walk[i]->setIsScored(false);

	}

	for (int i = 0; i < 3; i++)
	{
		if (i==0)
		{
			walk[i]->setXY(screenWidth+SPACE_B_2_WALK, GROUND_HEIGHT+MIN_WALL-walk[i]->getHeigh()+rand() % (SCREEN_HEIGHT-2*GROUND_HEIGHT-MAX_WALL) );
		}else
		{
			walk[i]->setXY(walk[i-1]->getX()+SPACE_B_2_WALK, GROUND_HEIGHT+MIN_WALL-walk[i]->getHeigh()+rand() %(SCREEN_HEIGHT-2*GROUND_HEIGHT-MAX_WALL)  );
		}

	}
}
void HelloWorld::initSprite(){
	CCSize visibleSize = CCDirector::sharedDirector()->getVisibleSize();
	CCPoint origin = CCDirector::sharedDirector()->getVisibleOrigin();

	m_bgSprite = CCSprite::create(RES_BACKGROUND);
	m_birdSprite = CCSprite::create(RES_FACE);
	m_bgSprite->setScaleX(visibleSize.width/m_bgSprite->getContentSize().width);
	m_bgSprite->setScaleY(visibleSize.height/m_bgSprite->getContentSize().height);
	m_bgSprite->setPosition(ccp(visibleSize.width/2,visibleSize.height/2));

	guide = CCSprite::create(RES_GUIDE);
	guide->setPosition(ccp(screenWidth/2,BIRD_Y));
	readyText = CCSprite::create(RES_READY);
	readyText->setPosition(ccp(screenWidth/2,TEXT_Y));
}

void HelloWorld::playSound(const char *mFile){
	if (isSound){
		CocosDenshion::SimpleAudioEngine::sharedEngine()->playEffect(mFile, false);
	}
}

void HelloWorld::ccTouchesEnded(CCSet* touches, CCEvent* event){
	if (bird->getState()==BIRD_READY)
	{
		getPlaying();
	}
	if ((bird->getState() == BIRD_FLYING) )
	{
		bird->actionTap();
		playSound(SOUND_WING);
	}

	return;

}
void HelloWorld::drawGround(){
	CCSize visibleSize = CCDirector::sharedDirector()->getVisibleSize();
	ground = new Ground();
	ground->addToScene(this,visibleSize);
	//ground->setState(GROUND_MOVING);
}


void HelloWorld::drawWalk(int delta){
	for (int i = 0; i < 3; i++)
	{
		walk[i]->update(delta);
		if (walk[i]->getX() + walk[i]->getWidth()<0)
		{
			if (i==0)
			{
				walk[i]->setXY(walk[2]->getX()+SPACE_B_2_WALK, GROUND_HEIGHT+MIN_WALL-walk[i]->getHeigh()+rand() %(SCREEN_HEIGHT-2*GROUND_HEIGHT-MAX_WALL));
			}else
			{
				walk[i]->setXY(walk[i-1]->getX()+SPACE_B_2_WALK, GROUND_HEIGHT+MIN_WALL-walk[i]->getHeigh()+rand() %(SCREEN_HEIGHT-2*GROUND_HEIGHT-MAX_WALL) );
			}
			walk[i]->setIsScored(false);
		}
	}

}
void HelloWorld::getGameOver(){
	bird->setState(BIRD_FALLING);
	for (int i = 0; i < 3; i++)
	{
		walk[i]->setState(WALK_READY);
	}
	ground->setState(GROUND_READY);
	showAd();
}
void HelloWorld::getPlaying(){
	bird->setState(BIRD_FLYING);
	for (int i = 0; i < 3; i++)
	{
		walk[i]->setState(WALK_MOVING);
	}
	guide->setVisible(false);
	readyText->setVisible(false);
	hideAd();
}


void HelloWorld::checkCollision(){
	for (int i = 0; i < 3; i++)
	{
		if (bird->checkCollision(walk[i]) || bird->isGround())
		{
			getGameOver();
			playSound(SOUND_HIT);
			break;
		}
	}
}
void HelloWorld::update(float dt)
{
	//ground->update(STEP);
	drawWalk(STEP);
	bird->getReady(STEP);
	if (bird->getState() == BIRD_FLYING)
	{
		checkCollision();
	}
	for (int i = 0; i < 3; i++)
	{
		if (bird->updateScore(walk[i]))
		{
			playSound(SOUND_POINT);
		}
	}
	if (bird->getState()==BIRD_FALLING)
	{
		if (!isGameOver){
			isGameOver = true;
			const char* hScore = localStorageGetItem(HIGH_SCORE);

			if (hScore != NULL) {
				unsigned int i = atoi(hScore);
				if (i < bird->getScore()) {
					CCString* strp = CCString::createWithFormat("%d", bird->getScore());
					strp->autorelease();
					localStorageSetItem(HIGH_SCORE, strp->getCString());
				}
			} else {
				CCString* strp = CCString::createWithFormat("%d", bird->getScore());
				strp->autorelease();
				localStorageSetItem(HIGH_SCORE, strp->getCString());
			}

			CCLOG ("your score: %d ",bird->getScore());
			drawGameOver();
		}
	}
	updateUiScore();
}   
void HelloWorld::restartNewGame(){
	this->removeAllChildrenWithCleanup(true);
	isGameOver = false;
	initSprite();
	this->addChild(m_bgSprite);
	initWalk();
	drawGround();
	this->addChild(guide);
	this->addChild(readyText);
	initBird();
	addLabelScore();
	addSoundButton();
	
}
void HelloWorld::updateUiScore()
{
	CCString* strp = CCString::createWithFormat("%d", bird->getScore());
	lbscore->setString(strp->getCString());
}
void HelloWorld::addLabelScore(){
	CCString* strp = CCString::createWithFormat("%d", bird->getScore());

	lbscore = CCLabelTTF::create(strp->getCString(), RES_FONT_NAME, 40);
	lbscore->setZOrder(getChildrenCount()); //font of all
	lbscore->CCNode::setPosition(screenWidth/2, SCREEN_HEIGHT - 50);

	this->addChild(lbscore);
}
void HelloWorld::drawGameOver(){
	
	playButton = CCMenuItemImage::create(
		RES_PLAY_BUTTON,
		RES_PLAY_BUTTON,
		this,
		menu_selector(HelloWorld::playButtonCallback));

	highScoreButton = CCMenuItemImage::create(
		RES_HIGHT_BUTTON,
		RES_HIGHT_BUTTON,
		this,
		menu_selector(HelloWorld::highScoreButtonCallback));

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

	int highScore = 0;
	if (localStorageGetItem(HIGH_SCORE) != NULL) {
		highScore = atoi(localStorageGetItem(HIGH_SCORE));
		CCLOG("HIGH SCORE: %s", localStorageGetItem(HIGH_SCORE));
	} else {
		CCLOG("%s", "high score null");
	}
	gameOverText = CCSprite::create(RES_GAME_OVER);
	gameOverText->setPosition(ccp(screenWidth/2,SCREEN_HEIGHT - 70));
	lbscore->setVisible(false);
	this->addChild(gameOverText);
	scoreBoard = new ScoreBoard(this, screenWidth, screenHeight, bird->getScore(), highScore);
	scoreBoard->draw();
	scoreBoard->startInAction();
	submidScore(bird->getScore());
	
}
void HelloWorld::addSoundButton(){
	CCMenuItemSprite* p1 = CCMenuItemSprite::create(CCSprite::create(RES_SOUND_BUTTON_ON), CCSprite::create(RES_SOUND_BUTTON_OFF));
	CCMenuItemSprite* p2 = CCMenuItemSprite::create(CCSprite::create(RES_SOUND_BUTTON_OFF), CCSprite::create(RES_SOUND_BUTTON_ON));
	soundButton = CCMenuItemToggle::createWithTarget(this, menu_selector(HelloWorld::soundButtonCallback), p1, p2, NULL);
	soundMenu = CCMenu::create();
	soundMenu->setPosition(CCPointZero);
	this->addChild(soundMenu, 1);
	soundButton->setScaleX(SOUND_BUTTON_WIDTH / soundButton->getContentSize().width);
	soundButton->setScaleY(SOUND_BUTTON_HEIGHT / soundButton->getContentSize().height);
	soundButton->setPosition(ccp(screenWidth - SOUND_BUTTON_WIDTH, screenHeight - SOUND_BUTTON_HEIGHT));
	soundMenu->addChild(soundButton);
	if (isSound){
		soundButton->setSelectedIndex(0);
	}
	else{
		soundButton->setSelectedIndex(1);
	}

}
void HelloWorld::playButtonCallback(CCObject* pSender)
{
	restartNewGame();
}
void HelloWorld::highScoreButtonCallback(CCObject* pSender)
{
	NativeUtils::showLeaderboard(LEADERBOARD_EASY_MODE);
}

void HelloWorld::soundButtonCallback(CCObject* pSender)
{
	if (soundButton->getSelectedIndex()==0)
	{
		isSound = true;
	}
	else{
		isSound = false;
	}

}
void HelloWorld::showAd(){
	NativeUtils::showAdJni();
}

void HelloWorld::hideAd(){
	NativeUtils::hideAdJni();
}

void HelloWorld::submidScore(long score){
	NativeUtils::submitScore(LEADERBOARD_EASY_MODE,score);
}
void HelloWorld::takeScreenShot()
{
	CCSize size = CCDirector::sharedDirector()->getWinSize();
	CCRenderTexture* texture = CCRenderTexture::create((int)size.width, (int)size.height, kCCTexture2DPixelFormat_RGBA8888);
	texture->setPosition(ccp(size.width / 2, size.height / 2));
	texture->begin();
	CCDirector::sharedDirector()->getRunningScene()->visit();
	texture->end();
	bool result = texture->saveToFile("screenshot.png", kCCImageFormatPNG);
}

