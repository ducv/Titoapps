#include "Walk.h"
Walk::Walk()
{
		x=y=0;
}
Walk::~Walk()
{
}
void Walk::update(int delta){
	if (state == WALK_MOVING)
	{
		x = x-delta;
		this->bottomSprite->setPositionX(x+width/2);
		this->topSprite->setPositionX(x+width/2);
	}
	
}