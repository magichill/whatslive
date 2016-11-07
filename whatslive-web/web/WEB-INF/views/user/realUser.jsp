<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>真实用户</title>
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

            require(['pagejs/user/realUser'], function (User) {
                var user = new User({
                    host: "${ctx}"
                });
                user.show();
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

        <!-- BEGIN TABLE USER-->
        <div id="content_user_list" data-sign="content" class="span12">
            <div class="portlet box blue">
                <div class="portlet-title">
                    <div class="caption"><i class="icon-reorder"></i>用户管理</div>
                </div>

                <div class="portlet-body">
                    <label id="userNum"></label>
                    <div id="form_user_search" class="form-inline">
                        <input type="text" name="search_nickName" class="span2" placeholder="昵称">
                        <select name="search_userRole" class="span2 m-wrap">
                            <option value="">所有用户</option>
                            <option value="1">认证用户</option>
                        </select>
                        <select name="search_userLevel" class="span2 m-wrap">
                            <option value="">所有等级</option>
                            <option value="0">0</option>
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="4">4</option>
                            <option value="5">5</option>
                        </select>
                        <select name="search_broadCastNum" class="span2 m-wrap">
                            <option value="">是否发起过直播</option>
                            <option value="0">是</option>
                        </select>
                        <button id="btn_user_search" type="button" class="btn blue">搜索<i class="icon-search"></i>
                        </button>
                    </div>
                    <table id="table_user_list" class="table table-striped table-bordered table-hover">
                        <thead>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <!-- END TABLE USER-->

        <!-- BEGIN TABLE COMPANY-->
        <div id="content_device_list" data-sign="content" class="span12" style="margin-left: 0px;">
            <div class="portlet box blue">
                <div class="portlet-title">
                    <div class="caption"><i class="icon-reorder"></i>设备管理</div>
                </div>
                <div class="portlet-body">
                    <div class="clearfix">
                        <div class="btn-group">
                            <button id="btn_device_list_back" class="btn blue">
                                返回 </i>
                            </button>
                        </div>
                    </div>
                    <table id="table_device_list" class="table table-striped table-bordered table-hover">
                        <thead>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <!-- END TABLE COMPANY-->

        <!-- BEGIN device EDIT -->
        <%--<div id="content_device_edit" data-sign="content" class="span12" style="margin-left: 0px;">
            <div class="portlet box blue">
                <div class="portlet-title">
                    <div class="caption"><i class="icon-list-alt"></i>设备详情</div>
                </div>
                <div id="div_device_edit" class="portlet-body form" style="min-height: 600px;">
                </div>
            </div>
        </div>--%>
        <!-- END device EDIT -->

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


            <!-- BEGIN add USER -->
            <%--<div id="content_user_add" data-sign="content" class="span12" style="margin-left: 0px;">
                <div class="portlet box blue">
                    <div class="portlet-title">
                        <div class="caption"><i class="icon-list-alt"></i>新增用户</div>
                    </div>
                    <div id="div_user_add" class="portlet-body form">
                    </div>
                </div>
            </div>--%>
            <!-- END add USER-->

            <!-- BEGIN COMFIRM MODAL -->
            <%--<div id="modal_confirm" class="modal hide fade" tabindex="-1" user="dialog"
                 aria-labelledby="modal_confirm_title" aria-hidden="true">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
                    <h3 id="modal_confirm_title">确认信息</h3>
                </div>
                <div class="modal-body">
                    <p id="modal_confirm_info" style="word-break:break-all"></p>
                </div>
                <div class="modal-footer">
                    <button id="btn_modal_confirm" data-dismiss="modal" class="btn blue">确定</button>
                    <button class="btn" data-dismiss="modal" aria-hidden="true">取消</button>
                </div>
            </div>--%>
            <!-- BEGIN COMFIRM MODAL -->

        </div>
        <!-- END PAGE CONTENT LIST-->

    </div>
    <!-- END PAGE CONTAINER-->
</div>
</body>
</html>