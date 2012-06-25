package com.chat;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

@SuppressWarnings("serial")
public class ChattingServiceServlet extends HttpServlet {
		private static final Logger LOG = Logger.getLogger(ChattingServiceServlet.class.getName());
		private LinkedList<User> users = new LinkedList<User>();
			  
			  @Override
			  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
			    
			    // Parse incoming message
			    XMPPService xmpp = XMPPServiceFactory.getXMPPService();
			    Message msg = xmpp.parseMessage(req);
			    JID jid = msg.getFromJid();
			    String body = msg.getBody();
			   
			    // Analyze the received message
			    
			    //Send the video ID
			    if(body.charAt(0) == 'i'){
			    	LOG.info(jid.getId() + ": " + body);
			    	int m;
			    	for(m = 0; m < users.size(); m ++){
			    		if(users.get(m).user.toString().equals(getRealID(jid).toString()))
			    			break;
			    	}
			    	
			    	//if found
			    	if(m < users.size())
			    		users.get(m).videoID = body.substring(1);
			    	//if not found
			    	else{
			    		users.add(new User(getRealID(jid) ,body.substring(1)));
			    	}
			    	
			    	//indicate that the user has entered the chat room
			    	String response = "You have entered chatting room: "+body.substring(1)+"\nThe below users are in the same chatting room with you ";
			    	msg = new MessageBuilder().withRecipientJids(jid).withBody(response).build();
			    	xmpp.sendMessage(msg);
			    	
			    	//Used to print out the current users
			    	
			    	for(int n = 0 ; n < users.size(); n++){	
			    		if(users.get(n).videoID.equals(body.substring(1))){
			    			String userinfo = users.get(n).user.toString();
			    			msg = new MessageBuilder().withRecipientJids(jid).withBody(userinfo).build();
			    			xmpp.sendMessage(msg);
				    	}
			    	}
			    }
			    
			    //Send text
			    else if(body.charAt(0) == 'c'){
			    	LOG.info(jid.getId() + ": " + body);
			    	//Find the video ID of the current user
			    	int index;
			    	for(index = 0; index < users.size(); index ++){
			    		if(users.get(index).user.toString().equals(getRealID(jid).toString()))
			    			break;
			    	}
			    	//Find the users with the same vdieo ID
			    	if(index < users.size()){
			    		LinkedList<JID> jids = getJIDsID(users.get(index).videoID);
			    		String response = jid.toString()+": "+body.substring(1);
			    	
			    		for(int n = 0 ; n < jids.size(); n++){			    		
			    			msg = new MessageBuilder().withRecipientJids(jids.get(n)).withBody(response).build();
			    			xmpp.sendMessage(msg);
			    		}
			    	}
			    }
			  }
			  
			  
			  public JID getRealID(JID receiveID){
				  StringBuffer sb = new StringBuffer();
				  int i = 5;
				  
				  while(receiveID.toString().charAt(i) != '/'){
					  sb.append(receiveID.toString().charAt(i));
					  i++;
					  assert i < receiveID.toString().length();
				  }
				  
				  return new JID(sb.toString().trim());
			  }
			  
			  public LinkedList<JID> getJIDsID(String video){
				  LinkedList<JID> jids = new LinkedList<JID>();
				  for(int i= 0; i < users.size();i++){
					  if(users.get(i).videoID.equals(video))
						  jids.add(users.get(i).user);
				  }
				  return jids;
			  }
			  
			  public LinkedList<String> getVideoIDs(){
				  LinkedList<String> ids = new LinkedList<String>();
				  for(int i = 0; i < users.size(); i ++){
					  if(!ids.contains(users.get(i).videoID))
						  ids.add(users.get(i).videoID);
				  }
				  return ids;
			  }
	}

