package com.tec.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tec.game.Game_Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            config.title = "SpaceInvaders";
            config.height = 700;
            config.width = 600;
            new LwjglApplication(new Game_Main(), config);
	}
}
