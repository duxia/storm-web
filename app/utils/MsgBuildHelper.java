package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import play.libs.Json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import models.entity.HistoryRoutePoint;
import models.entity.Message;

public class MsgBuildHelper {
	
	public static HistoryRoutePoint buildHistoryRoutePoint(String[] messages) {
		try {
			double GPSLongitude = Double.parseDouble(messages[4]);
			double GPSLatitude = Double.parseDouble(messages[5]);
			HistoryRoutePoint point = new HistoryRoutePoint(GPSLongitude, GPSLatitude);
			return point;
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 创建Message实体类
	 * @param messages
	 * @return
	 */
	public static Message buildMessageEntity(String[] messages) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			int ID = Integer.parseInt(messages[0]);
			String CompanyID = messages[1];
			String VehicleSimID = messages[2];
			Date GPSTime = sdf.parse(messages[3]);
			double GPSLongitude = Double.parseDouble(messages[4]);
			double GPSLatitude = Double.parseDouble(messages[5]);
			int GPSSpeed = Integer.parseInt(messages[6]);
			int GPSDirection = Integer.parseInt(messages[7]);
			int PassengerState = Integer.parseInt(messages[8]);
			int ReadFlag = Integer.parseInt(messages[9]);
			Date CreateDate = sdf.parse(messages[10]);
			Message message = new Message(
					ID, 
					CompanyID, 
					VehicleSimID, 
					GPSTime, 
					GPSLongitude, 
					GPSLatitude,
					GPSSpeed,
					GPSDirection,
					PassengerState,
					ReadFlag,
					CreateDate);
			return message;
		} catch (ParseException e) {
		} catch (NumberFormatException e) {
		}
		return null;
	}

	/**
	 * 根据消息创建消息实例并转换为json字符串
	 * @param messages
	 * @return
	 */
	public static String buildWithObj(String[] messages) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String jsonstr = null;
		try {
			int ID = Integer.parseInt(messages[0]);
			String CompanyID = messages[1];
			String VehicleSimID = messages[2];
			Date GPSTime = sdf.parse(messages[3]);
			double GPSLongitude = Double.parseDouble(messages[4]);
			double GPSLatitude = Double.parseDouble(messages[5]);
			int GPSSpeed = Integer.parseInt(messages[6]);
			int GPSDirection = Integer.parseInt(messages[7]);
			int PassengerState = Integer.parseInt(messages[8]);
			int ReadFlag = Integer.parseInt(messages[9]);
			Date CreateDate = sdf.parse(messages[10]);
			Message message = new Message(
					ID, 
					CompanyID, 
					VehicleSimID, 
					GPSTime, 
					GPSLongitude, 
					GPSLatitude,
					GPSSpeed,
					GPSDirection,
					PassengerState,
					ReadFlag,
					CreateDate);
			Gson gson = new GsonBuilder().create();
			jsonstr = gson.toJson(message);
			return jsonstr;
		} catch (ParseException e) {
		} catch (NumberFormatException e) {
		}
		return jsonstr;
	}
	
	/**
	 * 利用map创建json字符串
	 * @param messages
	 * @return
	 */
	public static String buildWithMap(String[] messages) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("ID", messages[0]);
		map.put("CompanyID", messages[1]);
		map.put("VehicleSimID", messages[2]);
		map.put("GPSTime", messages[3]);
		map.put("GPSLongitude", messages[4]);
		map.put("GPSLatitude", messages[5]);
		map.put("GPSSpeed", messages[6]);
		map.put("GPSDirection", messages[7]);
		map.put("PassengerState", messages[8]);
		map.put("ReadFlag", messages[9]);
		map.put("CreateDate", messages[10]);
		return Json.stringify(Json.toJson(map));
	}
}
