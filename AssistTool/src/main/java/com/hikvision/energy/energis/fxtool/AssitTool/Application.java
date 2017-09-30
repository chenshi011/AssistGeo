package com.hikvision.energy.energis.fxtool.AssitTool;


import com.hikvision.energy.energis.fxtool.AssitTool.view.MainView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by GOT.hodor on 2017/9/27.
 */

@SpringBootApplication
public class Application extends AbstractJavaFxApplicationSupport {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        AppSplashScreen appSplashScreen = new AppSplashScreen();
        launchApp(Application.class, MainView.class, appSplashScreen, args);
    }
}
