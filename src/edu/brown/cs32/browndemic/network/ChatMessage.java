package edu.brown.cs32.browndemic.network;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Graham
 */
public class ChatMessage implements GameData{
	private static final long serialVersionUID = -5719873045901402495L;
	
	//the message that this object carries
    private String _message;
    
    public ChatMessage(String m){
        _message = m;
    }
    
    public String getMessage(){
        return _message;
    }
    
    @Override
    public String getID(){
        return "M";
    }
    
}
