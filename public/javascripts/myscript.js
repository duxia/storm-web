$(document).ready(function(){
	//绑定日期选择器
	$(".form_datetime").datetimepicker({
		format: 'yyyy-mm-dd hh:ii:ss',
		autoclose: true,
		startDate: "2010-09-01 00:00:00",
		pickerPosition: "bottom-left",
		language: "zh-CN",
		initialDate: "2010-09-01 00:00:00",
	});
	//绑定实时推送车辆查询推荐
	$('#car_id').typeahead({
		hint:false,
	}, {
		name: 'countries',
		limit: 100,
		source: new Bloodhound({
				datumTokenizer: Bloodhound.tokenizers.whitespace,
				queryTokenizer: Bloodhound.tokenizers.whitespace,
				prefetch: '/caridPrefetch'
				}),
	});
	//绑定历史查询车辆查询推荐
	$('#offline-carid').typeahead({
		hint:false,
		classNames: {
			menu: "tt-menu2",
		},
	}, {
		name: 'countries',
		limit: 100,
		source: new Bloodhound({
				datumTokenizer: Bloodhound.tokenizers.whitespace,
				queryTokenizer: Bloodhound.tokenizers.whitespace,
				prefetch: '/caridPrefetch'
				}),
	});
});

//全局ws变量
var ws;
var SocketCreated = false;
//热力图全局变量
var heatmapOverlay;
//路书全局变量
var lushu;
//ladda按钮绑定全局变量
var laddaBtn;
//车辆实时查询入口函数
function carSearchOnline(form) {
	var carid = $("#car_id").val();
	if(!carid) {
		alert("车辆不能为空！");
	} else {
		$.ajax({
			type: "POST",
			url:'/carIdCheck',
			data:"car_id="+carid,
			error:function(request) {
	            alert("Connection error");
	        },
	        success:function(data) {
	        	if(data == "1") {
	        		alert("The carid is invalid!");
	        	} else {
	        		//还未建立ws连接
	        		if(!(SocketCreated && (ws.readyState == 0 || ws.readyState == 1))) {
	        			//建立websocket连接
	        			try {
	        				ws = new WebSocket("ws://192.168.100.159:9000/carLocWebSocket");
	        				//console.log("current carid: "+carid);
	        				ws.onopen = function(event) {
	        					WSonOpen(carid)
	        				};
	                        ws.onmessage =function(event) {
	                        	WSonMessage(event)
	                        };
	                        ws.onclose = function(event) {
	                        	WSonClose(event)
	                        };
	                        ws.onerror = function(event) {
	                        	WSonError(event)
	                        };
	                        SocketCreated = true;
						} catch (e) {
							try {
								//兼容火狐
								ws = new MozWebSocket("ws://192.168.100.159:9000/carLocWebSocket");
								ws.onopen = function(event) {
		        					WSonOpen(carid)
		        				};
		                        ws.onmessage =function(event) {
		                        	WSonMessage(event)
		                        };
		                        ws.onclose = function(event) {
		                        	WSonClose(event)
		                        };
		                        ws.onerror = function(event) {
		                        	WSonError(event)
		                        };
			                    SocketCreated = true;
							} catch (e) {
								$("#OLSearchToggle").after('<div class="alert alert-danger">您的浏览器不支持websocket,请更换浏览器!</div>');
							}
						}
	        		} else { //已建立ws连接
	        			ws.send(carid);
	        			$("#OLSearchToggle").next().children("p").get(0).innerText = "当前车辆："+carid;
	        		}
	        	}
	        }
		});
	}
	form.reset();//清空表单
	return false;
}

function WSonOpen(carid) {
	ws.send(carid);//发送第一条信息
	var message = "<table style='width:100%;'><tr><td>建立实时连接成功,正在推送实时车况...</td>";
	message += '<td style="text-align:right"><button type="button" class="btn btn-danger" onclick="closeWS()">断开连接</button></td></tr></table>';
	message += "<div><p>当前车辆："+carid+"</p></div><p></p>";
	$("#OLSearchToggle").after('<div id="CarLocWsSuccess" class="alert alert-success" style="display:none">'+message+'</div>');
	$("#CarLocWsSuccess").fadeIn(1000);
	console.log("连接已经建立!"+" ws.readyState:"+ws.readyState);

}

