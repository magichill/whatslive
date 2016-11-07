<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>封面修改</title>
</head>
<body>
<!-- BEGIN PAGE CONTAINER-->
<div class="container-fluid">

    <div id="div_video_edit">
        <!-- BEGIN FORM-->
        <form id="form_video" action="#" class="form-horizontal">
            <input type="hidden" name="id" value="${program.id}">
            <input type="hidden" name="pType" value="${program.PType}">
            <input type="hidden" id="vPic" name="vPic"/>
            <input type="hidden" id="sPic" name="sPic"/>
            <input type="hidden" name="picture" value="${program.picture}">
            <div class="control-group">
                <label class="control-label">标题<span class="required">*</span></label>
                <div class="controls">
                    <input type="text" name="PName" class="span6 m-wrap" value="${program.PName}"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">封面</label>

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
            <c:choose>
                <c:when test="${program.PType==3}">
                    <div class="control-group">
                        <label class="control-label">观看人数</label>
                        <div class="controls">
                            <input type="text" name="watchNum" class="span6 m-wrap" value="${program.watchNum}"/>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">点赞数</label>
                        <div class="controls">
                            <input type="text" name="likeNum" class="span6 m-wrap" value="${program.likeNum}"/>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">评论数</label>
                        <div class="controls">
                            <input type="text" name="commentNum" class="span6 m-wrap" value="${program.commentNum}"/>
                        </div>
                    </div>
                </c:when>
            </c:choose>

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