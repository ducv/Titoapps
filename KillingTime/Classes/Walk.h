#ifndef _WALK_H_
#define _WALK_H_

#include "cocos2d.h"
#include "Constant.h"

USING_NS_CC;


class Walk
{
public:
	Walk();
	~Walk();
private:
	CCSprite *topSprite;
	CCSprite *bottomSprite;
	float x, y;
	float height, width;
	int state;//ready, moving
	bool isScored ;
public:
	void setX(float x)
	{this->x = x;
	this->bottomSprite->setPositionX(x+width/2);
	this->topSprite->setPositionX(x+width/2);};
	float getX(){return x;};
	void setY(float y){this->y = y;
	this->bottomSprite->setPositionY(y+height/2);
	this->topSprite->setPositionY(y+3*height/2+SPACE_MIN+(rand() % SPACE_RANGE));
	};
	float getY(){return y;};
	float getWidth(){return width;};
	float getHeigh(){return height;};
	void addToScene(CCLayer *layer){
		layer->addChild(topSprite);
		layer->addChild(bottomSprite);
	}
	void init(){
		this->bottomSprite =CCSprite::create(RES_WALK);
		float scale =  WALK_WIDTH/bottomSprite->getContentSize().width;
		height = bottomSprite->getContentSize().height *scale;
		width = WALK_WIDTH;
		this->bottomSprite->setScale(scale);
		this->bottomSprite->setPositionX(x+width/2);
		this->bottomSprite->setPositionY(y+height/2);

		this->topSprite =CCSprite::create(RES_WALK);
		this->topSprite->setScale(scale);
		this->topSprite->setPositionX(x+width/2);
		this->topSprite->setPositionY(y+3*height/2+SPACE_MIN+(rand() % SPACE_RANGE));

			
	}
	void setXY(float x, float y){
		this->x = x;
		this->y=y;
		this->bottomSprite->setPositionX(x+width/2);
		this->bottomSprite->setPositionY(y+height/2);
		this->topSprite->setPositionX(x+width/2);
		this->topSprite->setPositionY(y+3*height/2+SPACE_MIN+(rand() % SPACE_RANGE));
	}
	void update(int delta);
	void setState(int state){this->state = state;};
	int getState(){return state;};

	CCSprite *getTopSprite(){
		return topSprite;
	};
	CCSprite *getBottomSprite(){
		return bottomSprite;
	};


	bool getIsScored(){
		return isScored;
	};
	void setIsScored(bool isScored){
		this->isScored = isScored;
	}
};
#endif

