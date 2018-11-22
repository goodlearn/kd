<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>快递助手 -- 锡职快递服务平台</title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
	<link href="${ctxStatic}/wx/wxcss/normalize.css" type="text/css" rel="stylesheet" />
	<link href="${ctxStatic}/wx/wxcss/common.css" type="text/css" rel="stylesheet" />
	<script src="${ctxStatic}/wx/wxjs/jquery.min.js" type="text/javascript"></script>
	<script src="${ctxStatic}/wx/wxjs/notice.js" type="text/javascript"></script>
	<script src="${ctxStatic}/wx/wxjs/common.js" type="text/javascript"></script>
	<style type="text/css">
		.expAssistantCont{
			width: 100%;
		}
		.expAssistantCont .controw1{
			width: 100%;
			height: 230px;
			background-image: linear-gradient(to top, #1f4974, #f4f4f4);
			position: relative;
		}
		.expAssistantCont .controw1 p{
			position: absolute;
			margin: 0px;
			left: 20px;
			bottom: 20px;
			font-size: 30px;
			font-weight: bolder;
			color: #fff;
		}

		.expAssistantCont .controw2{
			width: 100%;
			overflow: hidden;
		}
		.controw2Left{
			width: 50%;
			float: left;
			height: 230px;
		}
		.controw2Left .controw2Left1{
			width: 100%;
			height: 115px;
			background-image: linear-gradient(to top, #c9a47f, #c0bd3f);
			position: relative;
		}
		.controw2Left .controw2Left1 p{
			position: absolute;
			margin: 0px;
			left: 20px;
			bottom: 20px;
			font-size: 18px;
			font-weight: bolder;
			color: #fff;
		}
		.controw2Left .controw2Left2{
			width: 100%;
			height: 115px;
			background-image: linear-gradient(to top, #d15e5f, #a787ac);
			position: relative;
		}
		.controw2Left .controw2Left2 p{
			position: absolute;
			margin: 0px;
			left: 20px;
			bottom: 20px;
			font-size: 18px;
			font-weight: bolder;
			color: #fff;
		}
		.controw2right{
			width: 50%;
			float: left;
			height: 230px;
			background-image: linear-gradient(to top, #1e6e70, #90c292);
			position: relative;
		}
		.controw2right p{
			position: absolute;
			margin: 0px;
			left: 20px;
			bottom: 20px;
			font-size: 20px;
			font-weight: bolder;
			color: #fff;
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
<!-- 			<a href="./sendexpress.html">我要寄件</a>
 -->			<a href="./lazyboard.html">懒人排行</a>
			<a href="./expassistant.html">快递助手</a>
		</div>
	</div>
	<input id="PageContext" type="hidden" value="${pageContext.request.contextPath}" />
	<div class="expAssistantCont">
		<div class="controw1">
			<p>录入快递</p>
		</div>
		<div class="controw2">
			<div class="controw2Left">
				<div class="controw2Left1">
					<p>寄件信息</p>
				</div>
				<div class="controw2Left2">
					<p>送货信息</p>
				</div>
			</div>
			<div class="controw2right">
				<p>取货信息</p>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	var pageContextVal = $("#PageContext").val();
	$(function() {
		var initFunc = function(){
			var windowH = $(window).height();
			var row1H = $(".controw1").height();
			var row2H = windowH - row1H - 50; 

			$(".controw2right").css({"height":row2H+"px"});
			$(".controw2Left div").css({"height":row2H/2+"px"});
		};

		initFunc();

		$(window).resize(function(){
			initFunc();
		});

		//hyperlink
		$(".controw1").click(function(){
			window.location.href= pageContextVal+"/ul/reqAddExpress";
		});
		$(".controw2Left1").click(function(){
			rzAlert("温馨提示","新功能正在开发中，敬请期待！");
		});
		$(".controw2right").click(function(){
			window.location.href=pageContextVal+"/ul/reqPickExpress";
		});
		$(".controw2Left2").click(function(){
			rzAlert("温馨提示","新功能正在开发中，敬请期待！");
		});
	});
</script>
</body>
</html>