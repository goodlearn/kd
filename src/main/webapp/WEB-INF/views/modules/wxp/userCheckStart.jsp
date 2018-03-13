<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>实名认证 -- 锡职快递服务平台</title>
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
		.content{
			overflow: hidden;
		}
		.userCheckCont{
			width: 100%;
		}
		.userInfoCont{
			overflow: hidden;
			padding: 40px 0px 20px;
			background-image: linear-gradient(to top,#1bb7c3,#f4f4f4);
			box-shadow: 0px 3px 3px #c1c1c1;
		}
		.userInfoCont  .userInfoIcon{
			width: 18%;
			margin: 0px auto 10px;
		}
		.userInfoCont  .nickName{
			font-size: 14px;
			text-align: center;
			font-weight: bold;
			color: #fff;
		}

		.solidCont{
			width: 200%;
			overflow: hidden;
		}
		.checkState{
			width: 50%;
			margin:30px 0px;
			float: left;
		}
		.checkState .stateIcon{
			width: 30px;
			margin: 0 auto;
		}
		.checkState .checkStateTxt{
			text-align: center;
			font-size: 14px;
			padding: 5px 0px;
			color: #333333;
			font-weight: bold;
			margin-bottom: 50px;
		}
		.checkState .checkBtn{
			width: 80%;
			line-height: 40px;
			margin: 5px auto;
			font-weight: bold;
			text-align: center;
			color: #20d6da;
			border: 2px solid #20d6da;
			border-radius: 20px;
		}
		.checkState .checkprompt{
			font-size: 14px;
			text-align: center;
			color: #888888;
			margin: 8px 0px 0px;
		}

		.infoCheckEditCont{
			width: 50%;
			float: left;
			padding-top: 20px;
		}

		.submitBtn,.backBtn{
			width: 90%;
			margin: 0 auto 20px;
			text-align: center;
			line-height: 46px;
			border-radius: 23px;
			color: #20d6da;
			border: 2px solid #20d6da;
			font-weight: bolder;
		}

		#oldPhoneDiv{
			display: none;
		}
		
		.userIdImgUpload{
			width: 90%;
			margin: 0 auto 20px;
			overflow: hidden;
		}
		.userIdImgUpload .userIdImgCont{
			width: 100%;
			overflow: hidden;
			border-radius: 8px;
			border: 1px solid #e1e1e1;
			background: #fff;
		}
		.userIdImgUpload .userIdImgCont img{
			width: 100%;
			display: block;
			margin: 0 auto;
		}
		.userIdImgUpload .userIdImgCont .userIdImgUploadDesc{
			margin: 0px;
			line-height: 30px;
			font-size: 14px;
			color: #999999;
			font-weight: bold;
			text-align: center;
		}

		.exampleImg{
			text-align: center;
			font-size: 14px;
			color: blue;
			margin-bottom: 10px;
			text-decoration: underline;
		}

		.userUploadDesc{
			width: 90%;
			font-size: 14px; 
			color: #999999;
			margin: 0 auto 20px;
		}
		
		.imageCover{
			position: absolute;
			top: 0px;
			left: 0px;
			width: 100%;
			height: 100%;
			background: rgba(0,0,0,0.8);
			display: none;
		}

	</style>
