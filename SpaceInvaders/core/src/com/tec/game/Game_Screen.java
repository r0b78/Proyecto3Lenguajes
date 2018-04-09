package com.tec.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game_Screen implements Screen{
    private SpriteBatch batch;
    private Player player;
    private Sprite logo, limit;
    private ArrayList<Entity> aliens;
    private ArrayList<Integer[]> matrix_data;
    private ArrayList<Integer> extra_data;
    private ArrayList<Integer> walls_data;
    private Boolean back = false, down = false,connected = false;
    private Integer down_distance = 0;
    private Client client;
    private BitmapFont font;

    private String matrix = 
              "2 0/2 0/2 0/2 0/2 0/2 0/2 0/2 0/2 0/2 0/"
            + "1 0/1 0/1 0/1 0/1 0/1 0/1 0/1 0/1 0/1 0/"
            + "3 0/3 0/3 0/3 0/3 0/3 0/3 0/3 0/3 0/3 0/"
            + "1 0/1 0/1 0/1 0/1 0/1 0/1 0/1 0/1 0/1 0/"
            + "2 0/2 0/2 0/2 0/2 0/2 0/2 0/2 0/2 0/2 0,"
            + "390,600,1,"
            + "390,5,0,0,"
            + "1111111111111111111111111111111111111111111111111111111111111111111111"
            + "1111111111111111111111111111111111111111111111111111111111111111111111"
            + "1111111111111111111111111111111111111111111111111111111111111111111111";
    ////matrz , Xposmatriz, Yposmatriz,Velmatriz,Xjug,Vjug,Puntaje,diparojug,murods
    ///bicho disparo/

    @Override
    public void show(){
        initializeData();

        client = new Client();
        batch = new SpriteBatch();
        logo = new Sprite(new Texture("logo.png"));
        limit = new Sprite(new Texture("limit.png"));
        player = new Player("player.png",390f,70f,40f,25f,extra_data.get(4)+0f);
        aliens = new ArrayList<Entity>();
        font = new BitmapFont();

        initializeMatrix(5,10);
        limit.setBounds(370, 50, 600, 600);
        connected = client.connect();
        if(connected == true){
            new Thread(client).start();
        }
    }
    @Override
    public void render (float  delta) {
        
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
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
            keyboardEventHandler();

            batch.begin();
            logo.setBounds(100, Gdx.graphics.getHeight()-100, 200, 70);
            logo.draw(batch);
            limit.draw(batch);
            player.draw(batch);
            drawAlienMatrix(batch);
            batch.end();
            ////////////////////////////////////
            try {
                client.send(matrix);
                System.err.println(createDataFromInformation());
                matrix = client.recieve();
            } catch (IOException ex) {
                Logger.getLogger(Game_Screen.class.getName()).log(Level.SEVERE, null, ex);
            }
            ////////////////////////////////////
            ////////////////////////////////////
        }
    }

    public void keyboardEventHandler(){
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            player.moveLeft();
            extra_data.set(3, extra_data.get(3)-1);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            player.moveRight();
            extra_data.set(3, extra_data.get(3)+1);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            player.shoot();
            extra_data.set(6, 1);
        }else{
            extra_data.set(6, 0);
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

        for (int s = 0; s < 210; s++) {
            walls_data.add(Integer.parseInt(String.valueOf(data[data.length-1].charAt(s))));
        }
    }

    public void initializeMatrix(Integer rows, Integer columns){
        Integer xi = extra_data.get(0), yi = extra_data.get(1);
        Float speed = extra_data.get(2)+0f;
        Entity entity;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns & (c+(columns*r)) < matrix_data.size(); c++) {
                if(null == matrix_data.get(c+(columns*r))[0]){
                    continue;
                }else switch(matrix_data.get(c+(columns*r))[0]){
                    case 1:
                        entity = new Squid("squid.png",(xi+(36f*c)),(yi-(30f*r)),36f,30f,speed);
                        System.out.println("A squid was added.");
                        break;
                    case 2:
                        entity = new Crab("crab.png",(xi+(36f*c)),(yi-(30f*r)),36f,30f,speed);
                        System.out.println("A crab was added.");
                        break;
                    case 3:
                        entity = new Octopus("octopus.png",(xi+(36f*c)),(yi-(30f*r)),36f,30f,speed);
                        System.out.println("An octopus was added.");
                        break;
                    default:
                        continue;
                }
                aliens.add(entity);
            }
        }
    }

    public String createDataFromInformation(){
        String data="";
        for(Integer md = 0; md < matrix_data.size(); md++){
            data += Integer.toString(matrix_data.get(md)[0]) + " " +Integer.toString(matrix_data.get(md)[1]);
            if(md != matrix_data.size()-1){
                data += "/";
            }
        }
        for(Integer e = 0; e < extra_data.size(); e++){
            data += "," + Integer.toString(extra_data.get(e));
        }
        data += ",";
        for (int s = 0; s < walls_data.size(); s++){
            data += Integer.toString(walls_data.get(s));
        }
        return data;
    }

    public void drawAlienMatrix(SpriteBatch spriteBatch){
        Entity entity;
        for (Integer e = 0; e < aliens.size(); e++) {
            entity = aliens.get(e);
            if(entity != null){
                entity.draw(spriteBatch);
                moveAlien(entity);
                for (Integer b = 0; b < player.getBullets().size(); b++) {
                    if(entity.collision(player.getBullets().get(b))){
                        aliens.remove(entity);
                        player.destroyBullet(player.getBullets().get(b));
                        matrix_data.get(e)[0] = 0;
                    }
                }
            }
        }
        if(down){
            extra_data.set(1, extra_data.get(1)+1);
        }else if(back){
            extra_data.set(0, extra_data.get(0)-1);
        }else{
            extra_data.set(0, extra_data.get(0)+1);
        }
        if(!aliens.isEmpty()){
            if(down_distance<=aliens.get(0).getHeight()){
               down_distance++;
            }else{
               down = false;
               down_distance = 0;
            }
        }else{
            initializeMatrix(5,10);
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
        if(entity.getX()+entity.getWidth() >= Gdx.graphics.getWidth()- 50){
            back = true;
            down = true;
        }
        if(entity.getX() <= 390){
            back = false;
            down = true;
        }
    }
    
    @Override
    public void dispose () {
        batch.dispose();
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
