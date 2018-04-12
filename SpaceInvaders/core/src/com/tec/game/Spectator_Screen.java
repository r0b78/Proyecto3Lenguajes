/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tec.game;

/**
 *
 * @author aaronsolera
 */
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Spectator_Screen implements Screen{
    private SpriteBatch batch;
    private Player player;
    private Sprite logo, limit;
    private ArrayList<Entity> aliens, walls;
    private ArrayList<Integer[]> matrix_data;
    private ArrayList<Integer> extra_data;
    private ArrayList<Integer> walls_data;
    private Boolean back = false, down = false,connected = false, game_over = false, linked = false;
    private Integer down_distance = 0;
    private Client client;
    private BitmapFont font;
    private Random alien_shoot;
    private String matrix, old_matrix;

    @Override
    public void show(){
        client = new Client();
        batch = new SpriteBatch();
        logo = new Sprite(new Texture("logo.png"));
        limit = new Sprite(new Texture("limit.png"));
        player = new Player("player.png",270f,20f,40f,25f,5f);
        font = new BitmapFont();
        alien_shoot = new Random();

        limit.setBounds(0, 0, 600, 600);
        connected = client.connect();
        if(connected == true){
            new Thread(client).start();
        }
    }
   @Override
    public void render (float  delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        ////////////////////////////////////
        if(!connected){
            batch.begin();
            logo.setBounds((Gdx.graphics.getWidth()/2)-100, (Gdx.graphics.getHeight()/2), 200, 70);
            font.draw(batch, "Please, turn on the Space Invaders server", (Gdx.graphics.getWidth()/2)-135, (Gdx.graphics.getHeight()/2)-10);
            logo.draw(batch);
            batch.end();
            connected = client.connect();
            if(connected == true){
                new Thread(client).start();
            }
        }else{
            if(client.recieve() != null){
                matrix = client.recieve();
                if(!linked){
                    initializeData();
                    initializeGame(5,10);
                    player.setLife(extra_data.get(7));
                    linked = true;
                }
                if(matrix.length() >= 400){
                    updateData();
                }
                player.setLife(extra_data.get(7));
                player.setPosition(extra_data.get(3), 20);
                player.setScore(extra_data.get(5));
                if(extra_data.get(6) == 1){
                    player.shoot();
                }
                batch.begin();
                logo.setBounds(10, Gdx.graphics.getHeight() - 80, 200, 70);
                logo.draw(batch);
                limit.draw(batch);
                player.draw(batch);
                //drawAlienMatrix(batch);
                updateBunkers(batch);
                font.draw(batch, "SCORE: " + player.getScore(), Gdx.graphics.getWidth()-100, Gdx.graphics.getHeight()-50);
                batch.end();
            }else{
                batch.begin();
                logo.setBounds((Gdx.graphics.getWidth()/2)-100, (Gdx.graphics.getHeight()/2), 200, 70);
                font.draw(batch, "Waiting a game to spectate...", (Gdx.graphics.getWidth()/2)-90, (Gdx.graphics.getHeight()/2)-10);
                logo.draw(batch);
                batch.end();
            }
            System.out.println("The new data is: " + matrix);
        }
        ////////////////////////////////////
    }
    
    public void updateData(){
        String[] data = matrix.split(",");
        String[] matrix_d = data[0].split("/");

        for(Integer md = 0; md < matrix_d.length; md++) {
            Integer[] temp = new Integer[2];
            temp[0] = Integer.parseInt(String.valueOf(matrix_d[md].charAt(0)));
            temp[1] = Integer.parseInt(String.valueOf(matrix_d[md].charAt(2)));
            matrix_data.set(md,temp);
        }
        
        for(Integer e = 1; e < data.length-1; e++) {
            extra_data.set(e-1, Integer.parseInt(data[e]));
        }

        for (Integer s = 0; s < 280; s++) {
            walls_data.set(s, Integer.parseInt(String.valueOf(data[data.length-1].charAt(s))));
        }
    }

    public ArrayList<String> getData(String data){
        ArrayList<String> temp_list = new ArrayList<String>();
        String[] temp_array = data.split(",");
        for (Integer d = 0; d < temp_array.length; d++) {
            temp_list.add(temp_array[d]);
        }
        return temp_list;
    }

    public void initializeData(){
        matrix_data = new ArrayList<Integer[]>();
        extra_data = new ArrayList<Integer>();
        walls_data =  new ArrayList<Integer>();

        String[] data = matrix.split(",");
        String[] matrix_d = data[0].split("/");

        for(Integer md = 0; md < matrix_d.length; md++) {
            Integer[] temp = new Integer[2];
            temp[0] = Integer.parseInt(String.valueOf(matrix_d[md].charAt(0)));
            temp[1] = Integer.parseInt(String.valueOf(matrix_d[md].charAt(2)));
            matrix_data.add(temp);
        }

        for(Integer e = 1; e < data.length-1; e++) {
            extra_data.add(Integer.parseInt(data[e]));
        }

        for (int s = 0; s < 280; s++) {
            walls_data.add(Integer.parseInt(String.valueOf(data[data.length-1].charAt(s))));
        }
    }
    
    public Entity createAlien(Integer rows, Integer columns){
        Integer xi = extra_data.get(0), yi = extra_data.get(1);
        Float speed = 1 + extra_data.get(2)*(1f/4f);
        Entity entity;
        
        switch(matrix_data.get((columns+10)*rows)[0]){
            case 1:
                entity = new Squid("squid.png",(xi+(36f*columns)),(yi-(30f*rows)),36f,30f,speed);
                entity.setID((columns+10)*rows);
                System.out.println("A squid was added.");
                return entity;
            case 2:
                entity = new Crab("crab.png",(xi+(36f*columns)),(yi-(30f*rows)),36f,30f,speed);
                entity.setID((columns+10)*rows);
                System.out.println("A crab was added.");
                return entity;
            case 3:
                entity = new Octopus("octopus.png",(xi+(36f*columns)),(yi-(30f*rows)),36f,30f,speed);
                entity.setID((columns+10)*rows);
                System.out.println("An octopus was added.");
                return entity;
            default:
                return null;
        }
    }

    public void initializeGame(Integer rows, Integer columns){
        Integer xi = extra_data.get(0), yi = extra_data.get(1);
        Float speed = 1 + extra_data.get(2)*(1f/4f);
        Entity entity, wall;
        
        aliens = new ArrayList<Entity>();
        for (Integer r = 0; r < rows; r++) {
            for (Integer c = 0; c < columns & (c+(columns*r)) < matrix_data.size(); c++) {
                if(null == matrix_data.get(c+(columns*r))[0]){
                    continue;
                }else switch(matrix_data.get(c+(columns*r))[0]){
                    case 1:
                        entity = new Squid("squid.png",(xi+(36f*c)),(yi-(30f*r)),36f,30f,speed);
                        entity.setType(1);
                        System.out.println("A squid was added.");
                        break;
                    case 2:
                        entity = new Crab("crab.png",(xi+(36f*c)),(yi-(30f*r)),36f,30f,speed);
                        entity.setType(2);
                        System.out.println("A crab was added.");
                        break;
                    case 3:
                        entity = new Octopus("octopus.png",(xi+(36f*c)),(yi-(30f*r)),36f,30f,speed);
                        entity.setType(3);
                        System.out.println("An octopus was added.");
                        break;
                    default:
                        continue;
                }
                entity.setID((columns*r)+c);
                aliens.add(entity);
            }
        }
        
        walls = new ArrayList<Entity>();
        for(Integer w = 0; w < 4; w++){
            for(Integer r = 0; r < 7; r++){
                for (Integer c = 0; c < 10; c++) {
                    if(walls_data.get((70*w)+(10*r)+c) == 1){
                        wall = new Bullet("bullet.png",45f + (150f*w) + (5f*c), 50f + (5f*r), 5f, 5f, 0f);
                        wall.setID((70*w)+(10*r)+c);
                        walls.add(wall);
                    }
                }
            } 
        }
    }
    
    public void updateBunkers(SpriteBatch spriteBatch){
        Entity alien, wall;
        
        for (Integer w = 0; w < walls.size(); w++) {
            wall = walls.get(w);
            wall.draw(spriteBatch);
            if(walls_data.get(wall.getID()) == 0){
                walls.remove(wall);
            }
            for (Integer b = 0; b < player.getBullets().size(); b++) {
                if(wall.collision(player.getBullets().get(b))){
                    walls.remove(wall);
                    player.destroyBullet(player.getBullets().get(b));
                    walls_data.set(wall.getID(), 0);
                }
            }
            for (Integer e = 0; e < aliens.size(); e++) {
                alien = aliens.get(e);
                if(wall.collision(alien)){
                    aliens.remove(alien);
                    walls.remove(wall);
                    matrix_data.get(alien.getID())[0] = 0;
                    walls_data.set(wall.getID(), 0);
                }else{
                    for (Integer b = 0; b < alien.getBullets().size(); b++) {
                        if(wall.collision(alien.getBullets().get(b))){
                            alien.destroyBullet(alien.getBullets().get(b));
                            walls.remove(wall);
                            walls_data.set(wall.getID(), 0);
                        }
                    }
                }   
            } 
        }
    }

    public void drawAlienMatrix(SpriteBatch spriteBatch){
        Entity entity, new_entity;
        
        for (Integer e = 0; e < aliens.size(); e++) {
            entity = aliens.get(e);
            
            if(matrix_data.get(entity.getID())[0] != entity.getType()){
                switch (matrix_data.get(entity.getID())[0]){
                    case 1:
                        new_entity = new Squid("squid.png",entity.getX(),entity.getY(),36f,30f,entity.speed);
                        new_entity.setID(entity.getID());
                        new_entity.setType(1);
                        System.out.println("A squid was added.");
                        aliens.add(new_entity);
                        aliens.remove(entity);
                        break;
                    case 2:
                        new_entity = new Crab("crab.png",entity.getX(),entity.getY(),36f,30f,entity.speed);
                        new_entity.setID(entity.getID());
                        new_entity.setType(2);
                        System.out.println("A crab was added.");
                        aliens.add(new_entity);
                        aliens.remove(entity);
                        break;
                    case 3:
                        new_entity = new Octopus("octopus.png",entity.getX(),entity.getY(),36f,30f,entity.speed);
                        new_entity.setID(entity.getID());
                        new_entity.setType(3);
                        System.out.println("An octopus was added.");
                        aliens.add(new_entity);
                        aliens.remove(entity);
                        break;
                    case 0:
                        aliens.remove(entity);
                    default:
                        break;
                }
                matrix_data.get(entity.getID())[0] =  entity.getType();
            }
            
            entity.draw(spriteBatch);
            moveAlien(entity);
            
            if(matrix_data.get(e)[1] == 1){
                entity.shoot();
            }
            for (Integer b = 0; b < player.getBullets().size(); b++) {
                if(entity.collision(player.getBullets().get(b))){
                    aliens.remove(entity);
                    player.setScore(player.getScore() + entity.getScore());
                    player.destroyBullet(player.getBullets().get(b));
                    matrix_data.get(entity.getID())[0] = 0;
                    extra_data.set(5, player.getScore());
                }
            }
            for (Integer b = 0; b < entity.getBullets().size(); b++) {
                if(player.collision(entity.getBullets().get(b))){
                    entity.destroyBullet(entity.getBullets().get(b));
                    player.lifeDown();
                    extra_data.set(7, player.getLife());
                    if(player.getLife() <= 0){
                        game_over = true;
                    }
                }
            }
        }
        if(down){
            extra_data.set(1, extra_data.get(1)+extra_data.get(2));
        }else if(back){
            extra_data.set(0, extra_data.get(0)-extra_data.get(2));
        }else{
            extra_data.set(0, extra_data.get(0)+extra_data.get(2));
        }
        if(!aliens.isEmpty()){
            if(down_distance<=aliens.get(0).getHeight()){
               down_distance++;
            }else{
               down = false;
               down_distance = 0;
            }
        }
    }
    
    public void moveAlien(Entity entity){
        if(down){
            entity.moveDown();
        }else if(back){
            entity.moveLeft();
        }else{
            entity.moveRight();
        }
        if(entity.getX()+entity.getWidth() >= Gdx.graphics.getWidth()- 20){
            back = true;
            down = true;
        }
        if(entity.getX() <= 20){
            back = false;
            down = true;
        }
    }
    
    @Override
    public void dispose () {
        batch.dispose();
        try {
            client.stop();
        } catch (IOException ex) {
            Logger.getLogger(Game_Screen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
    @Override
    public void resize(int i, int i1) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
}
