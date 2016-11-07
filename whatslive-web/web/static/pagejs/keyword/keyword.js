define(["./keyword_edit","../common/confirm"], function (KeywordEdit,Confirm) {

        function Keyword(options) {
            this.init(options);
        }

        Keyword.prototype = {
            init: function (options) {
                this.options = options;
                this.keyword_edit = new KeywordEdit({host: this.options.host});
                this.confirm = new Confirm({host: this.options.host});
            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_keyword_list").show();
                // 初始化UI组件
                App.initUniform();
                this.initEvents();
                this.refreshTable();
            },
            initEvents: function () {
                var that = this;
                // 搜索
                $("#btn_keyword_search").off("click.search").on("click.search", function () {
                    that.initTable();
                });
                // 新增
                $("#btn_keyword_new").off("click.search").on("click.search", function () {
                    that.opEdit({
                        id: null
                    });
                });
                // 修改
                $("#table_keyword_list tbody").off("click.op_keyword_edit").on("click.op_keyword_edit", "a[data-sign=op_keyword_edit]", function () {
                    var $op_keyword = $(this).closest("span[data-sign=op_keyword]");
                    that.opEdit({
                        id: $op_keyword.data("id")
                    });
                });

                // 删除
                $("#table_keyword_list tbody").off("click.op_keyword_delete").on("click.op_keyword_delete", "a[data-sign=op_keyword_delete]", function () {
                    var $op_keyword = $(this).closest("span[data-sign=op_keyword]");
                    that.opDelete({
                        id: $op_keyword.data("id"),
                    });
                });
                // 停用
                $("#table_keyword_list tbody").off("click.op_keyword_verify_down").on("click.op_keyword_verify_down", "a[data-sign=op_keyword_verify_down]", function () {
                    var $op_keyword = $(this).closest("span[data-sign=op_keyword]");
                    that.opEffect({
                        id: $op_keyword.data("id"),
                        op: "0"
                    });
                });
                // 启用
                $("#table_keyword_list tbody").off("click.op_keyword_verify_up").on("click.op_keyword_verify_up", "a[data-sign=op_keyword_verify_up]", function () {
                    var $op_keyword = $(this).closest("span[data-sign=op_keyword]");
                    that.opEffect({
                        id: $op_keyword.data("id"),
                        op: "1"
                    });
                });
            },
            getQueryParams: function () {
                var search_key = $("#form_keyword_search input[name=search_key]").val();
                var search_status = $("#form_keyword_search select[name=search_status]").val();
                return [
                    {name: "search_key", value: search_key},
                    {name: "search_status", value: search_status},
                ];
            },
            initTable: function () {
                var that = this;
                this.dataTable = $("#table_keyword_list").dataTable({
                        iDisplayLength: 20,
                        bProcessing: true,
                        bServerSide: true,
                        bSort: false,
                        bFilter: false,
                        bAutoWidth: false,
                        bDestroy: true,
                        sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                        sAjaxSource: this.options.host + "/keyword/list.json",
                        sServerMethod: "POST",
                        aoColumns: [
                            { sTitle: "ID", mData: "id"},
                            { sTitle: "关键词", mData: "key"},
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
                                    var up_down = "op_keyword_verify_up";
                                    if (obj.aData.status == 1) {
                                        color = "navy";
                                        action = "停用"
                                        up_down = "op_keyword_verify_down";
                                    }
                                    return _.template($("#temp_op").html(), {
                                        sign: "op_keyword",
                                        id: obj.aData.id,
                                        name: obj.aData.key,
                                        ops: [
                                            //{color: "blue", sign: "op_keyword_edit", id: "", name: "", btnName: "编辑"},
                                            {color: color, sign: up_down, id: "", name: "", btnName: action},
                                            {color: "red", sign: "op_keyword_delete", id: "", name: "", btnName: "删除"}
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
                this.keyword_edit.setOptions({
                    id: data.id,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.keyword_edit.show();
            },
            opDelete: function (data) {
                var that = this;
                that.confirm.show("d", function () {
                    $.ajax({
                        url: that.options.host + "/keyword/delete",
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
                    url: that.options.host + "/keyword/effect",
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
        return Keyword;
    }
);