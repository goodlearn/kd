<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>取货管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
	.funcBtn{
		padding:2px 10px;
		margin-left:8px;
		background:#0099cc;
		border-radius:5px;
		color:#fff;
		display:inline-block;
	}
	</style>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		
		function checkBatchEnd(formname) {
			var checkboxAll = $("input[name='sysExpressIds']");
	        var flag = false;
	        for (i = 0; i < checkboxAll.length; i++) {
	            if (checkboxAll[i].checked) {
	                flag = true;
	                break;
	            }
	        }
	        if (!flag) {
	            alert("请选择要取货的快递！");
	            return false;
	        } else {
	            if (confirm("确定要取货吗？")) {
	                formname.submit();
	            }
	        }
	    }
		
		function CheckAll() {
			var checkboxAll = $("input[name='sysExpressIds']");
			if($("#selectAll").is(":checked")){
				for (i = 0; i < checkboxAll.length; i++) {
					checkboxAll[i].checked = true;
		        }
			} else {
				for (j = 0; j < checkboxAll.length; j++) {
					checkboxAll[j].checked = false;
	           }
			}
		
	    }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active">快递列表</li>
	</ul>
	<form:form id="searchForm" modelAttribute="sysExpress" action="${ctx}/sys/sysExpress/endFormList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form" style="padding:10px 0px;">
			<li>
				<c:choose> 
				   <c:when test="${not empty searchValue}"> 
				      <input name="searchValue" type="text" value="${searchValue}" style="width:500px; font-weight:bold;  height:30px; margin:0;padding:0 0 0 5px;text-align:left;">
				   </c:when> 
				   <c:otherwise>  
				   	  <input name="searchValue" type="text" placeholder=
				   	  "身份证号/手机号/快递单号" value="" style="width:500px; font-weight:bold; height:30px; margin:0;padding:0 0 0 5px;text-align:left;">
				   </c:otherwise> 
				</c:choose>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<form:form id="batchForm" name="batchForm" action="${ctx}/sys/sysExpress/endBatchForm" method="post" class="breadcrumb form-search">
		<sys:message content="${message}"/>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th>取货</th>
					<th>快递单号</th>
					<th>手机</th>
					<th>快递状态</th>
					<th>快递公司</th>
					<th>取货码</th>
					<th>创建人</th>
					<th>创建时间</th>
					<th>上次操作人</th>
					<th>上次操作时间</th>
					<shiro:hasPermission name="sys:sysExpress:edit"><th>操作</th></shiro:hasPermission>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${page.list}" var="sysExpress">
				<tr>
					<td><input id="sysExpressIds" name="sysExpressIds" type="checkbox" value="${sysExpress.id}"></td>
					<td><a href="${ctx}/sys/sysExpress/detailForm?id=${sysExpress.id}">
						${sysExpress.expressId}
					</a></td>
					<td>
						${sysExpress.phone}
					</td>
					<td>
						${fns:getDictLabel(sysExpress.state, 'expressState', '0')}
					</td>
					<td>
						${fns:getDictLabel(sysExpress.company,'expressCompany','其它')}
					</td>
					<td>
						${sysExpress.pickUpCodeShow}
					</td>
					<td>
						${sysExpress.createBy.name}
					</td>
					<td>
						<fmt:formatDate value="${sysExpress.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<td>
						${sysExpress.updateBy.name}
					</td>
					<td>
						<fmt:formatDate value="${sysExpress.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<shiro:hasPermission name="sys:sysExpress:edit"><td>
						<a href="${ctx}/sys/sysExpress/endExpress?id=${sysExpress.id}" onclick="return confirmx('确认要取货吗？', this.href)">取货</a>
					</td></shiro:hasPermission>
				</tr>
			</c:forEach>
			</tbody>
		</table>
		<li class="btns">
			<input id="selectAll" name="selectAllBtn" onClick="CheckAll()" type="checkbox"/>全选/反选
            <div class="funcBtn" onClick="checkBatchEnd(batchForm)">批量取货</div>
		</li>
	</form:form>
	<div class="pagination">${page}</div>
</body>
</html>