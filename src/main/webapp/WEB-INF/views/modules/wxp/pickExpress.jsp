<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>取货助手 -- 锡职快递服务平台</title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
	<link href="${ctxStatic}/wx/wxcss/normalize.css" type="text/css" rel="stylesheet" />
	<link href="${ctxStatic}/wx/wxcss/common.css" type="text/css" rel="stylesheet" />
	<script src="${ctxStatic}/wx/wxjs/jquery.min.js" type="text/javascript"></script>
	<script src="${ctxStatic}/wx/wxjs/common.js" type="text/javascript"></script>
	<script src="${ctxStatic}/wx/wxjs/notice.js" type="text/javascript"></script>
	<script src="${ctxStatic}/wx/wxjs/regexp.js" type="text/javascript"></script>
	<script src="${ctxStatic}/wx/wxjs/jweixin-1.2.0.js" type="text/javascript"></script>
	<style type="text/css">
		.expPickCont{
			padding-bottom: 20px;
		}
		.expPickCont .expPickIcon{
			width: 15%;
			margin: 0 auto;
			text-align: center;
		}
		.expPickCont .expPicTitle{
			text-align: center;
			font-weight: bold;
		}

		.searchInfoBtn{
			width: 90%;
			margin: 0 auto;
			text-align: center;
			line-height: 46px;
			border-radius: 23px;
			background: #1f72ff;
			color: #fff;
			font-weight: bolder;
		}

		.expUserInfoCont{
			width: 90%;
			margin: 20px auto 20px; 
			font-size: 14px;
		}
		.expInfoCont{
			width:100%;
		}
		.expInfoCont .expUserInfoDiv{
			width: 100%;
			background: #fff;
			border: 1px solid #f1f1f1;
			border-radius: 8px;
			margin-bottom: 10px;
		}
		.expInfoCont .expUserInfoDiv .expUserInfo{
			width: 96%;
			overflow: hidden;
			padding: 8px 2%;
			border-bottom: 1px dotted #f1f1f1;
			position: relative;
		}
		.expInfoCont .expUserInfoDiv .expUserInfo p{
			margin: 0px 0px 5px;
			padding: 0px;
			line-height: 20px;
		}
		.expInfoCont .expUserInfoDiv .expUserInfo .userName{
			font-weight: bolder;
			background: url(../static/wx/wximages/username.png) no-repeat center left;
			background-size: 15px;
			padding-left: 20px;
		}
		.expInfoCont .expUserInfoDiv .expUserInfo .userPhone{
			background: url(../static/wx/wximages/userphone.png) no-repeat center left;
			background-size: 15px;
			padding-left: 20px;
		}
		.expInfoCont .expUserInfoDiv .expUserInfo .userAdress{
			background: url(../static/wx/wximages/useradress.png) no-repeat center left;
			background-size: 15px;
			padding-left: 20px;
			color: #888888;
		}
		.expInfoCont .expUserInfoDiv .expUserInfo .expComp{
			position: absolute;
			top: 10px;
			right: 0px;
			width: 70px;
			padding: 0px 15px;
			line-height: 30px;
			background: #f8931d;
			color: #fff;
			border-top-left-radius: 15px;
			border-bottom-left-radius: 15px;
			box-shadow: 3px 3px 3px #888888;
		}
		.expInfoCont .expUserInfoDiv .expUserOrderInfo{
			width: 96%;
			overflow: hidden;
			padding: 0px 2%;
		}
		.expInfoCont .expUserInfoDiv .expUserOrderInfo .SendOrderDate{
			margin: 0px;
			padding: 0px;
			float: left;
			font-size: 12px;
			color: #888888;
			background: url(../static/wx/wximages/userdate.png) no-repeat center left;
			background-size: 15px;
			padding-left: 20px;
			line-height: 40px;
		}
		.expInfoCont .expUserInfoDiv .expUserOrderInfo .SendOrderConfBtn{
			margin: 0px;
			padding: 0px 15px;
			float: right;
			background:#77ac05;
			color: #fff;
			border-radius: 5px;
			line-height: 26px;
			margin-top: 7px;
		}

		.expUserInfoNull{
			width: 100%;
			line-height: 150px;
			text-align: center;
			color: #888888;
			display: none;
		}
		
	</style>
