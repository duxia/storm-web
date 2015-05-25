package utils;

import akka.actor.ActorRef;
import redis.clients.jedis.JedisPubSub;

public class RedisSub extends JedisPubSub {
	
	private final ActorRef out;
	
	public RedisSub(ActorRef out) {
		this.out = out;
	}

	@Override
	public void onMessage(String channel, String message) {
		System.out.println("onMessage:channel["+channel+"],message["+message+"]");
		out.tell(message,out);
	}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		System.out.println("onSubscribe:channel["+channel+"],subscribedChannels["+subscribedChannels+"]");
	}
	
	
}
