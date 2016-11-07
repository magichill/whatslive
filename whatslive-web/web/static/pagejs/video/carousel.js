define(["./carousel_edit","../common/confirm"], function (CarouselEdit,Confirm) {

        function Carousel(options) {
            this.init(options);
        }

        Carousel.prototype = {
            init: function (options) {
                this.options = options;
                this.carousel_edit = new CarouselEdit({host: this.options.host});
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
                // 新增
                $("#btn_video_new").off("click.search").on("click.search", function () {
                    that.opEdit({
                        id: null
                    });
                });
                // 下线
                $("#table_live_list tbody").off("click.opEffect").on("click.op_video_verify_down", "a[data-sign=op_video_verify_down]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opEffect({
                        id: $op_video.data("id"),
                        op: "0"
                    });
                });
                // 上线
                $("#table_live_list tbody").off("click.op_vipdeo_verify_up").on("click.op_video_verify_up", "a[data-sign=op_video_verify_up]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opEffect({
                        id: $op_video.data("id"),
                        op: "1"
                    });
                });
                // 优先
                $("#table_live_list tbody").off("click.op_video_priority_on").on("click.op_video_priority_on", "a[data-sign=op_video_priority_on]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opPriority({
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
                // 下线
                $("#table_live_list tbody").off("click.op_video_priority_off").on("click.op_video_priority_off", "a[data-sign=op_video_priority_off]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opPriority({
                        id: $op_video.data("id"),
                        op: "0"
                    });
                });
                // 修改轮播信息
                $("#table_live_list tbody").off("click.op_carousel_change").on("click.op_carousel_change", "a[data-sign=op_carousel_change]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opEdit({
                        id: $op_video.data("id")
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
                this.dataTable = $("#table_live_list").dataTable({
                        iDisplayLength: 20,
                        bProcessing: true,
                        bServerSide: true,
                        bSort: false,
                        bFilter: false,
                        bAutoWidth: false,
                        bDestroy: true,
                        sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                        sAjaxSource: this.options.host + "/carousel/list.json",
                        sServerMethod: "POST",
                        aoColumns: [
                            { sTitle: "id", mData: "id"},
                            { sTitle: "封面", mData: null,
                                fnRender: function (obj) {
                                    var picture = obj.aData.picture;
                                    if (picture == undefined || "" == picture) {
                                        return "<a id='a_content_pic' href=''></a>"
                                    }
                                    return "<a id='a_content_pic' href='" + picture + "' target='_blank'><img style='width: 30px; height: 30px;' id='img_content_pic' src='" + picture + "' alt=''/></a>"
                                }
                            },
                            { sTitle: "名称", mData: "pname"},
                            { sTitle: "发起人", mData: "nickName"},
                            { sTitle: "发起时间", mData: "createTimeStr"},
                            { sTitle: "参与人数", mData: "watchNum"},
                            { sTitle: "点赞数", mData: "likeNum"},
                            { sTitle: "LiveId", mData: "liveId"},
                            { sTitle: "ActivityId", mData: "activityId"},
                            {
                                sTitle: "操作", mData: null,
                                fnRender: function (obj) {
                                    var color = "navy";
                                    var action = "下线"
                                    var up_down = "op_video_verify_down";
                                    var priority_color = "blue";
                                    var priority_action = "优先"
                                    var priority_on_off = "op_video_priority_on";
                                    if (obj.aData.priority != null && obj.aData.priority > 0) {
                                        priority_color = "yellow";
                                        priority_action = "取消优先";
                                        priority_on_off = "op_video_priority_off";
                                    }
                                    if (obj.aData.status == 0) {
                                        color = "red";
                                        action = "恢复";
                                        up_down = "op_video_verify_up";
                                    }
                                    var ops = [
                                        {color: "green", sign: "op_carousel_change", id: "", name: "", btnName: "修改轮播"},
                                        {color: color, sign: up_down, id: "", name: "", btnName: action},
                                        {color: priority_color, sign: priority_on_off, id: "", name: "", btnName: priority_action},
                                        //{color: "green", sign: "op_video_view", id: "", name: "", btnName: "点击观看"}

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
            opEdit: function(data){
                var that = this;
                this.carousel_edit.setOptions({
                    id: data.id,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.carousel_edit.show();
            },
            opEffect: function (data) {
                var that = this;
                that.confirm.show("d", function () {
                    $.ajax({
                        url: that.options.host + "/carousel/effect",
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
            }

        };
        return Carousel;
    }
);