define(["../user/realUser_edit","./cover_edit","./review","../common/confirm"], function (UserEdit,CoverEdit,Review,Confirm) {

        function Live(options) {
            this.init(options);
        }

        Live.prototype = {
            init: function (options) {
                this.options = options;
                this.user_edit = new UserEdit({host: this.options.host});
                this.cover_edit = new CoverEdit({host: this.options.host});
                this.video_review = new Review({host: this.options.host});
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
                this.refreshTable();
            },
            initEvents: function () {
                var that = this;
                // 按创建时间排序搜索
                $("#btn_video_search_createTime").off("click.search").on("click.search", function () {
                    that.initTable();
                });
                // 按举报次数搜索
                $("#btn_video_search_report").off("click.search").on("click.search", function () {
                    that.initTable("order_report");
                });
                // 打开可视化审核页面
                $("#btn_video_goto_review").off("click.goto").on("click.goto", function () {
                    //暂时使用跳转的方式
                    location.href = "/video/review?fId=46";
                    //that.opReview();
                });
                // 修改用户信息
                $("#table_live_list tbody").off("click.op_user_edit").on("click.op_user_edit", "a[data-sign=op_user_edit]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opEdit({
                        id: $op_video.data("name")
                    });
                });
                // 下线
                $("#table_live_list tbody").off("click.op_video_verify_down").on("click.op_video_verify_down", "a[data-sign=op_video_verify_down]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opEffect({
                        id: $op_video.data("id"),
                        op: "0"
                    });
                });
                // 上线
                $("#table_live_list tbody").off("click.op_video_verify_up").on("click.op_video_verify_up", "a[data-sign=op_video_verify_up]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opEffect({
                        id: $op_video.data("id"),
                        op: "1"
                    });
                });
                // 修改直播或者录播信息
                $("#table_live_list tbody").off("click.op_video_edit").on("click.op_video_edit", "a[data-sign=op_video_edit]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opChageCover({
                        id: $op_video.data("id")
                    });
                });
                // 取消推荐
                $("#table_live_list tbody").off("click.op_video_recommend_off").on("click.op_video_recommend_off", "a[data-sign=op_video_recommend_off]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opRecommend({
                        id: $op_video.data("id"),
                        op: "0"
                    });
                });
                // 推荐
                $("#table_live_list tbody").off("click.op_video_recommend_on").on("click.op_video_recommend_on", "a[data-sign=op_video_recommend_on]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opRecommend({
                        id: $op_video.data("id"),
                        op: "1"
                    });
                });
                // 关闭机器人
                $("#table_live_list tbody").off("click.op_video_closeRobot").on("click.op_video_closeRobot", "a[data-sign=op_video_closeRobot]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opLiveRobot({
                        id: $op_video.data("id"),
                        op: "0"
                    });
                });
                // 在线播放
                $("#table_live_list tbody").off("click.op_video_view").on("click.op_video_view", "a[data-sign=op_video_view]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opView({
                        id: $op_video.data("id"),
                        $a: $(this)
                    });
                });
                // 在线播放下线视频
                $("#table_live_list tbody").off("click.op_video_view_offLine").on("click.op_video_view_offLine", "a[data-sign=op_video_view_offLine]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opViewOffLine({
                        id: $op_video.data("id"),
                        $a: $(this)
                    });
                });
                // 生成录播回放
                $("#table_live_list tbody").off("click.op_video_create_replay").on("click.op_video_create_replay", "a[data-sign=op_video_create_replay]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opCreateReplay({
                        id: $op_video.data("id"),
                    });
                });
            },
            getQueryParams: function () {
                var search_videoTitle = $("#form_live_search input[name=search_videoTitle]").val();
                var search_videoStatus = $("#form_live_search input[name=search_videoStatus]").val();
                var search_videoType = $("#form_live_search input[name=search_videoType]").val();
                return [
                    {name: "search_videoTitle", value: search_videoTitle},
                    {name: "search_videoStatus", value: search_videoStatus},
                    {name: "search_videoType", value: search_videoType}
                ];
            },
            initTable: function (orderStr) {
                var that = this;
                that.queryProgramNumber();
                this.dataTable = $("#table_live_list").dataTable({
                        iDisplayLength: 20,
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
                            { sTitle: "封面",sWidth: "5%", mData: null,
                                fnRender: function (obj) {
                                    var picture = obj.aData.picture;
                                    if (picture == undefined || "" == picture) {
                                        return "<a id='a_content_pic' href=''></a>"
                                    }
                                    return "<a id='a_content_pic' href='" + picture + "' target='_blank'><img style='width: 30px; height: 30px;' id='img_content_pic' src='" + picture + "' alt=''/></a>"
                                }
                            },
                            { sTitle: "标题", sWidth: "15%", mData: "pname"},
                            { sTitle: "原始标题", sWidth: "15%", mData: "primaryPName"},
                            //{ sTitle: "举报次数", sWidth: "8%", mData: "reportNum"},
                            { sTitle: "发起人", sWidth: "12%", mData: "nickName"},
                            { sTitle: "发起时间",sWidth: "8%", mData: "createTimeStr"},
                            { sTitle: "参与人数", mData: "watchNum"},
                            { sTitle: "评论数", mData: "commentNum"},
                            { sTitle: "点赞数", mData: "likeNum"},
                            {
                                sTitle: "操作", sWidth: "20%", mData: null,
                                fnRender: function (obj) {
                                    var color = "navy";
                                    var action = "下线";
                                    var up_down = "op_video_verify_down";
                                    var color_recommend = "red";
                                    var action_recommend = "op_video_recommend_off";
                                    var recommend_on_off = "取消推荐";
                                    if (obj.aData.isShow != null && obj.aData.isShow == 0) {
                                        color_recommend = "yellow";
                                        action_recommend = "op_video_recommend_on";
                                        recommend_on_off = "推荐";
                                    }
                                    var ops = [
                                        {color: "yellow", sign: "op_video_edit", id: "", name: "", btnName: "修改直播"},
                                        {color: "blue", sign: "op_user_edit", id: "", name: "", btnName: "发起人信息"},
                                        {color: color, sign: up_down, id: "", name: "", btnName: action},
                                        {color: color_recommend, sign: action_recommend, id: "", name: "", btnName: recommend_on_off},
                                        {color: "green", sign: "op_video_view", id: "", name: "", btnName: "点击观看"},
                                        //{color: "red", sign: "op_video_closeRobot", id: "", name: "", btnName: "关闭机器人"}

                                    ];
                                    if(obj.aData.ptype == 3){
                                        ops = [
                                            {color: "yellow", sign: "op_video_edit", id: "", name: "", btnName: "修改录播"},
                                            {color: "blue", sign: "op_user_edit", id: "", name: "", btnName: "发起人信息"},
                                            //{color: "light-grey", sign: "op_video_create_replay", id: "", name: "", btnName: "生成回放"},
                                            {color: color, sign: up_down, id: "", name: "", btnName: action},
                                            {color: "green", sign: "op_video_view", id: "", name: "", btnName: "点击观看"},

                                        ];
                                    }
                                    if (obj.aData.status == 0) {
                                        color = "red";
                                        action = "恢复";
                                        up_down = "op_video_verify_up";
                                        ops = [
                                            {color: "blue", sign: "op_user_edit", id: "", name: "", btnName: "发起人信息"},
                                            {color: color, sign: up_down, id: "", name: "", btnName: action},
                                            {color: "green", sign: "op_video_view_offLine", id: "", name: "", btnName: "点击观看"}

                                        ];
                                    }
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
                    this.initTable();
                }
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
            },
            /*opReview: function () {
                var that = this;
                this.video_review.setOptions({
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.video_review.show();
            },*/
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
            opLiveRobot: function (data) {
                var that = this;
                that.confirm.show("d", function () {
                    $.ajax({
                        url: that.options.host + "/video/opLiveRobot",
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
            opChageCover: function(data){
                var that = this;
                this.cover_edit.setOptions({
                    id: data.id,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.cover_edit.show();
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
            opViewOffLine: function (opts) {
                var that = this;
                $.ajax({
                    url: that.options.host + "/video/viewOffLine",
                    type: "post",
                    data: {id: opts.id},
                    success: function (data) {
                        if (data.rsCode == "A00000") {
                            var href = that.options.host + "/video/page/toView?vuid=" + data.vuid + "&uuid=" + data.uuid;
                            window.open(href, "_blank");
                        } else {
                            $.gritter.add({title: "提示信息：", text: data.rsMsg, time: 2000});
                        }
                    }
                });
            },
            opCreateReplay: function (data) {
                var that = this;
                $.ajax({
                    url: that.options.host + "/video/createReplay",
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
            }

        };
        return Live;
    }

);