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
	<script src="${ctxStatic}/wx/wxjs/qrcode.min.js" type="text/javascript"></script>
	<script src="${ctxStatic}/wx/wxjs/common.js" type="text/javascript"></script>
	<style type="text/css">
			body{
			background: #f4f4f4;
		}
		.content{
			background: #82b927;
		}

		.userQRcodeTitle{
			width: 70%;
			margin: 20px auto;
			line-height: 40px;
			border: 2px solid #000;
			text-align: center;
			border-radius: 20px;
			font-size: 18px;
			font-weight: bolder; 
			display: none;
		}
		.userQRcodeCont{
			width: 100%;
			padding: 20px 0;
			margin: 0 auto;
			background: #82b927;
		} 
		#qrcode{
			width: calc(70% - 40px - 20px); 
			margin: 0 auto; 
			padding: 20px; 
			border: 10px solid #2f4e25;
			background: #fff;
			border-radius: 8px;
			position: relative;
		}
		#qrcode .refresh{
			width: 40px;
			height: 40px;
			position: absolute;
			left: calc(50% - 20px);
			top: calc(50% - 20px);
			background: url(../static/wx/wximages/refreshicon.png) rgba(0,0,0,0.8) no-repeat center center;  
			background-size: 35px 35px;
			border-radius: 5px;
		}
		.userQRcodeCont .userQRcodeDescTxt{
			width: 70%;
			line-height: 50px;
			margin: 0 auto;
			font-size: 25px;
			font-weight: bolder;
			text-align: center;
			color: #fff;
			border-bottom: 4px dotted #fff;
		}
		.userQRcodeCont .useStepCont{
			width: 70%; 
			margin: 20px auto 0px; 
		}
		.userQRcodeCont .useStepCont ul{
			margin: 0px; 
			padding: 0px; 
			list-style-type: none; 
			overflow: hidden; 
		}
		.userQRcodeCont .useStepCont ul li{
			width: calc(33.3% - 20px);
			padding: 10px 10px;
			float: left;
		} 
		.userQRcodeCont .useStepCont ul li .useStepTitle{
			width: 30px; 
			line-height: 30px; 
			border-radius: 15px; 
			margin: 0 auto; 
			font-size: 14px; 
			background: #fff; 
			text-align: center; 
		} 
		.userQRcodeCont .useStepCont ul li .useStepTxt{
			height: 30px; 
			font-size: 12px; 
			text-align: center; 
			color: #fff;
			margin: 0px; 
		} 
		.userQRcodeCont .useStepCont ul li .useStepIcon{
			width: 100%;
			height: 50px;
			margin: 5px 0;
		}
		.userQRcodeCont .useStepCont ul li:nth-child(1) .useStepIcon{
			background: url(../static/wx/wximages/qrcodeicon.png) no-repeat center center;  
			background-size: 40px 40px;
		}
		.userQRcodeCont .useStepCont ul li:nth-child(2) .useStepIcon{
			background: url(../static/wx/wximages/scanQRicon.png) no-repeat center center;
			background-size: 40px 40px;
		} 
		.userQRcodeCont .useStepCont ul li:nth-child(3) .useStepIcon{
			background: url(../static/wx/wximages/completeIcon.png) no-repeat center center; 
			background-size: 40px 40px;
		} 

		.userQRcodeCont .userInfoCont{
			width: 60%;
			overflow: hidden;
			font-size: 14px;
			margin: 0 auto;
			padding: 0 5%;
			background: #fff;
			display: none;
		}
		.userQRcodeCont .userInfoCont p{
			margin: 0px;
			padding: 0px;
			line-height: 30px;
		}
		.userQRcodeCont .userInfoCont .userName{
			float: left;
			background: url(../static/wx/wximages/uname.png) no-repeat center left;
			background-size: 18px;
			padding-left: 23px;
		}
		.userQRcodeCont .userInfoCont .userPhone{
			float: right;
			background: url(../static/wx/wximages/uphone.png) no-repeat center left;
			background-size: 18px;
			padding-left: 23px;
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
	<!-- 		<a href="./sendexpress.html">我要寄件</a> -->
			<a href="./lazyboard.html">懒人排行</a>
			<a href="./expassistant.html">快递助手</a>
		</div>
	</div>
	<input id="rTextData" type="hidden" value="${rTextData}" />
	<input id="PageContext" type="hidden" value="${pageContext.request.contextPath}" />
	<div class="userQRcodeCont">
		<div id="qrcode"><div class="refresh"></div></div>
		<p class="userQRcodeDescTxt">手机扫码，快捷取货</p>
		<div class="useStepCont">
			<ul>
				<li>
					<p class="useStepTitle">1</p>
					<p class="useStepIcon"></p>
					<p class="useStepTxt">向工作人员出示二维码</p>
				</li>
				<li>
					<p class="useStepTitle">2</p>
					<p class="useStepIcon"></p>
					<p class="useStepTxt">工作人员扫描二维码</p>
				</li>
				<li>
					<p class="useStepTitle">3</p>
					<p class="useStepIcon"></p>
					<p class="useStepTxt">完成取货</p>
				</li>
			</ul>
		</div>
	</div>
</div>
<script type="text/javascript">
	$(function() {
		var windowH = $(window).height();
		var contentH = $(".content").height();
		if (contentH < windowH) {
			$(".content").css({"height":windowH+"px"});
		}

		var qrcodeWH = $("#qrcode").width();

		// 数据生成
		var rTextData = $("#rTextData").val();

		var setQRcode = function(textdata){
			// 设置参数方式
			var qrcode = new QRCode('qrcode', {
			  text: textdata,
			  width: qrcodeWH,
			  height: qrcodeWH,
			  colorDark : '#000000',
			  colorLight : '#ffffff',
			  correctLevel : QRCode.CorrectLevel.H
			});
		};

		setQRcode(rTextData);

		$(".refresh").click(function(){
			//qrcode.clear();

			//setQRcode(textdata);
			location.reload();
		})
	});
</script>
</body>
</html>