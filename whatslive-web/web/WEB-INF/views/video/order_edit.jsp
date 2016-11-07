<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>预约修改</title>
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
                <label class="control-label">标签</label>
                <div class="controls">
                    <select name="tagId" id="tagId" class="span6 m-wrap">
                        <c:forEach var="tag" items="${tagList}">
                            <c:choose>
                                <c:when test="${tag.selected=='selected'}">
                                    <option value="${tag.id}" selected="selected">${tag.value}</option>
                                </c:when>
                                <c:otherwise>
                                    <option value="${tag.id}" >${tag.value}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
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