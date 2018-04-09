/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tec.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 *
 * @author aaronsolera
 */
public class Menu_Screen implements Screen{

    private SpriteBatch batch;
    private Sprite logo;
    private BitmapFont font;
    private Button b_player, b_spectator;
    private Game game;
    
    public Menu_Screen(Game game){
        this.game = game;
    }
    
    @Override
    public void show(){
        batch = new SpriteBatch();
        logo = new Sprite(new Texture("logo.png"));
        font = new BitmapFont();
        b_player = new Button("play_button.png", "play_button.png",(Gdx.graphics.getWidth()/4)-50, (Gdx.graphics.getHeight()/2)-50, 100, 100);
        b_spectator = new Button("spectator_button.png", "spectator_button.png", (3*(Gdx.graphics.getWidth()/4))-50, (Gdx.graphics.getHeight()/2)-50, 100, 100);
        }

    @Override
    public void render(float f) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        logo.setBounds((Gdx.graphics.getWidth()/2)-100, (Gdx.graphics.getHeight()/2), 200, 70);
        font.draw(batch, "Please, choice a game mode", (Gdx.graphics.getWidth()/2)-95, (Gdx.graphics.getHeight()/2)-10);
        logo.draw(batch);
        b_player.update(batch);
        b_player.nextScreen(game, new Game_Screen());
        b_spectator.update(batch);
        batch.end();
    }

    @Override
    public void resize(int i, int i1) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
    
}
