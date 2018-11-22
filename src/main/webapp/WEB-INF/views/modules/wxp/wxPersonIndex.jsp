<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>锡职快递服务平台</title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
	<link href="${ctxStatic}/wx/wxcss/normalize.css" type="text/css" rel="stylesheet" />
	<link href="${ctxStatic}/wx/wxcss/main.css" type="text/css" rel="stylesheet" />
	<script src="${ctxStatic}/wx/wxjs/jquery.min.js" type="text/javascript"></script>
	<script src="${ctxStatic}/wx/wxjs/notice.js"  type="text/javascript"></script>
	<script src="${ctxStatic}/wx/wxjs/common.js" type="text/javascript"></script>
</head>
<body>
	<div class="content">
		<div class="top autoH">
			<div class="logo autoH">
				<img src="${ctxStatic}/wx/wximages/logo.png" width="100%">
			</div>
		</div>
		<div class="middle">
			<form id="theForm">
				<div class="forminput">
					<input type="text" id="expressNum" name="expressNum" placeholder="请输入快递单号...">
					<input id="PageContext" type="hidden" value="${pageContext.request.contextPath}" />
					<div class="submBtn"></div>
				</div>
			</form>
		</div>
		<div class="funContdiv">
			<div class="funCont">
				<div class="userFun">
					<p class="funIcon userFunColor1"></p>
					<p class="funTxt">个人中心</p>
				</div>
				<div class="userFun">
					<p class="funIcon userFunColor2"></p>
					<p class="funTxt">懒人排行</p>
				</div>
				<div class="userFun">
					<p class="funIcon userFunColor3"></p>
					<p class="funTxt">取货二维码</p>
				</div>
				<div class="userFun">
					<p class="funIcon userFunColor4"></p>
					<p class="funTxt">敬请期待</p>
				</div>
				<c:if test="${not empty isManager}">
					<div class="userFun">
						<p class="funIcon userFunColor5"></p>
						<p class="funTxt">快递助手</p>
					</div>
				</c:if>
			</div>
		</div>
	
		<div class="footer">
			<p class="copytxt">&copy;信息技术工程学院</p>
		</div>
	</div>
	<script type="text/javascript">
	$(function(){
		var pageContextVal = $("#PageContext").val();
		$(".userFun").click(function(){
			var clickNum = $(this).index();
			switch(clickNum){
				//个人中心
				case(0): 
					window.location.href= pageContextVal+"/ul/userHome"; 
					break;
				case(1): 
					window.location.href= pageContextVal+"/ul/reqLazyboard"; 
					break;
				case(2): window.location.href= pageContextVal+"/ul/reqPersonQRcode"; break;
				case(3): rzAlert("温馨提示","新功能正在开发中，敬请期待！"); break;
				case(4): window.location.href= pageContextVal+"/ul/reqExpressAssist"; break;
			}
		});

		var initFunc = function(){
			var windowH = $(document).height();
			var windowW = $(document).width();

			$(".content").css({"height": windowH+"px"});

			var contentW = $(".content").width();
			var contentH = $(".content").height();

			var contH = windowH / 3;
			$(".autoH").css({"height": contH+"px"});

			// userFunH  content
			var middleH = $(".middle").height() + 40;
			var userFunH = contentH - contH - middleH - 30 -20; //footer,userfun(margin)
			$(".funContdiv").css({"height":userFunH+"px"});

			var funIconH = $(".funIcon").width(); 
			$(".funIcon").css({"height": funIconH+"px","line-height": funIconH+"px","border-radius": funIconH/2 +"px","font-size":funIconH/2 +"px"});

		};

		initFunc();

		$(window).resize(function(){
			initFunc();
		});

		// input focus
		$(".middle .forminput input").focus(function(){
			$(".middle .forminput").css({"border":"1px solid rgba(31,114,255,.75)","box-shadow":"0 0 8px rgba(31,114,255,.5)"});
		});

		// input blur
		$(".middle .forminput input").blur(function(){
			$(".middle .forminput").css({"border":"1px solid #333333","box-shadow":"none"});
		});

		//submit arrow
		$(".submBtn").click(function(){
			var expressNum = $("#expressNum").val();//快递单号
			 $.ajax({
				    type:'GET',
				    url:pageContextVal+'/ul/indexQueryExpress',
				    data:{'expressNum':expressNum},
				    dataType: "json",
				    success:function(data){
				    	var jsontmp = data;
						switch(jsontmp.code) {
							case "1" : rzAlert("操作提示",jsontmp.message); break;
							case "0" : 
								rzAlert("操作提示",jsontmp.message);
								break;
						}
				    },
				    error:function(){
				      
				    }
				});
		});
	});
	</script>
</body>
</html>