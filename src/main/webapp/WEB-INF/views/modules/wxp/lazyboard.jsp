<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>锡职懒人排行 -- 锡职快递服务平台</title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
	<link href="${ctxStatic}/wx/wxcss/normalize.css" type="text/css" rel="stylesheet" />
	<link href="${ctxStatic}/wx/wxcss/common.css" type="text/css" rel="stylesheet" />
	<script src="${ctxStatic}/wx/wxjs/jquery.min.js" type="text/javascript"></script>
	<script src="${ctxStatic}/wx/wxjs/notice.js" type="text/javascript"></script>
	<script src="${ctxStatic}/wx/wxjs/common.js" type="text/javascript"></script>
	<style type="text/css">
		.lazyboardCont{
			width: 100%;
			overflow: hidden;
		}
		.lazyboardCont .lazyboardHead{
			width: 100%;
			background-image: linear-gradient(to top, #424890, #f4f4f4);
			box-shadow: 0px 3px 5px #666666;
		}
		 .lazyboardHead .lazyboardHeadTitle{
		 	text-align: center;
		 	font-size: 16px;
		 	line-height: 30px;
		 	color: #fff;
		 	font-weight: bold;
		 	background: url(../static/wx/wximages/sidaibg.png) no-repeat center center;
		 	background-size: cover;
		 }
		 .lazyboardCont .lazyboardHead .lazyboardRank{
			width: 100%;
			margin-top: 10px;
			padding-bottom: 10px;
			overflow: hidden;
		 }
		 .lazyboardCont .lazyboardHead .lazyboardRank .ranknumDiv{
		 	width: 32%;
		 	margin-left: 1%;
		 	padding: 10px 0px;
		 	float: left;
		 	position: relative;
		 }
		 .lazyboardHead .lazyboardRank .ranknumDiv .ranknum2{
		 	width: 40%;
		 	margin: calc(50% - 40%) auto;
		 	border-radius: calc(50%);
		 	border: 2px solid #72d664;
		 	overflow: hidden;
		 	box-shadow: 0px 0px 3px #72d664;
		 }
		 .lazyboardHead .lazyboardRank .ranknumDiv .ranknum1{
		 	width: 50%;
		 	margin: 0 auto calc(50% - 40%);
		 	border-radius: calc(50%);
		 	border: 2px solid #f8ca03;
		 	box-shadow: 0px 0px 3px #f8ca03;
		 	overflow: hidden;
		 }
		 .lazyboardHead .lazyboardRank .ranknumDiv .ranknum3{
		 	width: 40%;
		 	margin: calc(50% - 40%) auto;
		 	border-radius: calc(50%);
		 	border: 2px solid #b66ff3;
		 	box-shadow: 0px 0px 3px #b66ff3;
		 	overflow: hidden;
		 }
		 .lazyboardHead .lazyboardRank .ranknumDiv .rankNum2Icon{
		 	position: absolute;
		 	bottom: 30px;
		 	left: calc(50% - 15px);
		 	width: 30px;
		 	height: 30px;
		 	background: url(../static/wx/wximages/ranknum2.png) no-repeat center center;
		 	background-size: 100%;
		 }
		 .lazyboardHead .lazyboardRank .ranknumDiv .rankNum1Icon{
		 	position: absolute;
		 	bottom: 30px;
		 	left: calc(50% - 17px);
		 	width: 34px;
		 	height: 34px;
		 	background: url(../static/wx/wximages/ranknum1.png) no-repeat center center;
		 	background-size: 100%;
		 }
		 .lazyboardHead .lazyboardRank .ranknumDiv .rankNum3Icon{
		 	position: absolute;
		 	bottom: 30px;
		 	left: calc(50% - 15px);
		 	width: 30px;
		 	height: 30px;
		 	background: url(../static/wx/wximages/ranknum3.png) no-repeat center center;
		 	background-size: 100%;
		 }
		 .lazyboardHead .lazyboardRank .ranknumDiv .rankNumName{
		 	font-size: 12px;
		 	width: 90%;
		 	margin: 15px auto 0px;
		 	line-height: 20px;
		 	color: #fff;
		 	text-align: center;
		 	overflow: hidden;
			white-space: nowrap;
			text-overflow: ellipsis;
		 }

		 .lazyboardCont .lazyboardNavCont{
		 	width: 88%;
		 	margin: 30px auto 20px;
		 	overflow: hidden;
		 	border: 1px solid #4099eb;
		 	border-radius: 19px;
		 	box-shadow: 0px 0px 2px #888888;
		 }
		 .lazyboardCont .lazyboardNavCont .lazyboardNav{
		 	width: 33.3%;
		 	line-height: 38px;
		 	float: left;
		 	text-align: center;
		 	color: #333333;
		 	font-size: 14px;
		 	font-weight: bold;
		 }
		 .lazyboardCont .lazyboardNavCont .lazyboardNavFocus{
			background-image: linear-gradient(to top, #424890, #4099eb);
			color: #fff;
		 }
		 
		 .boardRankScroll{
		 	width: 300%;
		 	margin: 20px 0px;
		 	overflow: hidden;
		 }
		 .lazyboardCont .lazyboardRankCont{
		 	width: calc(100% / 3);
		 	float: left;
		 }
		 .lazyboardCont .lazyboardRankCont .lazyboardRankDiv{
		 	width: 96%;
		 	height: 52px;
		 	padding: 10px 2%;
		 	margin-top: 5px;
		 	overflow: hidden;
		 	background: #fff;
		 	border-top: #f4f4f4;
		 	border-bottom: #f4f4f4;
		 	box-shadow: 0px 0px 2px #e1e1e1;
		 }
		 .lazyboardRankCont .lazyboardRankDiv .lazyboardRankNumTxt{
		 	width: 35px;
		 	padding-right:15px;
		 	line-height: 52px;
		 	text-align: center;
		 	font-size: 16px;
		 	font-weight: bolder;
		 	font-style: italic;
		 	float: left;
		 	color: #333333;
		 }
		 .lazyboardRankCont .lazyboardRankDiv .lazyboardRankIcon{
			width: 50px;
			height: 50px;
			float: left;
			border: 2px solid #4099eb;
			border-radius: 25px;
			overflow: hidden;
		 }
		 .lazyboardRankCont .lazyboardRankDiv .lazyboardRankNameTxt{
		 	width: calc(100% - 50px - 52px - 85px - 10px - 25px);
		 	color: #333333;
		 	padding-left: 10px;
		 	line-height: 52px;
		 	font-size: 14px;
		 	float: left;
		 	overflow: hidden;
		 	white-space: nowrap;
		 	text-overflow: ellipsis;
		 }
		 .lazyboardRankCont .lazyboardRankDiv .lazyboardRankScore{
		 	width: 95px;
		 	padding-right: 10px;
		 	line-height: 52px;
		 	font-size: 20px;
		 	font-weight: bolder;
		 	float: right;
		 	color: #4099eb;
		 	text-align: right;
		 }

		 .tab2,.tab3{
		 	
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
	<div class="lazyboardCont">
		<div class="lazyboardHead">
			<div class="lazyboardHeadTitle">锡职懒人榜</div>
			<div class="lazyboardRank">
				<c:if test="${not empty totalData}">
					<c:forEach var="td" items="${totalData}" begin="1" end="1" step="1" varStatus="i"> 
				        	 <div class="ranknumDiv">
								<div class="ranknum2">
									<img src="${td.headimgurl}" width="100%">
								</div>
								<div class="rankNum2Icon"></div>
								<div class="rankNumName">${td.nickname}</div>
							</div>
				    </c:forEach>
				      <c:forEach var="td" items="${totalData}" begin="0" end="0" step="1" varStatus="i"> 
				        	 <div class="ranknumDiv">
								<div class="ranknum1">
									<img src="${td.headimgurl}" width="100%">
								</div>
								<div class="rankNum1Icon"></div>
								<div class="rankNumName">${td.nickname}</div>
							</div>
				    </c:forEach>
				    <c:forEach var="td" items="${totalData}" begin="2" end="2" step="1" varStatus="i"> 
				        	 <div class="ranknumDiv">
								<div class="ranknum3">
									<img src="${td.headimgurl}" width="100%">
								</div>
								<div class="rankNum3Icon"></div>
								<div class="rankNumName">${td.nickname}</div>
							</div>
				    </c:forEach>
				</c:if>
				<c:if test="${empty totalData}">
						暂无数据
				</c:if>
				
				
			</div>
		</div>

		<div class="lazyboardNavCont">
			<div class="lazyboardNav lazyboardNavFocus">总排名</div>
			<div class="lazyboardNav">年排名</div>
			<div class="lazyboardNav">月排名</div>
		</div>

		<div class="boardRankScroll">
			<div class="lazyboardRankCont tab1">
				<c:if test="${not empty totalData}">
					<c:forEach  var="td" items="${totalData}" varStatus="i">
					     <div class="lazyboardRankDiv">
							<div class="lazyboardRankNumTxt">${i.count}</div>
							<div class="lazyboardRankIcon">
								<img src="${td.headimgurl}" width="100%">
							</div>
							<div class="lazyboardRankNameTxt">${td.nickname}</div>
							<div class="lazyboardRankScore">${td.expressNum}</div>
						</div>
					</c:forEach>
				</c:if>
				<c:if test="${empty totalData}">
						暂无数据
				</c:if>
			</div>

			<div class="lazyboardRankCont tab2">
					<c:if test="${not empty yearData}">
						<c:forEach  var="td" items="${yearData}" varStatus="i">
						     <div class="lazyboardRankDiv">
								<div class="lazyboardRankNumTxt">${i.count}</div>
								<div class="lazyboardRankIcon">
									<img src="${td.headimgurl}" width="100%">
								</div>
								<div class="lazyboardRankNameTxt">${td.nickname}</div>
								<div class="lazyboardRankScore">${td.expressNum}</div>
							</div>
						</c:forEach>
					</c:if>
					<c:if test="${empty yearData}">
						暂无数据
					</c:if>
			</div>

			<div class="lazyboardRankCont tab3">
				<c:if test="${not empty monthData}">
						<c:forEach  var="td" items="${monthData}" varStatus="i">
						     <div class="lazyboardRankDiv">
							<div class="lazyboardRankNumTxt">${i.count}</div>
							<div class="lazyboardRankIcon">
								<img src="${td.headimgurl}" width="100%">
							</div>
							<div class="lazyboardRankNameTxt">${td.nickname}</div>
							<div class="lazyboardRankScore">${td.expressNum}</div>
						</div>
					</c:forEach>
				</c:if>
				<c:if test="${empty monthData}">
					暂无数据
				</c:if>
		</div>
		
	</div>
</div>

<script type="text/javascript">
	$(function() {
		$(".lazyboardNavCont .lazyboardNav").click(function(){
			var windowW = $(window).width();
			if (windowW > 600) {
				windowW = 600;
			}
			var indexnum = $(this).index();
			var focusnum = $(".lazyboardNavCont .lazyboardNav").index($(".lazyboardNavFocus"));
			$(this).siblings().removeClass("lazyboardNavFocus");
			$(this).addClass("lazyboardNavFocus");

			if (indexnum != focusnum) {
				$(".boardRankScroll").stop();

				switch(indexnum){
					case 0 : $(".boardRankScroll").animate({"margin-left": "0px"});	 break;
					case 1 : $(".boardRankScroll").animate({"margin-left": -windowW+"px"}); break;
					case 2 : $(".boardRankScroll").animate({"margin-left": -windowW*2+"px"}); break;
				}
			}
		});
	});
</script>
</body>
</html>