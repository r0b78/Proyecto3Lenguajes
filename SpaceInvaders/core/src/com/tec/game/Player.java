/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tec.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;

/**
 *
 * @author aaronsolera
 */
public class Player extends Entity{
    private Sprite life;
    private Integer life_number = 3;
    
    public Player(String src, Float x, Float y, Float width, Float height, Float speed){
        super(src, x, y, width, height, speed);
        setScore(0);
        life = new Sprite();
    }
    
    @Override
    public void draw(SpriteBatch spriteBatch){
        sprite.draw(spriteBatch);
        drawLife(spriteBatch);
        drawBullet(spriteBatch);
    }
    
    @Override
    public void drawBullet(SpriteBatch spriteBatch){
        for(Integer b = 0; b < bullets.size(); b++){
            if(bullets.get(b).getY() >= 600){
                destroyBullet(bullets.get(b));
            }else{
                bullets.get(b).draw(spriteBatch);
            }
        }
    }
        
    @Override
    public void moveLeft(){ 
        if(getX() >= 20)
            sprite.translateX(-speed); 
    }
    
    @Override
    public void moveRight(){ 
        if(getX()+getWidth() <= Gdx.graphics.getWidth()- 20)
        sprite.translateX(speed); 
    }
    
    public void drawLife(SpriteBatch spriteBatch){
        for(Integer l = 1; l <= life_number; l++){
            life.setBounds(getX()+((15)*(l-1)), getY()-3, 10, 2);
            life.setTexture(new Texture("life"+Integer.toString(l)+".png"));
            life.draw(spriteBatch);
        }
    }
    
    @Override
    public void shoot(){
        bullets.add(new Bullet("bullet.png", getX()+(getWidth()/2), getY()+getHeight()+2, 3f, 3f, 5f));
    }
   
    public void lifeUp(){ 
        if(life_number <= 3)
            life_number++; 
    }
    
    public void lifeDown(){ 
        if(life_number >= 1)
            life_number--; 
    }
    
    public Integer getLife(){
        return life_number;
    }
    
    public void setLife(Integer life){
        life_number = life;
    }
}
