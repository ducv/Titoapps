#ifndef _BIRD_H_
#define _BIRD_H_

#include "cocos2d.h"
#include "Constant.h"
#include "Walk.h"

USING_NS_CC;


class Bird
{
private:
	CCSprite *m_sprite;
	int state;//ready, flying, free falling
	CCAction* curAction;
	int time;
	int score;
	CCAnimate* flappAction; 
	CCLayer* layer;

public:
	Bird(CCLayer* layer);
	virtual ~Bird(void);
public:

	void setPosition(CCPoint ccpoint){
		m_sprite->setPosition(ccpoint);
	};
	void getReady(int time);
public:
	  void setSprite(CCSprite *m_sprite){
		  this->m_sprite = m_sprite;
		  float scale =  BIRD_WIDTH/m_sprite->getContentSize().width;
		  this->m_sprite->setScale(scale);
		  this->m_sprite->setPosition(ccp(BIRD_X,BIRD_Y));
		  this->m_sprite->setAnchorPoint(ccp(0.5f,0.5f));
	  };
	  CCSprite *getSprite(){
		  return m_sprite;
	  }

	  void setState(int state){this->state = state;};
	  int getState(){return state;};
	  bool checkCollision(Walk *walk);
	  float getDuraDown(CCPoint up, CCPoint down);
	  void actionTap();
	  void stopAction();
	  CCPoint getWorldPosition(CCSprite *pipe);
	  bool isCollision(CCSprite *pipe);

	  bool isGround();

	  bool updateScore(Walk *walk);
	  int getScore(){
		  return score;
	  };
	  void setScore(int score){
		  this->score = score;
	  }
	   void touchGroundCallBack(CCObject* pSender);
};

#endif

