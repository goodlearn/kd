<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>微信表管理</title>
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
		<li class="active"><a href="${ctx}/sys/sysWxInfo/">微信表列表</a></li>
		<shiro:hasPermission name="sys:sysWxInfo:edit"><li><a href="${ctx}/sys/sysWxInfo/form">微信表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="sysWxInfo" action="${ctx}/sys/sysWxInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>微信关联号：</label>
				<form:input path="openId" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>身份证号：</label>
				<form:input path="idCard" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>微信关联号</th>
				<th>身份证号</th>
				<th>微信昵称</th>
				<th>头像Url</th>
				<th>快递数量</th>
				<th>备注信息</th>
				<shiro:hasPermission name="sys:sysWxInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="sysWxInfo">
			<tr>
				<td><a href="${ctx}/sys/sysWxInfo/form?id=${sysWxInfo.id}">
					${sysWxInfo.openId}
				</a></td>
				<td>
					${sysWxInfo.idCard}
				</td>
				<td>
					${sysWxInfo.nickname}
				</td>
				<td>
					${sysWxInfo.headimgurl}
				</td>
				<td>
					${sysWxInfo.expressNum}
				</td>
				<td>
					<fmt:formatDate value="${sysWxInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${sysWxInfo.remarks}
				</td>
				<shiro:hasPermission name="sys:sysWxInfo:edit"><td>
    				<a href="${ctx}/sys/sysWxInfo/form?id=${sysWxInfo.id}">修改</a>
					<a href="${ctx}/sys/sysWxInfo/delete?id=${sysWxInfo.id}" onclick="return confirmx('确认要删除该微信表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>