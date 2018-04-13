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

public class Spectator_Screen implements Screen{
    private SpriteBatch batch;
    private Player player;
    private Sprite logo, limit;
    private ArrayList<Entity> aliens, walls;
    private ArrayList<Integer[]> aliens_data;
    private ArrayList<Integer> extra_data;
    private ArrayList<Integer> walls_data;
    private Boolean back = false, down = false,connected = false, game_over = false, linked = false;
    private Integer down_distance = 0;
    private Client client;
    private BitmapFont font;
    private Random alien_shoot;
    private String matrix;

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
                if(!linked){
                    matrix = client.recieve();
                    initializeData();
                    initializeGame(5,10);
                    player.setLife(extra_data.get(7));
                    linked = true;
                }else{
                    if(client.recieve().split(",")[0].length() == 199){
                        matrix = client.recieve();
                        updateData();
                        System.out.println("The new data is: " + client.recieve());
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
                    drawAlienMatrix(batch);
                    updateBunkers(batch);
                    font.draw(batch, "SCORE: " + player.getScore(), Gdx.graphics.getWidth()-100, Gdx.graphics.getHeight()-50);
                    batch.end();
                }
            }else{
                batch.begin();
                logo.setBounds((Gdx.graphics.getWidth()/2)-100, (Gdx.graphics.getHeight()/2), 200, 70);
                font.draw(batch, "Waiting a game to spectate...", (Gdx.graphics.getWidth()/2)-90, (Gdx.graphics.getHeight()/2)-10);
                logo.draw(batch);
                batch.end();
            }
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
            aliens_data.set(md,temp);
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
        aliens_data = new ArrayList<Integer[]>();
        extra_data = new ArrayList<Integer>();
        walls_data =  new ArrayList<Integer>();
       
        String[] data = matrix.split(",");
        String[] matrix_d = data[0].split("/");
        
        for(Integer md = 0; md < matrix_d.length; md++) {
            Integer[] temp = new Integer[2];
            temp[0] = Integer.parseInt(String.valueOf(matrix_d[md].charAt(0)));
            temp[1] = Integer.parseInt(String.valueOf(matrix_d[md].charAt(2)));
            aliens_data.add(temp);
        }

        for(Integer e = 1; e < data.length-1; e++) {
            extra_data.add(Integer.parseInt(data[e]));
        }

