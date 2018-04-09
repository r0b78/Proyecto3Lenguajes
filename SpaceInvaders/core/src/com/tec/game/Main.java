package com.tec.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends ApplicationAdapter {
	private SpriteBatch batch;
	private Player player;
        private Sprite logo, limit;
        private ArrayList<Entity> aliens;
        private boolean back = false, down = false,connected = false;
        private Integer down_distance = 0;
        private Client client;
        private BitmapFont font;
        
        private String matrix = 
                    "2/2/2/2/2/2/2/2/2/2/"
                  + "1/1/1/1/1/0/1/1/1/1/"
                  + "3/3/3/0/3/3/3/0/3/3/"
                  + "1/1/1/1/1/0/1/1/1/1/"
                  + "2/2/2/2/2/2/2/2/2/2"
                  + ",390,600";
        
	@Override
	public void create (){
            client = new Client();
            batch = new SpriteBatch();
            logo = new Sprite(new Texture("logo.png"));
            limit = new Sprite(new Texture("limit.png"));
            player = new Player("player.png",390f,70f,40f,25f,5f);
            aliens = new ArrayList<Entity>();
            font = new BitmapFont();
            
            initializeMatrix(matrix,5,10);
            limit.setBounds(370, 50, 600, 600);
            connected = client.connect();
            if(connected == true){
                new Thread(client).start();
            }
        }

	@Override
	public void render () {
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
                Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                
                keyboardEventHandler();
                
		batch.begin();
                logo.setBounds(100, Gdx.graphics.getHeight()-100, 200, 70);
                logo.draw(batch);
                ////////////////////////////////////
                try {
                    client.send(matrix);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.err.println(client.recieve());
                ////////////////////////////////////
                limit.draw(batch);
                player.draw(batch);
                drawAlienMatrix(batch);
		batch.end();
            }
	}
	
        public void keyboardEventHandler(){
            if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
                player.moveLeft();
            }
            if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                player.moveRight();
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                player.shoot();
            }
        }
        
        public ArrayList<Integer> parseData(String data){
            String[] d = data.split(",");
            String[] type = d[0].split("/");
            ArrayList<Integer> info =  new ArrayList<Integer>();
            
            for (Integer t = 0; t < type.length; t++) {
                info.add(Integer.parseInt(type[t]));
            }
            for (Integer t = 1; t < d.length; t++) {
                info.add(Integer.parseInt(d[t]));
            }
            return info;
        }
        
        public void initializeMatrix(String matrix,Integer rows, Integer columns){
            ArrayList<Integer> data = parseData(matrix);
            Integer xi = data.get(rows*columns), yi = data.get((rows*columns)+1);
            Entity entity;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns & (c+(columns*r)) < data.size(); c++) {
                    if(null == data.get((c+(columns*r)))){
                        continue;
                    }else switch (data.get((c+(columns*r)))) {
                        case 1:
                            entity = new Squid("squid.png",(xi+(36f*c)),(yi-(30f*r)),36f,30f,1f);
                            System.out.println("A squid was added.");
                            break;
                        case 2:
                            entity = new Crab("crab.png",(xi+(36f*c)),(yi-(30f*r)),36f,30f,1f);
                            System.out.println("A crab was added.");
                            break;
                        case 3:
                            entity = new Octopus("octopus.png",(xi+(36f*c)),(yi-(30f*r)),36f,30f,1f);
                            System.out.println("An octopus was added.");
                            break;
                        default:
                            continue;
                    }
                    aliens.add(entity);
                }
            }
        }
         
        public void drawAlienMatrix(SpriteBatch spriteBatch){
            Entity entity;
            for (Integer e = 0; e < aliens.size(); e++) {
                entity = aliens.get(e);
                if(entity != null){
                    entity.draw(spriteBatch);
                    moveAlien(entity);
                    for (int b = 0; b < player.getBullets().size(); b++) {
                        if(entity.collision(player.getBullets().get(b))){
                            aliens.remove(entity);
                            player.destroyBullet(player.getBullets().get(b));
                        }
                    }
                }
            }
            if(!aliens.isEmpty()){
                if(down_distance<=aliens.get(0).getHeight()){
                   down_distance++;
                }else{
                   down = false;
                   down_distance = 0;
                }
            }else{
                initializeMatrix(
                    "2/2/2/2/2/2/2/2/2/2/"
                  + "1/1/1/1/1/0/1/1/1/1/"
                  + "3/3/3/0/3/3/3/0/3/3/"
                  + "1/1/1/1/1/0/1/1/1/1/"
                  + "2/2/2/2/2/2/2/2/2/2"
                  + ",390,600",5,10);
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
}
