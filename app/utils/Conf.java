package utils;

import java.util.ArrayList;
/**
 * 单例类,保存全局配置
 * @author Dx
 *
 */
public class Conf {

	/*
	 * 单例模式
	 */
	private static final Conf conf = new Conf();
	/*
	 * 数据库链接
	 */
	private final String JdbcDriver = "com.mysql.jdbc.Driver";
	private final String MySqlHost = "jdbc:mysql://192.168.100.158:3306/test";
	private final String MySqlUser = "root";
	private final String MySqlPassword = "root";
	private final String ClusterResultTableName = "ClusterResult";
	private final String ClusterResultColGPSTime = "GPSTime";
	private final String ClusterResultColCreateTime = "CreateTime";
	private final String ClusterResultColClusterResult = "ClusterResult";

	/*
	 * 原始数据表信息(模拟数据源)
	 */
	private final String columnDelimiter = ",";
	private final int primaryKeyIndex = 2;
	/*
	 * kafka配置
	 */
	private final String zkHosts = "dxclustermaster:2181,dxclusterslave1:2181,dxclusterslave2:2181";
	private final ArrayList<String> zkServers = new ArrayList<String>();
	private final String groupId = "group3";
	private final String topic = "CarInfo";
	private final String kafkaBrokeList = "dxclustermaster:6667,dxclusterslave1:6667,dxclusterslave2:6667";
	/*
	 * Redis配置
	 */
	private final String redisHost = "dxclustermaster";
	private final int redisPort = 6379;
	private final String redisCarLocChannel = "CarLocChannel";
	private final String redisCarClusterChannel = "CarClusterChannel";
	private final String redisCurrentCarLocMap = "redisCurrentCarLocMap";//车辆当前位置缓存map
	private final String redisCurrentClusterResultMap = "redisCurrentClusterResultMap";//实时聚类结果缓存map
	private final String redisCurrentCarIdSet = "car_id_set";
	/*
	 * storm配置
	 */
	private final int emitFrequencyInSeconds = 1;//tickTuple发射频率
	private final String entityClusterBoltOutputStreamStr = "ClusterResultEntityJsonStr";//EntityClusterBolt发射的json字符串流
	private final String entityClusterBoltOutputStreamObj = "ClusterResultEntity";//EntityClusterBolt发射的对象流
	
	private Conf() {
		zkServers.add("dxclustermaster");
		zkServers.add("dxclusterslave1");
		zkServers.add("dxclusterslave2");
	}
	/**
	 * 返回单例类实例
	 * @return
	 */
	public static Conf getInstance() {
		return conf;
	}
	
	public String getJdbcDriver() {
		return JdbcDriver;
	}

	public String getMySqlHost() {
		return MySqlHost;
	}

	public String getMySqlUser() {
		return MySqlUser;
	}

	public String getMySqlPassword() {
		return MySqlPassword;
	}

	public String getClusterResultTableName() {
		return ClusterResultTableName;
	}
	public String getClusterResultColGPSTime() {
		return ClusterResultColGPSTime;
	}
	public String getClusterResultColCreateTime() {
		return ClusterResultColCreateTime;
	}
	public String getClusterResultColClusterResult() {
		return ClusterResultColClusterResult;
	}
	public String getColumnDelimiter() {
		return columnDelimiter;
	}
	
	public int getPrimaryKeyIndex() {
		return primaryKeyIndex;
	}
	
	public String getZkHosts() {
		return zkHosts;
	}
	public ArrayList<String> getZkServers() {
		return zkServers;
	}
	public String getGroupId() {
		return groupId;
	}

	public String getTopic() {
		return topic;
	}

	public String getKafkaBrokeList() {
		return kafkaBrokeList;
	}
	
	public String getRedisHost() {
		return redisHost;
	}
	
	public int getRedisPort() {
		return redisPort;
	}
	
	public String getRedisCarLocChannel() {
		return redisCarLocChannel;
	}
	public String getRedisCarClusterChannel() {
		return redisCarClusterChannel;
	}
	public String getRedisCurrentCarLocMap() {
		return redisCurrentCarLocMap;
	}
	
	public String getRedisCurrentClusterResultMap() {
		return redisCurrentClusterResultMap;
	}
	public String getRedisCurrentCarIdSet() {
		return redisCurrentCarIdSet;
	}
	
	public int getEmitFrequencyInSeconds() {
		return emitFrequencyInSeconds;
	}
	
	public String getEntityClusterBoltOutputStreamStr() {
		return entityClusterBoltOutputStreamStr;
	}
	
	public String getEntityClusterBoltOutputStreamObj() {
		return entityClusterBoltOutputStreamObj;
	}
}