function WSonMessage(event) {
	var message = jQuery.parseJSON(event.data);
	map.clearOverlays();
	var point = new BMap.Point(message.GPSLongitude,message.GPSLatitude);
	BMap.ConvertorOne.translate(point,0,function (point){//百度地图坐标转换回调函数
		var marker = new BMap.Marker(point,{
			icon:new BMap.Symbol(BMap_Symbol_SHAPE_FORWARD_CLOSED_ARROW, {
			    scale: 3,
			    strokeWeight: 1,
			    rotation: message.GPSDirection,//方向角
			    fillColor: 'red',
			    fillOpacity: 0.8
			  })
			});
		map.addOverlay(marker);
		var label = new BMap.Label("当前经度："+point.lng+",当前纬度："+point.lat,{offset:new BMap.Size(20,-10)});
		label.setStyle({maxWidth:"none"}) ;
		marker.setLabel(label); //添加label
		map.setCenter(point);
		map.setZoom(15);
	});
	//更新时间
	$("#CarLocWsSuccess").children("p").slideToggle(function (){
		$(this).text("更新时间："+message.GPSTime);
		$(this).slideToggle();
	});
	
	console.log($("#CarLocWsSuccess").children("p").text());
	console.log("收到消息: "+message);
}

function WSonClose() {
	console.log("连接已经关闭!");
}

function WSonError() {
	console.log("连接错误!");
}
//关闭websocket连接
function closeWS() {
	ws.close();
	map.clearOverlays();
	map.reset();
	$("#CarLocWsSuccess").fadeOut(function (){
		$(this).remove();
	});
}

//实时聚类查询入口函数
function carClusterOnline(btn) {
	//还未建立ws连接
	if(!(SocketCreated && (ws.readyState == 0 || ws.readyState == 1))) {
		//建立websocket连接
		try {
			ws = new WebSocket("ws://192.168.100.159:9000/carClusterWebSocket");
//			console.log("current carid: "+carid);
			ws.onopen = function(event) {
				WSClusteronOpen(btn)
			};
            ws.onmessage =function(event) {
            	WSClusteronMessage(event)
            };
            ws.onclose = function(event) {
            	WSClusteronClose(event)
            };
            ws.onerror = function(event) {
            	WSClusteronError(event)
            };
            SocketCreated = true;
		} catch (e) {
			try {
				//兼容火狐
				ws = new MozWebSocket("ws://192.168.100.159:9000/carClusterWebSocket");
				ws.onopen = function(event) {
					WSClusteronOpen(carid)
				};
                ws.onmessage =function(event) {
                	WSClusteronMessage(event)
                };
                ws.onclose = function(event) {
                	WSClusteronClose(event)
                };
                ws.onerror = function(event) {
                	WSClusteronError(event)
                };
                SocketCreated = true;
			} catch (e) {
				$("#OLSearchToggle").after('<div class="alert alert-danger">您的浏览器不支持websocket,请更换浏览器!</div>');
			}
		}
	} else { //已建立ws连接
		// TODO:
	}
}

function closeClusterWS(btn) {
	ws.close();
	var parent = $(btn).parent();
	$(btn).remove();
	parent.append('<button class="btn btn-success btn-block" onclick="carClusterOnline(this)">开启实时分析</button>');
	map.clearOverlays();
	map.reset();
	$("#ClusterWsSuccess").fadeOut(function(){
		$(this).remove();
	})
}

function WSClusteronOpen(btn) {
	ws.send("hello");
	var parent = $(btn).parent();
	$(btn).remove();
	parent.append('<button class="btn btn-danger btn-block" onclick="closeClusterWS(this)">关闭实时分析</button>');
	var message = '<div style="line-height:3">建立连接成功,正在推送实时热度分析...</div><p></p>';
	$("#ClusterInfoToggle").after('<div id="ClusterWsSuccess" class="alert alert-success" style="display:none">'+message+'</div>');
	$("#ClusterWsSuccess").fadeIn(1000);
	console.log("连接已经建立!");
	heatmapOverlay = new BMapLib.HeatmapOverlay({"radius":20,"visible":true,});
	map.addOverlay(heatmapOverlay);
}

function WSClusteronMessage() {
	console.log("收到消息："+event.data);
	var message = jQuery.parseJSON(event.data);
	var points = message.clusterResultEntity.ClusterResult;
	heatmapOverlay.setDataSet({data:points});
	heatmapOverlay.show();
	$("#ClusterWsSuccess").children("p").slideToggle(function(){
		$(this).text("当前分析时间为："+message.GPSTime);
		$(this).slideToggle();
	});
//	$("#ClusterWsSuccess").append("<p>当前分析时间为："+message.GPSTime+"</p>");
}

function WSClusteronClose() {
	console.log("连接已经关闭!");
}