</head>
<body>
<div class="content">
	<input id="timestamp" type="hidden" value="${timestamp}" />
	<input id="noncestr" type="hidden" value="${nonceStr}" />
	<input id="signature" type="hidden" value="${signature}" />
	<input id="appId" type="hidden" value="${appId}" />
	<div class="headerNav">
		<div class="headerNavTop"><div class="headerNavIcon headerNavIconOut"><span></span><span></span></div></div>
		<div class="headerNavCont">
			<a href="../index.html">快递首页</a>
			<a href="../userhome.html">个人中心</a>
			<a href="../delivery.html">送货上门</a>
		<!-- 	<a href="../sendexpress.html">我要寄件</a> -->
			<a href="../lazyboard.html">懒人排行</a>
			<a href="../expassistant.html">快递助手</a>
		</div>
	</div>

	<div class="expPickCont">
		<div class="expPickIcon">
			<img src="${ctxStatic}/wx/wximages/pickexpicon.png" width="100%">
		</div>
		<p class="expPicTitle">确认取货</p>

		<div class="expPickInput">
			<form>
				<input id="PageContext" type="hidden" value="${pageContext.request.contextPath}" />
				<input id="wxCode" type="hidden" value="${wxCode}" />
				<div class="userInputCont">
					<div class="inputTypeCont">
						<div class="inputTitle">证件</div>
						<input type="text" id="expPickUserId" class="commonInputFunc" name="username" placeholder="请输入身份证号码...">
						<div class="commonFuncBtnScan" id="scanQRCodeBtn"></div>
					</div>
				</div>
			</form>

			<div class="searchInfoBtn">查找信息</div>
		</div>
	</div>

	<div class="expUserInfoCont"> 
		<div class="expUserInfoNull">
			暂无查到相关信息
		</div>
		<div class="expInfoCont">
			
		</div>
	</div>

