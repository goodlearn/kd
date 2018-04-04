<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>微信用户表管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/sysWxUser/">微信用户表列表</a></li>
		<shiro:hasPermission name="sys:sysWxUser:edit"><li><a href="${ctx}/sys/sysWxUser/form">微信用户表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="sysWxUser" action="${ctx}/sys/sysWxUser/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>身份证号：</label>
				<form:input path="idCard" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>姓名：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>手机：</label>
				<form:input path="phone" htmlEscape="false" maxlength="200" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>身份证号</th>
				<th>姓名</th>
				<th>手机</th>
				<th>更新时间</th>
				<th>备注信息</th>
				<shiro:hasPermission name="sys:sysWxUser:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="sysWxUser">
			<tr>
				<td><a href="${ctx}/sys/sysWxUser/form?id=${sysWxUser.id}">
					${sysWxUser.idCard}
				</a></td>
				<td>
					${sysWxUser.name}
				</td>
				<td>
					${sysWxUser.phone}
				</td>
				<td>
					<fmt:formatDate value="${sysWxUser.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${sysWxUser.remarks}
				</td>
				<shiro:hasPermission name="sys:sysWxUser:edit"><td>
    				<a href="${ctx}/sys/sysWxUser/form?id=${sysWxUser.id}">修改</a>
					<a href="${ctx}/sys/sysWxUser/delete?id=${sysWxUser.id}" onclick="return confirmx('确认要删除该微信用户表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>