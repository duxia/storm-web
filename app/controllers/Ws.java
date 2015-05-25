package controllers;

import models.MyWebSocketActor;
import play.libs.F.*;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import views.html.*;
import akka.actor.*;

public class Ws extends Controller {
	
	public static Result test() {
		return ok(websocket.render());
	}
	public static WebSocket<String> socket() {
		return WebSocket.withActor(new Function<ActorRef, Props>() {
	        public Props apply(ActorRef out) throws Throwable {
	            return MyWebSocketActor.props(out);
	        }
	    });
	}
	public static WebSocket<String> websocket() {
		return new WebSocket<String>() {
			
			@Override
			public void onReady(WebSocket.In<String> in,WebSocket.Out<String> out) {
				// TODO Auto-generated method stub
				final WebSocket.Out<String> output = out;
				// For each event received on the socket,
	            in.onMessage(new Callback<String>() {
	                public void invoke(String event) {

	                    // Log events to the console
	                    System.out.println(event);

	                    Jedis jedis = null;
	    	    		try {
	    	    			jedis = new Jedis("192.168.100.190",6379,0);
	    	    			JedisPubSub jedisPubSub = new JedisPubSub() {
	    	    				@Override
	    	    				public void onMessage(String channel, String message) {
	    	    					// TODO Auto-generated method stub
//	    	    					super.onMessage(channel, message);
	    	    					System.out.println("onMessage:channel["+channel+"],message["+message+"]");
	    	    					output.write(message);
	    	    				}
	    
	    	    				@Override
	    	    				public void onPMessage(String pattern, String channel, String message) {
	    	    					// TODO Auto-generated method stub
//	    	    					super.onPMessage(pattern, channel, message);
	    	    					System.out.println("onPMessage:channel["+channel+"],message["+message+"]");
	    	    				}
	    
	    	    				@Override
	    	    				public void onPSubscribe(String pattern, int subscribedChannels) {
	    	    					// TODO Auto-generated method stub
//	    	    					super.onPSubscribe(pattern, subscribedChannels);
	    	    					System.out.println("onPSubscribe:pattern["+pattern+"],subscribedChannels["+subscribedChannels+"]");
	    	    				}
	    
	    	    				@Override
	    	    				public void onPUnsubscribe(String pattern, int subscribedChannels) {
	    	    					// TODO Auto-generated method stub
//	    	    					super.onPUnsubscribe(pattern, subscribedChannels);
	    	    					System.out.println("onPUnsubscribe:pattern["+pattern+"],subscribedChannels["+subscribedChannels+"]");
	    	    				}
	    
	    	    				@Override
	    	    				public void onSubscribe(String channel, int subscribedChannels) {
	    	    					// TODO Auto-generated method stub
//	    	    					super.onSubscribe(channel, subscribedChannels);
	    	    					System.out.println("onSubscribe:channel["+channel+"],subscribedChannels["+subscribedChannels+"]");
	    	    				}
	    
	    	    				@Override
	    	    				public void onUnsubscribe(String channel, int subscribedChannels) {
	    	    					// TODO Auto-generated method stub
//	    	    					super.onUnsubscribe(channel, subscribedChannels);
	    	    					System.out.println("onUnsubscribe:channel["+channel+"],subscribedChannels["+subscribedChannels+"]");
	    	    				}
	    					};
	    					jedisPubSub.proceed(jedis.getClient(), "news.share","news.blog");
//	    	    			testRedis.proceed(jedis.getClient(), "news.*");
	    	    		} catch (Exception e) {
	    	    			e.printStackTrace();
	    	    		} finally {
	    	    			if (jedis != null) {
	    	    				jedis.disconnect();
	    	    			}
	    	    		}
	                }
	            });

	            // When the socket is closed.
	            in.onClose(new Callback0() {
	                public void invoke() {

	                    System.out.println("Disconnected");

	                }
	            });

//	            Jedis jedis = null;
//	    		try {
//	    			jedis = new Jedis("192.168.100.190",6379,0);
//	    			JedisPubSub jedisPubSub = new JedisPubSub() {
//	    				@Override
//	    				public void onMessage(String channel, String message) {
//	    					// TODO Auto-generated method stub
////	    					super.onMessage(channel, message);
//	    					System.out.println("onMessage:channel["+channel+"],message["+message+"]");
//	    					output.write(message);
//	    				}
//
//	    				@Override
//	    				public void onPMessage(String pattern, String channel, String message) {
//	    					// TODO Auto-generated method stub
////	    					super.onPMessage(pattern, channel, message);
//	    					System.out.println("onPMessage:channel["+channel+"],message["+message+"]");
//	    				}
//
//	    				@Override
//	    				public void onPSubscribe(String pattern, int subscribedChannels) {
//	    					// TODO Auto-generated method stub
////	    					super.onPSubscribe(pattern, subscribedChannels);
//	    					System.out.println("onPSubscribe:pattern["+pattern+"],subscribedChannels["+subscribedChannels+"]");
//	    				}
//
//	    				@Override
//	    				public void onPUnsubscribe(String pattern, int subscribedChannels) {
//	    					// TODO Auto-generated method stub
////	    					super.onPUnsubscribe(pattern, subscribedChannels);
//	    					System.out.println("onPUnsubscribe:pattern["+pattern+"],subscribedChannels["+subscribedChannels+"]");
//	    				}
//
//	    				@Override
//	    				public void onSubscribe(String channel, int subscribedChannels) {
//	    					// TODO Auto-generated method stub
////	    					super.onSubscribe(channel, subscribedChannels);
//	    					System.out.println("onSubscribe:channel["+channel+"],subscribedChannels["+subscribedChannels+"]");
//	    				}
//
//	    				@Override
//	    				public void onUnsubscribe(String channel, int subscribedChannels) {
//	    					// TODO Auto-generated method stub
////	    					super.onUnsubscribe(channel, subscribedChannels);
//	    					System.out.println("onUnsubscribe:channel["+channel+"],subscribedChannels["+subscribedChannels+"]");
//	    				}
//					};
//					jedisPubSub.proceed(jedis.getClient(), "news.share","news.blog");
////	    			testRedis.proceed(jedis.getClient(), "news.*");
//	    		} catch (Exception e) {
//	    			e.printStackTrace();
//	    		} finally {
//	    			if (jedis != null) {
//	    				jedis.disconnect();
//	    			}
//	    		}

	            // Send a single 'Hello!' message
	    		out.write("Hello World!");
//	    		out.close();
//	    		System.out.println("+++++++++++++++++");
//	            for(int i=0;i<10;i++) {
//	            	out.write("Hello!"+i);
//	            	try {
//	            		System.out.println("===========");
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//	            }
	            
			}
			
		};
	}
}
