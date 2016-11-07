<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
  <title>活动信息</title>
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
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/datetimepicker.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/select2/select2.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/select2/select2_metro.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/jquery.tagsinput.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/clockface.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/bootstrap-wysihtml5.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/datepicker.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/timepicker.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/colorpicker.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/toggle.buttons/bootstrap-toggle-buttons.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/css/daterangepicker.css"/>
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

      require(['pagejs/activity/activity'], function (Activity) {
        var activity = new Activity({
          host: "${ctx}"
        });
        activity.show();
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
    <div id="content_activity_list" data-sign="content" class="span12">
      <div class="portlet box blue">
            <div class="portlet-title">
              <div class="caption"><i class="icon-reorder"></i>活动维护</div>
            </div>
            <div class="portlet-body">
              <div id="form_activity_search" class="form-inline">
                <button id="btn_activity_new" class="btn green">
                  新增活动 <i class="icon-plus"></i>
                </button>
              </div>
              <table id="table_activity_list" class="table table-striped table-bordered table-hover">
                <thead>
                </thead>
                <tbody>
                </tbody>
              </table>
            </div>

      </div>
    </div>
    <!-- END TABLE Live-->

    <div id="content_activity_edit" data-sign="content" class="span12" style="margin-left: 0px;">
      <div class="portlet box blue">
        <div class="portlet-title">
          <div class="caption"><i class="icon-list-alt"></i>活动维护</div>
        </div>
        <div id="div_activity_edit" class="portlet-body form">
        </div>
      </div>
    </div>

    <div id="contentList_manage" class="row-fluid">
      <!-- BEGIN TABLE COMPANY-->
      <div id="content_video_list" data-sign="content" class="span12">
        <div class="portlet box blue">
          <div class="portlet-title">
            <div class="caption"><i class="icon-reorder"></i>活动视频管理</div>
          </div>
          <div class="portlet-body">
            <div class="clearfix">
              <div class="btn-group">
                <button id="btn_video_list_new" class="btn green">
                  新增直播或录播 <i class="icon-plus"></i>
                </button>
              </div>
              <div class="btn-group">
                <button id="btn_video_list_back" class="btn blue">
                  返回 <i class="icon-arrow-left"></i>
                </button>
              </div>
            </div>
            <div id="form_video_search" class="form-inline">
              <input type="text" name="search_activity_videoTitle" class="span3" placeholder="直播标题">
              <button id="btn_activity_video_search" type="button" class="btn blue">搜索</button>
            </div>
            <table id="table_video_list" class="table table-striped table-bordered table-hover">
              <thead>
              </thead>
              <tbody>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <!-- BEGIN COMFIRM MODAL -->
      <div id="modal_album_con" class="modal hide fade" tabindex="-1" role="dialog"
           aria-labelledby="modal_album_con_title" aria-hidden="true">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
          <h3 id="modal_album_con_title">选择视频</h3>
        </div>
        <div id="modal_video_con_body" class="modal-body">
        </div>
        <div class="modal-footer">
          <button id="btn_album_close" class="btn" data-dismiss="modal" aria-hidden="true">关闭</button>
        </div>
      </div>
      <!-- BEGIN COMFIRM MODAL -->
    </div>

  <!-- END PAGE CONTAINER-->
  </div>
</div>
</body>
</html>