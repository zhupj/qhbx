var _viewer = this;
var view = jQuery("<span style='cursor:pointer;display:inline-block;margin-right:10px;' >查看</span>").attr("id","viewFile");
var editFile = jQuery("<span style='cursor:pointer;display:inline-block;margin-left:10px;'>编辑</span>").attr("id","editFile");
var downLoad = jQuery("<span style='cursor:pointer;display:inline-block;margin-left:20px;'>下载</span>").attr("id","downLoad");

var div = jQuery("#SY_COMM_FILE_DATA-FILE_NAME_div");
view.appendTo(div);
editFile.appendTo(div);
downLoad.appendTo(div);
 
jQuery("#viewFile").bind("click",function(){
	var file =new rh.ui.File();
	var fileID= _viewer.getPKCode();//jQuery("#SY_COMM_FILE_DATA-FILE_ID").val();
	var fileName= _viewer.getItem("FILE_NAME").getValue();//jQuery("#SY_COMM_FILE_DATA-FILE_NAME").val();
	file.viewFile(fileID,fileName);
});
jQuery("#editFile").bind("click",function(){
	var WfNiId = _viewer.getItem("WF_NI_ID").getValue();
	var file =new rh.ui.File();
	var fileID = _viewer.getPKCode();
	var fileName= _viewer.getItem("FILE_NAME").getValue();
	file.editFile(fileID, fileName, WfNiId);
	
});
//下载
jQuery("#downLoad").bind("click",function(){
	var file =new rh.ui.File();
	var fileID = _viewer.getPKCode();
	var fileName= _viewer.getItem("FILE_NAME").getValue();
	file.downloadFile(fileID, fileName);	
});
jQuery("#SY_COMM_FILE_DATA-FILE_NAME_label").hide();
jQuery("#SY_COMM_FILE_DATA-FILE_NAME").parent().removeClass("ui-form-default blank disabled");//.removeClass("blank").removeClass("disabled");
jQuery("#SY_COMM_FILE_DATA-FILE_NAME").removeClass("ui-form-default").removeClass("disabled").css({"background":"none "});//.css({background:""});
