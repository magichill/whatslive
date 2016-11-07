<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>用户编辑界面</title>
</head>
<body>
<!-- BEGIN PAGE CONTAINER-->
<div class="container-fluid">
    <!-- BEGIN PAGE CONTENT LIST-->
    <div id="contentList" class="row-fluid">
        <!-- BEGIN FORM-->
        <form id="form_user" action="#" class="form-horizontal">
            <input type="hidden" name="id" value="${user.id}"/>
            <div class="control-group">
                <label class="control-label">登录名<span class="required">*</span></label>
                <div class="controls">
                    <input type="text" name="loginName" class="span6 m-wrap" value="${user.loginName}"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">用户名<span class="required">*</span></label>
                <div class="controls">
                    <input type="text" name="userName" class="span6 m-wrap" value="${user.userName}"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">密码</label>
                <div class="controls">
                    <input type="password" name="pazzword" class="span6 m-wrap" />
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">角色<span class="required">*</span></label>
                <div class="controls">
                    <c:forEach var="item" items="${roleList}">
                        <label class="checkbox">
                            <input type="checkbox" name="roles" value="${item.id}"/>${item.roleName}
                        </label>
                    </c:forEach>
                    <div id="roles_error"></div>
                </div>
            </div>
            <div class="form-actions">
                <button id="btn_user_save" type="button" class="btn green">保存</button>
                <button id="btn_user_back" type="button" class="btn">返回</button>
            </div>
        </form>
        <!-- END FORM-->
    </div>
    <!-- END PAGE CONTENT LIST-->

</div>
<!-- END PAGE CONTAINER-->
</body>
</html>