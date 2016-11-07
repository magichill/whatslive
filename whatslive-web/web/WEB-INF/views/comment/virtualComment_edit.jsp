<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>预加载评论编辑</title>
</head>
<body>
<!-- BEGIN PAGE CONTAINER-->
<div class="container-fluid">

<div id="div_virtualComment_edit">
<!-- BEGIN FORM-->
<form id="form_virtualComment" action="#" class="form-horizontal">
<input type="hidden" name="id" value="${virtualComment.id}">
<input type="hidden" name="status" value="${virtualComment.status}">
<input type="hidden" name="createTime" value="${virtualComment.createTime}">
<input type="hidden" name="createUser" value="${virtualComment.createUser}">
<div class="control-group">
    <label class="control-label">评论内容<span class="required">*</span></label>
    <div class="controls">
        <input type="text" name="content" class="span6 m-wrap" value="${virtualComment.content}"/>
    </div>
</div>

<div class="form-actions">
    <button id="btn_virtualComment_save" type="button" class="btn green">保存</button>
    <button id="btn_virtualComment_back" type="button" class="btn blue">返回</button>
</div>

</form>
<!-- END FORM-->
</div>

</div>
</body>
</html>