<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>用户信息修改</title>
</head>
<body>
<!-- BEGIN PAGE CONTAINER-->
<div class="container-fluid">

<div id="div_user_edit">
<!-- BEGIN FORM-->
<form id="form_user" action="#" class="form-horizontal">
<input type="hidden" name="userId" value="${user.userId}">
    <input type="hidden" name="userName" value="${user.userName}">
<input type="hidden" readonly ="true" name="picture" class="span6 m-wrap" value="${user.picture}"/>

<div class="control-group">
    <label class="control-label">昵称</label>
    <div class="controls">
        <input type="text" readonly="true" name="nickName" class="span6 m-wrap" value="${user.nickName}"/>
    </div>
</div>

<div class="control-group">
    <label class="control-label">登录方式</label>
    <div class="controls">
        <select name="userType" class="span6 m-wrap" disabled="true">
            <option value="1" <c:if test="${user.userType == 1}">selected="selected" </c:if>>facebook</option>
            <option value="2" <c:if test="${user.userType == 2}">selected="selected" </c:if>>twitter</option>
            <option value="3" <c:if test="${user.userType == 3}">selected="selected" </c:if>>system</option>
            <option value="4" <c:if test="${user.userType == 4}">selected="selected" </c:if>>微博</option>
            <option value="5" <c:if test="${user.userType == 5}">selected="selected" </c:if>>QQ</option>
            <option value="6" <c:if test="${user.userType == 6}">selected="selected" </c:if>>微信</option>
        </select>
    </div>
</div>
<div class="control-group">
    <label class="control-label">自我介绍</label>
    <div class="controls">
        <div class="text-toggle-button" data-name="silentDownload">
            <textarea name="introduce" class="span6 m-wrap" rows="3">${user.introduce}</textarea>
        </div>
    </div>
</div>
<div class="control-group">
    <label class="control-label">头像图</label>

    <div class="controls">
        <div class="fileupload">
            <div class="fileupload-new thumbnail"
                 style="width: 200px; height: 150px;max-width: 200px; max-height: 150px; line-height: 20px;">
                <c:choose>
                    <c:when test="${fn:length(fn:split(user.picture,','))>0}">
                        <c:choose>
                            <c:when test="${fn:length(fn:split(user.picture,','))>=2}">
                                <c:set var="imgSrc" value="${fn:split(user.picture,',')[1]}"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="imgSrc" value="${user.picture}"/>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
<%--
                    <c:when test="${fn:length(user.picturePath)>0}">
                        <c:set var="imgSrc" value="${fileHost}${user.picturePath}"/>
                    </c:when>
--%>
                    <c:otherwise>
                        <c:set var="imgSrc" value=""/>
                    </c:otherwise>
                </c:choose>
                <a id="a_content_picture" href="${imgSrc}" target="_blank">
                    <img id="img_content_picture" src="${imgSrc}" alt=""/>
                </a>
            </div>
<%--
            <div>
                <input type="file" id="file_content_picture"/>
            </div>
--%>
        </div>
    </div>
</div>

<div class="control-group">
    <label class="control-label">用户类型</label>
    <div class="controls">
        <select name="role" class="span6 m-wrap">
            <option value="0" <c:if test="${user.role == 0}">selected="selected" </c:if>>普通用户</option>
            <option value="1" <c:if test="${user.role == 1}">selected="selected" </c:if>>认证用户</option>
        </select>
    </div>
</div>

<div class="control-group">
    <label class="control-label">用户等级</label>
    <div class="controls">
        <select name="level" class="span6 m-wrap">
            <option value="0" <c:if test="${user.level == '' or user.level == 0}">selected="selected" </c:if>>0</option>
            <option value="1" <c:if test="${user.level == 1}">selected="selected" </c:if>>1</option>
            <option value="2" <c:if test="${user.level == 2}">selected="selected" </c:if>>2</option>
            <option value="3" <c:if test="${user.level == 3}">selected="selected" </c:if>>3</option>
            <option value="4" <c:if test="${user.level == 4}">selected="selected" </c:if>>4</option>
            <option value="5" <c:if test="${user.level == 5}">selected="selected" </c:if>>5</option>
        </select>
    </div>
</div>

<div class="control-group">
    <label class="control-label">第一次登录时间</label>
    <div class="controls">
        <input type="text" readonly="true" name="createTimeStr" class="span6 m-wrap" value="${user.createTimeStr}"/>
    </div>

</div>

<div class="control-group">
    <label class="control-label">最后登录时间</label>
    <div class="controls">
        <input type="text" readonly="true" name="lastLoginTimeStr" class="span6 m-wrap" value="${user.lastLoginTimeStr}"/>
    </div>
</div>

<div class="portlet-title">
    <div class="caption"><i class="icon-reorder"></i>视频列表</div>
</div>
<div class="portlet-body">
    <table id="table_video_list" class="table table-striped table-bordered table-hover">
        <thead>
        </thead>
        <tbody>
        </tbody>
    </table>
</div>

<div class="form-actions">
    <button id="btn_user_save" type="button" class="btn green">保存</button>
    <button id="btn_user_back" type="button" class="btn blue">返回</button>
</div>

</form>
<!-- END FORM-->
</div>

</div>
</body>
</html>