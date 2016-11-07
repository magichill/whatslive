<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>角色编辑界面</title>
</head>
<body>
<!-- BEGIN PAGE CONTAINER-->
<div class="container-fluid">
    <!-- BEGIN PAGE CONTENT LIST-->
    <div id="contentList" class="row-fluid">
        <!-- BEGIN FORM-->
        <form id="form_role" action="#" class="form-horizontal">
            <input type="hidden" name="id" value="${role.id}"/>
            <div class="control-group">
                <label class="control-label">角色名<span class="required">*</span></label>
                <div class="controls">
                    <input type="text" name="roleName" class="span6 m-wrap" value="${role.roleName}"/>
                </div>
            </div>
            <div class="form-actions">
                <button id="btn_role_save" type="button" class="btn green">保存</button>
                <button id="btn_role_back" type="button" class="btn">返回</button>
            </div>
        </form>
        <!-- END FORM-->
    </div>
    <!-- END PAGE CONTENT LIST-->

</div>
<!-- END PAGE CONTAINER-->
</body>
</html>