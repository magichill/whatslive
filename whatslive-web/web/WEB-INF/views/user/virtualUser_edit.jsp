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
<input type="hidden" id="vPic" name="vPic"/>
<input type="hidden" id="sPic" name="sPic"/>
<input type="hidden" name="userId" value="${user.userId}">
<input type="hidden" name="createTime" value="${user.createTime}">
<input type="hidden" name="userType" value="${user.userType}">
<input type="hidden" name="role" value="0">
<input type="hidden" readonly ="true" name="picture" class="span6 m-wrap" value="${user.picture}"/>

<div class="control-group">
    <label class="control-label">昵称<span class="required">*</span></label>
    <div class="controls">
        <input type="text" name="nickName" class="span6 m-wrap" value="${user.nickName}"/>
    </div>
</div>

<div class="control-group">
    <label class="control-label">自我介绍<span class="required">*</span></label>
    <div class="controls">
        <div class="text-toggle-button" data-name="silentDownload">
            <textarea name="introduce" class="span6 m-wrap" rows="3" >${user.introduce}</textarea>
        </div>
    </div>
</div>
<div class="control-group">
    <label class="control-label">头像图<span class="required">*</span></label>

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
                    <c:otherwise>
                        <c:set var="imgSrc" value=""/>
                    </c:otherwise>
                </c:choose>
                <a id="a_content_picture" href="${imgSrc}" target="_blank">
                    <img id="img_content_picture" src="${imgSrc}" alt=""/>
                </a>
            </div>
            <div>
                <input type="file" id="file_content_picture"/>
            </div>
        </div>
    </div>
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