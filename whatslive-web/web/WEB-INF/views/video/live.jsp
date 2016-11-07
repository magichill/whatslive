<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
  <title>当前直播</title>
  <!-- BEGIN GLOBAL MANDATORY STYLES -->
  <link href="${ctx}/static/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
  <link href="${ctx}/static/css/bootstrap-responsive.min.css" rel="stylesheet" type="text/css"/>
  <link href="${ctx}/static/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
  <link href="${ctx}/static/css/style-metro.css" rel="stylesheet" type="text/css"/>
  <link href="${ctx}/static/css/style.css" rel="stylesheet" type="text/css"/>
  <link href="${ctx}/static/css/style-responsive.css" rel="stylesheet" type="text/css"/>
  <link href="${ctx}/static/css/default.css" rel="stylesheet" type="text/css" id="style_color"/>
  <link href="${ctx}/static/css/uniform.default.css" rel="stylesheet" type="text/css"/>
  <link href="${ctx}/static/css/halflings.css" rel="stylesheet" type="text/css"/>
  <!-- END GLOBAL MANDATORY STYLES -->
  <!-- BEGIN PAGE LEVEL STYLES -->
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/bootstrap-fileupload.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/chosen.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/select2_metro.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/jquery.tagsinput.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/clockface.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/bootstrap-wysihtml5.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/datepicker.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/timepicker.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/colorpicker.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/toggle.buttons/bootstrap-toggle-buttons.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/daterangepicker.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/datetimepicker.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/multi-select-metro.css"/>
  <!-- END PAGE LEVEL STYLES -->
  <link rel="shortcut icon" href="${ctx}/static/image/favicon.ico"/>

  <!-- BEGIN PAGE LEVEL STYLES -->
  <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/jquery.dataTables/css/DT_bootstrap.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/jquery.gritter/jquery.gritter.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/bootstrap.modal/bootstrap-modal.css"/>

  <!-- END PAGE LEVEL STYLES -->
  <!-- BEGIN PAGE LEVEL PLUGINS -->
  <script type="text/javascript" src="${ctx}/static/lib/backdone/underscore-min.js"></script>
  <script type="text/javascript" src="${ctx}/static/lib/jquery.gritter/jquery.gritter.js"></script>
  <script type="text/javascript" src="${ctx}/static/lib/jquery.dataTables/js/jquery.dataTables.min.js"></script>
  <script type="text/javascript" src="${ctx}/static/lib/jquery.dataTables/js/DT_bootstrap.js"></script>
  <script type="text/javascript" src="${ctx}/static/lib/jquery.validation/jquery.validate.min.js"></script>
  <script type="text/javascript" src="${ctx}/static/lib/bootstrap.modal/bootstrap-modal.js"></script>
  <script type="text/javascript" src="${ctx}/static/lib/bootstrap.modal/bootstrap-modalmanager.js"></script>
  <script type="text/javascript" src="${ctx}/static/lib/require/require.js"></script>

  <script type="text/javascript" src="${ctx}/static/js/bootstrap-datepicker.js"></script>
  <script type="text/javascript" src="${ctx}/static/js/bootstrap-datetimepicker.js"></script>
  <script type="text/javascript" src="${ctx}/static/lib/toggle.buttons/jquery.toggle.buttons.js"></script>
  <script type="text/javascript" src="${ctx}/static/js/clockface.js"></script>
  <script type="text/javascript" src="${ctx}/static/js/date.js"></script>
  <script type="text/javascript" src="${ctx}/static/js/daterangepicker.js"></script>
  <script type="text/javascript" src="${ctx}/static/js/bootstrap-colorpicker.js"></script>
  <script type="text/javascript" src="${ctx}/static/lib/uploadify/js/jquery.uploadify.min.js"></script>
  <!-- END PAGE LEVEL PLUGINS -->
  <!-- BEGIN PAGE LEVEL SCRIPTS -->
  <script type="text/javascript" src="${ctx}/static/js/app.js"></script>
  <!-- END PAGE LEVEL SCRIPTS -->
  <script type="text/javascript">
    jQuery(function () {

      App.init({ctx: "${ctx}", fileHost: "${fileHost}"});

      // 因jsp冲突，将模板匹配字符改为{{}}
      _.templateSettings = {
        evaluate: /\{\{([\s\S]+?)\}\}/g,
        interpolate: /\{\{=([\s\S]+?)\}\}/g,
        escape: /\{\{-([\s\S]+?)\}\}/g
      };

      require.config({
        baseUrl: "${ctx}/static/",
        paths: {'jquery': 'js/jquery-1.10.1.min'},
        shim: {}
      });

      require(['pagejs/video/live'], function (Live) {
        var live = new Live({
          host: "${ctx}"
        });
        live.show();
      });

    });
  </script>
  <script id="temp_op" type="text/template">
    <span data-sign="{{=sign}}" data-id="{{=id}}" data-name="{{=name}}">
            {{ _.each(ops, function(op){ }}
            <a href="javascript:void(0);" class="btn mini {{=op.color}}" data-sign="{{=op.sign}}" data-id="{{=op.id}}"
               data-name="{{=op.name}}" style="min-width: {{=op.width||50}}px;">{{=op.btnName}}</a>
            {{ }); }}
        </span>
  </script>
