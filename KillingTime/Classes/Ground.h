#ifndef _GROUND_H_
#define _GROUND_H_

#include "cocos2d.h"
#include "Constant.h"

USING_NS_CC;
class Ground
{
private:
	CCSprite *sprite1; 
	//CCSprite *sprite2;
	float width;
	int state;//ready, moving
public:
	Ground();
	~Ground();
	void update(int delta);
	
	void addToScene(CCLayer *layer,CCSize visibleSize){
		width = visibleSize.width;
	this->sprite1 = CCSprite::create(RES_GROUND);
	this->sprite1->setScaleX(visibleSize.width/sprite1->getContentSize().width);
	this->sprite1->setScaleY(GROUND_HEIGHT/sprite1->getContentSize().height);
	this->sprite1->setPositionY(GROUND_HEIGHT/2);
	this->sprite1->setPositionX(width/2);

	
	/*this->sprite2 = CCSprite::create(RES_GROUND);
	this->sprite2->setScaleX(visibleSize.width/sprite2->getContentSize().width);
	this->sprite2->setScaleY(GROUND_HEIGHT/sprite2->getContentSize().height);
	this->sprite2->setPositionY(GROUND_HEIGHT/2);
	this->sprite2->setPositionX(sprite1->getPositionX()+width);*/

	layer->addChild(sprite1);
	//layer->addChild(sprite2);
	};
	void setState(int state){this->state = state;};
	int getState(){return state;};
	
private:

};
#endif
