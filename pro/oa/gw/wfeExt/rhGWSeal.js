GLOBAL.namespace("rh.vi");

rh.vi.gwSeal = function(options) {
	this.servId = options.servId || null;
	this.parHandler = options.parHandler || null;
	this._viewer = this.parHandler;
}

/**
 * 初始化
 */

rh.vi.gwSeal.prototype.init = function(){

	var _self = this;
	if (_self._viewer.wfCard && _self._viewer.wfCard.isWorkflow()){
		var sealVar = _self._viewer.wfCard.getCustomVarContent("canSeal");
		var printVar = _self._viewer.wfCard.getCustomVarContent("canPrint");
		//判断是否需要套红头，不需要套红头，则根据配置来显示盖章以及打印按钮
		var noRedHead = _self._viewer.wfCard.getCustomVarContent("noRedHead");
		var canSeal = false;
		if(sealVar && sealVar == "true"  && ((_self._viewer.getItem("GW_GY") && _self._viewer.getItem("GW_GY").getValue()=="1") || !_self._viewer.getItem("GW_GY"))){
			canSeal = true;
		}
		var canPrint = false;
		if(printVar && printVar == "true" && ((_self._viewer.getItem("GW_DY") && _self._viewer.getItem("GW_DY").getValue()=="1") || !_self._viewer.getItem("GW_DY"))){
			canPrint = true;
		}
		var printNum = _self._viewer.wfCard.getCustomVarContent("printNum");
		// 页面数据项，是否盖章，是否打印
		if (canSeal || canPrint){
			var itemCodes = _self._viewer.wfCard.getCustomVarContent("sealItemCode");
			if (itemCodes && itemCodes.length > 0){
				var codes = itemCodes.split(";");
				if (codes && codes.length > 0){
					for (var i=0; i<codes.length; i++){
						var fileItem = _self._viewer.form.getItem(codes[i]);
						if (fileItem){
							var fileData = fileItem.getValue();
							// 找出所有文件
							var fileList = fileItem._container.find(".file");
							if (fileList && fileList.length>0){
								for (var j=0; j<fileList.length; j++){
									var $strArr;
									var fileId = $(fileList[j]).attr("icode");
									var fileName = fileData[fileId]["FILE_NAME"];
									if(noRedHead && noRedHead == "true"){
										
									} else {
										//判断是否已套红头
										if(fileList.length < 2 || fileData[fileId]["ITEM_CODE"] != "ZHENGWEN"){
											//未套红头，则不允许盖章以及打印
											canSeal = false;
											canPrint = false;
										}
									}
									if(canSeal){ // 构造盖章按钮
										$strArr = new Array();
										$strArr.push("<span class='icon'>");
										$strArr.push("<span class='iconC icon-card-seal'></span>");
										$strArr.push("<a class='seal_file' href='javascript:;'>盖章</a>");
										$strArr.push("</span>");
										var $file = jQuery($strArr.join(""));
										$(fileList[j]).find(".edit").append($file);
										// 注册点击事件
										$file.find(".seal_file").data("id", fileId).data("name", fileName).click(function(){
											var $this = jQuery(this);
											var deptWhereSql = "";
											var deptLevel = System.getVar("@ODEPT_LEVEL@");
											if(deptLevel!="2"){
												deptWhereSql = "and KEEP_ODEPT_CODE in (select DEPT_CODE from SY_ORG_DEPT where DEPT_TYPE = 2 and CODE_PATH like ^%"+System.getVar("@ODEPT_CODE@")+"%^)";
											}else{
												deptWhereSql = "and KEEP_ODEPT_CODE = ^"+System.getVar("@ODEPT_CODE@")+"^";
											}
											var configStr = "BN_SEAL_BASIC_INFO_LIST,{'TARGET':'ID~SEAL_TYPE1~SEAL_NAME~SEAL_STATE~SEAL_OWNER_USER~KEEP_TDEPT_CODE~EKEY_ADDRESS~SEAL_CODE','HIDE':'ID~SEAL_OWNER_USER~KEEP_TDEPT_CODE~EKEY_ADDRESS~SEAL_CODE','HIDEOPTION':'ID','TYPE':'single','SOURCE':'ID~SEAL_TYPE1~SEAL_NAME~SEAL_STATE~SEAL_OWNER_USER~KEEP_TDEPT_CODE~EKEY_ADDRESS~SEAL_CODE','EXTWHERE':'and SEAL_STATE=1 "+deptWhereSql+"'}";	
											var options = {"config" :configStr,"rebackCodes":"ID~SEAL_TYPE1~SEAL_NAME~SEAL_STATE~EKEY_ADDRESS~SEAL_CODE","parHandler":this,"formHandler":this,"replaceCallBack":function(result){
												var ekeyAddress;
												if(result.EKEY_ADDRESS==""){
													ekeyAddress = ['-1'];
												}else{
													ekeyAddress = result.EKEY_ADDRESS.split(",");
												}
											
												_self.seal($this.data("id"), $this.data("name"),result.ID,ekeyAddress[0],result.SEAL_CODE);
											},"hideAdvancedSearch":true};
											var queryView = new rh.vi.rhSelectListView(options);
											queryView.show(null,[50,0]);
										
										});
									}
									if(canPrint){ // 构造打印按钮
										$strArr = new Array();
										$strArr.push("<span class='icon'>");
										$strArr.push("<span class='iconC icon-card-print'></span>");
										$strArr.push("<a class='print_file' href='javascript:;'>打印</a>");
										$strArr.push("</span>");
										var $file = jQuery($strArr.join(""));
										$(fileList[j]).find(".edit").append($file);
										var num = 0;
										//配置的打印份数
										if(printNum){
											num = printNum;
										}else if(_self._viewer.getItem("ZW_PRINT_NUM")){
											num = _self._viewer.getItem("ZW_PRINT_NUM").getValue();
										}
										// 注册点击事件
										$file.find(".print_file").data("id", fileId).data("name", fileName).data("printnum", num).click(function(){
											var $this = jQuery(this);
											_self.printFile($this.data("id"), $this.data("name"),parseInt($this.data("printnum")));
										});
									}
								}
							}
						}
					}
				}
			}
		}
	}
}

/**
 * 盖章方法
 */
rh.vi.gwSeal.prototype.seal = function(fileId,fileName,sealID,sealIndex,eKeySN) {
	var _self = this;
	var uploadUrl = "/file/" + fileId + "?keepMetaData=true";
	uploadUrl = uploadUrl + "&model=saveHist";
	var _officeParams = {"revision":false,"isSeal":true,"dataId":_self._viewer._pkCode,"fileId":fileId,"servId":_self._viewer.servId,"sealID":sealID,"sealIndex":sealIndex,"eKeySN":eKeySN};
	editOfficeFileExt(fileName, "/file/" + fileId, uploadUrl, _officeParams);
}

/**
 * 打印方法
 */
rh.vi.gwSeal.prototype.printFile = function(fileId,fileName,printnum) {
	var _self = this;
	var params = {"fileId":fileId,"dataId":_self._viewer._pkCode,"servId":_self._viewer.servId,"printNum":printnum};
	readOfficeFileExt(fileName, "/file/" + fileId, false, true, true, params);
}
