<link rel="stylesheet" type="text/css" href="/sy/comm/scalender/css/scalender/jquery-ui-1.10.3.custom-0.css">
<script src="/sy/comm/scalender/js/jquery-ui-1.10.3.custom.js"></script>
<script src="/sy/comm/scalender/js/jquery.ui.datepicker-zh-CN.js"></script>
<div class="scalender" style='position: relative;'>
	<div class='portal-box-title' style="z-index:100;overflow: visible; filter: Alpha(Opacity=0);opacity: 0;position: absolute;top:0"></div> 
	<div id="datepicker" style="width:100%;height:100%;"></div>
</div>
<script>
	$(function() {
 		var eventArr=[];
 		eventArr = loadData();
		$("#datepicker").datepicker({
			inline: true,
			firstDay:0,
			showOtherMonths: true,
			selectOtherMonths: false,
			regional:$.datepicker.regional[ "zh-CN" ],
			beforeShowDay:function(date){
				var d =date.getTime(),
					flag=false,
					index = 0,
					sTime,
					eTime,
					params;
					
				for(var i = 0,len=eventArr.length; i<len; i++){
					sTime = $.datepicker.parseDate('yy-mm-dd',eventArr[i]["CAL_START_TIME"].substring(0,10));
					eTime = $.datepicker.parseDate('yy-mm-dd',eventArr[i]["CAL_END_TIME"].substring(0,10));
					if(d>=sTime.getTime() && d<=eTime.getTime()){
						flag=true;
						index=i;
						params={
							"color":"#"+eventArr[index]["TYPE_COLOR"],
							"title":eventArr[index]["TYPE_NAME"]
						};
						if (params["color"] == '#undefined') {
							params["color"] = 'red';
						}
						if (params["title"] == undefined) {
							params["title"] = eventArr[index]["CAL_TITLE"];
						}
						break;
					} 
				}
				if(flag){
						return [true,params,eventArr[index]["CAL_TITLE"]];
				}else{
					return [true,null,null];
				}
			},
			onSelect:function(data){
				var opt = {};
				<#--opt["_WHERE_"] = " and S_USER = '" + System.getVar("@USER_CODE@") + "' and CAL_START_TIME < '" + data + " 23:59:59' and '" + data + " 00:00:00' < CAL_END_TIME";-->
				opt["_WHERE_"] = " and CAL_ID IN (SELECT CAL_ID FROM SY_COMM_CAL_USERS WHERE USER_CODE = '" + System.getVar("@USER_CODE@") + "') and CAL_START_TIME < '" + data + " 23:59:59' and '" + data + " 00:00:00' < CAL_END_TIME";
				var resultNum = FireFly.doAct("SY_COMM_CAL","finds",opt)["_OKCOUNT_"];
				var url = "SY_COMM_CAL_VIEW.card.do";
				var params = {};
				params["DATA"] = data;
				var strWhere = "";
				if (parseInt(resultNum) > 0) {
					url = "SY_COMM_CAL_VIEW.list.do";
					<#--params["_WHERE_"] = " and S_USER = '" + System.getVar("@USER_CODE@") + "' and CAL_START_TIME <= '" + data + " 23:59:59' and '" + data + " 00:00:00' <= CAL_END_TIME";-->
					strWhere = " and CAL_ID IN (SELECT CAL_ID FROM SY_COMM_CAL_USERS WHERE USER_CODE = '" + System.getVar("@USER_CODE@") + "') and CAL_START_TIME < '" + data + " 23:59:59' and '" + data + " 00:00:00' < CAL_END_TIME";
				}
				var tabOpt = {"url":url,"params":params,"menuFlag":3,"tTitle":"日程"};
				var tabP = jQuery.toJSON(tabOpt);
				tabP = tabP.replace(/\"/g,"'");
				window.open("/sy/comm/page/page.jsp?openTab="+encodeURIComponent(tabP)+"&where="+encodeURIComponent(strWhere));
			},
			onChangeMonthYear:function(year, month,inst){
				 var date = new Date(year,month);
				 eventArr=loadData(date);
			}
		}).on("click",".ui-datepicker-title",function(){
			var htmlStr = jQuery(this).text();
			htmlStr = htmlStr.replace(/[^u4E00-u9FA5]/g,' ');
			htmlStr = htmlStr.split(" ");
			var year = htmlStr[0];
			var month = htmlStr[2];
			if (month <= 9) {
				month = "0"+month;
			}
			var date = year + "-" + month + "-" + '01';
			
			var data = htmlStr[0] + "-" + htmlStr[2] + "-" + "01";
			<#--var url = "SY_COMM_CAL.show.do?initMode=month&initDate=" + data;-->
			var url = "SY_COMM_CAL.showCalendar.do?initDate=" + date;
			var params = {};  
			var options = {"url":url,"params":params,"menuFlag":3,"tTitle":"日程"}; 
			Tab.open(options);
		});
		
		
		function loadData(date){
			var startTime,
				endTime;

			if(!date){
				date = new Date();
			} else {
				<#-- 传入的date值月数是加1的，在这里去掉 -->
				date = new Date(date);
				date.setMonth(date.getMonth()-1);
			}
			startTime = $.datepicker.formatDate('yy-mm-dd',getStartTime(date));
			endTime   = $.datepicker.formatDate('yy-mm-dd',getEndTime(date));
		 	
		 	<#-- var str = "ADN CAL_START_TIME >'"+startTime+"' AND CAL_END_TIME < '"+endTime+"'"; -->
		 	var str = "ADN (CAL_START_TIME >'"+startTime+"' OR CAL_END_TIME < '"+endTime+"')";
		 	<#-- 原迷你日历中数据 -->
			var miniCalArr = FireFly.getListData("SC_COMM_MINI_CAL",{"_extWhere":str})._DATA_;
			
			<#-- var str = " and CAL_ID IN (SELECT CAL_ID FROM SY_COMM_CAL_USERS WHERE USER_CODE = '" + System.getVar("@USER_CODE@") + "') and CAL_START_TIME >'"+startTime+"' AND CAL_END_TIME < '"+endTime+"'"; -->
			var str = " and CAL_ID IN (SELECT CAL_ID FROM SY_COMM_CAL_USERS WHERE USER_CODE = '" + System.getVar("@USER_CODE@") + "') and (CAL_START_TIME >'"+startTime+"' OR CAL_END_TIME < '"+endTime+"')";
			<#-- 日程提醒中的数据 -->
			var calViewArr = FireFly.getListData("SY_COMM_CAL_VIEW",{"_WHERE_":str})._DATA_;
			
			var dataArr = [];
			for (var miniIndex=0; miniIndex<miniCalArr.length; miniIndex++) {
				dataArr.push(miniCalArr[miniIndex]);
			}
			for (var viewIndex=0; viewIndex<calViewArr.length; viewIndex++) {
				dataArr.push(calViewArr[viewIndex]);
			}
			return dataArr;
		}
		
		function getStartTime(date){
			var d = new Date(date);
				d.setDate(1);
			return d;
		}
		function getEndTime(date){
			var d = new Date(date);
				d.setMonth(date.getMonth()+1);
				d.setDate(1);
			return d;
		}
		
	});
</script>