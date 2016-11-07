define(["../common/confirm","./../common/video_list_choose"], function (Confirm,VideoList) {

        function ActivityVideoManage(options) {
            this.init(options);
        }

        ActivityVideoManage.prototype = {
            init: function (options) {
                this.options = options;
                this.video_list = new VideoList({host: this.options.host});
                this.confirm = new Confirm({host: this.options.host});

            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_video_list").show();
                // 初始化UI组件
                App.initUniform();
                this.initEvents();
                this.refreshTable();
            },
            initEvents: function () {
                var that = this;
                // 搜索
                $("#btn_activity_video_search").off("click.search").on("click.search", function () {
                    that.initTable();
                });
                //新增
                $("#btn_video_list_new").click(function(){
                    that.loadContent();
                });
                // 增加视频页面返回
                $("#btn_add_video_back").click(function(){
                    that.btnBack();
                });
                // 活动视频列表返回
                $("#btn_video_list_back").click(function(){
                    that.btnManageBack();
                });
                // 优先
                $("#table_video_list tbody").off("click.op_video_priority_on").on("click.op_video_priority_on", "a[data-sign=op_video_priority_on]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opRecommend({
                        id: $op_video.data("id"),
                        op: "2"
                    });
                });
                // 取消优先
                $("#table_video_list tbody").off("click.op_video_priority_off").on("click.op_video_priority_off", "a[data-sign=op_video_priority_off]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opRecommend({
                        id: $op_video.data("id"),
                        op: "3"
                    });
                });
                // 删除
                $("#table_video_list tbody").off("click.op_video_recommend_off").on("click.op_video_recommend_off", "a[data-sign=op_video_recommend_off]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opRecommend({
                        id: $op_video.data("id"),
                        op: "0"
                    });
                });
                // 在线播放
                $("#table_video_list tbody").off("click.op_video_view").on("click.op_video_view", "a[data-sign=op_video_view]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opView({
                        id: $op_video.data("id"),
                        $a: $(this)
                    });
                })

            },
            getQueryParams: function () {
                var search_videoTitle = $("#form_video_search input[name=search_activity_videoTitle]").val();
                var actId = this.options._id;
                return [
                    {name: "search_videoTitle", value: search_videoTitle},
                    {name: "search_actId", value: actId}
                ];
            },
            initTable: function (orderStr) {
                var that = this;
                this.dataTable = $("#table_video_list").dataTable({
                        iDisplayLength: 20,
                        bProcessing: true,
                        bServerSide: true,
                        bSort: false,
                        bFilter: false,
                        bAutoWidth: false,
                        bDestroy: true,
                        sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                        sAjaxSource: this.options.host + "/activity/programList",
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
                            { sTitle: "类型", sWidth: "8%", mData: null,
                                fnRender: function (obj) {
                                    switch (obj.aData.ptype) {
                                        case 1:
                                            return '直播';
                                        case 3:
                                            return '录播';
                                        default:
                                            return '未知';
                                    }
                                }
                            },
                            { sTitle: "标题", sWidth: "20%", mData: "pname"},
                            { sTitle: "发起人", sWidth: "12%", mData: "nickName"},
                            { sTitle: "发起时间",sWidth: "8%", mData: "createTimeStr"},
                            { sTitle: "参与人数", mData: "watchNum"},
                            { sTitle: "评论数", mData: "commentNum"},
                            { sTitle: "点赞数", mData: "likeNum"},
                            { sTitle: "直播状态", mData: null,
                                fnRender: function (obj) {
                                    switch (obj.aData.status) {
                                        case -2:
                                            return '小于16秒';
                                        case -1:
                                            return '转码中';
                                        case 0:
                                            switch (obj.aData.ptype) {
                                                case 2:
                                                    return '已取消';
                                                default:
                                                    return '已下线';
                                            }
                                        case 1:
                                            return '正常';
                                        default:
                                            return '';
                                    }
                                }
                            },
                            {
                                sTitle: "操作", sWidth: "20%", mData: null,
                                fnRender: function (obj) {
                                    var color = "blue";
                                    var action = "优先"
                                    var priority_on_off = "op_video_priority_on";
                                    if (obj.aData.priority != null && obj.aData.priority > 0) {
                                        color = "yellow";
                                        action = "取消优先";
                                        priority_on_off = "op_video_priority_off";
                                    }
                                    var ops = [
                                        //{color: color, sign: priority_on_off, id: "", name: "", btnName: action},
                                        {color: "red", sign: "op_video_recommend_off", id: "", name: "", btnName: "删除"},
                                        {color: "green", sign: "op_video_view", id: "", name: "", btnName: "点击观看"}
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
            refreshTable: function () {
                if (this.dataTable) {
                    this.dataTable.fnDraw();
                } else {
                    this.initTable();
                }
            },
            btnManageBack: function () {
                $("div[data-sign=content]").hide();
                $("#content_activity_list").show();
                if ($.isFunction(this.options.callback_btnBack)) {
                    this.options.callback_btnBack();
                }
            },
            refreshModalTable: function () {
                if (this.dataTable) {
                    this.dataTable.fnDraw();
                } else {
                    this.initTagAddVideoTable();
                }
            },
            loadContent: function () {
                var that=this;
                this.video_list.setOptions(this.options);
                this.video_list.setOptions({
                    callback_refresh: function (data) {
                        that.refreshTable();
                    },
                    _id:that.options._id,
                    type:1
                });
                this.video_list.show();
            },
            opRecommend: function (data) {
                var that = this;
                $.ajax({
                    url: that.options.host + "/commonChoose/recommend",
                    type: "post",
                    data: {type:1, id:this.options._id, programId: data.id, op: data.op},
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
            }

        };
        return ActivityVideoManage;
    }

);