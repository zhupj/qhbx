<%@ page language="java" contentType="text/html; charset=GBK"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=gbk" />
	<meta name="keywords" content="�������ٱ��չɷ����޹�˾,����,��������,�������ٱ���,��������" />
	<meta name="verify-v1" content="ku5eYujDwJor922aXLh2tNUokeTi14dftgw2Igobok0=" />
	<meta http-equiv="x-ua-compatible" content="ie=7" />
	<title>�������ٱ��չɷ����޹�˾</title>
	<link href="<%=request.getContextPath()%>/global/cssnew/aeon.css" rel="stylesheet" type="text/css" /> 
	<link href="<%=request.getContextPath()%>/global/cssnew/aeonNews.css" rel="stylesheet" type="text/css" /> 
	<link href="<%=request.getContextPath()%>/global/css/brand.css" rel="stylesheet" type="text/css"/>
	<link rel="stylesheet" href="css/layout.css" type="text/css" media="screen" charset="utf-8" />
	<link rel="stylesheet" href="css/jd.gallery.css" type="text/css" media="screen" charset="utf-8" />
	<link rel="stylesheet" href="css/ReMooz.css" type="text/css" media="screen" charset="utf-8" />
	<script src="scripts/mootools-1.2.1-core-yc.js" type="text/javascript"></script>
	<script src="scripts/mootools-1.2-more.js" type="text/javascript"></script>
	<script src="scripts/jd.gallery.js" type="text/javascript"></script>
	<script src="scripts/jd.gallery.transitions.js" type="text/javascript"></script>
	<script src="scripts/ReMooz.js" type="text/javascript"></script>
	</head>
		<script type="text/javascript">
			function startGallery() {
				var myGallery = new gallery($('myGallery'), {
					timed: false,
					useReMooz: true,
					embedLinks: false,
					defaultTransition: "fadeslideleft"
				});
			}
			window.addEvent('domready', startGallery);
		</script>
	<body>
		<div id="AllHtml">
			<div id="header">
				<jsp:include page="/cms/head.jsp" flush="true" />
			</div>
			<div id="maincontent">
					<jsp:include flush="true" page="/cms/aeonNews/leftpannel.jsp"></jsp:include>
				<div id="righttarea">
					<div id="rightflash">
						<div id="rightflasharea"> 
								<embed src="<%=request.getContextPath()%>/global/flash/aeonnews.swf" quality="high" wmode="transparent" bgcolor="#FFFFFF" width="764" height="206"  align="middle" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer"></embed>
						</div>
					</div>
					<jsp:include flush="true" page="/cms/aeonNews/intertek/newslocation.jsp"></jsp:include>
					<div id="overview">
						<div id="overviewtitle">�����ڿ�</div>
							<div class="overviewlist">
								<div class="currenttitle">
									<span style="float:left">2012��09���ܵ�ʮ��</span>
									<a style="float:right;" href="<%=request.getContextPath()%>/cms/aeonNews/intertek/">�����б�ҳ��</a>
								</div>
									<div class="content brand_body_1">
									<div id="myGallery">
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   ���棭���</font></h3>
											<p></p>
											<a href="201209/all/1.jpg" title="open image" class="open"></a>
											<img src="201209/pic/1.jpg" class="full" />
											<img src="201209/min/1.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   Ŀ¼</font></h3>
											<p></p>
											<a href="201209/all/2.jpg" title="open image" class="open"></a>
											<img src="201209/pic/2.jpg" class="full" />
											<img src="201209/min/2.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   ������</font></h3>
											<p></p>
											<a href="201209/all/3.jpg" title="open image" class="open"></a>
											<img src="201209/pic/3.jpg" class="full" />
											<img src="201209/min/3.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   5��6ҳ</font></h3>
											<p></p>
											<a href="201209/all/5.jpg" title="open image" class="open"></a>
											<img src="201209/pic/5.jpg" class="full" />
											<img src="201209/min/5.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   7��8ҳ</font></h3>
											<p></p>
											<a href="201209/all/7.jpg" title="open image" class="open"></a>
											<img src="201209/pic/7.jpg" class="full" />
											<img src="201209/min/7.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   9��10ҳ</font></h3>
											<p></p>
											<a href="201209/all/9.jpg" title="open image" class="open"></a>
											<img src="201209/pic/9.jpg" class="full" />
											<img src="201209/min/9.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   11��12ҳ</font></h3>
											<p></p>
											<a href="201209/all/11.jpg" title="open image" class="open"></a>
											<img src="201209/pic/11.jpg" class="full" />
											<img src="201209/min/11.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   13��14ҳ</font></h3>
											<p></p>
											<a href="201209/all/13.jpg" title="open image" class="open"></a>
											<img src="201209/pic/13.jpg" class="full" />
											<img src="201209/min/13.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   15��16ҳ</font></h3>
											<p></p>
											<a href="201209/all/15.jpg" title="open image" class="open"></a>
											<img src="201209/pic/15.jpg" class="full" />
											<img src="201209/min/15.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   17��18ҳ</font></h3>
											<p></p>
											<a href="201209/all/17.jpg" title="open image" class="open"></a>
											<img src="201209/pic/17.jpg" class="full" />
											<img src="201209/min/17.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   19��20ҳ</font></h3>
											<p></p>
											<a href="201209/all/19.jpg" title="open image" class="open"></a>
											<img src="201209/pic/19.jpg" class="full" />
											<img src="201209/min/19.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   21��22ҳ</font></h3>
											<p></p>
											<a href="201209/all/21.jpg" title="open image" class="open"></a>
											<img src="201209/pic/21.jpg" class="full" />
											<img src="201209/min/21.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   23��24ҳ</font></h3>
											<p></p>
											<a href="201209/all/23.jpg" title="open image" class="open"></a>
											<img src="201209/pic/23.jpg" class="full" />
											<img src="201209/min/23.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   25��26ҳ</font></h3>
											<p></p>
											<a href="201209/all/25.jpg" title="open image" class="open"></a>
											<img src="201209/pic/25.jpg" class="full" />
											<img src="201209/min/25.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   27��28ҳ</font></h3>
											<p></p>
											<a href="201209/all/27.jpg" title="open image" class="open"></a>
											<img src="201209/pic/27.jpg" class="full" />
											<img src="201209/min/27.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   29��30ҳ</font></h3>
											<p></p>
											<a href="201209/all/29.jpg" title="open image" class="open"></a>
											<img src="201209/pic/29.jpg" class="full" />
											<img src="201209/min/29.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   31��32ҳ</font></h3>
											<p></p>
											<a href="201209/all/31.jpg" title="open image" class="open"></a>
											<img src="201209/pic/31.jpg" class="full" />
											<img src="201209/min/31.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   33��34ҳ</font></h3>
											<p></p>
											<a href="201209/all/33.jpg" title="open image" class="open"></a>
											<img src="201209/pic/33.jpg" class="full" />
											<img src="201209/min/33.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   35��36ҳ</font></h3>
											<p></p>
											<a href="201209/all/35.jpg" title="open image" class="open"></a>
											<img src="201209/pic/35.jpg" class="full" />
											<img src="201209/min/35.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   37��38ҳ</font></h3>
											<p></p>
											<a href="201209/all/37.jpg" title="open image" class="open"></a>
											<img src="201209/pic/37.jpg" class="full" />
											<img src="201209/min/37.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   39��40ҳ</font></h3>
											<p></p>
											<a href="201209/all/39.jpg" title="open image" class="open"></a>
											<img src="201209/pic/39.jpg" class="full" />
											<img src="201209/min/39.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   41��42ҳ</font></h3>
											<p></p>
											<a href="201209/all/41.jpg" title="open image" class="open"></a>
											<img src="201209/pic/41.jpg" class="full" />
											<img src="201209/min/41.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   43��44ҳ</font></h3>
											<p></p>
											<a href="201209/all/43.jpg" title="open image" class="open"></a>
											<img src="201209/pic/43.jpg" class="full" />
											<img src="201209/min/43.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   45��46ҳ</font></h3>
											<p></p>
											<a href="201209/all/45.jpg" title="open image" class="open"></a>
											<img src="201209/pic/45.jpg" class="full" />
											<img src="201209/min/45.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   47��48ҳ</font></h3>
											<p></p>
											<a href="201209/all/47.jpg" title="open image" class="open"></a>
											<img src="201209/pic/47.jpg" class="full" />
											<img src="201209/min/47.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   49��50ҳ</font></h3>
											<p></p>
											<a href="201209/all/49.jpg" title="open image" class="open"></a>
											<img src="201209/pic/49.jpg" class="full" />
											<img src="201209/min/49.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   51��52ҳ</font></h3>
											<p></p>
											<a href="201209/all/51.jpg" title="open image" class="open"></a>
											<img src="201209/pic/51.jpg" class="full" />
											<img src="201209/min/51.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   53��54ҳ</font></h3>
											<p></p>
											<a href="201209/all/53.jpg" title="open image" class="open"></a>
											<img src="201209/pic/53.jpg" class="full" />
											<img src="201209/min/53.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   55��56ҳ</font></h3>
											<p></p>
											<a href="201209/all/55.jpg" title="open image" class="open"></a>
											<img src="201209/pic/55.jpg" class="full" />
											<img src="201209/min/55.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   57��58ҳ</font></h3>
											<p></p>
											<a href="201209/all/57.jpg" title="open image" class="open"></a>
											<img src="201209/pic/57.jpg" class="full" />
											<img src="201209/min/57.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   59��60ҳ</font></h3>
											<p></p>
											<a href="201209/all/59.jpg" title="open image" class="open"></a>
											<img src="201209/pic/59.jpg" class="full" />
											<img src="201209/min/59.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   61��62ҳ</font></h3>
											<p></p>
											<a href="201209/all/61.jpg" title="open image" class="open"></a>
											<img src="201209/pic/61.jpg" class="full" />
											<img src="201209/min/61.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   63��64ҳ</font></h3>
											<p></p>
											<a href="201209/all/63.jpg" title="open image" class="open"></a>
											<img src="201209/pic/63.jpg" class="full" />
											<img src="201209/min/63.jpg" class="thumbnail" />
										</div>
										
									<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   65��66ҳ</font></h3>
											<p></p>
											<a href="201209/all/65.jpg" title="open image" class="open"></a>
											<img src="201209/pic/65.jpg" class="full" />
											<img src="201209/min/65.jpg" class="thumbnail" />
										</div>
										
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   67��68ҳ</font></h3>
											<p></p>
											<a href="201209/all/67.jpg" title="open image" class="open"></a>
											<img src="201209/pic/67.jpg" class="full" />
											<img src="201209/min/67.jpg" class="thumbnail" />
										</div>	
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   69��70ҳ</font></h3>
											<p></p>
											<a href="201209/all/69.jpg" title="open image" class="open"></a>
											<img src="201209/pic/69.jpg" class="full" />
											<img src="201209/min/69.jpg" class="thumbnail" />
										</div>	
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   71��72ҳ</font></h3>
											<p></p>
											<a href="201209/all/71.jpg" title="open image" class="open"></a>
											<img src="201209/pic/71.jpg" class="full" />
											<img src="201209/min/71.jpg" class="thumbnail" />
										</div>	
										<div class="imageElement">
											<h3><font style="font-size:12px">2012��09��  �ܵ�10��   73��74ҳ</font></h3>
											<p></p>
											<a href="201209/all/73.jpg" title="open image" class="open"></a>
											<img src="201209/pic/73.jpg" class="full" />
											<img src="201209/min/73.jpg" class="thumbnail" />
										</div>	
										
									</div>
								</div>
							</div>
					</div>
					<div class="overviewbottom"> </div>
				</div>
			</div>
			<div id="copyright">
				<jsp:include page="/cms/copyright.jsp" flush="true" />
			</div>
		</div>
	</body>
</html>