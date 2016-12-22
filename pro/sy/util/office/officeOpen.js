var TANGER_OCX_bDocOpen = false; //标识是否已经打开了文档
var TANGER_OCX_OBJ = null; //标识控件对象
var NO_SAVE_TO_URL = "文件保存不成功";
var NOT_OPEN_FILE = "没有打开文件";

//office对象，可能是word、excel、wps、et
var _office = undefined;

/**
*	初始化_officeParam对象的值
**/
function initOfficeParam(){
	var fileAction = document.all.fileAction.value;
	if (fileAction.indexOf(";read;") >= 0) {
		_officeParam.read = true;
	}else if(fileAction.indexOf(';edit;') >= 0){
		_officeParam.edit = true;
	}else{
		_officeParam.read = true;
	}
	
	//扩展名
	var fileName = document.all.fileName.value;
	var extName = getFileExtName(fileName);
	if (extName == null || extName == "") {
		alert("文件名为不能为空！");
		return false;
	}
	extName = extName.toLowerCase();
	
	_officeParam.extName = extName;

	// 盖章用户名
	_officeParam.userLoginName = document.all.userLoginName.value;
	//是否盖章
	if(fileAction.indexOf(";seal;") >= 0){
		_officeParam.isSeal = true;
		// 印章ID
		_officeParam.sealID = document.all.sealID.value;
		//印章在EKey中索引号
		_officeParam.sealIndex = document.all.sealIndex.value;
		//eKey序列号
		_officeParam.eKeySN = document.all.eKeySN.value;
		// 文件ID
		_officeParam.fileId = document.all.fileId.value;
		// 数据ID
		_officeParam.dataId = document.all.dataId.value;
		// 服务ID
		_officeParam.servId = document.all.servId.value;
		// 是否加盖骑缝章
		_officeParam.isQfz = document.all.isQfz.value;
	}
	
	//是否记录修改痕迹
	if(fileAction.indexOf(";revision;") >= 0){
		_officeParam.revision = true;
	}

	//显示修改痕迹
	if(fileAction.indexOf(";showRevision;") >= 0){
		_officeParam.showRevision = true;
	}
			
	//是否能打印
	//if(fileAction.indexOf(";print;") >= 0){
	//	_officeParam.print = true;
	//}
	//不支持使用下拉菜单中的打印选项
	_officeParam.print = false;
	/** 编辑（查看）word文件时，是否显示文件修改痕迹，默认值为true（显示） **/
	var confDisRev = document.all.displayRev.value;
	if(confDisRev == "true"){
		_officeParam.displayRev = true;
	}else{
		_officeParam.displayRev = false;
	}
	
	//alert(_officeParam.displayRev);
	
	_officeParam.setOfficeVer(TANGER_OCX_OBJ.GetOfficeVer());
	_officeParam.setWpsVer(TANGER_OCX_OBJ.GetWPSVer());
	
	// 参数传递采用UTF8编码
	TANGER_OCX_OBJ.IsUseUTF8Data = true;
	
	//打开本地文件还是网络文件
	var downLoadUrl = document.all.downLoadUrl.value;
	
	var isLocalFile = (downLoadUrl.substring(0,4) == 'file');
	var localFile = "";
	if(isLocalFile){
		localFile = downLoadUrl.substring(6,downLoadUrl.length);
		_officeParam.isLocalFile = true;
		_officeParam.localFile = localFile;	
	}else{
		_officeParam.httpUrl = downLoadUrl;	
	}
	addCustomItemMenu();
	return true;
}

function addCustomItemMenu(){
	for(var i=0;i<_menuItems.length;i++){
		var menuItem = _menuItems[i];
		TANGER_OCX_OBJ.AddCustomButtonOnMenu(menuItem.id,menuItem.name,false);
	}
}

