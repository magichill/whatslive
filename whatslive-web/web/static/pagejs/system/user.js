/**
 * Created by zhuminghua on 14-4-23.
 */
define(["./user_new","./user_edit"], function (UserNew,UserEdit) {

        function User(options) {
            this.init(options);
        }

        User.prototype = {
            init: function (options) {
                this.options = options;
                this.user_new = new UserNew({host: this.options.host});
                this.user_edit = new UserEdit({host: this.options.host});
            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_user_list").show();
                this.initEvents();
                this.refreshTable();
            },
            initEvents: function () {
                var that = this;
                // 搜索
                $("#btn_user_search").off("click.search").on("click.search", function () {
                    that.initTable();
                });
                // 新增
                $("#btn_user_new").off("click.user_new").on("click.user_new", function () {
                    that.opNew();
                });
                $("#table_user_list tbody").off("click.op_user_edit").on("click.op_user_edit", "a[data-sign=op_user_edit]", function () {
                    var $op_user= $(this).closest("span[data-sign=op_user]");
                    that.opEdit({
                        id: $op_user.data("id")
                    });
                });

            },
            getQueryParams: function () {
                var search_userName = $("#form_user_search input[name=search_userName]").val();
                return [
                    {name: "search_userName", value: search_userName}
                ];
            },
            initTable: function () {
                var that = this;
                this.dataTable = $("#table_user_list").dataTable({
                    iDisplayLength: 20,
                    bProcessing: true,
                    bServerSide: true,
                    bSort: false,
                    bFilter: false,
                    bAutoWidth: false,
                    bDestroy: true,
                    sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                    sAjaxSource: this.options.host + "/system/user/list.json",
                    sServerMethod: "POST",
                    aoColumns: [
                        { sTitle: "ID", mData: "id"},
                        { sTitle: "登录名", mData: "loginName"},
                        { sTitle: "用户名", mData: "userName"},
                        { sTitle: "状态", mData: null,
                            fnRender: function (obj) {
                                return obj.aData.isEffective == 1 ? '<span class="label label-info">有效</span>' : '<span class="label label-important">无效</span>';
                            }
                        },
                        { sTitle: "操作", mData: null,
                            fnRender: function (obj) {
                                return _.template($("#temp_op").html(), {
                                    sign: "op_user",
                                    id: obj.aData.id,
                                    name: obj.aData.loginName,
                                    ops: [
                                        {color: "blue", sign: "op_user_edit", id: "", name: "", btnName: "修改"}
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
                this.user_new.setOptions({
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.user_new.show();
            },
            opEdit: function (data) {
                var that = this;
                this.user_edit.setOptions({
                    id: data.id,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.user_edit.show();
            }
        };

        return User;
    });