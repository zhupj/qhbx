var _viewer = this;

//构建审批单头部
var gwHeader = new rh.vi.gwHeader({
	"servId" : _viewer.opts.sId,
	"parHandler" : _viewer
});
gwHeader.init();
var nodeCodeBean = FireFly.doAct(_viewer.servId,"getNodeCode",{"S_WF_INST":_viewer.getItem("S_WF_INST").getValue(),"S_WF_NODE":_viewer.getItem("S_WF_NODE").getValue()});
if(nodeCodeBean.NODE_CODE!="2"){
	_viewer.getItem("GW_CONTACT_PHONE").obj.attr("readonly","true");
}
/*if(nodeCodeBean.get()){
	
}*/
var gwExtCard = new rh.vi.gwExtCardView({
	"parHandler" : _viewer
});
gwExtCard.init();

/**************************************************************************************************/
if (_viewer.wfCard && _viewer.wfCard._getBtn("fileToWenku")) {
	_viewer.wfCard._getBtn("fileToWenku").layoutObj.unbind("click").bind("click",function(event){
		var jsFileUrl = FireFly.getContextPath() + "/oa/servjs/OA_FILE_TO_WENKU.js";
	    jQuery.ajax({
	        url: jsFileUrl,
	        type: "GET",
	        dataType: "text",
	        async: false,
	        data: {},
	        success: function(data){
	            try {
	                var servExt = new Function(data);
	                servExt.apply(_viewer);
	            } catch(e){
	            	throw e;
	            }
	        },
	        error: function(){;}
	    });
	});
	
}


//当前正在办理，又有正文存在，则设置文件的权限为 只读
//判断返回getAuthBean()是否为空
var authBean = (_viewer.wfCard)? _viewer.wfCard.getAuthBean():undefined;
if (authBean && authBean.userDoInWf == "true") {
	if (_viewer.getByIdData("EXIST_ZHENGWEN")) { //存在正文 ， 文稿只读
		var fileItem = _viewer.getItem("ZHENGWEN");  //获取到正文 对象
		
    	if (fileItem) { //文稿的编辑隐藏
    		fileItem.obj.find(".file").each(function(i, item){
				if (jQuery(item).text().indexOf("文稿") > 0) { //文稿
					jQuery(item).find(".edit_file").parent().hide(); //隐藏编辑
				}
			});
    	}
	}
}

//获取公开类型对象
var openTypeObj = _viewer.form.getItem("OPEN_TYPE").obj;
//获取公开类型的值
var openTypeVal = openTypeObj.val();
//获取 [是否公开]val，如果为打开，并且为可读状态，则将[公开类型]置为可读
var isOpenObj = _viewer.form.getItem("ISOPEN").obj;

//初始化时候，如果 是否公开 为 否，公开类型则为不可选状态
if ((isOpenObj.val() || UIConst.NO) == UIConst.NO) {
	openTypeObj.attr("disabled", true).addClass("disabled");
}

var isOpenReadOnly = isOpenObj.attr("disabled") || "";

if (isOpenReadOnly == "" && isOpenObj.val() == UIConst.YES) {
	_viewer.form.getItem("OPEN_TYPE").obj.attr("disabled", false).removeClass("disabled");
}

//给[是否公开]绑定事件
isOpenObj.unbind("change").bind("change", function(){
	if (isOpenObj.val() == UIConst.YES) {
		openTypeVal = openTypeVal || UIConst.YES;
		//添加是否公开为 是 的时候，公开类型选取默认值
		if (openTypeVal == "0") {
			openTypeVal = "1";
		}
		_viewer.form.getItem("OPEN_TYPE").obj.attr("disabled", false).removeClass("disabled").val(openTypeVal);
	} else {
		_viewer.form.getItem("OPEN_TYPE").obj.attr("disabled", true).addClass("disabled").val("0");
	}
});





//给对象绑定事件
openTypeObj.unbind("change").bind("change", function(){
	//如果对象值为0，即没有选中
	if((openTypeObj.val() || "") == "") {
		_viewer.cardBarTipError("公开类型不可为空");
		openTypeVal = openTypeVal || UIConst.YES;
		openTypeObj.val(openTypeVal);
	} else {
		openTypeVal = openTypeObj.val();
	}
});