/**打开文件**/
function openFile(){
	TANGER_OCX_OBJ = document.all("TANGER_OCX");
	var result = initOfficeParam();
	if(!result){
		return ;
	}
	TANGER_OCX_OBJ.Statusbar = false;
	TANGER_OCX_OBJ.Titlebar = false;
	TANGER_OCX_OBJ.IsNoCopy = false;
	TANGER_OCX_OBJ.FileNew = false;
	TANGER_OCX_OBJ.FileOpen = false;
	TANGER_OCX_OBJ.FileClose = false;
	TANGER_OCX_OBJ.FileSave = false;	
	showPrintMenu(_officeParam.print);
	//if(!_officeParam.isSeal){
		if(_officeParam.extName == ".pdf"){
			openPdfFile();
		}else{
			openOfficeFile();
		}
	//}else{
	//	openAndSealOfficeFile();
	//}

}

/** 打开PDF文件 **/
function openPdfFile(){
	TANGER_OCX_OBJ.AddDocTypePlugin(".pdf","PDF.NtkoDocument","4.0.0.2","/ms_office/NTKOOleDocAll.dll",51,true);
	if(_officeParam.isLocalFile){
		TANGER_OCX_OBJ.OpenLocalFile(_officeParam.localFile);
	}else{
		TANGER_OCX_OBJ.OpenFromURL(_officeParam.httpUrl,false,"PDF.NtkoDocument");
		//TANGER_OCX_OBJ.AfterOpenFromURL = displayNtkoObj;
	}
	
	//隐藏工具栏
	TANGER_OCX_OBJ.Toolbars = false;
	TANGER_OCX_OBJ.IsShowToolMenu = false;

	//是否为另存
	TANGER_OCX_EnableFileSaveAsMenu(false);	
	
	displayNtkoObj();
}

/**打开Office文件**/
function openOfficeFile(){
	/*if(!_officeParam.existOffice && !_officeParam.existWps){
		alert("没有打开此文档的工具，请安装WPS或者OFFICE！");
		return;
	}*/
	if(!_officeParam.existOffice){
		alert("没有打开此文档的工具，请安装OFFICE！");
		return;
	}
	
	logOptTime("打开文件--开始");

	//编辑文件时不移除文件中的宏
	TANGER_OCX_OBJ.IsRemoveMacrosOnSave = false;
	
	try{
		if (_officeParam.read) {			
			if(readFileInit()){
				readOfficeDocument();
			}
		}else if(_officeParam.edit){ //编辑文件
			if(editFileInit()){ //打开文件
				logOptTime("打开文件--完成");
				editOfficeDocument(); //设置相关参数
				logOptTime("修改Word参数--完成");
			}
		}
	}catch(e){
		displayNtkoObj();
		throw e;
	}
	displayNtkoObj();
	if(_officeParam.isSeal){
		alert("请将光标移动至盖章位置。");
	}
}
/**文件盖章**/
function openAndSealOfficeFile(){
	//if(!_officeParam.existOffice && !_officeParam.existWps){
	//	alert("没有打开此文档的工具，请安装WPS或者OFFICE！");
	//	return;
	//}
	
	//logOptTime("打开文件--开始");

	//编辑文件时不移除文件中的宏
	//TANGER_OCX_OBJ.IsRemoveMacrosOnSave = false;
	//读取ekey信息
	var ntkosignctl = document.getElementById("ntkosignctl");
	alert(ntkosignctl);
	alert( ntkosignctl.IsEkeyConnected);
	if(ntkosignctl && ntkosignctl.IsEkeyConnected){
		if(ntkosignctl.EkeySN != _officeParam.eKeySN){
			alert("您选的印章不在当前ekey中。");
			//displayNtkoObj();
		}else if(!(_officeParam.extName == ".doc")){
			alert("请在OFFICE文档中盖章.");
		}else{
			//try{
			//	if(editFileInit()){
			//		logOptTime("打开文件--完成");
			//		editOfficeDocument(); //设置相关参数
			//		logOptTime("修改Word参数--完成");
			//	}
			//}catch(e){
			//	displayNtkoObj();
			//	throw e;
			//}
			//displayNtkoObj();
			try{
				TANGER_OCX_OBJ.AddSecSignFromEKey(_officeParam.userLoginName,0,0,1,2,false,true,true,false,null,parseInt(_officeParam.sealIndex)%10,false,true);
				// 添加盖章记录
				window.opener.FireFly.doAct("BN_FILE_SEAL_RECORD","saveSealRecord",{"isSeal":"yes","DATA_ID":_officeParam.dataId,"SEAL_ID":_officeParam.sealID,"SEAL_TYPE":1,"FILE_ID":_officeParam.fileId,"SERV_ID":_officeParam.servId});
				// 是否加盖骑缝章
				if (_officeParam.isQfz && "true" == _officeParam.isQfz) {
					TANGER_OCX_OBJ.AddSideSecSignFromEKey(_officeParam.userLoginName,parseInt(_officeParam.sealIndex)%10,2,false,true,false,false,null,false,false);
				}
			}catch(e){
				alert(e.message);
			}
		}
	}else{
		alert("请插入ekey。");
		//displayNtkoObj();
	}
}
/** 打印文件 **/
function openAndPrintFile(printNum,fileId,dataId,servId){
	try{
		// 获取文件已打印份数
		window.opener.FireFly.doAct("BN_FILE_PRINT_RECORD","queryPrintCopies",{"FILE_ID":fileId,"DATA_ID":dataId},false,false,function(result){
				var number = parseInt(printNum)-parseInt(result.PRINT_COPIES);
				if(number>0){
					showPrintMenu(true);
					var pp=TANGER_OCX.ActiveDocument.Application.Dialogs.item(88);
					if (pp.display()==-1) {
						if (pp.NumCopies>number) {
							alert("本次最多允许打印"+number+"份！");
						} else {
							pp.Execute();
							// 记录打印
							window.opener.FireFly.doAct("BN_FILE_PRINT_RECORD","savePrintRecord",{"FILE_ID":fileId,"DATA_ID":dataId,"SERV_ID":servId,"PRINT_NUM":pp.NumCopies},false,false,function(result){
							});
						}
					}
					showPrintMenu(false);
					TANGER_OCX.CancelLastCommand=true;	
				}else{
					alert("该文件已超出打印份数，不允许打印!");
				}
			});	
	}catch(e){
		throw e;
	}
}

