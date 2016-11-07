/**
 * Created by zhuminghua on 14-4-23.
 */
define(["./role_new"], function (RoleNew) {

    function Role(options) {
        this.init(options);
    }

    Role.prototype = {
        init: function (options) {
            this.options = options;
            this.role_new = new RoleNew({host: this.options.host});
        },
        setOptions: function (options) {
            this.options = $.extend(this.options, options);
        },
        show: function () {
            $("div[data-sign=content]").hide();
            $("#content_role_list").show();
            this.initEvents();
            this.refreshTable();
        },
        initEvents: function () {
            var that = this;
            // 搜索
            $("#btn_role_search").off("click.search").on("click.search", function () {
                that.initTable();
            });
            // 新增
            $("#btn_role_new").off("click.role_new").on("click.role_new", function () {
                that.opNew();
            });

            $("#table_role_list tbody").off("click.op_role_edit").on("click.op_role_edit", "a[data-sign=op_role_edit]", function () {
                var $op_role = $(this).closest("span[data-sign=op_role]");
                that.opEdit({
                    id: $op_role.data("id")
                });
            });
            $("#table_role_list tbody").off("click.op_role_authorize").on("click.op_role_authorize", "a[data-sign=op_role_authorize]", function () {
                var $op_role = $(this).closest("span[data-sign=op_role]");
                that.initTree({
                    id: $op_role.data("id"),
                    type: '1',
                    url: that.options.host
                });
            });
        },
        getQueryParams: function () {
            var search_roleName = $("#form_role_search input[name=search_roleName]").val();
            return [
                {name: "search_roleName", value: search_roleName}
            ];
        },
        initTable: function () {
            var that = this;
            this.dataTable = $("#table_role_list").dataTable({
                iDisplayLength: 20,
                bProcessing: true,
                bServerSide: true,
                bSort: false,
                bFilter: false,
                bAutoWidth: false,
                bDestroy: true,
                sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                sAjaxSource: this.options.host + "/system/role/list.json",
                sServerMethod: "POST",
                aoColumns: [
                    { sTitle: "ID", mData: "id"},
                    { sTitle: "角色名称", mData: "roleName"},
                    { sTitle: "操作", mData: null,
                        fnRender: function (obj) {
                            return _.template($("#temp_op").html(), {
                                sign: "op_role",
                                id: obj.aData.id,
                                name: obj.aData.roleName,
                                ops: [
                                    {color: "blue", sign: "op_role_edit", id: "", name: "", btnName: "修改"},
                                    {color: "blue", sign: "op_role_authorize", id: "", name: "", btnName: "资源授权"}
                                ]
                            });
                        }
                    }
                ],
                fnServerParams: function (aoData) {
                    aoData = $.merge(aoData, that.getQueryParams());
                }
            });
        },
        refreshTable: function () {
            if (this.dataTable) {
                this.dataTable.fnDraw();
            } else {
                this.initTable();
            }
        },
        opNew: function () {
            var that = this;
            this.role_new.setOptions({
                callback_btnSave: function () {
                    that.show();
                }
            });
            this.role_new.show();
        },
        opEdit: function (data) {
            var that = this;
            this.role_new.setOptions({
                id: data.id,
                callback_btnSave: function () {
                    that.show();
                }
            });
            this.role_new.show();
        },

        initTree: function (data) {
            var url = data.url + "/system/role/getTree?id=" + data.id;
            switch (data.type) {
                case '1':
                    url = data.url + "/system/role/getTree?id=" + data.id;
                    break;
                case '2':
                    url = data.url + "/system/role/getProductTree?id=" + data.id;
                    break;
            }

            $("div[data-sign=content]").hide();

            $("#btn_role_authorize_save").attr("disabled", false);
            $("#btn_role_authorize_save").off("click.role_authorize").on("click.role_authorize", function () {
                $("#role_authorize_funcIds").val();
                $("#role_authorize_id").val(data.id);
                that.btnSave_authorize(data.type);
            });

            $("#btn_role_authorize_back").off("click.role_authorize").on("click.role_authorize", function () {
                that.btnBack_authorize();
            });

            var that = this;
            if (this.dynatree) {
                //$("#div_role_authorize").dynatree("getTree").reload();
                $('#div_role_authorize').dynatree('destroy');
            }
            this.dynatree = $("#div_role_authorize").dynatree({
                checkbox: true,
                imagePath: "skin-custom/",
                selectMode: 3,
                minExpandLevel: 3,
                initAjax: {
                    url: url
                },
                onSelect: function (select, node) {
                    // Get a list of all selected nodes, and convert to a key array:
                    var selKeys = $.map(node.tree.getSelectedNodes(), function (node) {
                        return node.data.key;
                    });
                    $("#role_authorize_funcIds").val(selKeys.join(","));
                },
                onDblClick: function (node, event) {
                    node.toggleSelect();
                },
                onKeydown: function (node, event) {
                    if (event.which == 32) {
                        node.toggleSelect();
                        return false;
                    }
                },
                // The following options are only required, if we have more than one tree on one page:
                initId: "treeData",
                cookieId: "dynatree-Cb3",
                idPrefix: "dynatree-Cb3-"
            });
            $("#content_role_authorize").show();
        },
        btnSave_authorize: function (data) {
            var funcIds = $("#role_authorize_funcIds").val();
            var roleId = $("#role_authorize_id").val();
//            if (roleId == '' || funcIds == '')
//                return;

            var that = this;
            $("#btn_role_authorize_save").attr("disabled", true);


            var url = that.options.host + "/system/role/roleSave_authorize";
            switch (data) {
                case '1':
                    url = that.options.host + "/system/role/roleSave_authorize";
                    break;
                case '2':
                    url = that.options.host + "/system/role/roleSave_product";
                    break;
            }
            // 保存请求
            $.ajax({
                url: url,
                type: "post",
                data: {roleId: roleId, funcIds: funcIds},
                success: function (data) {
                    $("#btn_role_save").attr("disabled", false);
                    if (data.rsCode == "A00000") {
                        $.gritter.add({title: "提示信息：", text: "保存成功！", time: 1000});
                        that.btnBack_authorize();
                    } else {
                        $.gritter.add({title: "提示信息：", text: data.rsMsg, time: 2000});
                    }
                }
            });
        },
        btnBack_authorize: function () {
            this.show();
        }
    };

    return Role;
});