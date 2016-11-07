<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>文案编辑</title>
</head>
<body>
<!-- BEGIN PAGE CONTAINER-->
<div class="container-fluid">

<div id="div_document_edit">
<!-- BEGIN FORM-->
<form id="form_document" action="#" class="form-horizontal">
<input type="hidden" name="id" value="${document.id}">
<input type="hidden" name="status" value="${document.status}">
<input type="hidden" name="createTime" value="${document.createTime}">
<input type="hidden" name="createUser" value="${document.createUser}">
<div class="control-group">
    <label class="control-label">版本号<span class="required">*</span></label>
    <div class="controls">
        <input type="text" name="version" class="span6 m-wrap" value="${document.version}"/>
    </div>

</div>

<div class="control-group">
    <label class="control-label">文案内容<span class="required">*</span></label>
    <div class="controls">
        <div class="text-toggle-button" data-name="silentDownload">
            <textarea name="comment" class="span6 m-wrap" rows="8" >${document.comment}</textarea>
        </div>
    </div>
</div>

<div class="form-actions">
    <button id="btn_document_save" type="button" class="btn green">保存</button>
    <button id="btn_document_back" type="button" class="btn blue">返回</button>
</div>

</form>
<!-- END FORM-->
</div>

</div>
</body>
</html>