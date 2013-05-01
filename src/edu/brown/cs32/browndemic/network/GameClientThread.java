/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.brown.cs32.browndemic.network;
import edu.brown.cs32.browndemic.world.MainWorld;
import java.net.*;
import java.io.*;

/**
 *
 * @author gcarling
 */
public class GameClientThread extends Thread{

    //the socket this thread runs on
    private Socket _socket;
    //the client this thread is running for
    private GameClient _client;
    //the input to this thread
    private ObjectInputStream _input;

    //constructor, sets up basic stuff
    public GameClientThread(GameClient client, Socket socket){
        _client = client;
        _socket = socket;
        start();
    }

    /**
     * Close down this thread
     * @throws java.io.IOException
     */
    public void close() throws IOException{
        _input.close();
    }

    //run method: this runs as soon as the constructor finishes, this is how
    //a thread works. it BLOCKS at the initializtation of _input, then reads
    //in a loop
    @Override
    public void run(){
        try{
            _input = new ObjectInputStream(_socket.getInputStream());
            while (true){
                try{
                    GameData input = (GameData)_input.readObject();
                    _client.handle(input);
                }
                catch(IOException e){
                    _client.stop();
                }
                catch(ClassNotFoundException e){
                    _client.stop();
                }
            }
        }
        catch(IOException e){
            System.out.println("Error in GameClientThread when reading.");
        }
    }

}
