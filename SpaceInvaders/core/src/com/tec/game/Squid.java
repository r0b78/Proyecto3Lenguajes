/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tec.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 *
 * @author aaronsolera
 */
public class Squid extends Entity{
    
    public Squid(String src, Float x, Float y, Float width, Float height, Float speed) {
        super(src, x, y, width, height, speed);
        setScore(10);
    }
}
