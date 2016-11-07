define(["./document_edit","../common/confirm"], function (DocumentEdit,Confirm) {

        function Document(options) {
            this.init(options);
        }

        Document.prototype = {
            init: function (options) {
                this.options = options;
                this.document_edit = new DocumentEdit({host: this.options.host});
                this.confirm = new Confirm({host: this.options.host});
            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_document_list").show();
                // 初始化UI组件
                App.initUniform();
                this.initEvents();
                this.refreshTable();
            },
            initEvents: function () {
                var that = this;
                // 搜索
                $("#btn_document_search").off("click.search").on("click.search", function () {
                    that.initTable();
                });
                // 新增
                $("#btn_document_new").off("click.search").on("click.search", function () {
                    that.opEdit({
                        id: null
                    });
                });
                // 修改
                $("#table_document_list tbody").off("click.op_document_edit").on("click.op_document_edit", "a[data-sign=op_document_edit]", function () {
                    var $op_document = $(this).closest("span[data-sign=op_document]");
                    that.opEdit({
                        id: $op_document.data("id")
                    });
                });

                // 删除
                $("#table_document_list tbody").off("click.op_document_delete").on("click.op_document_delete", "a[data-sign=op_document_delete]", function () {
                    var $op_document = $(this).closest("span[data-sign=op_document]");
                    that.opDelete({
                        id: $op_document.data("id"),
                    });
                });
                // 停用
                $("#table_document_list tbody").off("click.op_document_verify_down").on("click.op_document_verify_down", "a[data-sign=op_document_verify_down]", function () {
                    var $op_document = $(this).closest("span[data-sign=op_document]");
                    that.opEffect({
                        id: $op_document.data("id"),
                        op: "0"
                    });
                });
                // 启用
                $("#table_document_list tbody").off("click.op_document_verify_up").on("click.op_document_verify_up", "a[data-sign=op_document_verify_up]", function () {
                    var $op_document = $(this).closest("span[data-sign=op_document]");
                    that.opEffect({
                        id: $op_document.data("id"),
                        op: "1"
                    });
                });
            },
            getQueryParams: function () {
                var search_version = $("#form_document_search input[name=search_version]").val();
                var search_status = $("#form_document_search select[name=search_status]").val();
                return [
                    {name: "search_version", value: search_version},
                    {name: "search_status", value: search_status},
                ];
            },
            initTable: function () {
                var that = this;
                this.dataTable = $("#table_document_list").dataTable({
                        iDisplayLength: 10,
                        bProcessing: true,
                        bServerSide: true,
                        bSort: false,
                        bFilter: false,
                        bAutoWidth: false,
                        bDestroy: true,
                        sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                        sAjaxSource: this.options.host + "/document/list.json",
                        sServerMethod: "POST",
                        aoColumns: [
                            { sTitle: "ID", mData: "id", bVisible:false},
                            { sTitle: "版本号", mData: "version"},
                            { sTitle: "文案内容", sWidth: "50%", mData: "comment"},
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
                                    var up_down = "op_document_verify_up";
                                    if (obj.aData.status == 1) {
                                        color = "navy";
                                        action = "停用"
                                        up_down = "op_document_verify_down";
                                    }
                                    return _.template($("#temp_op").html(), {
                                        sign: "op_document",
                                        id: obj.aData.id,
                                        name: obj.aData.key,
                                        ops: [
                                            {color: "blue", sign: "op_document_edit", id: "", name: "", btnName: "编辑"},
                                            {color: color, sign: up_down, id: "", name: "", btnName: action},
                                            //{color: "red", sign: "op_document_delete", id: "", name: "", btnName: "删除"}
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
                this.document_edit.setOptions({
                    id: data.id,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.document_edit.show();
            },
            opDelete: function (data) {
                var that = this;
                that.confirm.show("d", function () {
                    $.ajax({
                        url: that.options.host + "/document/delete",
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
                    url: that.options.host + "/document/effect",
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
        return Document;
    }
);