<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
  <title>直播统计</title>
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

      require(['pagejs/video/statistics'], function (Live) {
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
          <div class="portlet-title">
            <div class="caption"><i class="icon-reorder"></i>直播统计</div>
          </div>
          <div class="portlet-body">
            <label id="programNum"></label>
            <div id="form_live_search" class="form-inline">
              <input type="text" name="search_startTime_start" class="span2 m-wrap" readonly="true" placeholder="开始时间">
              <input type="text" name="search_startTime_end" class="span2 m-wrap" readonly="true" placeholder="结束时间">
              <button id="btn_video_search_createTime" type="button" class="btn blue">统计<i class="icon-search"></i>
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

  <!-- END PAGE CONTAINER-->
  </div>
</div>
</body>
</html>