</head>
<body>
<!-- BEGIN PAGE CONTAINER-->
<div class="container-fluid">
  <!-- BEGIN PAGE HEADER-->
  <div class="row-fluid">
    <!-- BEGIN PAGE TITLE & BREADCRUMB-->
    <h3 class="page-title">
      <small></small>
    </h3>
    <!-- END PAGE TITLE & BREADCRUMB-->
  </div>
  <!-- END PAGE HEADER-->

  <!-- BEGIN PAGE CONTENT LIST-->
  <div id="contentList" class="row-fluid">

    <!-- BEGIN TABLE Live-->
    <div id="content_live_list" data-sign="content" class="span12">
      <div class="portlet box blue">
        <c:choose>
          <c:when test="${videoStatus==1&&videoType==1}">
            <c:set var="title" value="当前直播"/>
            <c:set var="styleValue" value="display:block;"/>
          </c:when>
          <c:when test="${videoStatus==1&&videoType==3}">
            <c:set var="title" value="已结束"/>
            <c:set var="styleValue" value="display:block;"/>
          </c:when>
          <c:when test="${videoStatus==0}">
            <c:set var="title" value="被下线管理"/>
            <c:set var="styleValue" value="display:none;"/>
          </c:when>
        </c:choose>
            <div class="portlet-title">
              <div class="caption"><i class="icon-reorder"></i>${title}</div>
            </div>
            <div class="portlet-body">
              <label id="programNum"></label>
              <div id="form_live_search" class="form-inline">
                <input type="hidden" name="search_videoStatus" value="${videoStatus}" readonly="true">
                <input type="hidden" name="search_videoType" value="${videoType}" readonly="true">
                <input type="text" name="search_videoTitle" class="span2" placeholder="ID或者标题">
                <button id="btn_video_search_createTime" type="button" class="btn blue">按创建时间排序搜索<i class="icon-search"></i>
                </button>
                <button id="btn_video_search_report" type="button" class="btn blue">按举报次数排序搜索<i class="icon-search"></i>
                </button>
                <button id="btn_video_goto_review" type="button" style="${styleValue}" class="btn green">可视化界面<i class="icon-share-alt"></i>
                </button>
              </div>
              <table id="table_live_list" class="table table-striped table-bordered table-hover">
                <thead>
                </thead>
                <tbody>
                </tbody>
              </table>
            </div>

      </div>
    </div>
    <!-- END TABLE Live-->

    <!-- BEGIN UPDATE/ADD USER -->
    <div id="content_user_edit" data-sign="content" class="span12" style="margin-left: 0px;">
      <div class="portlet box blue">
        <div class="portlet-title">
          <div class="caption"><i class="icon-list-alt"></i>用户信息</div>
        </div>
        <div id="div_user_edit" class="portlet-body form">
        </div>
      </div>
      <!-- END UPDATE/ADD USER-->
    </div>
    <!-- END PAGE CONTENT LIST-->

    <div id="content_video_cover_edit" data-sign="content" class="span12" style="margin-left: 0px;">
      <div class="portlet box blue">
        <div class="portlet-title">
          <div class="caption"><i class="icon-list-alt"></i>修改封面</div>
        </div>
        <div id="div_video_edit" class="portlet-body form">
        </div>
      </div>
    </div>

    <%--<div id="content_video_review" data-sign="content" class="span12" style="margin-left: 0px;">
      <div class="portlet box blue">
        <div class="portlet-title">
          <div class="caption"><i class="icon-list-alt"></i>可视化审核页面</div>
        </div>
        <div id="div_video_review" class="portlet-body form">
        </div>
      </div>
    </div>--%>

  <!-- END PAGE CONTAINER-->
  </div>
</div>
</body>
</html>