/**
* 显示编辑器
**/
function displayNtkoObj(){
	TANGER_OCX_OBJ.width = "100%";
	TANGER_OCX_OBJ.height = "100%";
	logOptTime("显示文件");
}

function editFileInit(){
	//打开网络文件
	if(!_officeParam.isLocalFile){
		if (_officeParam.extName == ".doc") {
			if(_officeParam.existOffice){
				loadFile("Word.Document"); //打开文件
			}else if(_officeParam.existWps){
				loadFile("WPS.Document");
			}else{
				alert("请安装OFFICE！");
				return false;
			}
		} else if(_officeParam.extName == ".docx"){
			if(_officeParam.existOffice && _officeParam.officeVer > 11){
				loadFile("Word.Document");
			}else if(_officeParam.existWps){
				loadFile("WPS.Document");
			}else{
				alert("请安装更高版本的OFFICE！");
				return false;
			}
		} else if(_officeParam.extName == ".xls"){
			if(_officeParam.existOffice){
				loadFile("Excel.Sheet");
			}else if(_officeParam.existWps){
				loadFile("ET.WorkBook");
			}else{
				return false;
			}
		}else if(_officeParam.extName == ".xlsx"){
			if(_officeParam.existOffice){
				if(_officeParam.officeVer > 11){
					loadFile("Excel.Sheet");
				}else{
					alert("请安装更高版本的OFFICE！");
					return false;
				}
			}else if(_officeParam.existWps){
				loadFile("ET.WorkBook");
			}else{
				return false;
			}
		}else if(_officeParam.extName == ".wps"){
			if(_officeParam.existWps){
				loadFile("WPS.Document");
			}else if(_officeParam.existOffice){
				loadFile("Word.Document");
			}else{
				return false;
			}
		}else if(_officeParam.extName == ".et"){
			if(_officeParam.existWps){
				loadFile("ET.WorkBook");
			}else if(_officeParam.existOffice){
				loadFile("Excel.Sheet");
			}else{
				return false;
			}
		}
		
		return true;
	}
	//打开本地文件(相当于是编辑文件)
	var localFile = _officeParam.localFile;
	
	try{
		if (_officeParam.extName == ".doc") {
			TANGER_OCX_OBJ.OpenLocalFile(localFile);
		} else if(_officeParam.extName == ".docx"){
			if(_officeParam.existOffice && _officeParam.officeVer > 11){
				TANGER_OCX_OBJ.OpenLocalFile(localFile);
			}else if(_officeParam.existWps){
				TANGER_OCX_OBJ.OpenLocalFile(localFile);
			}else{
				alert("请安装更高版本的OFFICE！");
				return false;
			}
		} else if(_officeParam.extName == ".xls"){
			if(_officeParam.existOffice){
				TANGER_OCX_OBJ.OpenLocalFile(localFile);
			}else if(_officeParam.existWps){
				TANGER_OCX_OBJ.OpenLocalFile(localFile);
				var newFileName = convertXls2Et(localFile);
				if(newFileName){
					TANGER_OCX_OBJ.OpenLocalFile(newFileName);
					_officeParam.localFile = newFileName;
				}
			}
		}else if(_officeParam.extName == ".xlsx"){
			if(_officeParam.existOffice){
				if(_officeParam.officeVer > 11){
					TANGER_OCX_OBJ.OpenLocalFile(localFile);
				}else{
					alert("请安装更高版本的OFFICE！");
					return false;
				}
			}else if(_officeParam.existWps){
				TANGER_OCX_OBJ.OpenLocalFile(localFile);
			}
		}else if(_officeParam.extName == ".wps"){
			if(_officeParam.existWps){
				TANGER_OCX_OBJ.OpenLocalFile(localFile);
			}else if(_officeParam.existOffice){
				TANGER_OCX_OBJ.OpenLocalFile(localFile);
			}
		}else if(_officeParam.extName == ".et"){
			if(_officeParam.existWps){
				TANGER_OCX_OBJ.OpenLocalFile(localFile);
			}else if(_officeParam.existOffice){
				TANGER_OCX_OBJ.OpenLocalFile(localFile);
			}
		}
	}catch(exception){
		alert("localFile:"+downLoadUrl+";message:"+exception.message);
		throw exception;
	}
	
	return true;
}

