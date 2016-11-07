<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>活动维护</title>
</head>
<body>
<!-- BEGIN PAGE CONTAINER-->
<div class="container-fluid">

    <div id="div_video_edit">
        <!-- BEGIN FORM-->
        <form id="form_activity" action="#" class="form-horizontal">
            <input type="hidden" id="id" name="id" value="${activity.id}">
            <input type="hidden" id="status" name="status" value="${activity.status}">
            <input type="hidden" id="priority" name="priority" value="${activity.priority}">
            <input type="hidden" id="vPic" name="vPic"/>
            <input type="hidden" id="sPic" name="sPic"/>
            <div class="control-group">
                <label class="control-label">标题<span class="required">*</span></label>
                <div class="controls">
                    <input type="text" name="title" class="span6 m-wrap" value="${activity.title}"/>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label">标签<span class="required">*</span></label>
                <div class="controls">
                    <input type="text" name="tag" class="span6 m-wrap" value="${activity.tag}"/>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label">活动url</label>
                <div class="controls">
                    <input type="text" name="url" class="span6 m-wrap" value="${activity.url}"/>
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

            <div class="control-group">
                <label class="control-label">介绍</label>
                <div class="controls">
                    <div class="text-toggle-button" data-name="silentDownload">
                        <textarea name="ADesc" class="span6 m-wrap" rows="3">${activity.ADesc}</textarea>
                    </div>
                </div>
            </div>

            <div class="form-actions">
                <button id="btn_activity_save" type="button" class="btn green">保存</button>
                <button id="btn_activity_back" type="button" class="btn blue">返回</button>
            </div>

        </form>
        <!-- END FORM-->
    </div>

</div>
</body>
</html>