</head>
<body>
	<div class="content">
	<input id="appId" type="hidden" value="${appId}"/>
	<input id="timestamp" type="hidden" value="${timestamp}" />
    <input id="noncestr" type="hidden" value="${nonceStr}" />
    <input id="signature" type="hidden" value="${signature}" />
	<div class="userCheckCont">
		<div class="userInfoCont">
			<div class="userInfoIcon">
				<img src="${ctxStatic}/wx/wximages/userInfoIcon.png" width="100%">
			</div>
			<div class="nickName">用户认证</div>
		</div>
	</div>
	
	<div class="solidCont">
		<div class="checkState">
			<div class="stateIcon">
				<img src="${ctxStatic}/wx/wximages/uncheckIcon.png" width="100%">
			</div>
			<div class="checkStateTxt">未认证</div>
			
			<!-- 按钮组 -->
			<div class="checkBtn userRegBtn">新用户认证</div>
			<div class="checkprompt">认证通过开启信息化校园快递</div>
		</div>

		<div class="infoCheckEditCont" id="newUserReg">
			<form>
				<input id="PageContext" type="hidden" value="${pageContext.request.contextPath}" />
				<input id="wxCode" type="hidden" value="${wxCode}" />
				<div class="userInputCont">
					<div class="inputTypeCont">
						<div class="inputTitle">姓名</div>
						<input type="text" id="name" class="commonInput" name="name" placeholder="请输入你的姓名...">
					</div>
					<div class="inputTypeCont">
						<div class="inputTitle">证件</div>
						<input type="text" id="idCard" class="commonInput" name="idCard" placeholder="请输入身份证号码...">
					</div>
					<div class="inputTypeCont">
						<div class="inputTitle">手机</div>
						<input type="text" id="phone" class="commonInput" name="phone" placeholder="请输入你的手机号码...">
					</div>
					<div class="inputTypeCont">
						<div class="inputTitle">短信</div>
						<input type="text" id="msg" class="verifiInput" name="msg" placeholder="请输入验证码...">
						<input type="button" class="verifiBtn" value="发送验证码">
					</div>
					<div class="inputTypeCont" id="oldPhoneDiv">
						<div class="inputTitle">原手机</div>
						<input type="text" id="oldPhone" class="commonInput" name="oldPhone" placeholder="请输入你绑定的原手机号码...">
					</div>
				</div>
			</form>

			<div class="userIdImgUpload">
				<div class="userIdImgCont" id="userIdImgPositive">
					<img src="uploadfile/defaultimage.jpg" alt="图片加载中...">
					<p class="userIdImgUploadDesc">点击上传手持身份证照片</p>
				</div>
			</div>

			<div class="exampleImg">查看示例图片</div>

			<div class="userUploadDesc">
				请确保上传的个人证明均为有效证件，提交后我们将在3个工作日之内进行审核，并在公众号内通知审核结果，请耐心等待。
			</div>

			<div id = "userRegSubmitBtn" class="submitBtn userRegSubmitBtn">确认提交</div>
			<div class="backBtn">点我返回</div>
		</div>
	</div>

</div>

