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
									<span style="float:left">2013��12���ܵ�ʮ����</span>
									<a style="float:right;" href="<%=request.getContextPath()%>/cms/aeonNews/intertek/">�����б�ҳ��</a>
								</div>
									<div class="content brand_body_1">
									<div id="myGallery">
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   ���棭���</font></h3>
											<p></p>
											<a href="201312/all/1.jpg" title="open image" class="open"></a>
											<img src="201312/pic/1.jpg" class="full" />
											<img src="201312/min/1.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   Ŀ¼</font></h3>
											<p></p>
											<a href="201312/all/2.jpg" title="open image" class="open"></a>
											<img src="201312/pic/2.jpg" class="full" />
											<img src="201312/min/2.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   ������</font></h3>
											<p></p>
											<a href="201312/all/3.jpg" title="open image" class="open"></a>
											<img src="201312/pic/3.jpg" class="full" />
											<img src="201312/min/3.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��  5��6ҳ</font></h3>
											<p></p>
											<a href="201312/all/4.jpg" title="open image" class="open"></a>
											<img src="201312/pic/4.jpg" class="full" />
											<img src="201312/min/4.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   7��8ҳ</font></h3>
											<p></p>
											<a href="201312/all/5.jpg" title="open image" class="open"></a>
											<img src="201312/pic/5.jpg" class="full" />
											<img src="201312/min/5.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��  9��10ҳ</font></h3>
											<p></p>
											<a href="201312/all/7.jpg" title="open image" class="open"></a>
											<img src="201312/pic/7.jpg" class="full" />
											<img src="201312/min/7.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   11��12ҳ</font></h3>
											<p></p>
											<a href="201312/all/9.jpg" title="open image" class="open"></a>
											<img src="201312/pic/9.jpg" class="full" />
											<img src="201312/min/9.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   13��14ҳ</font></h3>
											<p></p>
											<a href="201312/all/11.jpg" title="open image" class="open"></a>
											<img src="201312/pic/11.jpg" class="full" />
											<img src="201312/min/11.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   15��16ҳ</font></h3>
											<p></p>
											<a href="201312/all/13.jpg" title="open image" class="open"></a>
											<img src="201312/pic/13.jpg" class="full" />
											<img src="201312/min/13.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   17��18ҳ</font></h3>
											<p></p>
											<a href="201312/all/15.jpg" title="open image" class="open"></a>
											<img src="201312/pic/15.jpg" class="full" />
											<img src="201312/min/15.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   19��20ҳ</font></h3>
											<p></p>
											<a href="201312/all/17.jpg" title="open image" class="open"></a>
											<img src="201312/pic/17.jpg" class="full" />
											<img src="201312/min/17.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   21��22ҳ</font></h3>
											<p></p>
											<a href="201312/all/19.jpg" title="open image" class="open"></a>
											<img src="201312/pic/19.jpg" class="full" />
											<img src="201312/min/19.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   23��24ҳ</font></h3>
											<p></p>
											<a href="201312/all/21.jpg" title="open image" class="open"></a>
											<img src="201312/pic/21.jpg" class="full" />
											<img src="201312/min/21.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   25��26ҳ</font></h3>
											<p></p>
											<a href="201312/all/23.jpg" title="open image" class="open"></a>
											<img src="201312/pic/23.jpg" class="full" />
											<img src="201312/min/23.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   27��28ҳ</font></h3>
											<p></p>
											<a href="201312/all/25.jpg" title="open image" class="open"></a>
											<img src="201312/pic/25.jpg" class="full" />
											<img src="201312/min/25.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   29��30ҳ</font></h3>
											<p></p>
											<a href="201312/all/27.jpg" title="open image" class="open"></a>
											<img src="201312/pic/27.jpg" class="full" />
											<img src="201312/min/27.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   31��32ҳ</font></h3>
											<p></p>
											<a href="201312/all/29.jpg" title="open image" class="open"></a>
											<img src="201312/pic/29.jpg" class="full" />
											<img src="201312/min/29.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   33��34ҳ</font></h3>
											<p></p>
											<a href="201312/all/31.jpg" title="open image" class="open"></a>
											<img src="201312/pic/31.jpg" class="full" />
											<img src="201312/min/31.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   35��36ҳ</font></h3>
											<p></p>
											<a href="201312/all/33.jpg" title="open image" class="open"></a>
											<img src="201312/pic/33.jpg" class="full" />
											<img src="201312/min/33.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   37��38ҳ</font></h3>
											<p></p>
											<a href="201312/all/35.jpg" title="open image" class="open"></a>
											<img src="201312/pic/35.jpg" class="full" />
											<img src="201312/min/35.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   39��40ҳ</font></h3>
											<p></p>
											<a href="201312/all/37.jpg" title="open image" class="open"></a>
											<img src="201312/pic/37.jpg" class="full" />
											<img src="201312/min/37.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   41��42ҳ</font></h3>
											<p></p>
											<a href="201312/all/39.jpg" title="open image" class="open"></a>
											<img src="201312/pic/39.jpg" class="full" />
											<img src="201312/min/39.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   43��44ҳ</font></h3>
											<p></p>
											<a href="201312/all/41.jpg" title="open image" class="open"></a>
											<img src="201312/pic/41.jpg" class="full" />
											<img src="201312/min/41.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2013��12��  �ܵ�15��   45��46ҳ</font></h3>
											<p></p>
											<a href="201312/all/43.jpg" title="open image" class="open"></a>
											<img src="201312/pic/43.jpg" class="full" />
											<img src="201312/min/43.jpg" class="thumbnail" />
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