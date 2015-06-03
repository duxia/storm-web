package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import play.Play;
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
	private final String JdbcDriver;// = "com.mysql.jdbc.Driver";
	private final String MySqlHost;// = "jdbc:mysql://192.168.100.158:3306/test";
	private final String MySqlUser;// = "root";
	private final String MySqlPassword;// = "root";
	private final String ClusterResultTableName;// = "ClusterResult";
	private final String ClusterResultColGPSTime;// = "GPSTime";
	private final String ClusterResultColCreateTime;// = "CreateTime";
	private final String ClusterResultColClusterResult;// = "ClusterResult";
	private final String CarGPSLogTableName;// = "CarGPSLog";
	private final int batchSize;// = 100;//持久化数据库batch大小

	/*
	 * 原始数据表信息(模拟数据源)
	 */
	private final String columnDelimiter;// = ",";
	private final int primaryKeyIndex;// = 2;
	/*
	 * kafka配置
	 */
	private final String zkHosts;// = "dxclustermaster:2181,dxclusterslave1:2181,dxclusterslave2:2181";
	private final Integer zkPort;// = 2181;
	private final boolean forceFromStart;// = false;
	private final ArrayList<String> zkServers = new ArrayList<String>();
	private final String groupId;// = "group3";
	private final String topic;// = "CarInfo";
	private final int batchBlock;// = 10000;//定义从mysql中一次出去的数据条数
	private final int sleepMilliSecond;// = 2000; //定义个batchBlock直接休眠的间隔
	private final String kafkaBrokeList;// = "dxclustermaster:6667,dxclusterslave1:6667,dxclusterslave2:6667";
	/*
	 * Redis配置
	 */
	private final String redisHost;// = "dxclustermaster";
	private final int redisPort;// = 6379;
	private final String redisCarLocChannel;// = "CarLocChannel";
	private final String redisCarClusterChannel;// = "CarClusterChannel";
	private final String redisCurrentCarLocMap;// = "redisCurrentCarLocMap";//车辆当前位置缓存map
	private final String redisCurrentClusterResultMap;// = "redisCurrentClusterResultMap";//实时聚类结果缓存map
	private final String redisCurrentCarIdSet;// = "car_id_set";
	/*
	 * storm配置
	 */
	private final int spoutNum;// = 1;
	private final int redisPubBolt1Num;// = 1;
	private final int currentCarLocPersistentBoltNum;// = 1;
	private final int carLocBuildBoltNum;// = 1;
	private final int entityClusterBoltNum;// = 1;
	private final int redisPubBolt2Num;// = 1;
	private final int clusterResultPersistentBoltNum;// = 1;
	private final int emitFrequencyInSeconds;// = 1;//tickTuple发射频率
	private final String entityClusterBoltOutputStreamStr;// = "ClusterResultEntityJsonStr";//EntityClusterBolt发射的json字符串流
	private final String entityClusterBoltOutputStreamObj;// = "ClusterResultEntity";//EntityClusterBolt发射的对象流
	/*
	 * 聚类参数
	 */
	private final double eps;// = 60;//簇扫描半径
	private final int minPts;// = 10;//最小包含点数阈值
	
	private Conf() {
		String baseDirectory = System.getProperty("user.dir");
		String confFile = baseDirectory + "/conf/conf.properties";
		Properties prop = new Properties();
		try {
		      FileInputStream in = new FileInputStream(confFile);
		      prop.load(in);
		    } catch (FileNotFoundException e) {
		      throw new ExceptionInInitializerError(e);
		    } catch (IOException e) {
		      throw new ExceptionInInitializerError(e);
		    }
		//===db config===
		JdbcDriver = prop.getProperty("JdbcDriver","com.mysql.jdbc.Driver");
		MySqlHost = prop.getProperty("MySqlHost","jdbc:mysql://192.168.100.158:3306/test");
		MySqlUser = prop.getProperty("MySqlUser","root");
		MySqlPassword = prop.getProperty("MySqlPassword","root");
		ClusterResultTableName = prop.getProperty("ClusterResultTableName","ClusterResult");
		ClusterResultColGPSTime = prop.getProperty("ClusterResultColGPSTime","GPSTime");
		ClusterResultColCreateTime = prop.getProperty("ClusterResultColCreateTime","CreateTime");
		ClusterResultColClusterResult = prop.getProperty("ClusterResultColClusterResult","ClusterResult");
		CarGPSLogTableName = prop.getProperty("CarGPSLogTableName","CarGPSLog");
		batchSize = Integer.parseInt(prop.getProperty("batchSize","100"));
		columnDelimiter = prop.getProperty("columnDelimiter",",");
		primaryKeyIndex = Integer.parseInt(prop.getProperty("primaryKeyIndex","2"));
		//===kafka config===
		zkHosts = prop.getProperty("zkHosts");
		zkPort = Integer.valueOf(prop.getProperty("zkPort","2181"));
		forceFromStart = Boolean.parseBoolean(prop.getProperty("forceFromStart","false"));
		for(String zkServer : prop.getProperty("zkServers").split(","))
			zkServers.add(zkServer);
		groupId = prop.getProperty("groupId");
		topic = prop.getProperty("topic");
		batchBlock = Integer.parseInt(prop.getProperty("batchBlock","10000"));
		sleepMilliSecond = Integer.parseInt(prop.getProperty("sleepMilliSecond","2000"));
		kafkaBrokeList = prop.getProperty("kafkaBrokeList");
		//===Redis配置===
		redisHost = prop.getProperty("redisHost");
		redisPort = Integer.parseInt(prop.getProperty("redisPort","6379"));
		redisCarLocChannel = prop.getProperty("redisCarLocChannel","CarLocChannel");
		redisCarClusterChannel = prop.getProperty("redisCarClusterChannel","CarClusterChannel");
		redisCurrentCarLocMap = prop.getProperty("redisCurrentCarLocMap","redisCurrentCarLocMap");
		redisCurrentClusterResultMap = prop.getProperty("redisCurrentClusterResultMap","redisCurrentClusterResultMap");
		redisCurrentCarIdSet = prop.getProperty("redisCurrentCarIdSet","car_id_set");
		//===storm配置===
		spoutNum = Integer.parseInt(prop.getProperty("spoutNum","1"));
		redisPubBolt1Num = Integer.parseInt(prop.getProperty("redisPubBolt1Num","1"));
		currentCarLocPersistentBoltNum = Integer.parseInt(prop.getProperty("currentCarLocPersistentBoltNum","1"));
		carLocBuildBoltNum = Integer.parseInt(prop.getProperty("carLocBuildBoltNum","1"));
		entityClusterBoltNum = Integer.parseInt(prop.getProperty("entityClusterBoltNum","1"));
		redisPubBolt2Num = Integer.parseInt(prop.getProperty("redisPubBolt2Num","1"));
		clusterResultPersistentBoltNum = Integer.parseInt(prop.getProperty("clusterResultPersistentBoltNum","1"));
		emitFrequencyInSeconds = Integer.parseInt(prop.getProperty("emitFrequencyInSeconds","1"));
		entityClusterBoltOutputStreamStr = prop.getProperty("entityClusterBoltOutputStreamStr","ClusterResultEntityJsonStr");
		entityClusterBoltOutputStreamObj = prop.getProperty("entityClusterBoltOutputStreamObj","ClusterResultEntity");
		//===聚类参数===
		eps = Double.parseDouble(prop.getProperty("eps","60"));
		minPts = Integer.parseInt(prop.getProperty("minPts","10"));
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
	public String getCarGPSLogTableName() {
		return CarGPSLogTableName;
	}
	public int getBatchSize() {
		return batchSize;
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
	public Integer getZkPort() {
		return zkPort;
	}
	public boolean isForceFromStart() {
		return forceFromStart;
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

	public int getBatchBlock() {
		return batchBlock;
	}
	public int getSleepMilliSecond() {
		return sleepMilliSecond;
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
	
	public int getSpoutNum() {
		return spoutNum;
	}
	public int getRedisPubBolt1Num() {
		return redisPubBolt1Num;
	}
	public int getCurrentCarLocPersistentBoltNum() {
		return currentCarLocPersistentBoltNum;
	}
	public int getCarLocBuildBoltNum() {
		return carLocBuildBoltNum;
	}
	public int getEntityClusterBoltNum() {
		return entityClusterBoltNum;
	}
	public int getRedisPubBolt2Num() {
		return redisPubBolt2Num;
	}
	public int getClusterResultPersistentBoltNum() {
		return clusterResultPersistentBoltNum;
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
	public double getEps() {
		return eps;
	}
	public int getMinPts() {
		return minPts;
	}
}
