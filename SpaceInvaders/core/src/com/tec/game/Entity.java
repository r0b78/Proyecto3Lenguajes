/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tec.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;

/**
 *
 * @author aaronsolera
 */
public abstract class Entity {
    public ArrayList<Bullet> bullets;
    public Sprite sprite;
    public Float speed;
    private Integer id_pos, score;
    
    public Entity(String src, Float x, Float y, Float  width, Float  height, Float  speed){
        sprite = new Sprite(new Texture(src));
        bullets = new ArrayList<Bullet>();
        sprite.setBounds(x, y, width, height);
        this.speed = speed;
    }
    
    public void setID(Integer id_pos){
        this.id_pos = id_pos;
    }
    
    public Integer getID(){
        return id_pos;
    }
    
    public void draw(SpriteBatch spriteBatch){
        sprite.draw(spriteBatch);
        drawBullet(spriteBatch);
    }
    
    public void drawBullet(SpriteBatch spriteBatch){
        for(Integer b = 0; b < bullets.size(); b++){
            if(bullets.get(b).getY() <= 10){
                destroyBullet(bullets.get(b));
            }else{
                bullets.get(b).draw(spriteBatch);
            }
        }
    }
    
    public void destroyBullet(Bullet b){
        bullets.remove(b);
    }
    
    public void shoot(){
        bullets.add(new Bullet("laser.png", getX()+(getWidth()/2), getY()-getHeight()+2, 12.5f, 22.5f, -speed));
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
    public ArrayList<Bullet> getBullets(){ return bullets; }
    public Integer getScore(){ return score; }
    
    public void setSpeed(Float speed){ this.speed = speed; }
    public void setScore(int score){ this.score = score; }
    public void setPosition(Integer x, Integer y){ sprite.setPosition(x, y); }
}
