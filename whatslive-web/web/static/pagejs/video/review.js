define(["./cover_edit","../common/confirm"], function (CoverEdit, Confirm) {

        function Review(options) {
            this.init(options);
        }

        Review.prototype = {
            init: function (options) {
                this.options = options;
                this.cover_edit = new CoverEdit({host: this.options.host});
                this.confirm = new Confirm({host: this.options.host});
            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_video_review").show();
                // 初始化UI组件
                App.initUniform();
                this.initEvents();
                this.refreshTable();
            },
            initEvents: function () {
                var that = this;

                // 直播监控
                $("#btn_monitor_live").off("click.monitor_live").on("click.monitor_live", function () {

                    that.opMonitorLive();
                });

                // 录播监控
                $("#btn_monitor_replay").off("click.monitor_replay").on("click.monitor_replay", function () {

                    that.opMonitorReplay();
                });

                // 按优先顺序排序搜索
                $("#btn_video_search_priority").off("click.search").on("click.search", function () {
                    that.initTable("order_priority");
                });
                // 下线
                $("#table_video_list tbody").off("click.opEffect").on("click.op_video_verify_down", "a[data-sign=op_video_verify_down]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opEffect({
                        id: $op_video.data("id"),
                        op: "0"
                    });
                });
                // 上线
                $("#table_video_list tbody").off("click.op_video_verify_up").on("click.op_video_verify_up", "a[data-sign=op_video_verify_up]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opEffect({
                        id: $op_video.data("id"),
                        op: "1"
                    });
                });

                // 取消优先
                $("#table_video_list tbody").off("click.op_video_priority_off").on("click.op_video_priority_off", "a[data-sign=op_video_priority_off]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opPriority({
                        id: $op_video.data("id"),
                        op: "0"
                    });
                });
                // 优先
                $("#table_video_list tbody").off("click.op_video_priority_on").on("click.op_video_priority_on", "a[data-sign=op_video_priority_on]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opPriority({
                        id: $op_video.data("id"),
                        op: "1"
                    });
                });
                // 取消推荐
                $("#table_video_list tbody").off("click.op_video_recommend_off").on("click.op_video_recommend_off", "a[data-sign=op_video_recommend_off]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opRecommend({
                        id: $op_video.data("id"),
                        op: "0"
                    });
                });
                // 推荐
                $("#table_video_list tbody").off("click.op_video_recommend_on").on("click.op_video_recommend_on", "a[data-sign=op_video_recommend_on]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opRecommend({
                        id: $op_video.data("id"),
                        op: "1"
                    });
                });
                // 查看直播或者录播
                $("#table_video_list tbody").off("click.op_video_view").on("click.op_video_view", "a[data-sign=op_video_view]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opView({
                        id: $op_video.data("id"),
                        $a: $(this)
                    });
                });
                // 修改直播或者录播信息
                $("#table_video_list tbody").off("click.op_video_edit").on("click.op_video_edit", "a[data-sign=op_video_edit]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opChageCover({
                        id: $op_video.data("id")
                    });
                });
            },
            getQueryParams: function () {
                var search_videoStatus = $("#form_video_search input[name=search_videoStatus]").val();
                var search_isReview = $("#form_video_search input[name=search_isReview]").val();
                var search_videoTitle = $("#form_video_search input[name=search_videoTitle]").val();
                var search_videoType = $("#form_video_search select[name=search_videoType]").val();
                var search_reportNum = $("#form_video_search select[name=search_reportNum]").val();
                return [
                    {name: "search_videoStatus", value: search_videoStatus},
                    {name: "search_videoType", value: search_videoType},
                    {name: "search_videoTitle", value: search_videoTitle},
                    {name: "search_reportNum", value: search_reportNum},
                    {name: "search_isReview", value: search_isReview}
                ];
            },
            initTable: function (orderStr) {
                var that = this;
                that.queryProgramNumber();
                this.dataTable = $("#table_video_list").dataTable({
                        iDisplayLength: 10,
                        bProcessing: true,
                        bServerSide: true,
                        bSort: false,
                        bFilter: false,
                        bAutoWidth: false,
                        bDestroy: true,
                        sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                        sAjaxSource: this.options.host + "/video/list.json",
                        sServerMethod: "POST",
                        aoColumns: [
                            { sTitle: "id", mData: "id"},
                            { sTitle: "userId", mData: "userId", bVisible:false},
                            { sTitle: "视频类型", mData: null,
                                fnRender: function (obj) {
                                    switch (obj.aData.ptype) {
                                        case 1:
                                            return '直播';
                                        case 2:
                                            return '预约';
                                        case 3:
                                            return '录播';
                                        default:
                                            return '';
                                    }
                                }
                            },
                            { sTitle: "封面", mData: null,
                                fnRender: function (obj) {
                                    var picture = obj.aData.picture;
                                    if (picture == undefined || "" == picture) {
                                        return "<a id='a_content_pic' href=''></a>"
                                    }
                                    var pictureList = picture.split(",");
                                    var len = pictureList.length <= 0 ? 0 : pictureList.length - 1;
                                    return "<a id='a_content_pic' href='" + pictureList[len] + "' target='_blank'><img style='width: 30px; height: 30px;' id='img_content_pic' src='" + pictureList[len] + "' alt=''/></a>"
                                }
                            },
                            { sTitle: "标题", mData: "pname"},
                            { sTitle: "开始时间", mData: "startTimeStr"},
                            { sTitle: "举报次数", mData:  "reportNum"},
                            { sTitle: "观看人数", mData: "watchNum"},
                            {
                                sTitle: "操作", mData: null,
                                fnRender: function (obj) {
                                    var color = "blue";
                                    var action = "优先"
                                    var priority_on_off = "op_video_priority_on";
                                    var color_recommend = "red";
                                    var action_recommend = "op_video_recommend_off";
                                    var recommend_on_off = "取消推荐";
                                    var edit_btn_name = "修改直播";
                                    if (obj.aData.priority != null && obj.aData.priority > 0) {
                                        color = "yellow";
                                        action = "取消优先";
                                        priority_on_off = "op_video_priority_off";
                                    }
                                    if (obj.aData.isShow != null && obj.aData.isShow == 0) {
                                        color_recommend = "yellow";
                                        action_recommend = "op_video_recommend_on";
                                        recommend_on_off = "推荐";
                                    }
                                    if(obj.aData.ptype == 3){
                                        edit_btn_name = "修改录播";
                                    }
                                    var ops = [
                                        {color: "yellow", sign: "op_video_edit", id: "", name: "", btnName: edit_btn_name},
                                        {color: color, sign: priority_on_off, id: "", name: "", btnName: action},
                                        {color: "navy", sign: "op_video_verify_down", id: "", name: "", btnName: "下线"},
                                        {color: color_recommend, sign: action_recommend, id: "", name: "", btnName: recommend_on_off},
                                        {color: "green", sign: "op_video_view", id: "", name: "", btnName: "点击观看"},


                                    ];
                                    return _.template($("#temp_op").html(), {
                                        sign: "op_video",
                                        id: obj.aData.id,
                                        name: obj.aData.userId,
                                        ops: ops
                                    });

                                }
                            }
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
            queryProgramNumber: function () {
                var that = this;
                var params = that.getQueryParams();
                $.ajax({
                    url: that.options.host + "/video/countProgram",
                    type: "post",
                    data: params,
                    success: function (data) {
                        $("#programNum").html(data.message);
                    }
                });
            },
            refreshTable: function () {
                if (this.dataTable) {
                    this.queryProgramNumber();
                    this.dataTable.fnDraw();
                } else {
                    this.initTable("order_priority");
                }
            },
            opEffect: function (data) {
                var that = this;
                that.confirm.show("d", function () {
                    $.ajax({
                        url: that.options.host + "/video/effect",
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
                });
            },
            opPriority: function (data) {
                var that = this;
                $.ajax({
                    url: that.options.host + "/video/priority",
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
            },
            opRecommend: function (data) {
                var that = this;
                $.ajax({
                    url: that.options.host + "/video/recommend",
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
            },
            opView: function (opts) {
                var that = this;
                $.ajax({
                    url: that.options.host + "/video/view",
                    type: "post",
                    data: {id: opts.id},
                    success: function (data) {
                        if (data.rsCode == "A00000") {
                            var href = data.url;
                            window.open(href, "_blank");
                        } else {
                            $.gritter.add({title: "提示信息：", text: data.rsMsg, time: 2000});
                        }
                    }
                });
            },
            opMonitorLive: function () {

                var that = this;
                var href = that.options.host + "/video/monitor?videoType=1&startNo=0&pageNo=1";
                window.open(href, "_blank");
            },
            opMonitorReplay: function () {

                var that = this;
                var href = that.options.host + "/video/monitor?videoType=3&startNo=0&pageNo=1";
                window.open(href, "_blank");
            },
            opChageCover: function(data){
                var that = this;
                this.cover_edit.setOptions({
                    id: data.id,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.cover_edit.show();
            }
        };
        return Review;
    }
);