/**
* 把共享工作簿xls文件转换成et文件
**/
function convertXls2Et(localFile){
	var actDoc = TANGER_OCX_OBJ.ActiveDocument;
	//alert(actDoc.RevisionNumber);
	if(actDoc.RevisionNumber > 0){
		try{
			var aSetting = actDoc.Application.DisplayAlerts;
			
			//屏蔽转换时的提示框
			actDoc.Application.DisplayAlerts = false;
			var newFileName = "c:\\zotnDoc\\new_" + getFileName(localFile);
			//转换为非共享工作簿
			actDoc.SaveAs(newFileName,null ,null ,null ,null ,null , 3);
			//打开非共享工作簿
			//TANGER_OCX_OBJ.OpenLocalFile(localFile, false, "et.workbook");
			//还原设置
			actDoc.Application.DisplayAlerts = aSetting;
			return newFileName;
		}catch(e){
			alert(e.message);
			throw e;		
		}
	}
}

/**
* 查看文件初始化
**/
function readFileInit(){
	var localFile = _officeParam.localFile;
	
	try{
		if(_officeParam.extName == ".docx"){
			//&& _officeParam.officeVer > 11
			if(_officeParam.existOffice ){
				loadFile("Word.Document");
			}else if(_officeParam.existWps){
				loadFile("WPS.Document");
			}else{
				alert("请安装更高版本的OFFICE！");
				return false;
			}
		}else if(_officeParam.extName == ".xlsx"){
			if(_officeParam.existOffice){
				//if(_officeParam.officeVer > 11){
					loadFile("Excel.Sheet");
				//}else{
				//	alert("请安装更高版本的OFFICE！");
				//	return false;
				//}
			}else if(_officeParam.existWps){
				loadFile("ET.WorkBook");
			}
		}else{
			loadFile();
		}
		
	}catch(exception){
		alert("localFile:"+localFile+";message:"+exception.message);
		throw exception;
	}
	
	return true;
}

