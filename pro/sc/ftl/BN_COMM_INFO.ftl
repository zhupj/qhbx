﻿<style>
.portal-box-con tr {line-height:20px;}
.portal-box-con table tbody tr:hover td.icon{background-position:0 -708px}
.portal-box-con table tbody tr td.icon{width:3px;height:9px;margin-top:10px;background:url("/sc/img/pt_style/default/front_bg.png") no-repeat 0 -688px}
</style>
<div class='portal-box pt-gsyw-wrapper' id="${id}__box">
<#assign hasDate = hasDate!"2">
<#assign conhei = hei!"auto">
<div class='portal-box-title ${titleBar}' style='position:relative;'>
	<span class='portal-box-title-top'></span>
	<span class="portal-box-title-label">${CHNL_NAME}</span>
	<div class="portal-box-title-bottom-line"></div>
	<#if hasDate=='2'>
	<span class="portal-box-more-sc">&nbsp<a href="#" onclick="openListMoreByChnl('${CHNL_ID}','${CHNL_NAME}')">更多</a></span>
	<#elseif hasDate=='3'>
	<span class="portal-box-more-sc">&nbsp<a href="#" onclick="openListMoreByChnlNomal('${CHNL_ID}','${CHNL_NAME}')">更多</a></span>
	<#else>
	<span class="portal-box-more-sc">&nbsp<a href="#" onclick="openDateListMoreByChnl('${CHNL_ID}','${CHNL_NAME}')">更多</a></span>
	</#if>
</div>
<#if conhei=='auto'>
<div class='portal-box-con info-column' style='height:auto;'>
<#else>
<div class='portal-box-con info-column' style='height:${hei}px;max-height:${hei}px;'>
</#if>
<table width="100%" style="table-layout:fixed;margin-left:8px;">
<#if (_DATA_?size == 0)>
<tr><td align="center">该栏目下没有信息！</td></tr>
</#if>
<#list _DATA_ as content>
<tr>
	<td class="icon"></td>
	<td style="width:${mwidth!'66%'};position: relative;">
		<a id = '${CHNL_ID}${content.NEWS_ID}' title='${content.NEWS_SUBJECT}' style="width:100%;margin-left:3px;display: block;height:24px;" href="javascript:void(0);" onclick="newsView('${content.NEWS_ID}')">
			<span class="elipd" style="max-width:96%;">${content.NEWS_SUBJECT}</span>
			<script>
			var newQx = "${NEW_QX!'0'}";
			if (rhDate.doDateDiff('D',"${content.NEWS_TIME?substring(0,10)}",rhDate.getCurentTime().substring(0, 10))<=newQx) {
				$("<i class='new'></i>").appendTo($("#${CHNL_ID}${content.NEWS_ID}"));
			}
			</script>
		</a>
	</td>
 	<td style="width:${twidth!'30%'};">
		<span style="float:right;margin-right:6px;color:#999999;">[
				${content.NEWS_TIME?substring(0,10)}]
		</span>
	</td>
 
</tr>
</#list>
</table>
</div>
</div>
/*style="background:url('${bgurl!''}') no-repeat;"*/
<script type ="text/javascript" >
	jQuery(document).ready(function(jQuery) {
		var boxHtml = jQuery("#${id}__temp #${id}__box").html();
		jQuery("#${id}__temp").html("<div class='portal-box pt-gsyw-wrapper' id='${id}__box'>" + boxHtml + "</div>");
	});
</script>