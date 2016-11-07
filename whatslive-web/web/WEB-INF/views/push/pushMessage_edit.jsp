<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>推送消息编辑</title>
</head>
<body>
<!-- BEGIN PAGE CONTAINER-->
<div class="container-fluid">

<div id="div_pushMessage_edit">
<!-- BEGIN FORM-->
<form id="form_pushMessage" action="#" class="form-horizontal">
<input type="hidden" name="id" value="${pushMessage.id}">
<input type="hidden" name="status" value="${pushMessage.status}">
<input type="hidden" name="createTime" value="${pushMessage.createTime}">
<input type="hidden" name="createUser" value="${pushMessage.createUser}">
<div class="control-group">
    <label class="control-label">消息内容<span class="required">*</span></label>
    <div class="controls">
        <input type="text" name="content" class="span6 m-wrap" value="${pushMessage.content}"/>
    </div>
</div>
    <div class="control-group">
        <label class="control-label">发送范围</label>
        <div class="controls">
            <select name="sendType" class="span6 m-wrap">
                <option value="-1" <c:if test="${pushMessage.sendType == -1}"> selected="selected" </c:if>>全部用户</option>
                <option value="0" <c:if test="${pushMessage.sendType == 0}"> selected="selected" </c:if>>普通用户</option>
                <option value="1" <c:if test="${pushMessage.sendType == 1}"> selected="selected" </c:if>>认证用户</option>
            </select>
        </div>
    </div>
<div class="form-actions">
    <button id="btn_pushMessage_save" type="button" class="btn green">保存</button>
    <button id="btn_pushMessage_send" type="button" class="btn yellow">发送</button>
    <button id="btn_pushMessage_back" type="button" class="btn blue">返回</button>
</div>

</form>
<!-- END FORM-->
</div>

</div>
</body>
</html>