//主送中两个项，要填写其中一项
//主送本单位绑定事件
_viewer.form.getItem("GW_MAIN_TO_CODE").obj.blur(function(){
	//获取主送本单位数值
	var gwMindToCodeVal = _viewer.getItem("GW_MAIN_TO_CODE").getValue() || "";
	//获取主送其他单位数值
	var gwMindToExtVal = _viewer.getItem("GW_MAIN_TO_EXT").getValue() || "";
	//如果主送其他单位和主送本单位没有数值
	if ("" == gwMindToExtVal && "" == gwMindToCodeVal) {
		_viewer.form.getItem("GW_MAIN_TO_CODE").getContainer().showError("该项必须输入！");
		_viewer.form.getItem("GW_MAIN_TO_EXT").getContainer().showError("该项必须输入！");
	} else {
		_viewer.form.getItem("GW_MAIN_TO_CODE").getContainer().showOk();
		_viewer.form.getItem("GW_MAIN_TO_EXT").getContainer().showOk();
	}
});
//主送其他单位绑定事件
_viewer.form.getItem("GW_MAIN_TO_EXT").obj.blur(function(){
	//获取主送本单位数值
	var gwMindToCodeVal = _viewer.getItem("GW_MAIN_TO_CODE").getValue() || "";
	//获取主送其他单位数值
	var gwMindToExtVal = _viewer.getItem("GW_MAIN_TO_EXT").getValue() || "";
	//如果主送其他单位和主送本单位没有数值
	if ("" == gwMindToExtVal && "" == gwMindToCodeVal) {
		_viewer.form.getItem("GW_MAIN_TO_CODE").getContainer().showError("该项必须输入！");
		_viewer.form.getItem("GW_MAIN_TO_EXT").getContainer().showError("该项必须输入！");
	} else {
		_viewer.form.getItem("GW_MAIN_TO_CODE").getContainer().showOk();
		_viewer.form.getItem("GW_MAIN_TO_EXT").getContainer().showOk();
	}
});

//保存前校验主送单位是否有数据
_viewer.beforeSave = function() {
	//获取主送本单位数值
	var gwMindToCodeVal = _viewer.getItem("GW_MAIN_TO_CODE").getValue() || "";
	//获取主送其他单位数值
	var gwMindToExtVal = _viewer.getItem("GW_MAIN_TO_EXT").getValue() || "";
	if ("" == gwMindToExtVal && "" == gwMindToCodeVal) {
		_viewer.form.getItem("GW_MAIN_TO_CODE").getContainer().showError("该项必须输入！");
		_viewer.form.getItem("GW_MAIN_TO_EXT").getContainer().showError("该项必须输入！");
		_viewer.cardBarTipError("校验未通过");
		return false;
	}
}

/**************************************************************************************************/

///**配置项*/
//var FileToWenkuOpts = {
//			"lockFlag":false,//是否锁定必填项
//			"fileType":"",//需要的文件类型，空为全选
//			"notNull":"DOCUMENT_TITLE,DOCUMENT_CHNL__NAME",//必填项
//			"readonly":"",//只读项
//			"disable":"",//失效项
//			"display":"DOCUMENT_CHNL__NAME,DOCUMENT_TITLE,DOCUMENT_CHNL__NAME",//显示项
//			//以上为工作流按钮上可以配置的参数
//			"enableExp":{},//列表操作表达式
//			"mainExp":{"FILE_CAT":["==","ZHENGWEN"],"DIS_NAME":["==","文稿"]},//列表主文档表达式
//			"auxExp":{"FILE_CAT":["!=","ZHENGWEN"],"DIS_NAME":["!=","文稿"]},//列表辅文档表达式
//			"uncheckedExp":{"FILE_CAT":["==","ZHENGWEN"],"DIS_NAME":["!=","文稿"]}//列表不预选中表达式
//		};
//
///**工作流启用配置*/
//if (_viewer.wfCard) {
//	var fileToWenkuBtn = _viewer.wfCard._getBtn("fileToWenku");
//	if(fileToWenkuBtn && fileToWenkuBtn.dataObj && fileToWenkuBtn.dataObj.WFE_PARAM){
//		var wfeBtnParam = StrToJson(fileToWenkuBtn.dataObj.WFE_PARAM);
//		
//		//覆盖默认值
//		if(wfeBtnParam.lockFlag && wfeBtnParam.lockFlag != ""){
//			FileToWenkuOpts["lockFlag"] = wfeBtnParam.lockFlag;
//		}
//		if(wfeBtnParam.fileType && wfeBtnParam.fileType != ""){
//			FileToWenkuOpts["fileType"] = wfeBtnParam.fileType;
//		}
//		if(wfeBtnParam.notNull && wfeBtnParam.notNull != ""){
//			FileToWenkuOpts["notNull"] = wfeBtnParam.notNull;
//		}
//		if(wfeBtnParam.readonly && wfeBtnParam.readonly != ""){
//			FileToWenkuOpts["readonly"] = wfeBtnParam.readonly;
//		}
//		if(wfeBtnParam.disable && wfeBtnParam.disable != ""){
//			FileToWenkuOpts["disable"] = wfeBtnParam.disable;
//		}
//		if(wfeBtnParam.display && wfeBtnParam.display != ""){
//			FileToWenkuOpts["display"] = wfeBtnParam.display;
//		}
//	}
//}
////发文页面添加模式下加载正文模板
//debugger;
//if(_viewer.byIdData["_ADD_"] == "true"){
//	var fileData = _viewer.form._loadFile("ZHENGWEN");
//	_viewer.getItem("ZHENGWEN").fillData(fileData);
//}
/**
 * 相关文件
 */
