define(["./virtualComment_edit","../common/confirm"], function (VirtualCommentEdit,Confirm) {

        function VirtualComment(options) {
            this.init(options);
        }

        VirtualComment.prototype = {
            init: function (options) {
                this.options = options;
                this.virtualComment_edit = new VirtualCommentEdit({host: this.options.host});
                this.confirm = new Confirm({host: this.options.host});
            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_virtualComment_list").show();
                // 初始化UI组件
                App.initUniform();
                this.initEvents();
                this.refreshTable();
            },
            initEvents: function () {
                var that = this;
                // 搜索
                $("#btn_virtualComment_search").off("click.search").on("click.search", function () {
                    that.initTable();
                });
                // 新增
                $("#btn_virtualComment_new").off("click.search").on("click.search", function () {
                    that.opEdit({
                        id: null
                    });
                });
                // 批量导入评论
                $("#btn_virtualComment_addBatch").off("click.addBatch").on("click.addBatch", function () {
                    that.batchAdd();
                });
                // 修改
                $("#table_virtualComment_list tbody").off("click.op_virtualComment_edit").on("click.op_virtualComment_edit", "a[data-sign=op_virtualComment_edit]", function () {
                    var $op_virtualComment = $(this).closest("span[data-sign=op_virtualComment]");
                    that.opEdit({
                        id: $op_virtualComment.data("id")
                    });
                });

                // 删除
                $("#table_virtualComment_list tbody").off("click.op_virtualComment_delete").on("click.op_virtualComment_delete", "a[data-sign=op_virtualComment_delete]", function () {
                    var $op_virtualComment = $(this).closest("span[data-sign=op_virtualComment]");
                    that.opDelete({
                        id: $op_virtualComment.data("id"),
                    });
                });
                // 停用
                $("#table_virtualComment_list tbody").off("click.op_virtualComment_verify_down").on("click.op_virtualComment_verify_down", "a[data-sign=op_virtualComment_verify_down]", function () {
                    var $op_virtualComment = $(this).closest("span[data-sign=op_virtualComment]");
                    that.opEffect({
                        id: $op_virtualComment.data("id"),
                        op: "0"
                    });
                });
                // 启用
                $("#table_virtualComment_list tbody").off("click.op_virtualComment_verify_up").on("click.op_virtualComment_verify_up", "a[data-sign=op_virtualComment_verify_up]", function () {
                    var $op_virtualComment = $(this).closest("span[data-sign=op_virtualComment]");
                    that.opEffect({
                        id: $op_virtualComment.data("id"),
                        op: "1"
                    });
                });
            },
            getQueryParams: function () {
                var search_content = $("#form_virtualComment_search input[name=search_content]").val();
                var search_status = $("#form_virtualComment_search select[name=search_status]").val();
                return [
                    {name: "search_content", value: search_content},
                    {name: "search_status", value: search_status},
                ];
            },
            initTable: function () {
                var that = this;
                this.dataTable = $("#table_virtualComment_list").dataTable({
                        iDisplayLength: 20,
                        bProcessing: true,
                        bServerSide: true,
                        bSort: false,
                        bFilter: false,
                        bAutoWidth: false,
                        bDestroy: true,
                        sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                        sAjaxSource: this.options.host + "/virtualComment/list.json",
                        sServerMethod: "POST",
                        aoColumns: [
                            { sTitle: "ID", mData: "id"},
                            { sTitle: "评论内容", sWidth: "40%", mData: "content"},
                            { sTitle: "创建人", mData: "createUser"},
                            { sTitle: "创建时间", mData: "createTimeStr"},
                            { sTitle: "状态", mData: null,
                                fnRender: function (obj) {
                                    switch (obj.aData.status) {
                                        case 1:
                                            return '<span class="label label-success">启用</span>';
                                        case 0:
                                            return '<span class="label label-warning">停用</span>';
                                        default:
                                            return '';
                                    }
                                }
                            },
                            {
                                sTitle: "操作", mData: null,
                                fnRender: function (obj) {
                                    var color = "green";
                                    var action = "启用";
                                    var up_down = "op_virtualComment_verify_up";
                                    if (obj.aData.status == 1) {
                                        color = "navy";
                                        action = "停用"
                                        up_down = "op_virtualComment_verify_down";
                                    }
                                    return _.template($("#temp_op").html(), {
                                        sign: "op_virtualComment",
                                        id: obj.aData.id,
                                        name: obj.aData.content,
                                        ops: [
                                            //{color: "blue", sign: "op_virtualComment_edit", id: "", name: "", btnName: "编辑"},
                                            {color: color, sign: up_down, id: "", name: "", btnName: action},
                                            {color: "red", sign: "op_virtualComment_delete", id: "", name: "", btnName: "删除"}
                                        ]
                                    });
                                }
                            }

                        ],
                        fnServerParams: function (aoData) {
                            aoData = $.merge(aoData, that.getQueryParams());
                        },
                        fnDrawCallback: function (oSettings) {
                            App.initUniform();
                        }
                    }
                );
            },
            refreshTable: function () {
                if (this.dataTable) {
                    this.dataTable.fnDraw();
                } else {
                    this.initTable();
                }
            },
            opEdit: function (data) {
                var that = this;
                this.virtualComment_edit.setOptions({
                    id: data.id,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.virtualComment_edit.show();
            },
            batchAdd: function () {
                var that = this;
                $.ajax({
                    url: that.options.host + "/virtualComment/saveBatchComment",
                    type: "post",
                    data: null,
                    success: function (data) {

                    }
                });
            },
            opDelete: function (data) {
                var that = this;
                that.confirm.show("d", function () {
                    $.ajax({
                        url: that.options.host + "/virtualComment/delete",
                        type: "post",
                        data: {id: data.id},
                        success: function (data) {
                            if (data.rsCode == "A00000") {
                                $.gritter.add({title: "提示信息：", text: "操作成功！", time: 1000});
                                that.refreshTable();
                            } else {
                                $.gritter.add({title: "提示信息：", text: data.rsMsg, time: 2000});
                            }
                        }
                    });
                });
            },
            opEffect: function (data) {
                var that = this;
                $.ajax({
                    url: that.options.host + "/virtualComment/effect",
                    type: "post",
                    data: {id: data.id, op: data.op},
                    success: function (data) {
                        if (data.rsCode == "A00000") {
                            $.gritter.add({title: "提示信息：", text: "操作成功！", time: 1000});
                            that.refreshTable();
                        } else {
                            $.gritter.add({title: "提示信息：", text: data.rsMsg, time: 2000});
                        }
                    }
                });
            }
        };
        return VirtualComment;
    }
);