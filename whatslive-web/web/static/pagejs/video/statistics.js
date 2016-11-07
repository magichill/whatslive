define(["../common/confirm"], function (Confirm) {

        function Statistics(options) {
            this.init(options);
        }

        Statistics.prototype = {
            init: function (options) {
                this.options = options;
                this.confirm = new Confirm({host: this.options.host});

            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_live_list").show();
                // 初始化UI组件
                App.initUniform();
                this.initEvents();
                // 初始化日期组件
                this.initDate();
                this.refreshTable();
            },
            initEvents: function () {
                var that = this;
                // 按创建时间排序搜索
                $("#btn_video_search_createTime").off("click.search").on("click.search", function () {
                    that.initTable();
                });
            },
            initDate: function () {
                $(".m-wrap[name=search_startTime_start],[name=search_startTime_end]").datepicker();
            },
            getQueryParams: function () {
                var search_startTime_start = $("#form_live_search input[name=search_startTime_start]").val();
                var search_startTime_end = $("#form_live_search input[name=search_startTime_end]").val();
                return [
                    {name: "search_startTime_start", value: search_startTime_start},
                    {name: "search_startTime_end", value: search_startTime_end},
                ];
            },
            initTable: function (orderStr) {
                var that = this;
                this.dataTable = $("#table_live_list").dataTable({
                        iDisplayLength: 20,
                        bProcessing: true,
                        bServerSide: true,
                        bSort: false,
                        bFilter: false,
                        bAutoWidth: false,
                        bDestroy: true,
                        sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                        sAjaxSource: this.options.host + "/statistics/list.json",
                        sServerMethod: "POST",
                        aoColumns: [
                            { sTitle: "日期", mData: "dateStr"},
                            { sTitle: "直播总数", mData: "liveCount"},
                            { sTitle: "已生成回放", mData: "replayTransedCount"},
                            { sTitle: "未生成回放", mData: "replayNotTransCount"},
                            { sTitle: "不生成回放", mData: "liveTooShortCount"},
                            { sTitle: "被下线", mData: "offlineCount"},
                        ],
                        fnServerParams: function (aoData) {
                            aoData = $.merge(aoData, that.getQueryParams());
                            aoData = $.merge(aoData, [
                                {name: orderStr, value: -1},
                            ]);

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

        };
        return Statistics;
    }

);