//获取相关文件的字段
var relate = _viewer.getItem("GW_RELATE");
//修改相关文件链接为按钮形式
relate.obj.css({"border":"0px #91bdea solid"}).parent().css({"margin-left": "2px"});
/*//单击确定后进行回调处理，如果不被覆盖则保存关联相关文件。
relate.callBack = function (arr){
	gwExtCard.getRelate(arr,_viewer,this);
}*/
//relate.afterDelete = function(relateId){
//	var datas={};
//	datas["_NOPAGE_"] = true;
//	datas["_searchWhere"] = " and RELATE_ID='"+relateId+"'";
//	//根据当前删除的相关ID获取关联的数据
//	var getData = FireFly.getListData("OA_GW_RELATE_FILE",datas);
//	var data =getData._DATA_;
//	//如果有关联的数据才进行删除
//	if(data.length){
//		var files ="";
//		var relateFiles = "";
//		//获取当前要删除的关联的文件ID以及主键
//		for(var i=0; i<data.length;i++){
//			files += data[i].FILE_ID+",";
//			relateFiles += data[i].RF_ID+",";
//		}
//		//删除关联的文件以及关联表中记录
//		FireFly.listDelete("SY_COMM_FILE",{"_PK_":files});
//		FireFly.listDelete("OA_GW_RELATE_FILE",{"_PK_":relateFiles});
//		_viewer.getItem("FUJIAN").refresh();
//	}
//};

//设置主送抄送的名称
try{
	var mainToCodeItem = _viewer.form.getItem("GW_MAIN_TO_CODE");
	if(mainToCodeItem && mainToCodeItem.type=='DictChoose' && !mainToCodeItem.isHidden){
		mainToCodeItem.setText(_viewer.form.getItem("GW_MAIN_TO").getValue());
	}
	
	var copyToCodeItem = _viewer.form.getItem("GW_COPY_TO_CODE");
	if(copyToCodeItem && copyToCodeItem.type=='DictChoose' && !copyToCodeItem.isHidden){
		copyToCodeItem.setText(_viewer.form.getItem("GW_COPY_TO").getValue());
	}
	
}catch(e){
	console.error("OA_GW_TYPE_FW_card.js:" + e.message);
}

//当前用户是总公司的，则显示公开类型，否则隐藏
var odeptLevel = System.getVar("@ODEPT_LEVEL@");
if (odeptLevel != "2") {
	var isOpenTr = jQuery("#ISOPEN-TR");
	isOpenTr.find("td").each(function(){
		var thisTd = jQuery(this);
		if (thisTd.attr("rowspan") == "3") {
			thisTd.attr("rowspan","2");
			jQuery("#S_USER-CODE").after(thisTd);
		}
	});
	/*isOpenTr.hide();*/
}

