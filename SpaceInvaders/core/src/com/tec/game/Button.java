/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tec.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Button {
    private Sprite skin;
    private Texture texture_release, texture_touched;
    private int secund,x,y,width,height;
    private boolean pressed=false, release=false,touched_nextscreen=false;

    public Button(String button_release, String button_touched, int x, int y, int width, int height) {
        texture_release = new Texture(button_release);
        texture_touched = new Texture(button_touched);
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
    }

    public void update (SpriteBatch spriteBatch)
    {
        skin = new Sprite(texture_release);
        skin.setSize(width, height);
        skin.setPosition(x, y);
        skin.draw(spriteBatch);
        checkIfClicked();
    }
    public boolean pressed()
    {
        return pressed;
    }
    public boolean release()
    {
        return release;
    }
    public boolean checkIfClicked ()
    {
        boolean touched=false;
        release=false;pressed=false;
        for (int i = 0; i < 10; i++)
        {
            int ix = Gdx.input.getX(i);
            int iy = -Gdx.input.getY(i) + Gdx.graphics.getHeight();
            if (Gdx.input.isTouched(i) && x < ix && y < iy && x + width > ix && y + height > iy)
            {
                skin.setTexture(texture_touched);
                pressed=true;
            }
            if (Gdx.input.justTouched() && Gdx.input.isTouched(i) && x < ix && y < iy && x + width > ix && y + height > iy)
            {
                release = true;
                touched_nextscreen=true;
                touched=true;
                System.out.println("Button clicked !");
            }
        }
        return touched;
    }

    public void nextScreen(Game game, Screen screen)
    {
        secund++;
        if(touched_nextscreen) {
            if(secund%50==0)
            {
                game.setScreen(screen);
            }
        }
    }
    public void exitGame()
    {
        if(checkIfClicked()) {
            Gdx.app.exit();
        }
    }

    public void dipose()
    {
        texture_release.dispose();
        texture_touched.dispose();
    }
}