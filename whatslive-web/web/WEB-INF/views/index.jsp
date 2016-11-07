<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<head>
    <title>whatslive后台管理系统</title>
    <!-- BEGIN PAGE LEVEL STYLES -->
    <link href="${ctx }/static/lib/jquery.gritter/jquery.gritter.css" rel="stylesheet" type="text/css"/>
    <!-- END PAGE LEVEL STYLES -->
    <!-- BEGIN PAGE LEVEL PLUGINS -->
    <script src="${ctx }/static/lib/jquery.gritter/jquery.gritter.js" type="text/javascript"></script>
    <!-- END PAGE LEVEL PLUGINS -->
    <!-- BEGIN PAGE LEVEL SCRIPTS -->
    <script src="${ctx }/static/js/app.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/index.js" type="text/javascript"></script>
    <!-- END PAGE LEVEL SCRIPTS -->
    <script>
        jQuery(document).ready(function () {
            var herf = $("span").filter(".selected").parent().attr("href");
            location.href = herf;
            App.init(); // initlayout and dao plugins
            Index.init();
            Index.initDashboardDaterange();
        });
    </script>
</head>
<body>
<%--
<div class="container-fluid">
<!-- BEGIN PAGE HEADER-->
<div class="row-fluid">
    <div class="span12">
        <!-- BEGIN PAGE TITLE & BREADCRUMB-->
        <h3 class="page-title">
            内容概括
            <small>欢迎登录乐视终端公共服务平台</small>
        </h3>
        <ul class="breadcrumb">
            <li>
                <i class="icon-home"></i>
                <a href="${ctx }/">首页</a>
                <i class="icon-angle-right"></i>
            </li>
            <li><a href="#">内容概括</a></li>
        </ul>
        <!-- END PAGE TITLE & BREADCRUMB-->
    </div>
</div>
<!-- END PAGE HEADER-->
<div id="dashboard">
<!-- BEGIN DASHBOARD STATS -->
<div class="row-fluid">
    <div class="span3 responsive" data-tablet="span6" data-desktop="span3">
        <div class="dashboard-stat blue">
            <div class="visual">
                <i class="icon-comments"></i>
            </div>
            <div class="details">
                <div class="number">
                    1349
                </div>
                <div class="desc">
                    待审核CP专辑
                </div>
            </div>
            <a class="more" href="#">
                查看更多 <i class="m-icon-swapright m-icon-white"></i>
            </a>
        </div>
    </div>
    <div class="span3 responsive" data-tablet="span6" data-desktop="span3">
        <div class="dashboard-stat green">
            <div class="visual">
                <i class="icon-shopping-cart"></i>
            </div>
            <div class="details">
                <div class="number">549</div>
                <div class="desc">待编辑视频</div>
            </div>
            <a class="more" href="#">
                查看更多 <i class="m-icon-swapright m-icon-white"></i>
            </a>
        </div>
    </div>
    <div class="span3 responsive" data-tablet="span6  fix-offset" data-desktop="span3">
        <div class="dashboard-stat purple">
            <div class="visual">
                <i class="icon-globe"></i>
            </div>
            <div class="details">
                <div class="number">124</div>
                <div class="desc">待上线专辑</div>
            </div>
            <a class="more" href="#">
                查看更多 <i class="m-icon-swapright m-icon-white"></i>
            </a>
        </div>
    </div>
    <div class="span3 responsive" data-tablet="span6" data-desktop="span3">
        <div class="dashboard-stat yellow">
            <div class="visual">
                <i class="icon-bar-chart"></i>
            </div>
            <div class="details">
                <div class="number">234</div>
                <div class="desc">昨日下线专辑</div>
            </div>
            <a class="more" href="#">
                查看更多 <i class="m-icon-swapright m-icon-white"></i>
            </a>
        </div>
    </div>
</div>
</div>
</div>--%>
</body>
</html>