function WSClusteronError() {
	console.log("连接错误!");
}
//车辆历史轨迹查询入口函数
function submitHistoryRoute(form) {
	console.log();
	var carid = $("#offline-carid").val();
	var formContent = $(form).serialize();
	if(!carid) {
		alert("车辆不能为空！");
	} else if(!($("#starttime").val())) {
		alert("起始时间不能为空！");
	} else if(!($("#endtime").val())) {
		alert("停止时间不能为空！");
	} else {
		//按钮效果
		var btn = $(form).children("button").get(0);
		laddaBtn = Ladda.create(btn);
		laddaBtn.start();
		//禁用文本编辑
		var allInput = $(form).find("input");
		allInput.attr("disabled",true);
		$.ajax({
			type: "POST",
			url:'/carIdCheck',
			data:"car_id="+carid,
			error:function(request) {
				laddaBtn.stop();
				allInput.attr("disabled",false);
	            alert("Connection error");
	        },
	        success:function(data) {
	        	if(data == "1") {
	        		laddaBtn.stop();
	        		allInput.attr("disabled",false);
	        		alert("The carid is invalid!");
	        	} else {
	        		$.ajax({
	        			type: "POST",
	        			url: "/historyRouteSearch",
	        			data: formContent,
	        			error: function(request) {
	        				laddaBtn.stop();
	        				allInput.attr("disabled",false);
	        				alert("Connection error");
	        			},
	        			success: function(data) {
	        				baiduLushu(data);
	        			}
	        		});
	        	}
	        }
		});
	}

	return false;
}
//调用百度路书功能
function baiduLushu(data) {
//	console.log(data.points[0]);
	var length = data.points.length;
	var arrPois = new Array();
	for(i=0;i<length;i++) {
		arrPois[i] = new BMap.Point(data.points[i].lng,data.points[i].lat);
	}
	//批量坐标转换
	BMap.Convertor.transMore(arrPois,0,callback);

}
//坐标批量转换回调
function callback(points) {
	var arrPois = new Array();
	var xyResult = null;
	for(var index in points){
		xyResult = points[index];
		if(xyResult.error != 0){continue;}//出错就直接返回;
		var point = new BMap.Point(xyResult.x, xyResult.y);
		arrPois.push(point);
	}
	//轨迹图层
	map.addOverlay(new BMap.Polyline(arrPois, {strokeColor: '#111'}));
	map.setViewport(arrPois);
	//路书层
	lushu = new BMapLib.LuShu(map,arrPois,{
	defaultContent:"",
	autoView:true,//是否开启自动视野调整，如果开启那么路书在运动过程中会根据视野自动调整
	icon  : new BMap.Icon('http://developer.baidu.com/map/jsdemo/img/car.png', new BMap.Size(52,26),{anchor : new BMap.Size(27, 13),imageSize:new BMap.Size(35,18)}),
	speed: 4500,
	enableRotation:true,//是否设置marker随着道路的走向进行旋转
	landmarkPois: [
				   {lng:116.314782,lat:39.913508,html:'加油站',pauseTime:2},
				   {lng:116.315391,lat:39.964429,html:'高速公路收费<div><img src="http://map.baidu.com/img/logo-map.gif"/></div>',pauseTime:3},
				   {lng:116.381476,lat:39.974073,html:'肯德基早餐<div><img src="http://ishouji.baidu.com/resource/images/map/show_pic04.gif"/></div>',pauseTime:2}
				]
	}); 
	
	$("#submitHistoryRouteBtn").slideToggle(1000,function(){
		laddaBtn.stop();
		var message = '<p>路线查询成功！可以进行轨迹回放。</p>';
		message += '<table style="width:100%"><tr>';
		message += '<td><button id="lushuStart" class="btn btn-primary btn-sm"><span class="glyphicon glyphicon-play"></span> 开始</button>';
		message += '<button id="lushuPause" class="btn btn-primary btn-sm"><span class="glyphicon glyphicon-pause"></span> 暂停</button>';
		message += '<button id="lushuStop" class="btn btn-primary btn-sm"><span class="glyphicon glyphicon-stop"></span> 结束</button></td>'
		message += '<td style="text-align:right"><button id="lushuRemove" class="btn btn-danger btn-sm" onclick="removeHistoryRouteSuccess()"><span class="glyphicon glyphicon-remove"></span> 清空</button></td>'
		message += '</tr></table>';		
		$(this).parent().after('<div id="HistoryRouteSuccess" class="alert alert-success" style="display:none">'+message+'</div>');
		$("#HistoryRouteSuccess").fadeIn(1000);
		$("#lushuStart").get(0).onclick = function(){
			lushu.start();
		}
		$("#lushuStop").get(0).onclick = function(){
			lushu.stop();
		}
		$("#lushuPause").get(0).onclick = function(){
			lushu.pause();
		}
		$(this).remove();
	});

}
//"清除"按钮响应
function removeHistoryRouteSuccess(){
	map.clearOverlays();
	map.reset();
	$("#HistoryRouteSuccess").fadeOut(function(){
		var form = $(this).prev();
		form.append('<button Id="submitHistoryRouteBtn" type="submit" class="btn btn-success ladda-button" data-style="zoom-out" style="float:right;display:none"><span class="ladda-label">查询</span></button>');
		form.get(0).reset();
		form.find("input").attr("disabled",false);
		$(this).remove();
		$("#submitHistoryRouteBtn").fadeIn(1000);
	});
}