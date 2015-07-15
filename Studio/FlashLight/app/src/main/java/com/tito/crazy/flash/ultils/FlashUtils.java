package com.tito.crazy.flash.ultils;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FlashUtils {
    public static final int LEVEL_0 = 0;
    public static final int LEVEL_3 = 3;
    private LEDUtilities ledUtilities;
    private CameraUltils cameraUltils;
    public Context context;
    private boolean active = false;
    private int level = LEVEL_0;
    private SoundUltils soundUltils;
    private boolean status = false;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public FlashUtils(Context context) {
        this.context = context;
        soundUltils = SoundUltils.getInstance(context);
        active = false;
        status = true;
    }


    public void init(SurfaceView surfaceView) {
        ledUtilities = LEDUtilities.getInstance(context);
        if (!ledUtilities.isSupported()&&surfaceView!=null) {
            cameraUltils = CameraUltils.getInstance(context,surfaceView);
        }
    }

    public void adjustBrightness() {
            if (active) {
                active = false;
                turnOff();
                soundUltils.playSound(false);
                level = LEVEL_0;
                status = false;
            } else {
                active = true;
                soundUltils.playSound(true);
                turnOn();
                level = LEVEL_3;
                status = true;
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    public void turnOn() {
        if (ledUtilities.isSupported()) {
            ledUtilities.setBrightness(LEDUtilities.HIGH_BRIGHTNESS);
        } else {
            cameraUltils.turnOnFlash();
        }
        status = true;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void turnOff() {
        if (ledUtilities.isSupported()) {
            ledUtilities.turnOff();
        } else {
            cameraUltils.turnOffFlash();
        }
        status = false;
    }

    public void release() {
        if (ledUtilities.isSupported()) {
            return;
        } else {
            cameraUltils.release();
        }
    }

    public void onStart() {
        if (!ledUtilities.isSupported()) {
//            cameraUltils.getCamera();
        }
    }

    public boolean getLedSupported() {
        return ledUtilities.isSupported();
    }
}
