<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>关键词编辑</title>
</head>
<body>
<!-- BEGIN PAGE CONTAINER-->
<div class="container-fluid">

<div id="div_keyword_edit">
<!-- BEGIN FORM-->
<form id="form_keyword" action="#" class="form-horizontal">
<input type="hidden" name="id" value="${keyword.id}">
<input type="hidden" name="status" value="${keyword.status}">
<input type="hidden" name="createTime" value="${keyword.createTime}">
<input type="hidden" name="createUser" value="${keyword.createUser}">
<div class="control-group">
    <label class="control-label">关键词<span class="required">*</span></label>
    <div class="controls">
        <input type="text" name="key" class="span6 m-wrap" value="${keyword.key}"/>
    </div>
</div>

<div class="form-actions">
    <button id="btn_keyword_save" type="button" class="btn green">保存</button>
    <button id="btn_keyword_back" type="button" class="btn blue">返回</button>
</div>

</form>
<!-- END FORM-->
</div>

</div>
</body>
</html>