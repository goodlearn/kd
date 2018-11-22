<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>出问题啦 -- 锡职快递服务平台</title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
	<link href="${ctxStatic}/wx/wxcss/normalize.css" type="text/css" rel="stylesheet" />
	<link href="${ctxStatic}/wx/wxcss/common.css" type="text/css" rel="stylesheet" />
	<script src="${ctxStatic}/wx/wxjs/jquery.min.js" type="text/javascript"></script>
	<script src="${ctxStatic}/wx/wxjs/common.js" type="text/javascript"></script>
	<style type="text/css">
		.notfoundCont{
			width: 100%;
		}
		.notfoundCont .notfoundImg{
			width: 150px;
			margin: 0 auto; 
		}
		.notfoundCont .notfoundImg img{
			display: block;
		}
		.notfoundCont .notfoundTxt{
			color: #747d86;
			text-align: center;
			font-size: 20px;
			font-weight: bold;
			margin-top: 10px;
		}
		.backIndex{
			width: 240px;
			margin: 40px auto 0px;
			text-align: center;
			line-height: 46px;
			border-radius: 23px;
			background: #1f72ff;
			color: #fff;
			font-weight: bolder;
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

	<div class="notfoundCont">
		<div class="notfoundImg">
			<img src="${ctxStatic}/wx/wximages/notfound.png" alt="图片加载中..." width="100%">
		</div>
		<div class="notfoundTxt">${message}</div>
	</div>
</div>
<script type="text/javascript">
</script>
</body>
</html>