package com.hikvision.energy.energis.fxtool.AssitTool;

import de.felixroske.jfxsupport.SplashScreen;

/**
 * Created by GOT.hodor on 2017/9/27.
 */
public class AppSplashScreen extends SplashScreen {

    private static String DEFAULT_IMAGE = "/images/splash/splash.png";

    public AppSplashScreen() {
        super();
    }

    @Override
    public String getImagePath() {
        return DEFAULT_IMAGE;
    }
}
