package com.chat;

import com.google.appengine.api.xmpp.JID;

public class User {
	JID user;
	String videoID;
	
	public User(JID user, String id){
		this.user = user;
		this.videoID = id;
	}
}