//只读文件
function readOfficeDocument() {
	var downLoadUrl = document.all.downLoadUrl.value;
	if (downLoadUrl == null || downLoadUrl == "") return false;
	
	TANGER_OCX_bDocOpen = true;
	//查看文件时，不允许复制内容
	TANGER_OCX_OBJ.IsStrictNoCopy=true;
	//隐藏工具栏
	TANGER_OCX_OBJ.Toolbars = false;
	TANGER_OCX_OBJ.IsShowToolMenu = false;

	//是否为另存
	TANGER_OCX_EnableFileSaveAsMenu(false);
	
	//是否默认显示修改痕迹
	if(_officeParam.displayRev){
		//显示痕迹
		TANGER_OCX_ShowRevisions(true);
		TANGER_OCX_PrintRevisions(true);		
	}else{
		//不显示痕迹
		TANGER_OCX_ShowRevisions(false);
		TANGER_OCX_PrintRevisions(false);
	}
	
	_office = new zotn.office(TANGER_OCX_OBJ);
	//取得当前编辑器是wps还是word
	_officeParam.currentApp = _office.getAppName();	
	
	if(_office.docType == DOC_TYPE.excel || _office.docType == DOC_TYPE.et){
		//设置excel文件只读
		TANGER_OCX_OBJ.SetReadOnly(true,PROTECTED_PASSWD);
	}
	
	//是否为显示留痕
	if(_officeParam.showRevision){
		_office.protect(PROTECTED_TYPE.COMMENTS);
	}else{
		_office.acceptAllRevisions();
		_office.protect(PROTECTED_TYPE.COMMENTS);
	}
}

/**
*编辑文件时：隐藏Ms Office编辑器的按钮。
**/
function hideMsOfficeEditorBtn(){
	if(_officeParam.currentApp == 'office' && _officeParam.officeVer > 11 && _officeParam.existOffice){
		try{
			TANGER_OCX_OBJ.Set2007UIIsEnable(0,2,false,false);
			TANGER_OCX_OBJ.Set2007UIIsEnable(0,7,false,false);
		}catch(e){
			
		}
	}
	
	//屏蔽按钮：是否为另存
	TANGER_OCX_EnableFileSaveAsMenu(false);
}

/**
*编辑文件
**/
function editOfficeDocument(){
	var downLoadUrl = document.all.downLoadUrl.value;
	if (downLoadUrl == null || downLoadUrl == "") return false;
	
	TANGER_OCX_bDocOpen = true;
	
	hideMsOfficeEditorBtn();

	if(_officeParam.isSeal){//盖章操作，则隐藏工具栏
		TANGER_OCX_OBJ.Toolbars = false;
		TANGER_OCX_OBJ.IsShowToolMenu = false;
	}
	
	TANGER_OCX_SetDocUser(document.all.userName.value);
	
	_office = new zotn.office(TANGER_OCX_OBJ);
	
	//取得当前编辑器是wps还是word
	_officeParam.currentApp = _office.getAppName();
	
	//隐藏特殊的菜单
	_office.hideMenuBtn();
	
	
	
	var verResult = _office.checkVersion();
	if(!verResult){
		//alert("wps版本不能处理。");
	}
	
	if(_officeParam.displayRev){
		//默认显示修改痕迹
		TANGER_OCX_ShowRevisions(true);
	} else {
		TANGER_OCX_ShowRevisions(false);
	}

	//不显示痕迹
	TANGER_OCX_PrintRevisions(false);
	
	//判断文件是否需要记录修改痕迹
	if(_officeParam.revision){
		_office.protectRevision();
	}else{
		_office.unprotectRevision();
	}
	
	try{
		//替换Word文件中指定书签的值
		var bookmark = document.getElementById("bookmark").value;
		if(bookmark && bookmark.length > 0){
			var params = opener[bookmark];
			for(var key in params){
				var oldVal = TANGER_OCX_OBJ.GetBookmarkValue(key);
				if(oldVal != params[key]){
					TANGER_OCX_OBJ.setBookmarkValue(key,params[key]);
				}
			}
		}
	}catch(e){
		alert(e.message);
	}
}


