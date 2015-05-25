import play.Application;
import play.GlobalSettings;
import play.db.DB;
import redis.clients.jedis.Jedis;
import utils.CarIdSet;
import utils.Conf;
import utils.JdbcConnection;


public class Global  extends GlobalSettings{

	Conf conf = Conf.getInstance();
	
	@Override
	public void onStart(Application arg0) {
		//从Redis中获取所有车辆id集合
		Jedis jedis = new Jedis(conf.getRedisHost(), conf.getRedisPort(), 0);
		CarIdSet.carIdSet = jedis.hkeys(conf.getRedisCurrentCarLocMap());
		jedis.close();
		JdbcConnection.setConnection(DB.getConnection());//初始化JDBC连接
	}

}
