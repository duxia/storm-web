package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.db.DB;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.F.Function;
import play.libs.Json;
import play.mvc.*;
import utils.CarIdSet;
import utils.JdbcConnection;
import utils.MsgBuildHelper;
import views.html.*;
import models.*;
import models.entity.HistoryRoutePoint;
import models.entity.Message;
import models.entity.HistoryRouteEntity;

public class Application extends Controller {
    
    // render index page
//    public static Result index() {
//        return ok(index.render());
//    }
    
    // render home page
    public static Result home() {
    	return ok(home.render());
    }
    
    public static Result baidumap() {
		return ok(baidumap.render());
	}
    
    //车牌号检测
    public static Result carIdCheck() {
    	DynamicForm requestDat = Form.form().bindFromRequest();
    	String carId = requestDat.get("car_id");
    	if (!CarIdSet.carIdSet.contains(carId)) {
    		return ok("1");//failed! carid is invalid!
		}
    	return ok("0");//success!
    }

    //实时车况推送
    public static WebSocket<String> carLocWebSocket() {
		return WebSocket.withActor(new Function<ActorRef, Props>() {
	        public Props apply(ActorRef out) throws Throwable {
	            return CarLocWSActor.props(out);
	        }
	    });
	}
    
    //实时聚类推送
    public static WebSocket<String> carClusterWebSocket() {
		return WebSocket.withActor(new Function<ActorRef, Props>() {
	        public Props apply(ActorRef out) throws Throwable {
	            return CarClusterWSActor.props(out);
	        }
	    });
	}
    
    //车辆历史轨迹查询
    public static Result historyRouteSearch() {
    	DynamicForm requestDat = Form.form().bindFromRequest();
		String carId = requestDat.get("offline-carid");
		String startTime = requestDat.get("starttime");
		String endTime = requestDat.get("endtime");
		Connection connection = JdbcConnection.getConnection();
		String sql = "select * from VehicleData20100901 where VehicleSimID="+carId+" and GPSTime between \""+startTime+"\" and \""+endTime+"\" order by ID";
		System.out.println(sql);
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			int colCount = resultSet.getMetaData().getColumnCount(); 
			String[] messages = new String[colCount];
			List<HistoryRoutePoint> list = new ArrayList<HistoryRoutePoint>();
			while(resultSet.next()) {
				for(int i=0;i<colCount;i++) {
					messages[i] = resultSet.getString(i+1); 
				}
				HistoryRoutePoint point = MsgBuildHelper.buildHistoryRoutePoint(messages);
				if(point != null) {
					list.add(point);
				}
			}
			HistoryRouteEntity resultMessages = new HistoryRouteEntity(list);
			System.out.println(Json.toJson(resultMessages));
			return ok(Json.toJson(resultMessages));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ok();
	}
    
    //自动提示预取数据接口
    public static Result caridPrefetch() {
		return ok(Json.toJson(CarIdSet.getCarIdSet()));
	}
    //测试
    public static Result testladda() {
		return ok(test.render());
	}
    
}
