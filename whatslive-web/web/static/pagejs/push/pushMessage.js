define(["./pushMessage_edit","../common/confirm"], function (PushMessageEdit,Confirm) {

        function PushMessage(options) {
            this.init(options);
        }

        PushMessage.prototype = {
            init: function (options) {
                this.options = options;
                this.pushMessage_edit = new PushMessageEdit({host: this.options.host});
                this.confirm = new Confirm({host: this.options.host});
            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_pushMessage_list").show();
                // 初始化UI组件
                App.initUniform();
                this.initEvents();
                this.refreshTable();
            },
            initEvents: function () {
                var that = this;
                // 搜索
                $("#btn_pushMessage_search").off("click.search").on("click.search", function () {
                    that.initTable();
                });
                // 新增
                $("#btn_pushMessage_new").off("click.search").on("click.search", function () {
                    that.opEdit({
                        id: null
                    });
                });
                // 修改
                $("#table_pushMessage_list tbody").off("click.op_pushMessage_edit").on("click.op_pushMessage_edit", "a[data-sign=op_pushMessage_edit]", function () {
                    var $op_pushMessage = $(this).closest("span[data-sign=op_pushMessage]");
                    that.opEdit({
                        id: $op_pushMessage.data("id")
                    });
                });
                $("#table_pushMessage_list tbody").off("click.op_pushMessage_send").on("click.op_pushMessage_send", "a[data-sign=op_pushMessage_send]", function () {
                    var $op_pushMessage = $(this).closest("span[data-sign=op_pushMessage]");
                    that.sendMessge({
                        id: $op_pushMessage.data("id"),
                        op: "1"
                    });
                });
            },
            getQueryParams: function () {
                var search_content = $("#form_pushMessage_search input[name=search_content]").val();
                var search_status = $("#form_pushMessage_search select[name=search_status]").val();
                return [
                    {name: "search_content", value: search_content},
                    {name: "search_status", value: search_status},
                ];
            },
            initTable: function () {
                var that = this;
                this.dataTable = $("#table_pushMessage_list").dataTable({
                        iDisplayLength: 20,
                        bProcessing: true,
                        bServerSide: true,
                        bSort: false,
                        bFilter: false,
                        bAutoWidth: false,
                        bDestroy: true,
                        sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                        sAjaxSource: this.options.host + "/pushMessage/list.json",
                        sServerMethod: "POST",
                        aoColumns: [
                            { sTitle: "ID", mData: "id"},
                            { sTitle: "消息内容", mData: "content"},
                            { sTitle: "目标", mData: null,
                                fnRender: function (obj) {
                                    switch (obj.aData.sendType) {
                                        case -1:
                                            return '所有用户';
                                        case 0:
                                            return '普通用户';
                                        case 1:
                                            return '认证用户';
                                        default:
                                            return '';
                                    }
                                }
                            },
                            { sTitle: "创建人", mData: "createUser"},
                            { sTitle: "创建时间", mData: "createTimeStr"},
                            { sTitle: "状态", mData: null,
                                fnRender: function (obj) {
                                    switch (obj.aData.status) {
                                    	case 0:
                                            return '<span class="label">未发送</span>';
                                        case 1:
                                            return '<span class="label label-info">发送中</span>';
                                        case 2:
                                            return '<span class="label label-success">发送成功</span>';
                                        case 3:
                                            return '<span class="label label-important">发送失败</span>';
                                        default:
                                            return '';
                                    }
                                }
                            },
                            {
                                sTitle: "操作", mData: null,
                                fnRender: function (obj) {
                                    var color = "green";
                                    var action = "发送";

                                    if (obj.aData.status == 3) {
                                        color = "yellow";
                                        action = "重发"
                                    }else if (obj.aData.status == 0){
                                        color = "green";
                                        action = "发送";
                                    }
                                    var ops = [
                                        {color: color, sign: "op_pushMessage_send", id: "", name: "", btnName: action}
                                    ]
                                    if(obj.aData.status == 1 || obj.aData.status == 2){
                                        ops = [];
                                    }
                                    return _.template($("#temp_op").html(), {
                                        sign: "op_pushMessage",
                                        id: obj.aData.id,
                                        name: obj.aData.key,
                                        ops: ops
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
                this.pushMessage_edit.setOptions({
                    id: data.id,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.pushMessage_edit.show();
            },
            sendMessge: function (data) {
                //暂时关闭此功能
                var that = this;
                $.ajax({
                    url: that.options.host + "/pushMessage/sendMessge",
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
        return PushMessage;
    }
);