#include "AppDelegate.h"
#include "Splash.h"

USING_NS_CC;

AppDelegate::AppDelegate() {

}

AppDelegate::~AppDelegate() 
{
}

bool AppDelegate::applicationDidFinishLaunching() {
    // initialize director
	CCDirector *pDirector = CCDirector::sharedDirector();
	pDirector->setOpenGLView(CCEGLView::sharedOpenGLView());
	pDirector->setProjection(kCCDirectorProjection2D);

	CCSize screenSize = CCEGLView::sharedOpenGLView()->getFrameSize();
	float scale = SCREEN_HEIGHT/screenSize.height;

	CCSize designSize = CCSizeMake(scale*screenSize.width, SCREEN_HEIGHT);
	CCSize resourceSize = CCSizeMake(scale*screenSize.width, SCREEN_HEIGHT);

	std::vector<std::string> searchPaths;
	std::vector<std::string> resDirOrders;


	//TargetPlatform platform = CCApplication::sharedApplication()->getTargetPlatform();
	//if (platform == kTargetIphone || platform == kTargetIpad)
	//{
	//	searchPaths.push_back("Published-iOS"); // Resources/Published-iOS
	//	CCFileUtils::sharedFileUtils()->setSearchPaths(searchPaths);

	//	if (screenSize.height > 480)
	//	{
	//		resourceSize = CCSizeMake(640, 960);
	//		resDirOrders.push_back("resources-iphonehd");
	//	}
	//	else
	//	{
	//		resDirOrders.push_back("resources-iphone");
	//	}

	//	CCFileUtils::sharedFileUtils()->setSearchResolutionsOrder(resDirOrders);
	//}
	//else if (platform == kTargetAndroid || platform == kTargetWindows)
	//{
	//	// Comments it since opengles2.0 only supports texture size within 2048x2048.
	//	//        if (screenSize.height > 1024)
	//	//        {
	//	//            resourceSize = CCSizeMake(1280, 1920);
	//	//            resDirOrders.push_back("resources-xlarge");
	//	//            resDirOrders.push_back("");
	//	//        }
	//	//        else 
	//	if (screenSize.height > 960)
	//	{
	//		resourceSize = CCSizeMake(640, 960);
	//		resDirOrders.push_back("resources-large");
	//		resDirOrders.push_back("resources-medium");
	//		resDirOrders.push_back("resources-small");
	//	}
	//	else if (screenSize.height > 480)
	//	{
	//		resourceSize = CCSizeMake(480, 720);
	//		resDirOrders.push_back("resources-medium");
	//		resDirOrders.push_back("resources-small");
	//	}
	//	else
	//	{
	//		resourceSize = CCSizeMake(320, 568);
	//		resDirOrders.push_back("resources-small");
	//	}

	//	CCFileUtils::sharedFileUtils()->setSearchResolutionsOrder(resDirOrders);
	//}
	CCFileUtils::sharedFileUtils()->setSearchResolutionsOrder(resDirOrders);
	pDirector->setContentScaleFactor(resourceSize.width/designSize.width);

	CCEGLView::sharedOpenGLView()->setDesignResolutionSize(designSize.width, designSize.height, kResolutionShowAll);


	// turn on display FPS
	pDirector->setDisplayStats(false);

	// set FPS. the default value is 1.0/60 if you don't call this
	pDirector->setAnimationInterval(1.0 / 60);

	// create a scene. it's an autorelease object
	CCScene *pScene = Splash::scene();

	// run
	pDirector->runWithScene(pScene);

	return true;
}

// This function will be called when the app is inactive. When comes a phone call,it's be invoked too
void AppDelegate::applicationDidEnterBackground() {
    CCDirector::sharedDirector()->stopAnimation();

    // if you use SimpleAudioEngine, it must be pause
    // SimpleAudioEngine::sharedEngine()->pauseBackgroundMusic();
}

// this function will be called when the app is active again
void AppDelegate::applicationWillEnterForeground() {
    CCDirector::sharedDirector()->startAnimation();

    // if you use SimpleAudioEngine, it must resume here
    // SimpleAudioEngine::sharedEngine()->resumeBackgroundMusic();
}
