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
									<span style="float:left">2011��03�µ�һ��</span>
									<a style="float:right;"href="<%=request.getContextPath()%>/cms/aeonNews/intertek/">�����б�ҳ��</a>
								</div>
								<div class="content brand_body_1">
									<div id="myGallery">
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   ���棭���</font></h3>
											<p></p>
											<a href="201103/all/1.jpg" title="open image" class="open"></a>
											<img src="201103/pic/1.jpg" class="full" />
											<img src="201103/min/1.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   Ŀ¼</font></h3>
											<p></p>
											<a href="201103/all/2.jpg" title="open image" class="open"></a>
											<img src="201103/pic/2.jpg" class="full" />
											<img src="201103/min/2.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   3��4ҳ</font></h3>
											<p></p>
											<a href="201103/all/3.jpg" title="open image" class="open"></a>
											<img src="201103/pic/3.jpg" class="full" />
											<img src="201103/min/3.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   5��6ҳ</font></h3>
											<p></p>
											<a href="201103/all/4.jpg" title="open image" class="open"></a>
											<img src="201103/pic/4.jpg" class="full" />
											<img src="201103/min/4.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   7��8ҳ</font></h3>
											<p></p>
											<a href="201103/all/5.jpg" title="open image" class="open"></a>
											<img src="201103/pic/5.jpg" class="full" />
											<img src="201103/min/5.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   9��10ҳ</font></h3>
											<p></p>
											<a href="201103/all/6.jpg" title="open image" class="open"></a>
											<img src="201103/pic/6.jpg" class="full" />
											<img src="201103/min/6.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   11��12ҳ</font></h3>
											<p></p>
											<a href="201103/all/7.jpg" title="open image" class="open"></a>
											<img src="201103/pic/7.jpg" class="full" />
											<img src="201103/min/7.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   13��14ҳ</font></h3>
											<p></p>
											<a href="201103/all/8.jpg" title="open image" class="open"></a>
											<img src="201103/pic/8.jpg" class="full" />
											<img src="201103/min/8.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   15��16ҳ</font></h3>
											<p></p>
											<a href="201103/all/9.jpg" title="open image" class="open"></a>
											<img src="201103/pic/9.jpg" class="full" />
											<img src="201103/min/9.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   17��18ҳ</font></h3>
											<p></p>
											<a href="201103/all/10.jpg" title="open image" class="open"></a>
											<img src="201103/pic/10.jpg" class="full" />
											<img src="201103/min/10.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   19��20ҳ</font></h3>
											<p></p>
											<a href="201103/all/11.jpg" title="open image" class="open"></a>
											<img src="201103/pic/11.jpg" class="full" />
											<img src="201103/min/11.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   21��22ҳ</font></h3>
											<p></p>
											<a href="201103/all/12.jpg" title="open image" class="open"></a>
											<img src="201103/pic/12.jpg" class="full" />
											<img src="201103/min/12.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   23��24ҳ</font></h3>
											<p></p>
											<a href="201103/all/13.jpg" title="open image" class="open"></a>
											<img src="201103/pic/13.jpg" class="full" />
											<img src="201103/min/13.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   25��26ҳ</font></h3>
											<p></p>
											<a href="201103/all/14.jpg" title="open image" class="open"></a>
											<img src="201103/pic/14.jpg" class="full" />
											<img src="201103/min/14.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   27��28ҳ</font></h3>
											<p></p>
											<a href="201103/all/15.jpg" title="open image" class="open"></a>
											<img src="201103/pic/15.jpg" class="full" />
											<img src="201103/min/15.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   29��30ҳ</font></h3>
											<p></p>
											<a href="201103/all/16.jpg" title="open image" class="open"></a>
											<img src="201103/pic/16.jpg" class="full" />
											<img src="201103/min/16.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   31��32ҳ</font></h3>
											<p></p>
											<a href="201103/all/17.jpg" title="open image" class="open"></a>
											<img src="201103/pic/17.jpg" class="full" />
											<img src="201103/min/17.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   33��34ҳ</font></h3>
											<p></p>
											<a href="201103/all/18.jpg" title="open image" class="open"></a>
											<img src="201103/pic/18.jpg" class="full" />
											<img src="201103/min/18.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   35��36ҳ</font></h3>
											<p></p>
											<a href="201103/all/19.jpg" title="open image" class="open"></a>
											<img src="201103/pic/19.jpg" class="full" />
											<img src="201103/min/19.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   37��38ҳ</font></h3>
											<p></p>
											<a href="201103/all/20.jpg" title="open image" class="open"></a>
											<img src="201103/pic/20.jpg" class="full" />
											<img src="201103/min/20.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   39��40ҳ</font></h3>
											<p></p>
											<a href="201103/all/21.jpg" title="open image" class="open"></a>
											<img src="201103/pic/21.jpg" class="full" />
											<img src="201103/min/21.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   41��42ҳ</font></h3>
											<p></p>
											<a href="201103/all/22.jpg" title="open image" class="open"></a>
											<img src="201103/pic/22.jpg" class="full" />
											<img src="201103/min/22.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   43��44ҳ</font></h3>
											<p></p>
											<a href="201103/all/23.jpg" title="open image" class="open"></a>
											<img src="201103/pic/23.jpg" class="full" />
											<img src="201103/min/23.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   45��46ҳ</font></h3>
											<p></p>
											<a href="201103/all/24.jpg" title="open image" class="open"></a>
											<img src="201103/pic/24.jpg" class="full" />
											<img src="201103/min/24.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   47��48ҳ</font></h3>
											<p></p>
											<a href="201103/all/25.jpg" title="open image" class="open"></a>
											<img src="201103/pic/25.jpg" class="full" />
											<img src="201103/min/25.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   49��50ҳ</font></h3>
											<p></p>
											<a href="201103/all/26.jpg" title="open image" class="open"></a>
											<img src="201103/pic/26.jpg" class="full" />
											<img src="201103/min/26.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   51��52ҳ</font></h3>
											<p></p>
											<a href="201103/all/27.jpg" title="open image" class="open"></a>
											<img src="201103/pic/27.jpg" class="full" />
											<img src="201103/min/27.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   53��54ҳ</font></h3>
											<p></p>
											<a href="201103/all/28.jpg" title="open image" class="open"></a>
											<img src="201103/pic/28.jpg" class="full" />
											<img src="201103/min/28.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   55��56ҳ</font></h3>
											<p></p>
											<a href="201103/all/29.jpg" title="open image" class="open"></a>
											<img src="201103/pic/29.jpg" class="full" />
											<img src="201103/min/29.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   57��58ҳ</font></h3>
											<p></p>
											<a href="201103/all/30.jpg" title="open image" class="open"></a>
											<img src="201103/pic/30.jpg" class="full" />
											<img src="201103/min/30.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   59��60ҳ</font></h3>
											<p></p>
											<a href="201103/all/31.jpg" title="open image" class="open"></a>
											<img src="201103/pic/31.jpg" class="full" />
											<img src="201103/min/31.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   61��62ҳ</font></h3>
											<p></p>
											<a href="201103/all/32.jpg" title="open image" class="open"></a>
											<img src="201103/pic/32.jpg" class="full" />
											<img src="201103/min/32.jpg" class="thumbnail" />
										</div>
										<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   63��64ҳ</font></h3>
											<p></p>
											<a href="201103/all/33.jpg" title="open image" class="open"></a>
											<img src="201103/pic/33.jpg" class="full" />
											<img src="201103/min/33.jpg" class="thumbnail" />
										</div>
										
									<div class="imageElement">
											<h3><font style="font-size:12px">2011��03��  ��1��   65��66ҳ</font></h3>
											<p></p>
											<a href="201103/all/34.jpg" title="open image" class="open"></a>
											<img src="201103/pic/34.jpg" class="full" />
											<img src="201103/min/34.jpg" class="thumbnail" />
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