        for (int s = 0; s < 280; s++) {
            walls_data.add(Integer.parseInt(String.valueOf(data[data.length-1].charAt(s))));
        }
    }
    
    public void createAlien(Integer id){
        Integer xi = extra_data.get(0), yi = extra_data.get(1), columns = 0, rows = 0;
        Entity entity;
        
        for (Integer r = 0; r < 5; r++) {
            for (Integer c = 0; c < 10 & (c+(10*r)) < aliens_data.size(); c++){
                if(id.equals((10*r)+c)){
                    switch(aliens_data.get(id)[0]){
                        case 1:
                            entity = new Squid("squid.png",(xi+(36f*c)),(yi-(30f*r)),36f,30f,0f);
                            entity.setID((columns+10)*rows);
                            entity.setColumn(c);
                            entity.setRow(r);
                            System.out.println("A squid was added.");
                            aliens.add(entity);
                            break;
                        case 2:
                            entity = new Crab("crab.png",(xi+(36f*c)),(yi-(30f*r)),36f,30f,0f);
                            entity.setID((columns+10)*rows);
                            entity.setColumn(c);
                            entity.setRow(r);
                            System.out.println("A crab was added.");
                            aliens.add(entity);
                            break;
                        case 3:
                            entity = new Octopus("octopus.png",(xi+(36f*c)),(yi-(30f*r)),36f,30f,0f);
                            entity.setID((columns+10)*rows);
                            entity.setColumn(c);
                            entity.setRow(r);
                            System.out.println("An octopus was added.");
                            aliens.add(entity);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
    
    public void createWall(Integer id){
        Entity wall;
        for(Integer w = 0; w < 4; w++){
            for(Integer r = 0; r < 7; r++){
                for (Integer c = 0; c < 10; c++) {
                    if(id == (70*w)+(10*r)+c){
                        wall = new Bullet("bullet.png",45f + (150f*w) + (5f*c), 50f + (5f*r), 5f, 5f, 0f);
                        wall.setID((70*w)+(10*r)+c);
                        walls.add(wall);    
                    }
                }
            } 
        }
    }

    public void initializeGame(Integer rows, Integer columns){
        Integer xi = extra_data.get(0), yi = extra_data.get(1);
        Float speed = extra_data.get(2) + 0f;
        Entity entity, wall;
        
        aliens = new ArrayList<Entity>();
        for (Integer r = 0; r < rows; r++) {
            for (Integer c = 0; c < columns & (c+(columns*r)) < aliens_data.size(); c++) {
                if(null == aliens_data.get(c+(columns*r))[0]){
                    continue;
                }else switch(aliens_data.get(c+(columns*r))[0]){
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
                entity.setColumn(c);
                entity.setRow(r);
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
    
    public Boolean checkIfExist(Integer id, ArrayList<Entity> list){
        for (Entity entity: list) {
            if(entity.getID().equals(id)){
                return true;
            }
        }
        return false;
    }
    
    public void updateBunkers(SpriteBatch spriteBatch){
        Entity alien, wall;
        for (int wd = 0; wd < walls_data.size(); wd++){
            if(walls_data.get(wd) == 1){
                if(!checkIfExist(wd, walls)){
                    createWall(wd);
                }
            }
        }
        for (Integer w = 0; w < walls.size(); w++) {
            wall = walls.get(w);
            wall.draw(spriteBatch);
            if(walls_data.get(wall.getID()) == 0){
                walls.remove(wall);
            }else{
                for (Integer b = 0; b < player.getBullets().size(); b++) {
                    if(wall.collision(player.getBullets().get(b))){
                        player.destroyBullet(player.getBullets().get(b));
                        walls_data.set(wall.getID(), 0);
                    }
                }
                for (Integer e = 0; e < aliens.size(); e++) {
                    alien = aliens.get(e);
                    if(wall.collision(alien)){
                        aliens.remove(alien);
                        aliens_data.get(alien.getID())[0] = 0;
                        walls_data.set(wall.getID(), 0);
                    }else{
                        for (Integer b = 0; b < alien.getBullets().size(); b++) {
                            if(wall.collision(alien.getBullets().get(b))){
                                alien.destroyBullet(alien.getBullets().get(b));
                                walls_data.set(wall.getID(), 0);
                            }
                        }
                    }   
                } 
            }
        }
    }
    
    public void drawAlienMatrix(SpriteBatch spriteBatch){
        Entity entity, new_entity;
        
        for (int a = 0; a < aliens_data.size(); a++){
            if(!checkIfExist(a, aliens) & aliens_data.get(a)[0] != 0 & aliens.size()<50){
                System.out.println("The alien "+a+" does not exist");
                createAlien(a);
            }
        }
        
        for (Integer e = 0; e < aliens.size(); e++) {
            entity = aliens.get(e);
            entity.setPosition(extra_data.get(0)+entity.getColumn()*entity.getWidth().intValue(), extra_data.get(1)-entity.getRow()*entity.getHeight().intValue());
            if(!aliens_data.get(entity.getID())[0].equals(entity.getType())){
                switch (aliens_data.get(entity.getID())[0]){
                    case 1:
                        new_entity = new Squid("squid.png",entity.getX(),entity.getY(),36f,30f,entity.speed);
                        new_entity.setID(entity.getID());
                        new_entity.setType(1);
                        new_entity.setColumn(entity.getColumn());
                        new_entity.setRow(entity.getRow());
                        System.out.println("A squid was added.");
                        aliens.add(new_entity);
                        aliens.remove(entity);
                        break;
                    case 2:
                        new_entity = new Crab("crab.png",entity.getX(),entity.getY(),36f,30f,entity.speed);
                        new_entity.setID(entity.getID());
                        new_entity.setType(2);
                        new_entity.setColumn(entity.getColumn());
                        new_entity.setRow(entity.getRow());
                        System.out.println("A crab was added.");
                        aliens.add(new_entity);
                        aliens.remove(entity);
                        break;
                    case 3:
                        new_entity = new Octopus("octopus.png",entity.getX(),entity.getY(),36f,30f,entity.speed);
                        new_entity.setID(entity.getID());
                        new_entity.setType(3);
                        new_entity.setColumn(entity.getColumn());
                        new_entity.setRow(entity.getRow());
                        System.out.println("An octopus was added.");
                        aliens.add(new_entity);
                        aliens.remove(entity);
                        break;
                    case 0:
                        aliens.remove(entity);
                    default:
                        break;
                }
            }
            
            entity.draw(spriteBatch);
            
            if(aliens_data.get(e)[1] == 1){
                entity.shoot();
            }
            
            for (Integer b = 0; b < player.getBullets().size(); b++) {
                if(entity.collision(player.getBullets().get(b))){
                    player.destroyBullet(player.getBullets().get(b));
                }
            }
            for (Integer b = 0; b < entity.getBullets().size(); b++) {
                if(player.collision(entity.getBullets().get(b))){
                    entity.destroyBullet(entity.getBullets().get(b));
                }
            }
        }
    }
    
    @Override
    public void dispose () {
        batch.dispose();
        try {
            client.stop();
        } catch (IOException ex) {}
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
