/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tec.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 *
 * @author aaronsolera
 */
public abstract class Entity {
    public Sprite sprite;
    public Float speed;
    
    public Entity(String src, Float x, Float y, Float  width, Float  height, Float  speed){
        sprite = new Sprite(new Texture(src));
        sprite.setBounds(x, y, width, height);
        this.speed = speed;
    }
    public void draw(SpriteBatch spriteBatch){
        sprite.draw(spriteBatch);
    }
    public Boolean collision(Entity object){
        if(getX()+getWidth()>=object.getX() & object.getX()+object.getWidth()>=getX()){
            if(getY()+getHeight()>=object.getY() & object.getY()+object.getHeight()>=getY()){
                return true;
            }
        }
        return false;
    }
    
    public void moveLeft(){ sprite.translateX(-speed); }
    public void moveRight(){ sprite.translateX(speed); }
    public void moveUp(){ sprite.translateY(speed); }
    public void moveDown(){ sprite.translateY(-speed); }
    
    public Float getX(){ return sprite.getX(); }
    public Float getY(){ return sprite.getY(); }
    public Float getWidth(){ return sprite.getWidth(); }
    public Float getHeight(){ return sprite.getHeight(); } 
    
    public void setSpeed(Float speed){ this.speed = speed; }
}
