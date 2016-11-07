<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>视频选择</title>
</head>
<body>
<!-- BEGIN PAGE CONTAINER-->
<div class="container-fluid">
    <!-- BEGIN PAGE CONTENT LIST-->
    <div id="contentList" class="row-fluid">

        <!-- BEGIN FORM-->
        <div id="form_album_con">
            <div id="form_video_search" class="form-inline">
                <div class="form-group">
                    <label class="sr-only"></label>
                    <input type="text" name="search_videoName" class="form-control" style="width:120px;" placeholder="ID或标题">
                </div>
                <button id="btn_video_search" type="button" class="btn blue">搜索</button>
            </div>


            <table id="table_album_video" class="table table-striped table-bordered table-hover">
                <thead>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
    </div>
</div>
<!-- END PAGE CONTENT LIST-->
</div>
<!-- END PAGE CONTAINER-->
</body>
</html>