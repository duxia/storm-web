package models.entity;

import java.util.Date;

/**
 * 定义消息实体类
 * @author Dx
 *
 */
public class Message {

	public int ID;
	
	public String CompanyID;
	
	public String VehicleSimID;
	
	public Date GPSTime;
	
	public double GPSLongitude;
	
	public double GPSLatitude;
	
	public int GPSSpeed;
	
	public int GPSDirection;
	
	public int PassengerState;
	
	public int ReadFlag;
	
	public Date CreateDate;

	public Message(int ID, String CompanyID, String VehicleSimID, Date GPSTime,
			double GPSLongitude, double GPSLatitude, int GPSSpeed,
			int GPSDirection, int PassengerState, int ReadFlag, Date CreateDate) {
		this.ID = ID;
		this.CompanyID = CompanyID;
		this.VehicleSimID = VehicleSimID;
		this.GPSTime = GPSTime;
		this.GPSLongitude = GPSLongitude;
		this.GPSLatitude = GPSLatitude;
		this.GPSSpeed = GPSSpeed;
		this.GPSDirection = GPSDirection;
		this.PassengerState = PassengerState;
		this.ReadFlag = ReadFlag;
		this.CreateDate = CreateDate;
	}

	public int getID() {
		return ID;
	}

	public String getCompanyID() {
		return CompanyID;
	}

	public String getVehicleSimID() {
		return VehicleSimID;
	}

	public Date getGPSTime() {
		return GPSTime;
	}

	public double getGPSLongitude() {
		return GPSLongitude;
	}

	public double getGPSLatitude() {
		return GPSLatitude;
	}

	public int getGPSSpeed() {
		return GPSSpeed;
	}

	public int getGPSDirection() {
		return GPSDirection;
	}

	public int getPassengerState() {
		return PassengerState;
	}

	public int getReadFlag() {
		return ReadFlag;
	}

	public Date getCreateDate() {
		return CreateDate;
	}
	
}
