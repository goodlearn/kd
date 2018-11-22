<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>录入快递 -- 锡职快递服务平台</title>
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
			position: relative;
		}
		.expEnterCont{
			padding-bottom: 20px;
		}
		.expEnterCont .expEnterIcon{
			width: 20%;
			margin: 0 auto;
		}
		.expEnterCont .expEnterIcon p{
			text-align: center;
			font-weight: bold;
		}

		.submitBtn{
			width: 90%;
			margin: 0 auto;
			text-align: center;
			line-height: 46px;
			border-radius: 23px;
			background: #1f72ff;
			color: #fff;
			font-weight: bolder;
		}
		
		.stopvoicerecord{
			width: 40%; 
			height: 50px;
			position: absolute;
			top: 20px;
			right: 40px;
			display: none;
			background:purple;
		}
		
		.stopvoicerecord{
			position: absolute;
			top: 0%;
			left: 0%;
			width: 100%;
			height: 100%;
			background-color: rgba(0,0,0,0.5);
			z-index: 1001;
			display: none;
		}
		.stopvoicerecord .voiceRecordCont{
			width: 60%; 
			background: #fff;
			border-radius: 8px;
			position: absolute;
			top: calc((100% - 216px) / 2);  /*30 + 110 +30 + 10 +36 == 216*/
			left: 20%;
			padding: 20px 0px 10px;
		}
		.stopvoicerecord .voiceRecordCont img{
			display: block;
			margin: 0 auto;
			width: 110px;
		}
		.stopvoicerecord .voiceRecordCont .voiceRecordState{
			margin: 0px;
			text-align: center;
			font-size: 14px;
			color: #777777;
			line-height: 30px;
		}
		.stopvoicerecord .voiceRecordCont #stopVoiceRecordBtn{
			margin: 10px auto 0px;
			width: 90%; 
			text-align: center;
			line-height: 36px;
			background: #3eb94e;
			font-size: 14px;
			color: #fff;
			font-weight: bold;
			border-radius: 18px;
		}
	</style>
</head>
<body>
<div class="content">
	<div class="headerNav">
		<div class="headerNavTop"><div class="headerNavIcon headerNavIconOut"><span></span><span></span></div></div>
		<div class="headerNavCont">
			<a href="../index.html">快递首页</a>
			<a href="../userhome.html">个人中心</a>
			<a href="../delivery.html">送货上门</a>
