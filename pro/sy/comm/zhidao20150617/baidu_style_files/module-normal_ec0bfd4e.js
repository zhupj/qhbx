F.module("/static/widget/browse/list/list.js",function(c,i){var b,f=c("/static/common/lib/tangram/base/base.js"),h=c("/static/common/ui/log/log.js"),j=c("/static/common/ui/ut/ut.js"),k=c("/static/common/ui/event/event.js"),e=c("/static/common/ui/store/store.js"),d=c("/static/common/ui/baidu/fx/fx.js"),n=c("/static/common/ui/submit/submit.js"),l=c("/static/common/ui/dialog/dialog.js"),g=F.context("user"),m=c("/static/common/ui/tip/tip.js");var a=f.createClass(function(){var B=this;function w(){if(f(".qlist-tr")){f.forEach(f(".qlist-tr"),function(E,D){f(E).on("click",function(I){var M=f.dom("a",E)[0];var H=f(".align-l",E)[0];var L=f.dom(H).attr("qid");var N=f.dom(H).attr("cid");var J=f("#tr-container-"+L);var K="http://zhidao.baidu.com/question/api/mini?qid="+L+"&oldq=1&sort=4&rn=2";f.event(I).preventDefault();f.event(I).stopPropagation();h.send({type:2058,action:"quickopen"});f(E).hide();f(E).prev("tr").prev("tr").addClass("tr-pre");if(f(".tipB").length){f(".tipB").first().hide()}f.forEach(f(".qlist-tr"),function(S,Q){if(f(S).attr("show")=="1"){f(S).attr("show","0");var P=f(".align-l",S)[0];var O=f(P).attr("qid");var R=f("#tr-container-"+O);f(S).show();d.collapse(R.find(".qslim-wrapper").get(0),{duration:300});R.hide();R.prev("tr").prev("tr").prev("tr").removeClass("tr-pre")}});f.dom(M).addClass("title-gray");if(f(E).attr("show")=="no"){f.get(K,function(O){f("#container-"+L).html(O);f(".ikqb-map").each(function(P,R){var S=f("<iframe/>").attr({frameborder:"0",width:"430",height:"310",style:"display:none;",className:"answer-map"}),Q=f(R).attr("map")||f(R).attr("src");S.attr("src","http://zhidao.baidu.com/html/map"+Q.replace(/^iknow/i,""));f(R).before(S).remove();S.after(f("<p/>").addClass("f-info").html("�������Դ�ڰٶȵ�ͼ�����ս���԰ٶȵ�ͼ�������Ϊ׼��")).show()});f(E).attr("show","1");if(f(".qslim-error",f("#container-"+L).get(0)).length){if(f(".qslim-fold-txt",f("#container-"+L).get(0)).length){f(".qslim-fold-txt",f("#container-"+L).get(0)).on("click",function(P){P.preventDefault();d.collapse(J.find(".qslim-wrapper").get(0),{duration:300});J.hide();f(E).show();f(E).attr("show","0");f(E).prev("tr").prev("tr").removeClass("tr-pre");if(f(".tipB").length){f(".tipB").first().hide()}})}}else{s(L,N);f(".quicksub",J.get(0)).first().on("click",y);q(f(".quickanswer-edit",J.get(0)).get(0));if(f("#btn-fold-"+L)){f("#btn-fold-"+L).on("click",function(P){P.preventDefault();d.collapse(J.find(".qslim-wrapper").get(0),{duration:300});J.hide();f(E).show();f(E).attr("show","0");f(E).prev("tr").prev("tr").removeClass("tr-pre");if(f(".tipB").length){f(".tipB").first().hide()}})}else{if(f(".tipB").length){f(".tipB").first().hide()}}if(f(".qslim-title a",J.get(0))){if(location.href.indexOf("word")!=-1){f(".qslim-title a",J.get(0)).first().attr("alog-action","br-s-title")}}}J.show();d.expand(J.find(".qslim-wrapper").get(0),{duration:300,onafterfinish:function(){if(f(".quickanswer-edit",J.get(0)).length){f(".quickanswer-edit",J.get(0)).get(0).focus()}if(!e.get("tipBShowed")){x(L)}}})},"html")}else{J.show();d.expand(J.find(".qslim-wrapper").get(0),{duration:300,onafterfinish:function(){if(f(".quickanswer-edit",J.get(0)).length){f(".quickanswer-edit",J.get(0)).get(0).focus()}}});J.children("td").css("color","#333333");f(E).attr("show","1")}});f(E).mouseenter(function(H){f.event(H).stopPropagation();f(E).addClass("tr-hover");if(f(".tipA").length){f(".tipA").first().hide();if(!e.get("haveDeleted")){A()}}if(f(".btn-qlist-del",f(E).get(0))){f(".btn-qlist-del",f(E).get(0)).first().css("visibility","visible")}});f(E).mouseleave(function(H){f.event(H).stopPropagation();f(E).removeClass("tr-hover");if(f(".btn-qlist-del",f(E).get(0))){f(".btn-qlist-del",f(E).get(0)).first().css("visibility","hidden")}});var G=f.dom("a",E)[0];f.dom(G).on("click",function(H){H.preventDefault()})})}if(f(".opt-sort").length){if(f(".sort-default")){f(".sort-default").attr("href","javascript:void(0)")}}f("#keyword-search-box form").submit(function(D){if(f.string.trim(f(this.word).val())==""||f.string.trim(f(this.word).val())=="���ؼ���ɸѡ"){var E=f(this.lm).val();var G=f(this.cid).val();if(E==0||E==8960){E=2}else{if(E==9472){E=4}else{E=8}}location.href="http://zhidao.baidu.com/browse/"+G+"?lm="+E+"&pn=0";f.event(D).preventDefault()}});if(f(".btn-refresh").length){f(".btn-refresh").on("click",function(D){D.preventDefault();if(location.href.match("refresh=1")){location.reload()}else{location.href=f(this).attr("href")}})}}function y(E){var G=f(f.event(E).target);var D=G.parents("div");var I=D.attr("qid"),J=D.attr("cid");var H=f(".quickanswer-edit",D.get(0)).val();if(f.string(H).trim()==""||H=="����༭�ش�����ͼƬ���������⵽��������ҳ"){f(".quick-error-tip",D.get(0)).first().html("����������������");f(".quickanswer-edit",D.get(0)).first().focus();return}k.fire("login.check",{isLogin:function(){r(G)},noLogin:function(){k.fire("login.log",{onLoginSuccess:function(){r(G)}})}})}function r(G){var K=G.parents(".answerarea");var I=K.attr("qid"),H=K.attr("cid");var J=f(".quickanswer-edit",K.get(0)).val();var D=G.parents("tr").prev("tr");var O={cm:100009,qid:I,cid:H,path:"/question/"+I+".html",co:J,fr:"qlquick",utdata:j.report()["c"]};if(f(".anoy",K.get(0)).get(0).checked){O.anoy=1}var P='<a alog-action="qb-username" class="user-name" href="http://www.baidu.com/p/'+g.name+'?from=zhidao" target="_blank">'+g.name+"</a>";if((O.anoy==1)||(g.isNoUserName)){P='<a alog-action="qb-username" class="user-name" href="#">����</a>'}if(!g.name){P='<span style="font-size:12px;">�ҵĻش�</span>'}var N='<div class="answer" id="answer-new-'+I+'">                   <div class="line">                        <div class="line info f-info">                            <span class="grid-r">1��ǰ</span>'+P+'</div><div class="line content" style="font-size:14px">                                <pre id="answer-content-'+I+'" accuse="aContent" class="answer-text mb-10">'+J+"</pre>                            </div>                   </div>                    </div>";var M='<div class="answer" id="answer-new-'+I+'">                   <div class="line">                        <div class="line info f-info">                            <span class="grid-r">1��ǰ</span>'+P+'</div><div class="line content" style="font-size:14px">                                <pre id="answer-content-'+I+'" accuse="aContent" class="answer-text mb-10">'+J+'</pre>                            </div>                            <div class="answer-inmis">�ش������Զ��ύ��Ŷ�������ĵȴ�һ��ɡ�</div>                   </div>                    </div>';var E='<div class="answer" style="border:none" id="answer-new-'+I+'">                   <div class="line">                        <div class="line info f-info">                            <span class="grid-r">1��ǰ</span>'+P+'</div><div class="line content" style="font-size:14px">                                <pre id="answer-content-'+I+'" accuse="aContent" class="answer-text mb-10">'+J+"</pre>                            </div>                   </div>                    </div>";var L='<div class="answer" style="border:none" id="answer-new-'+I+'">                   <div class="line">                        <div class="line info f-info">                            <span class="grid-r">1��ǰ</span>'+P+'</div><div class="line content" style="font-size:14px">                                <pre id="answer-content-'+I+'" accuse="aContent" class="answer-text mb-10">'+J+'</pre>                            </div>                            <div class="answer-inmis">�ش������Զ��ύ��Ŷ�������ĵȴ�һ��ɡ�</div>                   </div>                    </div>';new n({url:"/submit/ajax",params:O,beforeJump:function(S,R){h.send({type:2040,submit_click:"click",uname:g.name});f(".quick-num",D.get(0)).html((parseInt(f(".quick-num",D.get(0)).html().slice(0,-2))+1)+"�ش�");if(f(".qslim-ans-num",K.next().get(0)).length){f(".qslim-ans-num",K.next().get(0)).html("��"+(parseInt(f(".qslim-ans-num",K.next().get(0)).html().substring(1,4))+1)+"��")}else{f(".qslim-ans-info",G.parents("tr").get(0)).html("<h3><span class='qslim-ans-title'>�ش�</span><span class='qslim-ans-num'>��1��</span></h3>")}f(".qinfo-td",D.get(0)).html('<a href="#" id="haveanswered-'+I+'" class="btn-haveanswered"></a>');var T=new m({target:f(".qinfo-td",D.get(0)).get(0),direction:"down",arrow:"true",radius:true,offset:[-20,8],width:54,content:"�ѻش�",shadow:true,skin:"tip-txt"});f("#haveanswered-"+I).mouseenter(function(U){T.show()});f("#haveanswered-"+I).mouseleave(function(U){T.hide()});K.hide();if(R.check==1){if(f("#ans-container-"+I)){f("#ans-container-"+I).prepend(M)}else{f(".qslim-ans-info",G.parents("tr").get(0)).append('<div id="ans-container-'+I+'"></div>');f("#ans-container-"+I).append(L)}}else{if(f("#ans-container-"+I).length){f("#ans-container-"+I).prepend(N)}else{f(".qslim-ans-info",G.parents("tr").get(0)).append('<div id="ans-container-'+I+'"></div>');f("#ans-container-"+I).append(E)}d.highlight("answer-new-"+I,{duration:3000,beginColor:"#FDFEED",endColor:"#FDFDFD"})}var Q=e.get("answerData");e.set("answerData",Q?(Q+"|"+I):I);if(!g.name){}}})}function v(D,G){if((f(".qlist-tr").length-f(".btn-haveanswered").length)>1){var H={cm:100673,qid:f(D).attr("qid")};new n({url:"/submit/ajax",params:H,beforeJump:function(J,I){f(D).parents(".qlist-tr").remove();f("#tr-container-"+f(D).attr("qid")).remove();G.hide();if(!e.get("haveDeleted")){e.set("haveDeleted",1)}}})}else{var E=location.href.replace(/pn=([0-9]*)/,"pn=0");location.href=E;window.location.reload()}}function s(E,G){var D='<textarea class="quickanswer-edit" placeholder="����༭�ش�����ͼƬ���������⵽��������ҳ" name="quickanswer-edit" tip="����༭�ش�����ͼƬ���������⵽��������ҳ"></textarea><p class="quicksubinfo"><span class="quick-error-tip"></span><input class="anoy" type="checkbox"/><span>����</span><a qid="'+E+'" cid="'+G+'" class="quicksub btn btn-32-green"><em><b>�ύ�ش�</b></em></a></p>';f("#editBox-"+E).html(D)}function t(){var D=u();var E;if(g.isLogin){E=new m({target:"",direction:"down",arrow:"true",radius:true,offset:[-15,8],width:117,content:"�Ҳ���ش����",shadow:true,skin:"tip-qlist-del"})}else{E=new m({target:"",direction:"down",arrow:"true",radius:true,offset:[-15,8],width:145,content:"�Ҳ���ش����<br/>����¼�󷽿�ʹ�ã�",shadow:true,skin:"tip-qlist-del"})}f.forEach(f(".qlist-tr .align-l"),function(H,G){if(f.array(D).contains(f(H).attr("qid"))){f(".qinfo-td",f(H).parents(".qlist-tr").get(0)).html('<a href="#" id="haveanswered-'+f(H).attr("qid")+'" class="btn-haveanswered"></a>');var K=new m({target:f(".qinfo-td",f(H).parents(".qlist-tr").get(0)).get(0),direction:"down",arrow:"true",radius:true,offset:[-20,8],width:54,content:"�ѻش�",shadow:true,skin:"tip-txt"});f("#haveanswered-"+f(H).attr("qid")).mouseenter(function(L){f.event(L).stopPropagation();K.show();f(H).parents(".qlist-tr").first().addClass("tr-hover")});f("#haveanswered-"+f(H).attr("qid")).mouseleave(function(L){f.event(L).stopPropagation();K.hide()})}else{var J=/.*word=(.*)/;var I=J.exec(location.href);if(!I||(I[1].substring(0,1)=="&")){f(".qinfo-td",f(H).parents(".qlist-tr").get(0)).html('<a href="#" id="qlist-del-'+f(H).attr("qid")+'" class="btn-qlist-del"></a>');f("#qlist-del-"+f(H).attr("qid")).mouseover(function(L){f.event(L).stopPropagation();E.show(f("#qlist-del-"+f(H).attr("qid")).get(0));f(H).parents(".qlist-tr").first().addClass("tr-hover")});f("#qlist-del-"+f(H).attr("qid")).mouseleave(function(L){if(E.getStatus()=="show"){E.hide()}});f("#qlist-del-"+f(H).attr("qid")).mouseout(function(L){if(E.getStatus()=="show"){E.hide()}});f("#qlist-del-"+f(H).attr("qid")).on("click",function(L){f.event(L).preventDefault();f.event(L).stopPropagation();k.fire("login.check",{isLogin:function(){v(H,E)},noLogin:function(){k.fire("login.log",{onLoginSuccess:function(){v(H,E)}})}})})}}})}function u(){var E=[],D;if(g.isLogin&&(D=e.get("answerData"))){E=D.split("|")}else{e.remove("answerData")}return E}function q(D){f(D).on("focus",function(){if(f(this).val()=="����༭�ش�����ͼƬ���������⵽��������ҳ"){f(this).val("")}f(this).css({color:"#000",border:"1px solid #60A506"});if(f.browser.isGecko){var E=f(D).css("height");E=parseInt(E.substring(0,E.indexOf("px")));f(D).get(0).scrollTop=f(D).get(0).scrollHeight}});f(D).on("blur",function(){if(!f(this).val()){f(this).val("����༭�ش�����ͼƬ���������⵽��������ҳ")}if(f(this).val()=="����༭�ش�����ͼƬ���������⵽��������ҳ"){f(this).css({color:"#acacac"})}f(this).css({border:"1px solid #CCC"})});if(f.ie){f(D).on("propertychange",function(){f(D).next().find(".quick-error-tip").first().html("");if((f(D).get(0).scrollHeight>72)&&(f(D).get(0).scrollHeight<140)){f(D).css("height","140px")}})}else{f(D).on("input",function(){f(D).next().find(".quick-error-tip").first().html("");if((f(D).get(0).scrollHeight>72)&&(f(D).get(0).scrollHeight<140)){f(D).addClass("quickanswer-edit-higher");if(f.browser.isGecko){f(D).css({"overflow-y":"auto"})}}else{if(f.browser.isGecko){f(D).css({"overflow-y":""})}}})}}function o(){if(!e.get("tipAShowed")){var D=new m({target:f(".table-list tbody").children().first().children(".align-l").get(0),direction:"right",arrow:"true",radius:true,offset:[-20,0],closebox:true,width:190,content:"点击展开问题详情，快速回答！",shadow:true,skin:"tipA"});D.show();e.set("tipAShowed",1)}}function x(E){if(f(".tipA").length){f(".tipA").first().hide()}var D=new m({target:f("#container-"+E).get(0),direction:"right",arrow:"true",radius:true,offset:[-120,0],closebox:true,width:190,content:"�ڴ˵���������������ҳ",shadow:true,skin:"tipB"});D.show();e.set("tipBShowed",1)}function A(){if(!e.get("haveDeleted")&&(f(".tip-del-help").length==0)&&(f(".btn-qlist-del").length)){if((f(".tipA").length==0)||(f(".tipA").css("display")=="none")){var D=new m({target:f(".table-list tbody").children().first().children(".qinfo-td").get(0),direction:"right",arrow:"true",radius:true,offset:[-10,0],closebox:true,width:210,content:"����ش����⣿������ɾ��",shadow:true,skin:"tip-del-help"});D.show();e.set("haveDeleted",1);var E=setTimeout(function(){D.hide()},5000);if(f(".tip-del-help").length){f(".tip-del-help").first().on("mouseover",function(){clearTimeout(E)});f(".tip-del-help").first().on("mouseout",function(){E=setTimeout(function(){D.hide()},5000)})}}}}function C(){if(!e.get("haveDeleted")&&(f(".btn-qlist-del").length)){var D=f(".table-list tbody").children().first().find(".btn-qlist-del").first();D.css("visibility","visible")}}function p(){var D=f(".keyword-cancelbutton").first();f(".txt-keyword").first().on("focus",function(){D.css("display","block");if(f(this).val()=="���ؼ���ɸѡ"||f(this).val()==""){f(this).val("");D.css("display","none")}});f(".txt-keyword").first().on("blur",function(){if(f(this).val()==""||f(this).val()=="���ؼ���ɸѡ"){f(this).val("���ؼ���ɸѡ");f(this).css("color","#acacac");D.css("display","none")}});if(f.ie<9){f(".txt-keyword").first().on("propertychange",function(){D.css("display","block");if(f(this).val()==""||f(this).val()=="���ؼ���ɸѡ"){D.css("display","none")}f(this).css("color","black")})}else{f(".txt-keyword").first().on("input",function(){D.css("display","block");if(f(this).val()==""||f(this).val()=="���ؼ���ɸѡ"){D.css("display","none")}f(this).css("color","black")})}if(f(".keyword-cancelbutton").length){f(".keyword-cancelbutton").first().on("click",function(E){f.event(E).preventDefault();f(".txt-keyword").first().attr("value","");f(".txt-keyword").first().trigger("focus");D.css("display","none");f(".txt-keyword").css("color","#acacac")})}}function z(){var D=/lm=(9472|4)/;var E=/lm=(9728|8)/;if(D.exec(location.href)){f("#pipe-prev").css("visibility","hidden");f("#pipe-after").css("visibility","hidden")}else{if(E.exec(location.href)){f("#pipe-after").css("visibility","hidden")}else{f("#pipe-prev").css("visibility","hidden")}}}B.init=function(){if(location.hash){var D=location.hash.substring(1);f("#tr-container-"+D).prev("tr").trigger("click")}w();t();o();C();A();p()}});i=new a();return i},["/static/common/lib/tangram/base/base.js","/static/common/ui/log/log.js","/static/common/ui/ut/ut.js","/static/common/ui/event/event.js","/static/common/ui/store/store.js","/static/common/ui/baidu/fx/fx.js","/static/common/ui/submit/submit.js","/static/common/ui/dialog/dialog.js","/static/common/ui/tip/tip.js"]);