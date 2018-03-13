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
</head>
<body>
	<form  method="post" id="imgform" enctype="multipart/form-data" action="dy_upload_image.jspx">
          <a href="javascript:;" class="imgstyle">请选择文件
            <input type="file" name="uploadImage" id="uploadImage">
          </a>
       <input type="button" onclick="gosubmit()" id="imgbtn" value="上传" />
	</form>
<script type="text/javascript">
	function gosubmit(){
	    var formdata=new FormData();
	    //formdata.append('name', 'uploadImage');
	    formdata.append('uploadImage',$('#uploadImage').get(0).files[0]);
	    formdata.append('idCard',"1234");
	    $.ajax({
	        url:'upIdCard',
	        type:'post',
	        contentType:false,
	        data:formdata,
	        processData:false,
	        success:function(info){    
	        },
	        error:function(err){
	            console.log(err)
	        }
	    });
	}
</script>
</body>
</html>