<!-- 			<a href="../sendexpress.html">我要寄件</a>
 -->			<a href="../lazyboard.html">懒人排行</a>
			<a href="../expassistant.html">快递助手</a>
		</div>
	</div>

	<div class="expEnterCont">
		<div class="expEnterIcon">
			<img src="${ctxStatic}/wx/wximages/expentericon.png" width="100%">
			<p>录入快递</p>
		</div>
		${message}
		<div class="expEnterInput">
				<input id="timestamp" type="hidden" value="${timestamp}" />
				<input id="noncestr" type="hidden" value="${nonceStr}" />
				<input id="signature" type="hidden" value="${signature}" />
				<input id="appId" type="hidden" value="${appId}" />
			<form>
				<input id="PageContext" type="hidden" value="${pageContext.request.contextPath}" />
				<input id="wxCode" type="hidden" value="${wxCode}" />
				<div class="userInputCont">
					<div class="inputTypeCont">
						<div class="inputTitle">单号</div>
						<input type="text" id="expressId" class="commonInputFunc" name="expressId" placeholder="请输入快递单号...">
						<div class="commonFuncBtnScan" id="scanQRCodeBtn"></div>
					</div>
					<div class="inputTypeCont">
						<div class="inputTitle">手机</div>
						<input type="text" id="phone" class="commonInputFunc" name="phone" placeholder="请输入收件人号码...">
						<div class="commonFuncBtnVoice" id="voiceRecordBtn"></div>
					</div>
					<div class="inputTypeCont">
						<div class="inputTitle">公司</div>
						<select id="company" name="company">  
                             <c:forEach var="dict" items="${fns:getDictList('expressCompany')}">  
                                <option value="${dict.value}">  
                                ${dict.label}  
                                </option>  
                              </c:forEach>  
                         </select>  
					</div>
				</div>
			</form>
			<div class="submitBtn">录入信息</div>
		</div>
	</div>
	<!-- cover voice record -->
	<div class="stopvoicerecord">
		<div class="voiceRecordCont">
			<img src="${ctxStatic}/wx/wximages/voicerecordicon.png" alt="正在录音中...">
			<p class="voiceRecordState">正在录音中...</p>
			<div id="stopVoiceRecordBtn">完成录音并识别</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	$(function() {
		
		var submitFunc = function(){
			$(".submitBtn").unbind("click");
			//检测快递单号
			var expressId = $.trim($("#expressId").val());
			if (!CheckExpNum(expressId)) {
				rzAlert("操作提示","快递单号格式不正确！");
				$(".submitBtn").bind("click",submitFunc);
				return false;
			}

			// 手机号码
			var phone = $.trim($("#phone").val());
			if (!CheckPhoneNum(phone)) {
				rzAlert("操作提示","手机号码格式不对！");
				$(".submitBtn").bind("click",submitFunc);
				return false;
			}
			
			//$(".msgcover").fadeIn();
			//$(".coverMsgCont").fadeIn();
			var pageContextVal = $("#PageContext").val();
			var company = $("#company").val();
			var wxCode = $("#wxCode").val();
			var pickUpCode = $("#pickUpCode").val();
			$.ajax({
			    type:'POST',
			    url:pageContextVal+'/ul/saveExpress',
			    data:{'expressId':expressId,'phone':phone,'company':company,'pickUpCode':pickUpCode,'code':wxCode},
			    dataType: "json",
			    success:function(data){
			    	var prompt = "操作提示";
			    	var code = data.code;
			    	var message = data.message;
			    	if(code == "0"){
			    		rzAlert(prompt,message);
			    		$("#expressId").val("");
			    		$("#phone").val("");
			    	}else{
			    		rzAlert(prompt,message);
			    	}
			    	$(".submitBtn").bind("click",submitFunc);
		    	},
			    error:function(){
			    	$(".submitBtn").bind("click",submitFunc);  
			    }
			});
		};
		
		$(".submitBtn").bind("click",submitFunc);
		
		var initFun = function(){
			var windowW = $(window).width();
			var windowH = $(window).height();
			$(".content").css({"height": windowH + "px"});
			
		};

		initFun();

		$(window).resize(function(){
			initFun();
		});
		
		// JSSDK
		var timestamp = $("#timestamp").val();//时间戳
        var nonceStr = $("#noncestr").val();//随机串
        var signature = $("#signature").val();//签名
        var appId = $("#appId").val();//签名
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
	            jsApiList : ['scanQRCode'],  
	            success : function(res) {  

	            }  
	        });  

	        //扫描二维码  
	        document.querySelector('#scanQRCodeBtn').onclick = function() {  
	            wx.scanQRCode({  
	                needResult : 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，  
	                scanType : [ "qrCode", "barCode" ], // 可以指定扫二维码还是一维码，默认二者都有  
	                success : function(res) {  
	                    //扫码后获取结果参数赋值给Input
	                    var url = res.resultStr;
	                    var qrCodenum = url.split(",");
	                    $("#expressId").val(qrCodenum[1]);
	                }  
	            });  
	        };//end_document_scanQRCode  

	     // 语音识别
	        document.querySelector('#voiceRecordBtn').onclick = function(){
	        	wx.startRecord();
	        	$(".stopvoicerecord").show();
	        };

	        $("#stopVoiceRecordBtn").click(function(){
	        	wx.stopRecord({
					success: function (res) {
						$(".stopvoicerecord").hide();
						var localId = res.localId;
						wx.translateVoice({
							localId: localId, // 需要识别的音频的本地Id，由录音相关接口获得
							isShowProgressTips: 1, // 默认为1，显示进度提示
							success: function (res) {
								var result = res.translateResult.replace(/[^0-9]/ig,"");
								$("#phone").val(result);
							}
						});
					},
					cancel: function() {
						alert("拒绝了就不能录音了哦！");
					}
				});
	        });
	        
	          
	    });//end_ready 
	})
</script>
</body>
</html>