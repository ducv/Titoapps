#ifndef __MENU_SCENE_H__
#define __MENU_SCENE_H__

#include "cocos2d.h"
#include "Bird.h"
#include "Ground.h"
#include "Walk.h"
#include "Constant.h"

USING_NS_CC;
class MenuLayer: public cocos2d::CCLayerColor 

{
public:
	void gotoGamePlay();
	void fadeOut(float dt);
	virtual void onEnter();  
	void initSprite();
	void playButtonCallback(CCObject* pSender);
	void highScoreButtonCallback(CCObject* pSender);
	void drawGround();
	void update(float dt);
	// there's no 'id' in cpp, so we recommend returning the class instance pointer
	static cocos2d::CCScene* scene();
	virtual void keyBackClicked(void);

protected:
private:
	CCSprite *m_bgSprite;
	Ground *ground;
	CCMenu* pMenu;
	float screenHeight, screenWidth;
	CCMenuItemImage *playButton, *highScoreButton;
	CCSprite *jumpFaceText;
	CCLabelTTF *name;
	void addName();
public:
	CREATE_FUNC(MenuLayer);

};
#endif // __MENU_SCENE_H__