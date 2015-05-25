package models.entity;

import java.util.List;

public class HistoryRouteEntity {

	public List<HistoryRoutePoint> points;
	
	public HistoryRouteEntity(List<HistoryRoutePoint> points) {
		this.points = points;
	}
}
