<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>取货码信息管理</title>
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
		<li class="active"><a href="${ctx}/sys/pickUpCode/">取货码信息列表</a></li>
		<shiro:hasPermission name="sys:pickUpCode:edit"><li><a href="${ctx}/sys/pickUpCode/form">取货码信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="pickUpCode" action="${ctx}/sys/pickUpCode/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>快递键：</label>
				<form:input path="codeKey" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>快递值：</label>
				<form:input path="codeValue" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>快递公司：</label>
				<form:select path="companyKey" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('expressCompany')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>快递键</th>
				<th>快递值</th>
				<th>快递公司</th>
				<th>更新时间</th>
				<th>备注信息</th>
				<shiro:hasPermission name="sys:pickUpCode:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="pickUpCode">
			<tr>
				<td><a href="${ctx}/sys/pickUpCode/form?id=${pickUpCode.id}">
					${pickUpCode.codeKey}
				</a></td>
				<td>
					${pickUpCode.codeValue}
				</td>
				<td>
					${fns:getDictLabel(pickUpCode.companyKey, 'expressCompany', '')}
				</td>
				<td>
					<fmt:formatDate value="${pickUpCode.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${pickUpCode.remarks}
				</td>
				<shiro:hasPermission name="sys:pickUpCode:edit"><td>
    				<a href="${ctx}/sys/pickUpCode/form?id=${pickUpCode.id}">修改</a>
					<a href="${ctx}/sys/pickUpCode/delete?id=${pickUpCode.id}" onclick="return confirmx('确认要删除该取货码信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>