/** 追加打印份数  **/
if(_viewer.wfCard && _viewer.wfCard._getBtn("appendPrintNum")){
	_viewer.wfCard._getBtn("appendPrintNum").layoutObj.unbind("click").click(function(){
		var printInfo = gwExtCard._getPrintFileInfo();
		//是否有错误
		if(Tools.actIsSuccessed(printInfo)){
			var rhGwSeal = new rh.vi.rhGwSeal();
			rhGwSeal.appendPringNum(printInfo);
		}else{
			alert(printInfo._MSG_);
		}
	});
}

//使用吉大正元印章系统查看盖章文件：预览功能。
var zwItem = _viewer.getItem("ZHENGWEN");
if (zwItem) {
	var zwItemObj = zwItem.getObj();
	if(System.getVar("@C_MOBILE_SERVER@") == "true"){ //如果是移动办公系统，则不处理
		return;
	}
	//如果是电脑，则用印章系统查看带水印的文件
	if(System.getVar("@C_DEVICE_TYPE@") == "DESKTOP"){
		jQuery(".view_file", zwItemObj).each(function() {
			var fileBean = jQuery(this).data("fileBean");
			if (fileBean && fileBean["ITEM_CODE"] == "ZHENGWEN") { //如果文件子类型也为正文，则
				var existSealPdf = _viewer.getByIdData("EXIST_SEAL_PDF_FILE");
				if(existSealPdf == "true"){ //如果该文件为已经改过章的文件
					jQuery(this).unbind().click(function(){ 
						//覆盖原有打开文件的方法，使用印章系统的页面打开文件，并增加水印。
						var viewFileObj = jQuery(this);
						var param = {"fileId":viewFileObj.data("id")};
						var result = FireFly.doAct("OA_GW_SEAL_FILE","getViewSealFileInfo",param,false);	
						if(result.URL){
							window.open(result.URL);
						} else if (result._MSG_ && result._MSG_.indexOf("ERROR,") == 0){ //如果后台出错
							alert(result._MSG_);
						}
					});	
				}
			}
		});
	}
}


//显示、隐藏是否公开以及公开类型
var isOpen = _viewer.wfCard.getCustomVarContent("isOpen");
if(!isOpen){//如果可以公开==false 则不显示
	jQuery("#ISOPEN-TR").css("display","none");
}else{
	jQuery("#OA_GW_TMPL_FW_GS-ISOPEN").attr("disabled",false); 
	jQuery("#OA_GW_TMPL_FW_GS-ISOPEN").css("color","black"); 
	jQuery("#OA_GW_TMPL_FW_GS-ISOPEN").css("background-color","white"); 
}


/**
 * 批量打印
 */
var printDaGdInfoObj = _viewer.wfCard._getBtn('printDaGdInfo');
if (printDaGdInfoObj) {
	// 动态装载意见代码
	Load.scriptJS("/oa/zh/batch-print.js");
	new rh.oa.batchPrint({
		"parHandler": _viewer,
		"actObj":printDaGdInfoObj,
		"printPic":true,
		"printAudit":true
	});
}


