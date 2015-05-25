package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import play.libs.Json;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import utils.Conf;
import utils.MsgBuildHelper;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class CarLocWSActor extends UntypedActor {
	
	private Jedis jedis;
	private Jedis jedisClient;
	private JedisPubSub jedisPubSub;
	private static Conf conf = Conf.getInstance();
	private String currentCarId;
	/*
	 * 表示sub端链接状态,初始化:未连接
	 */
	private boolean subFlag = false;
	
	public static Props props(ActorRef out) {
		return Props.create(CarLocWSActor.class , out);
	}
	
	private final ActorRef out;
	
	public CarLocWSActor(ActorRef out) {
		this.out = out;
	}
	
	@Override
	public void preStart() throws Exception {
		//建立redis连接
		jedis = new Jedis(conf.getRedisHost(),conf.getRedisPort(),0);
		jedisClient = new Jedis(conf.getRedisHost(), conf.getRedisPort());
		jedisPubSub = new JedisPubSub() {

			@Override
			public void onMessage(String channel, String message) {
//				System.out.println("onMessage:channel["+channel+"],message["+message+"]");
				String[] messageArray = message.split(conf.getColumnDelimiter());
				if(messageArray[conf.getPrimaryKeyIndex()].equals(currentCarId)) {
					String jsonString = MsgBuildHelper.buildWithMap(messageArray);
		            if (jsonString != null) {
		            	out.tell(jsonString, self());
//		            	System.out.println(jsonString);
					} 
				}
			}

			@Override
			public void onSubscribe(String channel, int subscribedChannels) {
				System.out.println("onSubscribe:channel["+channel+"],subscribedChannels["+subscribedChannels+"]");
			}

			@Override
			public void onUnsubscribe(String channel, int subscribedChannels) {
				System.out.println("onUnsubscribe:channel["+channel+"],subscribedChannels["+subscribedChannels+"]");
			}
			
		};

		super.preStart();
	}

	@Override
	public void postStop() throws Exception {
//		self().tell(PoisonPill.getInstance(), self());
		if(subFlag) {
			System.out.println("=========stop-unsubscribe=========");
			jedisPubSub.unsubscribe();
			while(jedisPubSub.isSubscribed()) {
				Thread.sleep(50);
			}
			subFlag = false;
		}
		if(jedis != null) {
			System.out.println("=========stop-disconnect=========");
			jedis.disconnect();
		}
		if(jedisClient != null) {
			jedisClient.disconnect();
		}
		System.out.println("ws connection close!");
//		super.postStop();
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof String) {
			currentCarId = (String) message;
			System.out.println("received message: "+message);
//            out.tell("I received your message: " + message, self());
            
            //从redis中取出最近一次位置记录
            Map<String, String> currentCarLoc = jedisClient.hgetAll(conf.getRedisCurrentCarLocMap());
            String[] messageArray = currentCarLoc.get(currentCarId).split(conf.getColumnDelimiter());
            String jsonString = MsgBuildHelper.buildWithMap(messageArray);
            if (jsonString != null) {
            	out.tell(jsonString, self());
            	System.out.println(jsonString);
			} 
            
            //创建pub/sub子线程
            if(!subFlag) {
            	subFlag = true;
            	new Thread(new Runnable() {
    				@Override
    				public void run() {
//    					jedis.subscribe(jedisPubSub, conf.getRedisChannel());	
    					jedisPubSub.proceed(jedis.getClient(), conf.getRedisCarLocChannel());
    					System.out.println("Exit the subscribe Thread: "+Thread.currentThread().getName());
    				}
    			}).start();
            }
            
            
//            out.tell("redis:"+jedis.smembers("car_id_set"), self());
            
//            if(subFlag) {//已经建立过sub端
//            	System.out.println("=========Unsubcribe=========");
//            	subFlag = false;
////            	try {
//            		jedisPubSub.unsubscribe();
//            		System.out.println(jedisPubSub.isSubscribed());
////				} catch (Exception e) {
////				}
//            		Thread.sleep(5000);
//            		System.out.println(jedisPubSub.isSubscribed());
//            }
//            
//        	System.out.println("=========Subcribe=========");
//        	subFlag = true;
//        	new Thread(new Runnable() {
//				@Override
//				public void run() {
////					jedis.subscribe(jedisPubSub, conf.getRedisChannel());	
//					jedisPubSub.proceed(jedis.getClient(), conf.getRedisChannel());
//					System.out.println("Exit the Thread "+Thread.currentThread().getName());
//				}
//			}).start();
            
        }
	}

}
