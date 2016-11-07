define([], function () {

    function UserDeviceList(options) {
        this.init(options);
    }

    UserDeviceList.prototype = {
        init: function (options) {
            this.options = options;
        },
        setOptions: function (options) {
            this.options = $.extend(this.options, options);
        },
        show: function () {
            $("div[data-sign=content]").hide();
            $("#content_device_list").show();
            this.loadContent();

        },
        loadContent: function () {
            var that = this;
            var url = this.options.host + "/user/realUser/page/userNew";
            var params = {uid: this.options.id};
            $("#div_user_edit").empty();
            $("#div_user_edit").block({message: "Loading..."});
            $("#div_user_edit").load(url + " #form_user", params, function (responseText, textStatus, XMLHttpRequest) {
                $("#div_user_edit").unblock();
                that.loadContentFinish();
            });
        },
        loadContentFinish: function () {
            this.initEvents();
            this.refreshTable();
        },
        initEvents: function () {
            var that = this;
            //处理
            $("#table_device_list tbody").off("click.op_device_view").on("click.op_device_view", "a[data-sign=op_device_view]", function () {
                var $op_device = $(this).closest("span[data-sign=op_device]");
                that.opEdit({
                    id: $op_device.data("id")
                });
            });
            // 返回
            $("#btn_device_list_back").off("click").on("click", function () {
                that.btnBack();
            });
        },
        getQueryParams: function () {
            return [
                {name: "uid", value: this.options.id}
            ];
        },
        initTable: function () {
            var that = this;
            this.dataTable = $("#table_device_list").dataTable({
                    iDisplayLength: 20,
                    bProcessing: true,
                    bServerSide: true,
                    bSort: false,
                    bFilter: false,
                    bAutoWidth: false,
                    bDestroy: true,
                    sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                    sAjaxSource: this.options.host + "/user/realUser/deviceList",
                    sServerMethod: "POST",
                    aoColumns: [
                        { sTitle: "id", mData: "id"},

                        { sTitle: "设备类型", mData: null,
                            fnRender: function (obj) {
                                switch (obj.aData.platformId) {
                                    case 1:
                                        return 'IOS';
                                    case 2:
                                        return 'Android';
                                    default:
                                        return '';
                                }
                            }
                        },
                        { sTitle: "设备型号", mData: "devModel"},
                        { sTitle: "操作系统", mData: "osVersion"},
                        { sTitle: "应用版本", mData: "editionId"},
                        { sTitle: "渠道", mData: "corporationId"},
                        { sTitle: "channelId", mData: "channelId"}
                    ],
                    fnServerParams: function (aoData) {
                        aoData = $.merge(aoData, that.getQueryParams());
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
        showDiv: function () {
            $("div[data-sign=content]").hide();
            $("#content_device_list").show();
        },
        btnBack: function () {
            $("div[data-sign=content]").hide();
            $("#content_user_list").show();
            if ($.isFunction(this.options.callback_btnBack)) {
                this.options.callback_btnBack();
            }
        }
    };
    return UserDeviceList;
});