//如果是文印节点 则是否公开下拉框只读
if(_viewer.form.getItem("S_WF_NODE").getValue()=="11 文印"){
	jQuery("#OA_GW_TMPL_FW_GS-ISOPEN").attr("disabled","disabled"); 
	jQuery("#OOA_GW_TMPL_FW_GS-OPEN_TYPE").attr("disabled","disabled");
}
/**
*批量下载
*/
if(_viewer.wfCard && _viewer.wfCard.isWorkflow()){//流程启动
	//该节点是否允许批量下载
	var batchDownload = _viewer.wfCard.getCustomVarContent("batchDownload")||"false";
	//页面上哪些文件字段允许批量下载
	var fileItemCodes = _viewer.wfCard.getCustomVarContent("fileItemCodes")||"";
	if("true"==batchDownload && fileItemCodes!=""){
		//构造批量下载按钮
		var fileCodes = fileItemCodes.split(",");
		for(var i=0; i<fileCodes.length; i++){
			var file = _viewer.form.getAttachFile(fileCodes[i]);
			if(file){
				//渲染批量下载按钮
				file._container.find(".uploadBtntr").first().after("<span class='batchDownloadBtn'><a class='rh-icon rhGrid-btnBar-a' actcode='upload'>" +
			"<span class='rh-icon-inner' style='padding:0 7px 2px 5px;'>批量下载</span></a></span>");
				//绑定下载事件
				file._container.find(".batchDownloadBtn").data("filecode",fileCodes[i]).click(function(){
					var currFile = _viewer.form.getAttachFile(jQuery(this).data("filecode"));
					var fileList = currFile._container.find(".file");
					if(fileList && fileList.length>0){
						window.open(FireFly.getContextPath() + '/' + _viewer.servId + '.batchDownloadFile.do?data=' + 
							encodeURIComponent(jQuery.toJSON({"SERV_ID":_viewer.servId,"DATA_ID":_viewer.getPKCode(),"FILE_CODES":jQuery(this).data("filecode")})));
					}else{
						alert("没有文件可下载");
					}
				});
			}
		}
	}
}
/**
*正文转pdf
*/
if(_viewer.wfCard && _viewer.wfCard.isWorkflow()){
	// 流程已启动，某个节点配置了转pdf按钮
	var transPdfBtn = _viewer.wfCard._getBtn("TRANS_PDF");
	if(transPdfBtn){
		transPdfBtn.layoutObj.unbind("click").bind("click", function(){
			//判断是否已转过pdf，并将正文信息回传
			FireFly.doAct(_viewer.servId, "getGwZwFileInfo", {"FILE_CAT":"ZHENGWEN","DATA_ID":_viewer._pkCode}, false, false, function(result){
				if(result.HAS_PDF && result.HAS_PDF=="true"){
					alert("正文已转pdf");
				} else {
					if(result.HAS_ZW && result.HAS_ZW=="true"){
						var zwId = result.ZW_FILE_ID;
						var zwName = result.ZW_FILE_NAME;
						var zwServId = result.ZW_SERV_ID;
						var params = {"fileId":zwId,"dataId":_viewer._pkCode,"servId":zwServId,"fileCat":"ZHENGWEN"};
						readOfficeFileExt(zwName, "/file/"+zwId, false, false, true, params);
						jQuery(document).data("_viewer",_viewer);
						jQuery(document).data("zwServId",zwServId);
						//回调事件
						if(typeof(window["officeClientCloseCallBack"]) != "function"){
							window["officeClientCloseCallBack"] = function(){
								//刷新页面
								window.location.reload();
								window["officeClientCloseCallBack"] = null;
							}
						}
					} else {
						alert("未找到正文红头文件");
					}
				}
			});
		});
	}
}



/**-----------------------------------------------添加样式--------------------------------------------------------------*/
//html模板中将输入框width设为100%
jQuery(".right").css({"width":"100%"});

//html模板中div width设为100%
jQuery(".right div").css({"width":"100%","padding-bottom":"0px","padding-right":"2px"});

//表格边距
jQuery(".rh-tmpl-tabel td").css({"padding":"3px 8px 3px 5px"});

//文件的td样式不要padding
jQuery(".file td").css({"padding":"0"});

//文件头部表格内部输入框
jQuery(".gwHeaderTable td span").css({"line-height":"2"});

//文本域在表格中的左外边距
jQuery(".ui-textarea-default").css({"margin-left":"0px","width":"100%"});

//将表格中的下拉框对齐
jQuery(".gw-year-code").css({"width":"150px"});

// 重置表格高度
jQuery(".ui-staticText-default").css({"height":"auto"});
jQuery(".hidden-td").css({"padding":"0px 0px 0px 0px","border":"0px #FFF solid"});

//针对公文编号做处理
jQuery("#GW_YEAR-left").css({"padding":"0px"});
jQuery("#GW_YEAR-right").css({"padding":"0px"});
jQuery("td[code='GW_YEAR']").css({"padding":"0px"}).find("input").css({"height":"20px","line-height":"20px"});
jQuery("td[code='GW_YEAR_NUMBER']").css({"padding":"0px"}).find("input").css({"height":"20px","line-height":"20px"});

//主送样式
jQuery("div[code='GW_MAIN_TO_CODE']").find("div[class='inner']").css({"padding-right":"2px"});
jQuery("div[code='GW_MAIN_TO_EXT']").find("div[class='inner']").css({"padding-right":"2px"});



