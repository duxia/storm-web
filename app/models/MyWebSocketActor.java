package models;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class MyWebSocketActor extends UntypedActor {
	
	Jedis jedis = null;
	
	public static Props props(ActorRef out) {
		return Props.create(MyWebSocketActor.class , out);
	}
	
	private final ActorRef out;
	
	public MyWebSocketActor(ActorRef out) {
		this.out = out;
	}
	
	@Override
	public void postStop() throws Exception {
		if(jedis != null) {
			jedis.disconnect();
		}
		super.postStop();
	}

	@Override
	public void onReceive(Object message) throws Exception {
//		if (message instanceof String) {
            out.tell("I received your message: " + message, self());
            
    		try {
    			jedis = new Jedis("192.168.100.190",6379,0);
    			JedisPubSub jedisPubSub = new JedisPubSub() {
    				@Override
    				public void onMessage(String channel, String message) {
//    					super.onMessage(channel, message);
    					System.out.println("onMessage:channel["+channel+"],message["+message+"]");
    					out.tell(message,self());
    				}

    				@Override
    				public void onPMessage(String pattern, String channel, String message) {
//    					super.onPMessage(pattern, channel, message);
    					System.out.println("onPMessage:channel["+channel+"],message["+message+"]");
    				}

    				@Override
    				public void onPSubscribe(String pattern, int subscribedChannels) {
//    					super.onPSubscribe(pattern, subscribedChannels);
    					System.out.println("onPSubscribe:pattern["+pattern+"],subscribedChannels["+subscribedChannels+"]");
    				}

    				@Override
    				public void onPUnsubscribe(String pattern, int subscribedChannels) {
//    					super.onPUnsubscribe(pattern, subscribedChannels);
    					System.out.println("onPUnsubscribe:pattern["+pattern+"],subscribedChannels["+subscribedChannels+"]");
    				}

    				@Override
    				public void onSubscribe(String channel, int subscribedChannels) {
//    					super.onSubscribe(channel, subscribedChannels);
    					System.out.println("onSubscribe:channel["+channel+"],subscribedChannels["+subscribedChannels+"]");
    				}

    				@Override
    				public void onUnsubscribe(String channel, int subscribedChannels) {
//    					super.onUnsubscribe(channel, subscribedChannels);
    					System.out.println("onUnsubscribe:channel["+channel+"],subscribedChannels["+subscribedChannels+"]");
    				}
				};
				jedisPubSub.proceed(jedis.getClient(), "news.share","news.blog");
//    			testRedis.proceed(jedis.getClient(), "news.*");
    		} catch (Exception e) {
    			e.printStackTrace();
    		} finally {
    			if (jedis != null) {
    				jedis.disconnect();
    			}
    		}
//        }
	}

}
