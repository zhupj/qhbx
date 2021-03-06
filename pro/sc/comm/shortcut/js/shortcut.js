// JavaScript Document
/**
 * @date 2013-08-17c
 * 修改记录
 * 1.显示对话框时，在设置状态下，显示正常样式，在保存状态下，根据权限显示是否默认
 */

/**
 * @author jason
 */
(function($){
	
	"use strict";  
	
	var ShortCut=function(element,options){
		this.init('shortcut',element,options);
	}
	ShortCut.prototype={
			
		constructor:ShortCut
	   ,init : function(type,element,options){
		  	this.type = type;
			this.$element 		= $(element);
		    this.options 		= this.getOptions(options);		
			this.dialogEl		= $(this.options.dialogEl);
			this.carouselEl		= this.$element.find(this.options.carouselEl);
			this.showDialog		= false;
			this.showRightPanel = false;
			this.btnAct			= [{"save": "保存"},{"setup": "设置"}];
			this.blankTmpl		= "<div class='blank'>点击设置添加自己的快捷菜单</div>";
			this.adminOptTmpl   = "<a href='javascript:void(0);'class='js-dialog-admin-opt admin-opt'><input type='checkbox'name='admin-opt'/><span>管理员操作</span></a>";
			this.cachedArr		= {};
			
			this.perSize = parseInt(this.$element.attr("data-size"),10);
			if(!$.isNumeric(this.perSize)) {
				this.perSize = 6;
			}
			if (this.perSize ===3){
				//alert(123);
				/*this.$element.find(".shortcut-carousel").css({
					height:function(){
						return $(this).height()/2;
					},
					marginTop:function(){
						return -$(this).height()/2;
					}	
				});*/
				var tempHeihgt = this.options.carouselParam.height/2 ;
				this.carouselParam = $.extend({},this.options.carouselParam,{height:tempHeihgt});
			}else{
				this.carouselParam = $.extend({},this.options.carouselParam);
			}
			
			
			
			
			//初始化图片轮转
			this.initSlider();
			
			//初始化滚动条
			this.initScrollBar();
			//初始化sortable
			this.initSortable();
			//bind  click event
			this._bindEvent();
		}
	   	, getOptions : function (options) {
		  	return $.extend({}, $.fn[this.type].defaults, this.$element.data(), options);
		}
		, _bindEvent : function(){
		
			var $this=this, 
				timeoutId,timeLeaveId;
			//显示快捷菜单
			this.carouselEl.on("mouseenter","a.shortcut-thumbnail",function(event){
				event.preventDefault();
				//1. 定位
				//2. show
				//3. 设置状态	
				clearTimeout(timeoutId);
				clearTimeout(timeLeaveId);
				
				$this.reset();
				 
				var self   = this,
					offset = $(this).parent().position(),
					width  = 66,
					l	   = offset.left+width+5+24,
					t	   = offset.top+5+18;	
				
				timeoutId = setTimeout(function(){
					
					$this.renderDialog($(self).attr("data-type"));
					
					if($this.inUseArr.length>0 || $this.noUseArr.length>0 ){
						$this._showDialog(l,t);
						
					}else{
					
						$this._closeDialog();
					}
				},100);
		    });
			this.carouselEl.on("mouseleave","a.shortcut-thumbnail",function(event){
				event.preventDefault();
				clearTimeout(timeoutId);
				timeLeaveId =setTimeout(function(){
					$this.reset();
					$this._closeDialog();	
				},300);
		    });  
			//关闭dialog
			$(".page1").on("mouseleave",function(){
				timeLeaveId =setTimeout(function(){
					$this.reset();
					$this._closeDialog();	
				},1000);
			});
			
 			this.dialogEl.on({
				mouseenter:function(){
					clearTimeout(timeLeaveId);	
				},
				mouseleave:function(){
					//当为设置状态时，鼠标离开dialog关闭
					var btnStatus = $this.getBtnStatus();
					btnStatus=="setup" && $this._closeDialog();
				}	
			});
			 
			this.dialogEl.on("change",".js-dialog-admin-opt>input",function(event){
				//如果在保存状态下更改checkbox，则重置left和right panel,即将right panel中default移至left
				if((!$(this).prop("checked")) && $this.getBtnStatus()=="save"){
					$this.renderDialog($this.menuType);
					$this.log("change","test");
				}else{
					$(".shortcut-submenu-item",".menu-left.on").toggleClass("default");
				}
			});
			
			this.dialogEl.on("click", "a", function(event) {
				if(!checkeKey()){ //如果使用Key登录则进行检查判断
					event = event || window.event;
					event.preventDefault();
					this.dialogEl.off("click","a");
					parent.jQuery("#loginOut").trigger("click");
					return false;
				}
			});
			// 当为设置状态时，点击子菜单跳转至相应页面;当为保存状态时，不可操作
			this.dialogEl.on("click",".js-select",function(event){
				event.preventDefault();
				if($this.getBtnStatus()=="save"){
					return false;
				}else{
					var url   = $(this).attr("href"),
						title = $(this).find("span").html(),
						rel   = $(this).attr("data-rel"),
						id	  = $(this).parent().attr("id");
//					$this.log("url",url);
//					$this.log("title",title);
//					$this.log("id",id);
					var userCode = System.getVar("@USER_CODE@");
					url = url+"&userCode=" + userCode;
					if(rel=="tab"){
						Tab.open({"url":url,"tTitle":title,"menuFlag":3,"menuId":id});	
					}else{
						window.open(url);
					}
					return true;
				}
			});
			// 添加/删除子菜单
			this.dialogEl.on("click",".js-mask-select",function(event){
				event.preventDefault();
				
				if(!$this.showRightPanel){//只显示左侧panel时
					return true;
				}
 
				var $item = $(this).parent(),
					   mp = $item.closest(".menu-panel");
				if(mp.is($("#submenu-in-use"))){
					/*if($item.hasClass("default")){
						$item.removeClass("default");
					}*/
					$item.appendTo("#submenu-no-use .mCSB_container");	
	
					$this.updateScrollPanel("#submenu-no-use");
	
					$this.showRP();

					if($this.getItemsCount(mp)==0){
						$("#submenu-in-use .mCSB_container").html($this.blankTmpl);
					}
				}else{
					$item.appendTo("#submenu-in-use .mCSB_container");
					$(".blank").remove();
					$this.updateScrollPanel("#submenu-in-use");
					if($this.getItemsCount(mp)==0){
						$(".dialog-back").trigger("hideRigthPanel");
					}
				} 
				return false;
			});
			this.dialogEl.on("click",".js-dialog-setting",function(event){
				if($this.showRightPanel){
					$this.hideRP();
					//当点击保存时，更新到数据库
					$this.updateItem();
					//移除管理员操作
					$this.removeAdminOpt();
					alert("设置成功");
					$this._closeDialog();
				}else{
					//渲染dialog header，判断是否是管理员，以添加管理员操作
					$this.addAdminOpt()
					$this.showRP();
				}
				//设置按钮状态
				$this.resetBtnStatus();
				
			});
			//按esc 退出
			$(document).on("keydown",function(event){
				if($this.options.closeOnEscape && event.which==27 ){
					event.preventDefault();
					$this._closeDialog();
					return false;
				}
			});
		}
		/**
		 * 重置
		 */
		,reset:function(){
			if(this.showRightPanel){
				this.inUseArr = [];
				this.noUseArr = [];
				this.hideRP("nomal");
				this.resetBtnStatus();
				this.showRightPanel=false;
				$(".menu-left").removeClass("on");
			} 
			this.removeAdminOpt();
		}
		/**
		 * 显示右侧panel
		 */
		,showRP:function(){
			//如果右侧为空，直接操作左侧菜单进行删除
			if(!this.getItemsCount($(".dialog-back"))){
					$(".menu-left").addClass("on");
			}else{
				$(".dialog-back").stop().animate({right:"-400px",height:"400px",opacity:1},500,function(){
					$(".menu-left").addClass("on");
				});
			}
			this.showRightPanel=true;
		}
		/**
		 * 隐藏右侧panel
		 */
		,hideRP:function(noAnim){
			if(!this.showRightPanel){
				return;	
			}
			if(noAnim=="nomal"){
				$(".dialog-back").css({right:0,height:0,opacity:0},function(){
					$(".menu-left").removeClass("on");	
				});
			}else{
				$(".dialog-back").stop().animate({right:0,height:0,opacity:0} ,500,function(){
					$(".menu-left").removeClass("on");	
				});
			}
			this.showRightPanel=false;

		} 
		,getItemsCount:function(o){
			return o.find(".shortcut-submenu-item").length;
		}
		/**
		* 获取子菜单
		* method getMenuList 
		* @param {Object} 形如{"pid":"oa1231"}
		* @return {Array} 获得某一类型的数组 或 全部
		*/
		,getMenuList:function(obj){
			var tempArr    = [],
				submenuArr = this.cachedArr[this.menuType];
			
			if(submenuArr){
				tempArr = submenuArr;
				this.log("getMenuList--true--tempArr",tempArr);
				this.log("getMenuList--true--this.menuArr",this.cachedArr);
			}else{
				var result = parent.FireFly.doAct("SC_SYS_SHORTCUT_WORK_CONF","getShortCutWork",{"PID":obj},false,false);
				this.log("getMenuList--false--result###########",result);
				
				this.cachedArr[this.menuType] = result._DATA_;
				tempArr = this.cachedArr[this.menuType];
				
				this.log("getMenuList--false--tempArr###########",tempArr);
				this.log("getMenuList--false--this.menuArr###########",this.cachedArr);
			}
			return tempArr;
		}
		/**
		 * 初始化子菜单
		 */
		,initMenu:function(){
		
			var submenuArr   = this.getMenuList(this.menuType);
			
			this.inUseArr = [];
			this.noUseArr = [];
			for(var h in submenuArr){
				
				if(submenuArr[h]["STCUT_ISUSE"]==1){
					this.inUseArr.push(submenuArr[h]);	
				}else{
					this.noUseArr.push(submenuArr[h]);	
				}
			}
			
		}
		/**
		 * 初始化滚动条
		 */
		,initScrollBar:function(){
			var leftParam  = $.extend({},this.options.scrollParam,{set_height:"224px"}),
				rightParam = $.extend({},this.options.scrollParam,{set_height:"324px"});
			$(".menu-left .menu-panel").mCustomScrollbar(leftParam);
			$(".menu-right .menu-panel").mCustomScrollbar(rightParam);
		}
		/**
		 * 初始化sort排序
		 */
		,initSortable:function(){
			$( "#submenu-in-use .mCSB_container, #submenu-no-use .mCSB_container" ).sortable({
				cursor:"move",//设置移动时鼠标样式
				cancel: ".default" 
			}).disableSelection();
		}
		/**
		 * 初始化图片轮转
		 */
		 
		,initSlider:function(){
		
			//加载主菜单
			this.loadMenuData();
			//图片轮转
			this.carouselEl.carouFredSel(this.carouselParam); 
			
		}
		/**
		 * 权限判断，是否显示管理员操作
		 */
		,isAllowSpeacilSetting:function(){
			var roles = System.getVar("@ROLE_CODES@");
			return roles && roles.indexOf("BNADMIN")>-1;
		}
		/**
		 * 添加管理员操作dom
		 */
		,addAdminOpt:function(){
			if(this.isAllowSpeacilSetting()){
				if(!$(".js-dialog-admin-opt").length){
					$(this.adminOptTmpl).insertBefore('.js-dialog-setting');
				}else{
					$(".js-dialog-admin-opt").find("input").prop("checked",false);
				}
			}
		}
		/**
		 * 删除管理员操作dom
		 */
		,removeAdminOpt:function(){
			if($(".js-dialog-admin-opt",".dialog-header").length){
				$(".js-dialog-admin-opt",".dialog-header").remove();
			}
		}
		/**
		 * 加载主菜单
		 */
		 
		,loadMenuData:function(){
			
			//通过前台获取系统配置的虚拟快捷工作根目录
			var rootShortcut = System.getVar("@C_SC_SHORCUT_WORK_ROOT_MENU@");
			
			var result = parent.FireFly.doAct("SC_SYS_SHORTCUT_WORK_CONF","getMainMenu",{"PID": rootShortcut},false,false);
			
			var menuArr = result._DATA_;
			this.log("loadMenuData",menuArr);
		
			var menuHtml = '',
		  	    pageSize = this.perSize,
		  	    nowPage  = 0;
				debugger;
		    for(var i=0 ,len=menuArr.length; i < len ;i++){
				
		    	  //如果不支持链接跳转，设置href 和 hover样式
		    	  var href,
		    	  	  hoverDisable,
		    	  	  openType;
				  var opts,tabP;
				  if(menuArr[i]["MENU_ID"]=="OA_HOME__zhbx"){
					  href = "/sy/comm/page/page.jsp";
		    		  hoverDisable='';
		    		  openType="page";
				  }else if(menuArr[i]["MENU_TYPE"]==1){//服务
				      opts = {"tTitle":encodeURIComponent(menuArr[i]["MENU_NAME"]),"url":menuArr[i]["MENU_INFO"]+".list.do","menuFlag":menuArr[i]["DS_MENU_FLAG"]};
		    		  tabP = jQuery.toJSON(opts);
					  tabP = tabP.replace(/\"/g,"'");
					  href = "/sy/comm/page/page.jsp?openTab=" + tabP;
		    		  hoverDisable='';
		    		  openType="page";
		    	  }else if(menuArr[i]["MENU_TYPE"]==2){//链接
				  
					  var newUrl = Tools.systemVarReplace(menuArr[i]["MENU_INFO"]);
				      opts = {"tTitle":encodeURIComponent(menuArr[i]["MENU_NAME"]),"url":FireFly.getContextPath()+"/"+newUrl,"menuFlag":menuArr[i]["DS_MENU_FLAG"]};
		    		  tabP = jQuery.toJSON(opts);
					  tabP = tabP.replace(/\"/g,"'");
					  href = "/sy/comm/page/page.jsp?openTab=" + tabP;
		    		  hoverDisable='';
		    		  openType="page";
		    	  }else{
		    		  href = "javascript:void(0);";
		    		  hoverDisable = " nohover";
		    		  openType="page";
		    	  }
		    	  if(href.indexOf('pt2OA.jsp')>0){
		    	  var userCode = System.getVar("@USER_CODE@");
				  href = href+"&userCode=" + userCode;
		    	  }

		    	  // zjx - 如果是作为主菜单出现的'党群门户'菜单，做特殊处理
		    	  if (menuArr[i]['MENU_ID'] == 'dangqunmenhu__2') {
		    		 hoverDisable = '';
		    		 openType="tab";
		    		 var param = {"url":menuArr[i]['MENU_INFO'],"tTitle":"党群门户","menuFlag":3};
		    		 param = jQuery.toJSON(param);
		    		 menuHtml+="<div class='shortcut-item'><a href='javascript:Tab.open("+param+");' class='shortcut-thumbnail"+hoverDisable+"' data-type='"+menuArr[i]["MENU_ID"]+"' data-rel='"+openType+"' target='_self'><img src='/sy/comm/desk/css/images/app_rh-icons/"+menuArr[i]["DS_ICON"]+".png'/><span title='"+menuArr[i]["MENU_NAME"]+"'>"+menuArr[i]["MENU_NAME"]+"</span></a></div>";
		    	  } else {
					if(menuArr[i]["MENU_INFO"]){
						menuHtml+='<div class="shortcut-item"><a href="'+href+'" class="shortcut-thumbnail'+hoverDisable+'" data-type="'+menuArr[i]["MENU_ID"]+'" data-rel="'+openType+'" target="_blank"><img src="/sy/comm/desk/css/images/app_rh-icons/'+menuArr[i]["DS_ICON"]+'.png"/><span title="'+menuArr[i]["MENU_NAME"]+'">'+menuArr[i]["MENU_NAME"]+'</span></a></div>';
					}else{
						 menuHtml+="<div class='shortcut-item'><a class='shortcut-thumbnail"+hoverDisable+"' data-type='"+menuArr[i]["MENU_ID"]+"' data-rel='"+openType+"' target='_blank'><img src='/sy/comm/desk/css/images/app_rh-icons/"+menuArr[i]["DS_ICON"]+".png'/><span title='"+menuArr[i]["MENU_NAME"]+"'>"+menuArr[i]["MENU_NAME"]+"</span></a></div>";
					}
		    	  }
		    	  	  
				  if((i+1)%pageSize == 0){
					$("<div class='page' data-page='page_"+nowPage+"'></div>").html(menuHtml).appendTo(this.$element.find(".shortcut-foo"));
					nowPage++;
					menuHtml='';
				 }else if((i==len-1)){
					$("<div class='page' data-page='page_"+nowPage+"'></div>").html(menuHtml).appendTo(this.$element.find(".shortcut-foo"));
					menuHtml='';
				 }
		    
		    } 
		}
		/**
		* 排序方法
		* @method orderBy   
		* @param {String} type 根据指定属性排序
		* @param {Boolean} desc 是否降序 true:降序，false:升序
		* @return{Function} 匿名的compare排序方法，供Array.sort调用
		*/
		,orderBy:function(type,desc){
			return function(v1,v2){
				if(desc){
					return v2[type]-v1[type];
				}else{
					return v1[type]-v2[type];
				}
			};
		}
		,log:function(msg,obj){
//			console.log("-------"+msg+"----begin----");
//			console.log(arguments[1]);
			//console.log("-------"+msg+"----end----");
			
		}
		/**
		 * 渲染dialog
		 */
		  
		,renderDialog:function(pid){
		
			if(!pid){
				return false;
			}
			this.addAdminOpt();
			//设置父菜单
			this.menuType = pid ;
			
			//初始化菜单数据
			this.initMenu();
			
			//1.设置left panel
			var inUseArr =  this.inUseArr,
				noUseArr =  this.noUseArr,
				tempInUseArr = [],
				tempNoUseArr = [];
				
			for(var i in inUseArr){
				
					tempInUseArr.push(inUseArr[i]);	
				
			}
			 
			//2013-11-16
			var rel = this.$element.find("[data-type='"+pid+"']").attr("data-rel");
		 
			if(tempInUseArr.length){
				tempInUseArr.sort(this.orderBy("STCUT_ORDER",false));
				var inUseHtml = '';

				for(var h in tempInUseArr){
					var opts = {"tTitle":"快捷菜单","url":tempInUseArr[h]["MENU_INFO"],"menuFlag":3};
					var tabP = jQuery.toJSON(opts);
					
					tabP = tabP.replace(/\"/g,"'");
					var href = "/sy/comm/page/page.jsp?openTab="+(encodeURIComponent(tabP));
					inUseHtml+="<div class='shortcut-submenu-item"+(tempInUseArr[h]["S_PUBLIC"]==1 ? " default":"")+"' id='"+tempInUseArr[h]["MENU_ID"]+"' data-type='"+tempInUseArr[h]["MENU_PID"]+"'><a href="+href+" data-rel='"+rel+"' class='js-select dialog-thumbnail' target='_blank'><img src='/sy/comm/desk/css/images/app_rh-icons/"+tempInUseArr[h]["DS_ICON"]+".png' alt='"+tempInUseArr[h]["MENU_NAME"]+"'/><span title='"+tempInUseArr[h]["MENU_NAME"]+"'>"+tempInUseArr[h]["MENU_NAME"]+"</span></a><a href='#'class='js-mask-select menu-select'></a></div>";
				}
				$("#submenu-in-use .mCSB_container").html(inUseHtml);
			}else{
				$("#submenu-in-use .mCSB_container").html(this.blankTmpl);
			}
			
			for(var i in noUseArr){
				
					tempNoUseArr.push(noUseArr[i]);	 
				 
			}
			
			if(tempNoUseArr.length){
				tempNoUseArr.sort(this.orderBy("STCUT_ORDER",false));
				var noUseHtml = '';
				for(var h in tempNoUseArr){
					noUseHtml+="<div class='shortcut-submenu-item' id='"+tempNoUseArr[h]["MENU_ID"]+"' data-type='"+tempNoUseArr[h]["MENU_PID"]+"'><a data-rel='"+rel+"' class='js-select dialog-thumbnail' target='_blank'><img src='/sy/comm/desk/css/images/app_rh-icons/"+tempNoUseArr[h]["DS_ICON"]+".png'alt='"+tempNoUseArr[h]["MENU_NAME"]+"'/><span title='"+tempNoUseArr[h]["MENU_NAME"]+"'>"+tempNoUseArr[h]["MENU_NAME"]+"</span></a><a href='#'class='js-mask-select menu-select'></a></div>";
				}
				$("#submenu-no-use .mCSB_container").html(noUseHtml);
			}else{
				$("#submenu-no-use .mCSB_container").empty();
			}
			
			return true;
		}
		/**
		* 设置按钮状态  [保存|设置]
		*@method setBtnStatus
		*/
		,resetBtnStatus:function(){
			var i,btnAct ;
			if(this.showRightPanel){
				btnAct = this.btnAct[0];
			}else{
				btnAct = this.btnAct[1];
			}
			for(i in btnAct){
				$(".js-dialog-setting").attr("data-action",i).find("span").html(btnAct[i]);
			}
		}
		/**
		* 设置按钮状态  [保存|设置]
		*@method setBtnStatus
		*@param {Object} context 上下文
		*/
		,getBtnStatus:function(){
			return $(".js-dialog-setting").attr("data-action");
		}
		,updateItem:function(){
			var leftIdArr   = $( "#submenu-in-use .mCSB_container" ).sortable("toArray"),
				rightIdArr  = $( "#submenu-no-use .mCSB_container" ).sortable("toArray"),
				filter		= ["MENU_ID","STCUT_ISUSE","STCUT_ORDER","STCUT_ID","S_PUBLIC"],
				sortedIdArr = [],
				tempArr     = [],
				param		= {};
		 
				sortedIdArr = leftIdArr.concat(rightIdArr);
			
			tempArr = this.cachedArr[this.menuType];
			
			var flag = false;

			if($(".js-dialog-admin-opt>input").prop("checked")){
				flag=true;
			}
			 
			for(var h in tempArr){
				//排序
				tempArr[h]["STCUT_ORDER"]=$.inArray(tempArr[h]["MENU_ID"],sortedIdArr);
				
				if($.inArray(tempArr[h]["MENU_ID"],leftIdArr) > -1){
					//设置为常用
					tempArr[h]["STCUT_ISUSE"] = 1;
					//设置为公共菜单
					if(flag){
						tempArr[h]["S_PUBLIC"] = 1;
					}
				}else{
					tempArr[h]["STCUT_ISUSE"] = 2;
					tempArr[h]["S_PUBLIC"] = 2;
				}
			}
			this.cachedArr[this.menuType] = tempArr;
			
			this.log("cachedArr",this.cachedArr);
			this.log("updateItem >>before  JSON.stringify",tempArr);
			//保存到数据库   //$.toJSON  tools.js中的方法

//			param["submenuData"]   = $.toJSON(tempArr,filter);
			// 修复bug，上面的方法在IE8下会出错，所以在下面有做了一回数组 -- 开始
			var tempArray = [];
			$.each(tempArr, function(index, item) {
				var tempJson = {};
				tempJson["MENU_ID"] = item["MENU_ID"];
				tempJson["STCUT_ISUSE"] = item["STCUT_ISUSE"];
				tempJson["STCUT_ORDER"] = item["STCUT_ORDER"];
				tempJson["STCUT_ID"] = item["STCUT_ID"];
				tempJson["S_PUBLIC"] = item["S_PUBLIC"];
				tempArray.push(tempJson);
			});
			param["submenuData"] = $.toJSON(tempArray);
			// 修复bug，上面的方法在IE8下会出错，所以在下面有做了一回数组 -- 结束
			
			if(flag){
				param["isAdminOpt"]=1;
			}
//			this.log("updateItem >>saveData",param);
			
		    var resultData = FireFly.doAct("SC_SYS_SHORTCUT_WORK_CONF","savePersonCutWorkBySys",param,false,false);
		    
		    this.log("updateItem >>after",resultData);
			
		}
 
		/**
		 * 动态添加item时，更新scroll panel
		 */
		,updateScrollPanel:function(o){
			$(o).mCustomScrollbar("update"); 
			$(o).mCustomScrollbar("scrollTo","bottom",{
				scrollInertia:200,
				scrollEasing:"easeInOutQuad"
			}); 
		}
		,_showDialog:function(l,t){
			if(!this.showDialog){ 
			
				this.dialogEl.css({"left":l+5,"top":t}).show();
				this.showDialog = true;
			}else{
			
				this.dialogEl.animate({"left":l+5,"top":t});
			}
		}
		/**
		 * close 弹出窗
		 */
		,_closeDialog : function(clsType){		 
			//TODO 保存顺序到数据库
			if(this.dialogEl.is(":visible")){
				//if(confirm("是否保存设置!")){
				//this.updateItem();
				//}
				//clsType=="immediate"?this.dialogEl.hide():this.dialogEl.hide(500);
				this.dialogEl.hide();
				this.showDialog = false;
			}
		}
		 
	};//end of prototype
    
	/*
	 *	shortcut PLUGIN DEFINITION
  	 * ========================= 
	 */
	var old = $.fn.shortcut ;
	
	$.fn.shortcut=function(options){	
		return this.each(function() {
			var $this=$(this)
				,uuid = $this.attr("data-size")
			 	,data = $this.data('shortcut'+uuid)
			    ,opts=$.extend({},$.fn.shortcut.defaults,options);
		 
			if(!data){
				$this.data('shortcut'+uuid,(data=new ShortCut(this,opts)));
			}
		});
		
	};
	$.fn.shortcut.Constructor = ShortCut;
 	/*
	 * shortcut NO CONFLICT
  	 * ====================
  	 */ 
    $.fn.shortcut.noConflict = function () {
      $.fn.shortcut = old ;
      return this ;
    }
	
	
	$.fn.shortcut.defaults={
		closeOnEscape:true,  			// 按esc 退出
		carouselParam:{					// 图片轮转参数
			height	: 186,
			circular: false,
			infinite: false,
			auto 	: false,
			items   : 1,
			prev	: {	
				button	: "#shortcut-prev",
				key		: "left"
			},
			next	: { 
				button	: "#shortcut-next",
				key		: "right"
			} 
	   },
	   scrollParam:{
		    theme:"dark-thin",
			set_height:"400px",
			scrollInertia:200,
			advanced:{
				updateOnContentResize:true
			}
	   }
    };
	
	$(function () {
		  $('.shortcut-wrapper').shortcut({
			dialogEl  	: ".shortcut-dialog" ,
			carouselEl	: ".shortcut-foo" 
		  });
    });
})(window.jQuery);