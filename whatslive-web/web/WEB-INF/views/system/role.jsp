<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>角色管理</title>
    <!-- BEGIN PAGE LEVEL STYLES -->
    <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/jquery.dataTables/css/DT_bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/jquery.gritter/jquery.gritter.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/toggle.buttons/bootstrap-toggle-buttons.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/bootstrap.modal/bootstrap-modal.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/jquery.dynatree/skin-vista/ui.dynatree.css" id="skinSheet"/>
    <!-- END PAGE LEVEL STYLES -->
    <!-- BEGIN PAGE LEVEL PLUGINS -->

    <script type="text/javascript" src="${ctx}/static/js/jquery-ui-1.10.1.custom.min.js"></script>
    <script type="text/javascript" src="${ctx}/static/js/jquery.cookie.min.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/jquery.dataTables/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/backdone/underscore-min.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/jquery.gritter/jquery.gritter.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/jquery.dynatree/jquery.dynatree.js"></script>
    <!-- END PAGE LEVEL PLUGINS -->
    <!-- BEGIN PAGE LEVEL SCRIPTS -->
    <script type="text/javascript" src="${ctx}/static/lib/jquery.dataTables/js/DT_bootstrap.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/jquery.validation/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/toggle.buttons/jquery.toggle.buttons.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/uploadify/js/jquery.uploadify.min.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/backdone/underscore-min.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/bootstrap.modal/bootstrap-modal.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/bootstrap.modal/bootstrap-modalmanager.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/require/require.js"></script>
    <script type="text/javascript" src="${ctx}/static/js/app.js"></script>
    <!-- END PAGE LEVEL SCRIPTS -->
    <script type="text/javascript">
        jQuery(function () {

            App.init();

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

            require(['pagejs/system/role'], function (Role) {
                var role = new Role({
                    host: "${ctx}"
                });
                role.show();
            });

        });
    </script>
    <script id="temp_op" type="text/template">
        <span data-sign="{{=sign}}" data-id="{{=id}}" data-name="{{=name}}">
            {{ _.each(ops, function(op){ }}
            <a href="javascript:void(0);" class="btn mini {{=op.color}}" data-sign="{{=op.sign}}" data-id="{{=op.id}}"
               data-name="{{=op.name}}">{{=op.btnName}}</a>
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
        <!-- BEGIN TABLE COMPANY-->
        <div id="content_role_list" data-sign="content" class="span12">
            <div class="portlet box blue">
                <div class="portlet-title">
                    <div class="caption"><i class="icon-reorder"></i>角色管理</div>
                </div>
                <div class="portlet-body">
                    <div class="clearfix">
                        <div class="btn-group">
                            <button id="btn_role_new" class="btn green">
                                新增角色 <i class="icon-plus"></i>
                            </button>
                        </div>
                    </div>
                    <div id="form_role_search" class="form-inline">
                        <input type="text" name="search_roleName" class="span2" placeholder="角色名">
                        <button id="btn_role_search" type="button" class="btn blue">搜索</button>
                    </div>
                    <table id="table_role_list" class="table table-striped table-bordered table-hover">
                        <thead>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <!-- END TABLE COMPANY-->

        <!-- BEGIN PRODUCT PACKAGE -->
        <div id="content_role_edit" data-sign="content" class="span12" style="margin-left: 0px;">
            <div class="portlet box blue">
                <div class="portlet-title">
                    <div class="caption"><i class="icon-list-alt"></i>角色详细信息</div>
                </div>
                <div id="div_role_edit" class="portlet-body form" >
                </div>
            </div>
        </div>

        <div id="content_role_authorize" data-sign="content" class="span12" style="margin-left: 0px;">
            <form id="form_role_authorize" action="#" class="form-horizontal">
            <div class="portlet box blue">
                <div class="portlet-title">
                    <div class="caption"><i class="icon-list-alt"></i>角色授权页面</div>
                </div>
                <input type="hidden" id="role_authorize_id"/>
                <input type="hidden" id="role_authorize_funcIds"/>
                <div id="div_role_authorize" class="portlet-body form" >
                </div>
            </div>
            <div class="form-actions">
                <button id="btn_role_authorize_save" type="button" class="btn green">保存</button>
                <button id="btn_role_authorize_back" type="button" class="btn">返回</button>
            </div>
           </form>
        </div>
        <!-- END PRODUCT PACKAGE -->

        <!-- BEGIN COMFIRM MODAL -->
        <div id="modal_confirm" class="modal hide fade" tabindex="-1" role="dialog"
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
        </div>
        <!-- BEGIN COMFIRM MODAL -->

    </div>
    <!-- END PAGE CONTENT LIST-->

</div>
<!-- END PAGE CONTAINER-->
</body>
</html>