<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>信信息检查表管理</title>
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
		<li class="active"><a href="${ctx}/sys/sysWxUserCheck/">信息检查表列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="sysWxUserCheck" action="${ctx}/sys/sysWxUserCheck/" method="post" class="breadcrumb form-search">
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
				<th>激活状态</th>
				<th>更新时间</th>
				<shiro:hasPermission name="sys:sysWxUserCheck:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="sysWxUserCheck">
			<tr>
				<td><a href="${ctx}/sys/sysWxUserCheck/form?id=${sysWxUserCheck.id}">
					${sysWxUserCheck.idCard}
				</a></td>
				<td>
					${sysWxUserCheck.name}
				</td>
				<td>
					${sysWxUserCheck.phone}
				</td>
				<td>
					${fns:getDictLabel(sysWxUserCheck.state, 'userCheckState', '未激活')}
				</td>
				<td>
					<fmt:formatDate value="${sysWxUserCheck.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sys:sysWxUserCheck:edit"><td>
    				<a href="${ctx}/sys/sysWxUserCheck/active?id=${sysWxUserCheck.id}">激活</a>
    				<a href="${ctx}/sys/sysWxUserCheck/refuse?id=${sysWxUserCheck.id}">催促</a>
				</td></shiro:hasPermission>
				<shiro:hasPermission name="sys:sysWxUserCheck:delete"><td>
    				<a href="${ctx}/sys/sysWxUserCheck/delete?id=${sysWxUserCheck.id}" onclick="return confirmx('确认要删除该信信息检查表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>