<script type="text/javascript">
	$(function() {
		var pageContextVal = $("#PageContext").val();
		var wxCodeVal = $("#wxCode").val();
		var windowW = $(window).width();
		var windowH = $(window).height();
		if (windowW > 600) {
			windowW = 600;
		}
		
		
		var topH = $(".userCheckCont").height();
		$(".solidCont").css({"height":(windowH - topH) + "px"});
		var topH = $(".userCheckCont").height();
		$(".userIdImgCont img").attr("src","uploa1dfile/defaulti1mage.jpg");

		// 样例图片展示  501*377
		var imageH = windowW * 377 / 501; 
		$(".coverCont").css({"margin-top":(windowH - imageH)/2 + "px"});
		$(".exampleImg").click(function(){
			$(".imageCover").fadeIn();
		});

		$(".imageCover").click(function(){
			$(".imageCover").fadeOut();
		});

		// 用户上传图片函数
		// JSSDK
		var appId = $("#appId").val();
		var timestamp = $("#timestamp").val();//时间戳
        var nonceStr = $("#noncestr").val();//随机串
        var signature = $("#signature").val();//签名
        wx.config({
            debug : true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
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
	            jsApiList : ['chooseImage','previewImage','uploadImage','downloadImage'],  
	            success : function(res) {  

	            }  
	        });  

	        //扫描二维码  
	        document.querySelector('#userIdImgPositive').onclick = function() {  
	            wx.chooseImage({
					count: 1, // 默认9
					sizeType: ['compressed'], // 可以指定是原图还是压缩图，默认二者都有
					sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
					success: function (res) {
						var localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
						$.ajax({
						    type: 'POST',
						    url: 'upIdCard',
						    data: localIds,
						    success:function(data){
						    	alert("图片上传成功！");  // 需要返回一个图片连接
						    	var imgaddr = data;
						    	$(".userIdImgCont image").attr("src",imgaddr);
						    },
						    error:function(){
						    	
						    }
						    
						});
					}
				});
	        };//end_document_scanQRCode  
	        
	          
	    });//end_ready 
		
		
		
		
		
		$(".solidCont").css({"margin-left":0+"px"});

		$(".userRegBtn").click(function(){
			$(".solidCont").animate({"margin-left":-windowW+"px"},"fast");
		});

		$(".backBtn").click(function(){
			$(".solidCont").animate({"margin-left":0+"px"},"fast");
		});

		var submitFunc = function(){
			console.log("[userCheckStart]----submit btn");
			$(".userRegSubmitBtn").unbind("click");
			// 信息验证

			// 名字
			var name = $.trim($("#name").val());
			if (name.length > 20) {
				rzAlert("操作提示","名字长度超过20");
				$(".userRegSubmitBtn").bind("click",submitFunc);
				return false;
			}
			if (!CheckUserName(name)) {
				rzAlert("操作提示","姓名不能为空！");
				$(".userRegSubmitBtn").bind("click",submitFunc);
				return false;
			}

			// 身份证
			var idCard = $.trim($("#idCard").val());
			if (!CheckUserId(idCard)) {
				rzAlert("操作提示","身份证格式不对！");
				$(".userRegSubmitBtn").bind("click",submitFunc);
				return false;
			}

			// 手机号码
			var phone = $.trim($("#phone").val());
			if (!CheckPhoneNum(phone)) {
				rzAlert("操作提示","手机号码格式不对！");
				$(".userRegSubmitBtn").bind("click",submitFunc);
				return false;
			}

			
			var msg = $("#msg").val();
			var oldPhone = $("#oldPhone").val();
			$.ajax({
			    type:'POST',
			    url:pageContextVal+'/ul/savePersonUserInfo',
			    data:{'name':name,'idCard':idCard,'phone':phone,'msg':msg,'oldPhone':oldPhone,'wxCode':wxCodeVal},
			    dataType: "json",
			    success:function(data){
			    	var prompt = "操作提示";
			    	var code = data.code;
			    	var message = data.message;
			    	if(code == "0"){
			    		rzAlert(prompt,message);
			    		window.location.href= pageContextVal+"/ul/reqPersonIndex";
			    	}else if(code == "10"){
						rzAlert(prompt,message);
						$("#oldPhoneDiv").show();
			    	}else{
			    		rzAlert(prompt,message);
			    	}
			    	
			    	$(".userRegSubmitBtn").bind("click",submitFunc);
		    	},
			    error:function(){
			    	$(".userRegSubmitBtn").bind("click",submitFunc);
			    }
			});
		};
		
		$(".userRegSubmitBtn").bind("click",submitFunc);

		$(".verifiBtn").click(function(){
			// 信息验证

			// 手机号码
			var phone = $.trim($("#phone").val());
			if (!CheckPhoneNum(phone)) {
				rzAlert("操作提示","手机号码格式不对！");
				return false;
			}

			$.ajax({
			    type:'POST',
			    url:pageContextVal+'/wx/sendWxPhoneMsgCode',
			    data:{'phone':phone},
			    dataType: "json",
			    success:function(data){
			    	//var result = JSON.parse(data);
			    	if(data.code == "0"){
			    		rzAlert("操作提示",data.message);
			    		$(".verifiBtn").attr('disabled', 'disabled');
			    		$(".verifiBtn").css({"background":"#888888"});
						var countNum = 60;
						$(".verifiBtn").val("重发短信("+countNum+")");
			    		var timer = setInterval(function(){
							--countNum;
							if (countNum == 0) {
								$(".verifiBtn").attr('disabled', false);
								$(".verifiBtn").css({"background":"#1f72ff"});
								$(".verifiBtn").val("发送验证码");
								window.clearInterval(timer);
							} else {
								$(".verifiBtn").val("重发短信("+countNum+")");
							}
						},1000);
			    	}else if(data.code == "1"){
			    		rzAlert("操作提示",data.message);
			    	}else if(data.code == "2"){
			    		rzAlert("操作提示",data.message);
			    	}
			    },
			    error:function(){
			      
			    }
			});
			
		})
	});
</script>
</body>
</html>