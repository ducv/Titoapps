#include "ScoreBoard.h"
ScoreBoard::ScoreBoard(CCLayer *layer,int screenWidth, int screenHeight, int score = 0, int bestScore = 0){

	startPoint = ccp(screenWidth/2,screenHeight/2+100);
	endPoint = ccp(screenWidth/2,screenHeight/2+20);
	_score = score;
	_bestScore = bestScore;

	this->screenHeight = screenHeight;
	this->screenWidth = screenWidth;
	this->layer = layer;
	this->width = SCORE_PANEL_WIDTH;

	boardSprite = CCSprite::create(RES_SCORE_PANEL);
	float scalex =  SCORE_PANEL_WIDTH/boardSprite->getContentSize().width;

	this->height = SCORE_PANEL_HEIGH;
	float scaley =  SCORE_PANEL_HEIGH/boardSprite->getContentSize().height;

	this->boardSprite->setScaleX(scalex);
	this->boardSprite->setScaleY(scaley);
	boardSprite->setPosition(startPoint);

	float duraDown = 0.5f;
	CCMoveTo* movedown = CCMoveTo::create(duraDown, endPoint);
	CCSpawn* fall = CCSpawn::create(CCEaseSineIn::create(movedown), NULL);
	/*CCSequence* tap = CCSequence::create(fall,CCCallFuncN::create(layer, callfuncN_selector(ScoreBoard::finishActionCallBack)), NULL);*/
	CCSequence* tap = CCSequence::create(fall, NULL);
	inAction = tap;
	
	addLabelScoreBest();
}

void ScoreBoard::draw(){
	this->layer->addChild(boardSprite);
}

void ScoreBoard::startInAction(){
	boardSprite->runAction(inAction);
}

void ScoreBoard::addLabelScoreBest()
{
	CCString* strp = CCString::createWithFormat("%d", _score);
	strp->autorelease();
	lbscore = CCLabelTTF::create(strp->getCString(), RES_FONT_NAME, 30);
	lbscore->setZOrder(this->layer->getChildrenCount()); //font of all
	lbscore->CCNode::setPosition(screenWidth / 2 + SCORE_PANEL_WIDTH/4-15, boardSprite->getPositionY() - 45 * height / 100);
	lbscore->setColor(ccc3(0,0,0));
	this->layer->addChild(lbscore);

	CCString* strBestScore = CCString::createWithFormat("%d", _bestScore);
	strBestScore->autorelease();
	lbbest = CCLabelTTF::create(strBestScore->getCString(), RES_FONT_NAME, 30);
	lbbest->setZOrder(this->layer->getChildrenCount()); //font of all
	lbbest->CCNode::setPosition(screenWidth / 2 + SCORE_PANEL_WIDTH / 4-15, boardSprite->getPositionY() - 90 * height / 100);
	lbbest->setColor(ccc3(0,0,0));
	this->layer->addChild(lbbest);
	CCString* medalStr;
	if (_score >= _bestScore&&_score>0)
	{
		medalStr = CCString::create(MEDAL_GOLD);
	}
	else{
		medalStr = CCString::create(MEDAL_SILVER);
	}
	
	medalStr->autorelease();
	medal = CCLabelTTF::create(medalStr->getCString(), RES_FONT_NAME, 30);
	medal->setZOrder(this->layer->getChildrenCount()); //font of all
	medal->CCNode::setPosition(screenWidth / 2 - SCORE_PANEL_WIDTH / 4+10, boardSprite->getPositionY() - 45 * height / 100);
	medal->setColor(ccc3(0, 0, 0));
	this->layer->addChild(medal);

}
void ScoreBoard::finishActionCallBack(CCObject* pSender){
	/*lbscore->setVisible(true);
	lbbest->setVisible(true);*/
}
