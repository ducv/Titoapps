#include "Bird.h"
Bird::Bird(CCLayer* layer)
{
	time = 0;
	curAction = NULL;
	score = 0;
	this->layer = layer;
}
Bird::~Bird(void)
{
}


void Bird::getReady(int time){
	if (state==BIRD_READY)
	{
		this->time ++;
		float y = BIRD_Y+BIRD_APLITUDE* sin(this->time/5);
		m_sprite->setPositionY(y);
	}
	else{
		time = 0;
	}
}
bool Bird::checkCollision(Walk *walk){
	return (isCollision(walk->getBottomSprite())||isCollision(walk->getTopSprite()));
	
}
bool Bird::isGround(){
	if(m_sprite->getPosition().y - BIRD_WIDTH/2-2<=GROUND_HEIGHT){
		return true;
	}else{
		return false;
	}
}
void Bird::actionTap(){
	stopAction();
	float jumpDuration = 0;
	CCPoint uppos = m_sprite->getPosition();
	if (uppos.y + BIRD_JUMP <= SCREEN_HEIGHT){
		uppos.y += BIRD_JUMP;
		jumpDuration = BIRD_JUMP_DURATION;
	}
	else{
		jumpDuration = ((SCREEN_HEIGHT - uppos.y)*BIRD_JUMP_DURATION) / BIRD_JUMP;
		uppos.y = SCREEN_HEIGHT;
	}
	CCPoint downpos = ccp(uppos.x, GROUND_HEIGHT+BIRD_WIDTH/2);
	float duraDown = getDuraDown(uppos, downpos);
	CCMoveTo* moveup = CCMoveTo::create(jumpDuration, uppos);
	CCRotateTo* faceup = CCRotateTo::create(jumpDuration, -30);
	CCSpawn* fly = CCSpawn::create(faceup, CCEaseSineOut::create(moveup), NULL);
	CCMoveTo* movedown = CCMoveTo::create(duraDown, downpos);
	CCRotateTo* facedown = CCRotateTo::create(duraDown, 90);
	CCSpawn* fall = CCSpawn::create(facedown, CCEaseSineIn::create(movedown),  NULL);
	CCFiniteTimeAction* actionMoveDone = CCCallFuncN::create( NULL,callfuncN_selector(Bird::touchGroundCallBack));
	CCSequence* tap = CCSequence::create(fly, fall,	NULL);
	curAction = tap;
	m_sprite->runAction(tap);
}
float Bird::getDuraDown(CCPoint up, CCPoint down){
	float dy = up.y - down.y;
	float duraDown;
	/*if (dy <= BIRD_JUMP*2) 
		duraDown = BIRD_JUMP_DURATION;
	else {
		duraDown = dy * (BIRD_JUMP_DURATION * 1/2) / BIRD_JUMP;
	}*/
	duraDown = dy * (BIRD_FALL_DURATION) / (SCREEN_HEIGHT-GROUND_HEIGHT);

	return duraDown;
}

void Bird::touchGroundCallBack(CCObject* pSender){
	this->state = BIRD_DIED;
	
}
void Bird::stopAction(){
	if (curAction != NULL)
	{
		m_sprite->stopAction(curAction);
		curAction = NULL;
	}
}
bool Bird::isCollision(CCSprite *pipe){
	CCSize spipe = pipe->boundingBox().size;
	CCPoint ppipe = getWorldPosition(pipe);

	CCSize sbird = m_sprite->boundingBox().size;
	CCPoint pbird = m_sprite->getPosition();

	float d = 4; 

	float maxx1 = ppipe.x + spipe.width/2 - d;
	float minx1 = ppipe.x - spipe.width/2 + d;
	float maxy1 = ppipe.y + spipe.height/2 - d;
	float miny1 = ppipe.y - spipe.height/2 + d;

	float maxx2 = pbird.x + sbird.width/2 - d;
	float minx2 = pbird.x - sbird.width/2 + d;
	float maxy2 = pbird.y + sbird.height/2 - d;
	float miny2 = pbird.y - sbird.height/2 + d;

	return !(maxx1 < minx2 ||
		maxx2 < minx1 ||
		maxy1 < miny2 ||
		maxy2 < miny1);

}
CCPoint Bird::getWorldPosition(CCSprite *pipe)
{
	return ((CCParallaxNode*)pipe->getParent())->convertToWorldSpace(pipe->getPosition());
}
bool Bird::updateScore(Walk *walk){
	if (getWorldPosition(walk->getBottomSprite()).x < m_sprite->getPosition().x)
	{
		if (!walk->getIsScored())
		{
			walk->setIsScored(true);
			score++;
			return true;
		}else
		{
			return false;
		}
	}
	return false;
}