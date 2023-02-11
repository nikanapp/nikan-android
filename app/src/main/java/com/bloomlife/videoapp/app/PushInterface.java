package com.bloomlife.videoapp.app;

public interface PushInterface {
	
	void onReceiveMessage(String content, String alias, String topic, boolean hasNotified);
}
