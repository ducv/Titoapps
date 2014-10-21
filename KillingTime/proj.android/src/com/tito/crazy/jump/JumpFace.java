/****************************************************************************
Copyright (c) 2010-2011 cocos2d-x.org

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 ****************************************************************************/
package com.tito.crazy.jump;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.Cocos2dxHelper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tito.crazy.jump.base.BaseGameActivity;
import com.tito.crazy.jump.utils.ConfigUtils;
import com.tito.crazy.jump.utils.NativeUtils;

public class JumpFace extends BaseGameActivity {
	static AdView adView;
	static Activity me;
//	private static final String AD_UNIT_ID = "ca-app-pub-1126446154858350/2460333829";
	private static final String AD_UNIT_ID = "ca-app-pub-1126446154858350/3640748620";
	

	// final String ADMOB_ID = "a150f54aa7dab27";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Cocos2dxHelper.init(this, this);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		_init();
	}

	private void _init() {
		NativeUtils.configure(this);
		if (ConfigUtils.USE_AD_MOB) {
			_initAdMob();
		}
	}

	private void _initAdMob() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		int width = getDisplaySize(getWindowManager().getDefaultDisplay()).x;
		LinearLayout.LayoutParams adParams = new LinearLayout.LayoutParams(
				width, LinearLayout.LayoutParams.WRAP_CONTENT);
		adView = new AdView(this);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId(AD_UNIT_ID);
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.addTestDevice("HASH_DEVICE_ID").build();

		adView.loadAd(adRequest);
		adView.setBackgroundColor(Color.BLACK);
		adView.setBackgroundColor(0);
		addContentView(adView, adParams);
	}

	// Helper get display screen to avoid deprecated function use
	private Point getDisplaySize(Display d) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			return getDisplaySizeGE11(d);
		}
		return getDisplaySizeLT11(d);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private Point getDisplaySizeGE11(Display d) {
		Point p = new Point(0, 0);
		d.getSize(p);
		return p;
	}

	private Point getDisplaySizeLT11(Display d) {
		try {
			Method getWidth = Display.class.getMethod("getWidth",
					new Class[] {});
			Method getHeight = Display.class.getMethod("getHeight",
					new Class[] {});
			return new Point(
					((Integer) getWidth.invoke(d, (Object[]) null)).intValue(),
					((Integer) getHeight.invoke(d, (Object[]) null)).intValue());
		} catch (NoSuchMethodException e2) // None of these exceptions should
											// ever occur.
		{
			return new Point(-1, -1);
		} catch (IllegalArgumentException e2) {
			return new Point(-2, -2);
		} catch (IllegalAccessException e2) {
			return new Point(-3, -3);
		} catch (InvocationTargetException e2) {
			return new Point(-4, -4);
		}
	}


	public void showAd() {
		if (ConfigUtils.USE_AD_MOB && adView != null)
			showHideAd.sendEmptyMessage(0);
	}

	public void hideAd() {
		if (ConfigUtils.USE_AD_MOB && adView != null)
			showHideAd.sendEmptyMessage(1);
	}
	
	
	static Handler showHideAd = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (adView != null) {
					adView.setVisibility(View.VISIBLE);
				}
				Log.w("Show", "Show");
				break;
			case 1:
				if (adView != null) {
					adView.setVisibility(View.INVISIBLE);
				}
				Log.w("Hide", "Hide");
				break;

			default:
				break;
			}

			super.handleMessage(msg);
		}
	};

	public Cocos2dxGLSurfaceView onCreateView() {
		Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(this);
		// JumpFace should create stencil buffer
		glSurfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 8);

		return glSurfaceView;
	}

	static {
		System.loadLibrary("jumpface");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ConfigUtils.USE_AD_MOB && adView != null) {
			adView.resume();
		}
	}

	@Override
	protected void onPause() {
		if (ConfigUtils.USE_AD_MOB && adView != null) {
			adView.pause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (ConfigUtils.USE_AD_MOB && adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}

	@Override
	public void onSignInFailed() {
	}

	@Override
	public void onSignInSucceeded() {
	}

	/**
	 * Get boolean to identify if user is signed in
	 * 
	 * @return
	 */
	public boolean getSignedIn() {
		return isSignedIn();
	}

	/**
	 * 
	 * @return
	 */
	public GoogleApiClient getCustomApiClient() {
		return getApiClient();
	}

	/**
	 * 
	 */
	public void signInGooglePlay() {
		beginUserInitiatedSignIn();
	}

	/**
	 * 
	 */
	public void signOutGooglePlay() {
		signOut();
	}

	// InCloud methods not work yet.

	/**
	 * 
	 * @param key
	 * @param app_state
	 */
	public void inCloudSaveOrUpdate(final int key, final byte[] app_state) {
		AppStateManager.update(getCustomApiClient(), key, app_state);
	}

	/**
	 * 
	 * @param key
	 */
	public void inCloudLoad(final int key) {
		AppStateManager.load(getCustomApiClient(), key);
	}

	/**
	 * 
	 */
	public void requestGameAndCloudSave() {
		setRequestedClients(BaseGameActivity.CLIENT_GAMES
				| BaseGameActivity.CLIENT_APPSTATE);
	}
}
