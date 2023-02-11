package com.bloomlife.android.agent;

public interface PushInterface {
	
	void onReceiveMessage(String content, String alias, String topic, boolean hasNotified);
}
