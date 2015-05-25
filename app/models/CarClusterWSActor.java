package models;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import utils.Conf;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * 该类是对车辆实时聚类WebSocket的响应
 * @author Administrator
 *
 */
public class CarClusterWSActor extends UntypedActor {

	private Jedis jedis;
	private Jedis jedisClient;
	private JedisPubSub jedisPubSub;
	private static Conf conf = Conf.getInstance();

	/*
	 * 表示sub端链接状态,初始化:未连接
	 */
	private boolean subFlag = false;
	
	public static Props props(ActorRef out) {
		return Props.create(CarClusterWSActor.class, out);
	}

	private final ActorRef out;
	
	public CarClusterWSActor(ActorRef out) {
		this.out = out;
	}
	
	/**
	 * 在开启WS链接前调用,建立资源连接
	 */
	@Override
	public void preStart() throws Exception {
		jedis = new Jedis(conf.getRedisHost(),conf.getRedisPort(),0);
		jedisClient = new Jedis(conf.getRedisHost(), conf.getRedisPort());
		jedisPubSub = new JedisPubSub() {

			@Override
			public void onMessage(String channel, String message) {
				out.tell(message, self());
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
	
	/**
	 * 在接收关闭命令后调用,关闭打开的资源
	 */
	@Override
	public void postStop() throws Exception {
		if(subFlag) {
			System.out.println("=========stop-unsubscribe=========");
			jedisPubSub.unsubscribe();
			while(jedisPubSub.isSubscribed()) {
				Thread.sleep(50);
			}
			subFlag = false;
		}
		if(jedis != null) {
			jedis.close();
		}
		System.out.println("ws connection close!");
		super.postStop();
	}
	
	/**
	 * 接收客户端消息时响应,返回新的消息
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof String) {
			
			String jsonStr = jedisClient.get(conf.getRedisCurrentClusterResultMap());
			out.tell(jsonStr, self());
			
			if(!subFlag) {
				subFlag = true;
            	new Thread(new Runnable() {
    				@Override
    				public void run() {
    					jedisPubSub.proceed(jedis.getClient(), conf.getRedisCarClusterChannel());
    					System.out.println("Exit the subscribe Thread: "+Thread.currentThread().getName());
    				}
    			}).start();
			}
		}
	}

}
