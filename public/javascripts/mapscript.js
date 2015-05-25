/**
 * 用于操作百度地图
 */

var map = new BMap.Map("mapcontainer");		// 创建地图实例
//var point = new BMap.Point(118.787752,32.036468);
//var marker = new BMap.Marker(point);  // 创建标注
//map.addOverlay(marker);              // 将标注添加到地图中
//
//var label = new BMap.Label("当前经度："+point.lng+",当前纬度："+point.lat,{offset:new BMap.Size(20,-10)});
//label.setStyle({maxWidth:"none"}) ;
//marker.setLabel(label); //添加label

map.centerAndZoom("南京", 12);
map.enableScrollWheelZoom();   //启用滚轮放大缩小，默认禁用
map.enableContinuousZoom();    //启用地图惯性拖拽，默认禁用
map.addControl(new BMap.NavigationControl());
var top_left_control = new BMap.ScaleControl({anchor: BMAP_ANCHOR_TOP_LEFT});// 左上角，添加比例尺
var top_left_navigation = new BMap.NavigationControl();  //左上角，添加默认缩放平移控件
var top_right_navigation = new BMap.NavigationControl({anchor: BMAP_ANCHOR_TOP_RIGHT, type: BMAP_NAVIGATION_CONTROL_SMALL});
var mapType1 = new BMap.MapTypeControl({mapTypes: [BMAP_NORMAL_MAP,BMAP_HYBRID_MAP]});
var mapType2 = new BMap.MapTypeControl({anchor: BMAP_ANCHOR_TOP_LEFT});

var overView = new BMap.OverviewMapControl();
var overViewOpen = new BMap.OverviewMapControl({isOpen:true, anchor: BMAP_ANCHOR_BOTTOM_RIGHT});
map.addControl(top_left_control);        
map.addControl(top_left_navigation);     
map.addControl(top_right_navigation); 
map.addControl(mapType1);          //2D图，卫星图
map.addControl(mapType2);          //左上角，默认地图控件
map.setCurrentCity("深圳");        //由于有3D图，需要设置城市哦
map.addControl(overView);          //添加默认缩略地图控件
map.addControl(overViewOpen);