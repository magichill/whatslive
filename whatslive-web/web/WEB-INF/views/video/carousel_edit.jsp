<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>轮播修改</title>
</head>
<body>
<!-- BEGIN PAGE CONTAINER-->
<div class="container-fluid">

    <div id="div_video_edit">
        <!-- BEGIN FORM-->
        <form id="form_video" action="#" class="form-horizontal">
            <input type="hidden" id="id" name="id" value="${program.id}">
            <input type="hidden" id="status" name="status" value="${program.status}">
            <input type="hidden" id="vPic" name="vPic"/>
            <input type="hidden" id="sPic" name="sPic"/>
            <div class="control-group">
                <label class="control-label">标题<span class="required">*</span></label>
                <div class="controls">
                    <input type="text" name="PName" class="span6 m-wrap" value="${program.PName}"/>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label">liveId</label>
                <div class="controls">
                    <input type="text" name="liveId" class="span6 m-wrap" value="${program.liveId}"/>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label">activityId</label>
                <div class="controls">
                    <input type="text" name="activityId" class="span6 m-wrap" value="${program.activityId}"/>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label">发起用户<span class="required">*</span></label>
                <div class="controls">
                    <select name="userId" id="userId" class="span6 m-wrap" data-placeholder="选择用户...">
                        <option value="${program.userId}">${program.nickName}</option>
                        <c:forEach var="user" items="${userList}">
                            <option value="${user.userId}">${user.nickName}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label">适配操作系统</label>
                <div class="controls">
                    <select name="from" class="span6 m-wrap">
                        <option value="3" <c:if test="${program.from == 3}">selected="selected" </c:if>>3|全部</option>
                        <option value="2" <c:if test="${program.from == 2}">selected="selected" </c:if>>2|Android</option>
                        <option value="1" <c:if test="${program.from == 1}">selected="selected" </c:if>>1|IOS</option>
                    </select>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label">修改封面</label>

                <div class="controls">
                    <div class="fileupload">
                        <div class="fileupload-new thumbnail"
                             style="width: 200px; height: 150px;max-width: 200px; max-height: 150px; line-height: 20px;">
                            <a id="a_content_picture" href="" target="_blank">
                                <img id="img_content_picture" src="" alt=""/>
                            </a>
                        </div>
                        <div>
                            <input type="file" id="file_content_picture"/>
                        </div>
                    </div>
                </div>
            </div>



            <div class="form-actions">
                <button id="btn_video_save" type="button" class="btn green">保存</button>
                <button id="btn_video_back" type="button" class="btn blue">返回</button>
            </div>

        </form>
        <!-- END FORM-->
    </div>

</div>
</body>
</html>