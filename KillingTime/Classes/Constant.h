#ifndef _CONSTANT_H_
#define _CONSTANT_H_

#define GROUND_HEIGHT 100 
#define SPACE_RANGE 20
#define SPACE_MIN 90
#define WALK_WIDTH 50
#define WALK_DELTA 5
#define SCREEN_HEIGHT 480
#define SPACE_B_2_WALK 220
#define BUTTON_WIDTH  100
#define BUTTON_HEIGH  60
#define MIN_WALL 40
#define MAX_WALL 70
#define SOUND_BUTTON_WIDTH  30
#define SOUND_BUTTON_HEIGHT  30


#define SCORE_PANEL_WIDTH  250
#define SCORE_PANEL_HEIGH  140

#define GAME_STATE_WATTING  0
#define GAME_STATE_READY  1
#define GAME_STATE_PLAYING  2
#define GAME_STATE_OVER  3

//resource
#define RES_BACKGROUND "background.png"
#define RES_GROUND "ground.png"
#define RES_FACE "face.png"
#define RES_WALK "pipe.png"
#define RES_PLAY_BUTTON "play_button.png"
#define RES_HIGHT_BUTTON "highscore_button.png"
#define RES_GUIDE "guide.png"
#define RES_SCORE_PANEL "score_panel.png"
#define RES_READY "ready.png"
#define RES_JUMP_FACE "jump_face.png"
#define RES_GAME_OVER "game_over.png"
#define RES_LOGO "logo_tito.png"
#define RES_NUMBER "number.png"
#define RES_SOUND_BUTTON_ON "sound_on.png"
#define RES_SOUND_BUTTON_OFF "sound_mute.png"

#define RES_FONT_NAME "fonts/digital.ttf"

#define SOUND_WING "sounds/wing.ogg"
#define SOUND_DIE "sounds/die.ogg"
#define SOUND_POINT "sounds/point.ogg"
#define SOUND_SWOOSHING "sounds/swooshing.ogg"
#define SOUND_HIT "sounds/hit.ogg"


#define MEDAL_SILVER "Silver"
#define MEDAL_GOLD "Gold"

//bird

#define BIRD_WIDTH 25
#define BIRD_READY 0
#define BIRD_FLYING 1
#define BIRD_FALLING 2
#define BIRD_DIED 3
#define BIRD_X 50
#define BIRD_Y 200

#define BIRD_JUMP 50
#define BIRD_JUMP_DURATION 0.4f
#define BIRD_FALL_DURATION 1.7f
#define BIRD_DELTA 2
#define BIRD_APLITUDE 2
#define TEXT_Y 300

#define STEP 2

//WALK
#define WALK_READY 0
#define WALK_MOVING 1
//GROUND
#define GROUND_READY 0
#define GROUND_MOVING 1

#define HIGH_SCORE "jumpface_high_score"

#endif
