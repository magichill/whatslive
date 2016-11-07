/**
 * Created by wangruifeng  on 14-4-23.
 */
define(function () {

        function Log(options) {
            this.init(options);
        }

        Log.prototype = {
            init: function (options) {
                this.options = options;
            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_log_list").show();
                this.initEvents();
                this.refreshTable();
            },
            initEvents: function () {
                var that = this;
                // 搜索
                $("#btn_log_search").off("click.search").on("click.search", function () {
                    that.initTable();
                });
                $("#table_log_list tbody").off("click.op_log_content").on("click.op_log_content", "a[data-sign=op_log_content]", function () {
                    var $op_log= $(this).closest("span[data-sign=op_log]");
                    that.opConetent({
                        id: $op_log.data("id")
                    });
                });
            },
            getQueryParams: function () {
                var opUserName = $("#form_log_search input[name=opUserName]").val();
                var clientId = $("#form_log_search input[name=clientId]").val();
                var opTime = $("#form_log_search input[name=opTime]").val();
                var opType = $("#form_log_search input[name=opType]").val();
                return [
                    {name: "opUserName", value: opUserName},
                    {name: "clientId", value: clientId},
                    {name: "opTime", value: opTime},
                    {name: "opType", value: opType}
                ];
            },
            initTable: function () {
                var that = this;
                this.dataTable = $("#table_log_list").dataTable({
                    iDisplayLength: 20,
                    bProcessing: true,
                    bServerSide: true,
                    bSort: false,
                    bFilter: false,
                    bAutoWidth: false,
                    bDestroy: true,
                    sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                    sAjaxSource: this.options.host + "/system/log/list.json",
                    sServerMethod: "POST",
                    aoColumns: [
                        { sTitle: "ID", mData: "id"},
                        { sTitle: "操作用户", mData: "opUserName"},
                        { sTitle: "客户端地址", mData: "clientIp"},
                        { sTitle: "应用服务器地址", mData: "systemId"},
                        { sTitle: "操作时间", mData: "opTimes"},
                        { sTitle: "操作类型", mData: "opType"},
                        { sTitle: "操作对象ID", mData: "opObjectid"},
                        { sTitle: "操作内容", mData: null,
                            fnRender: function (obj) {
                                return _.template($("#temp_op").html(), {
                                    sign: "op_log",
                                    id: obj.aData.id,
                                    ops: [
                                        {color: "blue", sign: "op_log_content", id: "", name: "", btnName: "显示"}
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
            opConetent: function (data) {
                var url = this.options.host + "/system/log/opContent";
                var params = {id: data.id};
                this.loadContent(url, params);
            },
            loadContent: function (url, params) {
                var that = this;
                $("#modal_confirm_body").empty();
                $("#modal_confirm_body").load(url + " #content_log_list", params, function (responseText, textStatus, XMLHttpRequest) {
                    that.showModal();
                });
            },
            showModal: function () {
                $("#modal_confirm_log").modal({
                    width: 800,
                    maxHeight: 600
                });
            },
            hideModal: function () {
                $("#modal_confirm_log").modal("hide");
            }

        };

        return Log;
    });