var _saveTime = 0;

/**
* 保存文档
*/
function saveOfficeFile(){
	var uploadUrl = document.all.upLoadUrl.value;
	var fileName = document.all.fileName.value;
	
	if(!TANGER_OCX_bDocOpen){
		return;
	}
	
	if(!_open_file_ok){
		return;
	}
	
	try{
		//wps编辑过的文件docx/xlsx文件都会变成doc/xls格式，因此文件名也要修改
		if(_officeParam.currentApp == "wps"){
			if(_officeParam.extName == ".docx"){
				fileName = getFileNameWithoutExt(fileName) + ".doc";
			}else if(_officeParam.extName == ".xlsx"){
				fileName = getFileNameWithoutExt(fileName) + ".xls";
			}
		}
		
		_office.changeCellFocus();
	
		if(_officeParam.edit){
			//取消锁定痕迹
			_office.unprotectRevision();
		}
		
		var retHTML = TANGER_OCX_OBJ.SaveToURL(
			uploadUrl,  //此处为uploadedit.jsp
			"EDITFILE",	//文件输入域名称,可任选,不与其他<input type=file name=..>的name部分重复即可
			"", //可选的其他自定义数据－值对，以&分隔。如：myname=tanger&hisname=tom,一般为空
			fileName, //文件名,此处从表单输入获取，也可自定义
			"actForm" //控件的智能提交功能可以允许同时提交选定的表单的所有数据.此处可使用id或者序号
		); //此函数会读取从服务器上返回的信息并保存到返回值中。
		
		if(_officeParam.edit && _officeParam.revision){
			//锁定修改痕迹
			_office.protectRevision();
		}
		
		if(StringUtils.startWith(retHTML,"{\"_MSG_\":\"ERROR,")){
			alert("文件保存失败，请将文件备份，防止丢失。");
		}
	}catch(err){
		alert(NO_SAVE_TO_URL + ":" + err.number + ":" + err.description);
	}
	
	var curTime = new Date();
	_saveTime = curTime.getTime();
}

//点击显示痕迹或隐藏痕迹按钮
function showOrHiddenMark(enable){
	if(enable){
		TANGER_OCX_PrintRevisions(true);
		TANGER_OCX_ShowRevisions(true);
	}else{
		TANGER_OCX_PrintRevisions(false);
		TANGER_OCX_ShowRevisions(false);
	}
}

//是否显示打印菜单
function showPrintMenu(enable){
	//是否为打印
	if (enable) {
		TANGER_OCX_EnableFilePrintMenu(true);
		TANGER_OCX_EnableFilePrintPreviewMenu(true);
	} else {
		TANGER_OCX_EnableFilePrintMenu(false);
		TANGER_OCX_EnableFilePrintPreviewMenu(false);
	}
}

function loadFile(ProgId){ //打开文件
	var readonly = false;
	
	if(_officeParam.read){
		readonly = true;
	}
	
	logOptTime("装载文件0");
	
	if(_officeParam.isLocalFile){
		TANGER_OCX_OBJ.OpenLocalFile(_officeParam.localFile);
	}else{
		if(ProgId){
			TANGER_OCX_OBJ.OpenFromURL(_officeParam.httpUrl,readonly);
		}else{
			TANGER_OCX_OBJ.OpenFromURL(_officeParam.httpUrl,readonly);
		}
	}
	logOptTime("装载文件1");
}

/**
 * 记录操作时间
 * @param name
 */
function logOptTime(name){
	var msg = "";
	if(stopwatch){
		var timeCount = stopwatch.time();
		msg = name + ":\t" + timeCount ;
	}else{
		var nowTime = new Date();
		msg = nowTime.getSeconds() + ":" + nowTime.getMilliseconds() + "\t\t\t" + name;
	}

	var _msg_out = document.getElementById("_msg_out");
	_msg_out.innerHTML = _msg_out.innerHTML + msg + "\r\n"
}