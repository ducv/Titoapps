#ifndef _SCORE_BOARD_H_
#define _SCORE_BOARD_H_

#include "cocos2d.h"
#include "Constant.h"


USING_NS_CC;
class ScoreBoard {
private:
	CCAction* inAction;
	CCSprite *boardSprite;
	int screenWidth;
	int screenHeight;
	CCLayer *layer;
	int height;
	int width;
	CCPoint startPoint, endPoint;
	CCLabelTTF* lbscore;
	CCLabelTTF* lbbest;
	CCLabelTTF* medal;

	int _score, _bestScore;

private:
	void addLabelScoreBest();

public:
	ScoreBoard(CCLayer *layer,int screenWidth, int screenHeight, int score, int bestScore);
	~ScoreBoard();
	void draw();
	void startInAction();
	void finishActionCallBack(CCObject* pSender);
};


#endif