#ifndef __HELLOWORLD_SCENE_H__
#define __HELLOWORLD_SCENE_H__

#include "cocos2d.h"
#include "Bird.h"
#include "Ground.h"
#include "Walk.h"
#include "Constant.h"
#include "ScoreBoard.h"


class HelloWorld : public cocos2d::CCLayer
{
private:
	Bird *bird;
	CCSprite *m_bgSprite;
	CCSprite *m_birdSprite;
	Ground *ground;
	Walk *walk[3];
	float screenHeight, screenWidth;
	int gameState ;
	CCSprite *guide;
	CCSprite *readyText;
	CCSprite *gameOverText;
	
	CCLabelTTF* lbscore;
	bool isGameOver;
	CCMenuItemImage *playButton, *highScoreButton;
	CCMenu* pMenu;
	ScoreBoard* scoreBoard;
	CCMenu* soundMenu;
	CCMenuItemToggle* soundButton;
			 
public:
    // Here's a difference. Method 'init' in cocos2d-x returns bool, instead of returning 'id' in cocos2d-iphone
    virtual bool init();  

    // there's no 'id' in cpp, so we recommend returning the class instance pointer
    static cocos2d::CCScene* scene();
      
	void initSprite();
	void ccTouchesEnded(cocos2d::CCSet* touches, cocos2d::CCEvent* event);
	void playButtonCallback(CCObject* pSender);
	void highScoreButtonCallback(CCObject* pSender);
	void soundButtonCallback(CCObject* pSender);

	void update(float dt);
	void drawBackground(CCSprite sprite);
	void drawGround();
	void drawWalk(int delta);
	void initWalk();
	void initBird();
	void getReady();

	/// menu
	void drawReady();
	void getPlaying();
	void drawGameOver();
	void checkCollision();
	void getGameOver();
	void draw(CCRect rect);
	void addLabelScore();
	void updateUiScore();
	void restartNewGame();
	virtual void keyBackClicked(void);

	void playSound(const char *mFile);
	bool isSound;
	void addSoundButton();

	void showAd();
	void hideAd();

	void takeScreenShot();
	void submidScore(long score);


    // implement the "static node()" method manually
    CREATE_FUNC(HelloWorld);

};

#endif // __HELLOWORLD_SCENE_H__
