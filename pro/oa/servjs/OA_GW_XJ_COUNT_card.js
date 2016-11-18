var _viewer = this;

var beginTime = _viewer.getItem("BEGIN_TIME"); //开始时间
var endTime = _viewer.getItem("END_TIME"); //结束时间

//绑定开始时间和结束时间联动
rhDate.compareTwoDate(beginTime.obj, endTime.obj, "yyyy-MM-dd");

_viewer.getBtn("save").remove();

//绑定事件
_viewer.getBtn("count").unbind("click").bind("click", function() {
	outExeclFile();
});

//去除收发文字典项
_viewer.getItem("COUNT_TYPE").obj.find("option").each(function(){
	if (jQuery(this).val().indexOf("OA_GW_TMPL_FW") >= 0) {
		jQuery(this).remove();
	}
	if (jQuery(this).val().indexOf("OA_GW_TMPL_SW") >= 0) {
		jQuery(this).remove();
	}
});

//调用后台程序，生成excel文件
function outExeclFile() {
	var odeptStr = _viewer.getItem("ODEPT_CODE").getText();
	var data = {"FULL_NAME":odeptStr + "下级机构上报审批单数量统计",
				"FILE_NAME":odeptStr + "下级机构上报审批单数量统计",
				"ODEPT_CODE":_viewer.getItem("ODEPT_CODE").getValue(),
				"BEGIN_TIME":beginTime.getValue(),
				"END_TIME":endTime.getValue(),
				"XDOC_KEY":"OA_GW_XJ_COUNT",
				"COUNT_TYPE":_viewer.getItem("COUNT_TYPE").getValue()
			   };
	var out = FireFly.doAct("SY_XDOC_FALG","isExiteData",data);
	if (out["_MSG_"].indexOf("ERROR,") >= 0) {
		_viewer.cardBarTipError(out["_MSG_"].replace("ERROR,",""));
	} else {
		var paramBean = FireFly.doAct("SY_XDOC_FALG","initData",data);
		runXdoc(paramBean["data"]);
		//FireFly.doFormAct("SY_XDOC_FALG", "run",data);
	}
}

function runXdoc(data){
	XDoc.server = data["URL"];
	var jdbcName = data["jdbc_name"] || "rhoajdbc/rhoa";
	data["jdbc_name"] = jdbcName;
	data["_format"] = data["format"] || "docx";
	data["_filename"] = data["fullName"] || "data";
	XDoc.run(data["filePath"], data, "newwindow");
}