</div>
<script type="text/javascript">
	$(function() {
		var pageContextVal = $("#PageContext").val();
		var wxCodeVal = $("#wxCode").val();
		function rzGetExpInfo(name,phone,expComp,address,expNum) {
			var $expUserInfoDiv = $("<div class='expUserInfoDiv'></div>");

			var $expUserInfo = $("<div class='expUserInfo'></div>");
			$expUserInfoDiv.append($expUserInfo);
			var $userName = $("<p class='userName'>"+name+"</p>");
			$expUserInfo.append($userName);
			var $userPhone = $("<p class='userPhone'>"+phone+"</p>");
			$expUserInfo.append($userPhone);
			var $userAddress = $("<p class='userAdress'>"+address+"</p>");
			$expUserInfo.append($userAddress);
			var $expComp = $("<div class='expComp'>"+expComp+"</div>");
			$expUserInfo.append($expComp);

			var $expUserOrderInfo = $("<div class='expUserOrderInfo'></div>")
			$expUserInfoDiv.append($expUserOrderInfo);
			var $SendOrderDate = $("<p class='SendOrderDate'>"+expNum+"</p>");
			$expUserOrderInfo.append($SendOrderDate);
			var $SendOrderConfBtn = $("<p class='SendOrderConfBtn'>确认收货</p>");
			$expUserOrderInfo.append($SendOrderConfBtn);

			$SendOrderConfBtn.bind("click",function(){
				var state = confirm("确认取货？");
				if(!state){
				   return false;
				}
				var expNum = $(this).siblings().first().text();
				 $.ajax({
				     type:'POST',
				     url:pageContextVal+'/ul/endExpress',
				     data:{'expNum':expNum,'code':wxCodeVal},
				     dataType: "json",
				     success:function(data){
				    	switch(data.code){
					    	case "1" : rzAlert("操作提示",data.message); break;
							case "0" : 
						     	alert("取货成功");
						     	$expUserInfoDiv.remove();
						     	//$(this).parent().parent().hide();
								break;
				    	}
				     },
				     error:function(){
				    	 rzAlert("操作提示","操作失败");
				     }
				    
				 });
			});

			$(".expInfoCont").append($expUserInfoDiv);
		}
		
		var searchBtnClick = function(){
			// 身份证格式校验
			var expPickUserId = $.trim($("#expPickUserId").val());
			if (!CheckUserId(expPickUserId)) {
				rzAlert("操作提示","身份证格式不对！");
				return false;
			}
			
			 $.ajax({
			     type:'POST',
			     url:pageContextVal+'/ul/queryExpress',
			     data:{'idCard':expPickUserId,'code':wxCodeVal},
				 dataType: "json",
			     success:function(data){
			    	    var jsontmp3 = data;
						$(".expUserInfoNull").hide();
						switch(jsontmp3.code) {
							case "1" : rzAlert("操作提示",jsontmp3.message); break;
							case "2" : rzAlert("操作提示",jsontmp3.message); break;
							case "0" : 
								$(".expUserInfoNull").hide();
								$(".expInfoCont").empty();
								if (jsontmp3.num == '0') {
									$(".expUserInfoNull").show();
									return;
								}
								for(var i=0; i < jsontmp3.num; i++){
									//alert(expInfo);
									var expInfo = jsontmp3.expressList[i];
									rzGetExpInfo(expInfo.name,expInfo.phone,expInfo.company,expInfo.address,expInfo.expressId);
								}
								break;
						}
			     },
			     error:function(){
			      
			     }
			 });
		};
		$(".searchInfoBtn").click(function() {
			searchBtnClick();
		});
		
		// JSSDK
		var appId = $("#appId").val();
		var timestamp = $("#timestamp").val();//时间戳
        var nonceStr = $("#noncestr").val();//随机串
        var signature = $("#signature").val();//签名
        wx.config({
            debug : false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
            appId : appId, // 必填，公众号的唯一标识
            timestamp : timestamp, // 必填，生成签名的时间戳
            nonceStr : nonceStr, // 必填，生成签名的随机串
            signature : signature,// 必填，签名，见附录1
            jsApiList : [ 
            	'checkJsApi',
		        'onMenuShareTimeline',
		        'onMenuShareAppMessage',
		        'onMenuShareQQ',
		        'onMenuShareWeibo',
		        'hideMenuItems',
		        'showMenuItems',
		        'hideAllNonBaseMenuItem',
		        'showAllNonBaseMenuItem',
		        'translateVoice',
		        'startRecord',
		        'stopRecord',
		        'onRecordEnd',
		        'playVoice',
		        'pauseVoice',
		        'stopVoice',
		        'uploadVoice',
		        'downloadVoice',
		        'chooseImage',
		        'previewImage',
		        'uploadImage',
		        'downloadImage',
		        'getNetworkType',
		        'openLocation',
		        'getLocation',
		        'hideOptionMenu',
		        'showOptionMenu',
		        'closeWindow',
		        'scanQRCode',
		        'chooseWXPay',
		        'openProductSpecificView',
		        'addCard',
		        'chooseCard',
		        'openCard'
            ]
        // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
        });

        wx.ready(function() {  
	        wx.checkJsApi({  
	            jsApiList : ['scanQRCode','startRecord','stopVoice','translateVoice'],  
	            success : function(res) {  

	            }  
	        });  

	        //扫描二维码  
	        document.querySelector('#scanQRCodeBtn').onclick = function() {  
	            wx.scanQRCode({
	                needResult : 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，  
	                scanType : [ "qrCode", "barCode"], // 可以指定扫二维码还是一维码，默认二者都有  
	                success : function(res) {  
	                    //扫码后获取结果参数赋值给Input
	                    var qrcodeNum = res.resultStr;

	                    // ajax
	                    $.ajax({
						    type:'POST',
						    url:pageContextVal+'/ul/queryQRecordData',
						    data:{'qRecordData':qrcodeNum},
						    dataType: "json",
						    success:function(data){
						    	var jsontmp = data;
								switch(jsontmp.code) {
									case "1" : rzAlert("操作提示",jsontmp.message); break;
									case "2" : rzAlert("操作提示",jsontmp.message); break;
									case "0" : 
										$("#expPickUserId").val(jsontmp.QRecordIdCard);
										searchBtnClick();
										break;
								}
						    },
						    error:function(){
						      
						    }
						});
	                }  
	            });  
	        };//end_document_scanQRCode  
	          
	    });//end_ready 

	});
</script>
</body>
</html>