/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tec.game;

import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author aaronsolera
 */
public class Client implements Runnable{
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Boolean running = true;
    private String recieved_data;

    public Client(){}
    
    public Boolean connect(){
        try {
            socket = new Socket("localhost",8081);
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Successed connection to the server");
            return true;
        } catch (IOException ex) {
            return false;
        }
        
    }
    
    @Override
    public void run() {
        while(running){
            if(socket != null){
                    try {
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        recieved_data = in.readLine();
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
            }
        }
    }
    
    public void stop() throws IOException{
        socket.close();
        running = false;
    }
    
    public String recieve(){
        return recieved_data;
    }
    
    public void send(String message) throws IOException{
        out.println(message);
    }
}
