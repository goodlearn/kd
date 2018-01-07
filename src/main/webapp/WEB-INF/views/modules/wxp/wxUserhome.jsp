<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户中心 -- 锡职快递服务平台</title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
	<link href="${ctxStatic}/wx/wxcss/normalize.css" type="text/css" rel="stylesheet" />
	<link href="${ctxStatic}/wx/wxcss/common.css" type="text/css" rel="stylesheet" />
	<script src="${ctxStatic}/wx/wxjs/jquery.min.js" type="text/javascript"></script>
	<script src="${ctxStatic}/wx/wxjs/common.js" type="text/javascript"></script>
	<style type="text/css">
		body{
			background: #f4f4f4;
		}
		
		.userInfoCont{
			
		}
		.userHeaderDiv{
			background: #fff;
		}
		.userHeaderDiv .userHeader{
			width: 90%;
			margin: 0 auto;
			overflow: hidden;
			padding: 20px 0px 10px;
			border-bottom: 1px solid #f1f1f1;
		}
		.userHeader .headerImg{
			width: 20%;
			float: left;
			border-radius: 50%;
			overflow: hidden;
		}
		.userHeader .headerImg img{
			display: block;
		}
		.userHeader p{
			margin: 0;
			margin-left: 15px;
			padding-left: 35px;
			font-weight: bold;
			float: left;
			background: url(../static/wx/wximages/student.png) no-repeat center left;
			background-size: 30px;
			overflow: hidden;
			white-space: nowrap; 
			text-overflow: ellipsis;
		}

		.userHeaderDiv .headerProm{
			width: 90%;
			margin: 0 auto;
			font-size: 12px;
			color:#888888;
		}
		.headerProm .editUserInfo{
			line-height: 60px;
			font-size: 14px;
			color:#333333;
			background: url(../static/wx/wximages/rightArrow.png) no-repeat center right;
			background-size: 20px;
			border-bottom: 1px solid #f4f4f4;
		}
		.headerProm .expUserInfo{
			line-height: 60px;
			font-size: 14px;
			color:#333333;
			background: url(../static/wx/wximages/rightArrow.png) no-repeat center right;
			background-size: 20px;
		}

		.userNewsCont{
			width: 90%;
			padding: 0px 5%;
			margin: 30px auto 10px;
			background: #fff;
		}
		.userNewsCont .userNews{
			width: 100%;
			border-bottom: 1px solid #f4f4f4;
			background: url(../static/wx/wximages/rightArrow.png) no-repeat center right;
			background-size: 20px;
		}
		.userNewsCont .userNews p{
			width: 80%;
			margin:0px;
			padding-left: 30px;
			line-height: 60px;
			font-size: 12px;
			background: url(../static/wx/wximages/usernotice.png) no-repeat center left;
			background-size: 20px;
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
		}
		.userNewsCont .userNewsLast{
			border:none;
		}
	</style>
</head>
<body>
	<div class="content">
	<div class="headerNav">
		<div class="headerNavTop"><div class="headerNavIcon headerNavIconOut"><span></span><span></span></div></div>
		<div class="headerNavCont">
			<a href="./index.html">快递首页</a>
			<a href="./userhome.html">个人中心</a>
			<a href="./delivery.html">送货上门</a>
			<a href="./sendexpress.html">我要寄件</a>
			<a href="./lazyboard.html">懒人排行</a>
			<a href="./expassistant.html">快递助手</a>
		</div>
	</div>

	<div class="userInfoCont">
		<div class="userHeaderDiv">
			<div class="userHeader">
				<div class="headerImg">
					<img src="${ctxStatic}/wx/wximages/userHeadImg.jpg" alt="正在加载中..." width="100%">
				</div>
				<p>${sysWxUser.name}</p>
			</div>
			<div class="headerProm">
				<p>请及时完善个人信息，方便快递员配送</p>
				<input id="openId" type="hidden" value="${openId}" />
				<input id="PageContext" type="hidden" value="${pageContext.request.contextPath}" />
				<div class="editUserInfo">
					完善个人信息
				</div>
				<div class="expUserInfo">
					我的懒人排行
				</div>
			</div>
		</div>

		<div class="userNewsCont">
			<div class="userNews  userNewsRead">
				<p>[通知]双十一来临，尽快完善个人信息</p>
			</div>
			<div class="userNews  userNewsRead">
				<p>[通知]双十一来临，尽快完善个人信息</p>
			</div>
			<div class="userNews  userNewsUnread userNewsLast">
				<p>[通知]双十一来临，尽快完善个人信息</p>
			</div>
		</div>
	</div>

	<div class="footer">
		<p class="copyInfotxt">&copy;信息技术工程学院</p>
	</div>
</div>
<script type="text/javascript">
	$(function() {
		var openId = $("#openId").val();
		var pageContextVal = $("#PageContext").val();
		var initFun = function(){
			var windowH = $(window).height();
			var contentH = $(".userInfoCont").height();
			if ((windowH - 30 -50)>contentH) {
				$(".userInfoCont").css({"height":(windowH -30 - 50)+"px"}); //nav footer
			}

			var headerW = $(".userHeader").width();
			var headImgH = headerW*0.2;
			var headPW = headerW*0.8 - 60; // 15+35
			$(".userHeader p").css({"line-height":headImgH+"px","width":headPW+"px"});
		};

		initFun();

		$(window).resize(function(){
			initFun();
		});

		// user Info Edit
		$(".editUserInfo").click(function(){
			window.location.href=pageContextVal+"/wx/reqUserInfoEdit?openId="+openId; 
		});
		$(".expUserInfo").click(function(){
			window.location.href="userLazyboard.html"; 
		});

		// user notice url
		$(".userNewsCont .userNews p").click(function(){
			var clickNum = $(this).index();
			switch(clickNum){
				case(0): window.location.href="./notice/usernotice.html"; break;
			}
		});
	});
</script>
</body>
</html>