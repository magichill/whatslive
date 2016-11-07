define(["../user/realUser_edit","./order_edit","../common/confirm"], function (UserEdit,OrderEdit,Confirm) {

        function Order(options) {
            this.init(options);
        }

        Order.prototype = {
            init: function (options) {
                this.options = options;
                this.user_edit = new UserEdit({host: this.options.host});
                this.order_edit = new OrderEdit({host: this.options.host});
                this.confirm = new Confirm({host: this.options.host});
            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_user_list").show();
                // 初始化UI组件
                App.initUniform();
                this.initEvents();
                this.refreshTable();
            },
            initEvents: function () {
                var that = this;
                // 按时间排序搜索
                $("#btn_video_search_pType").off("click.search").on("click.search", function () {
                    that.initTable("order_pType");
                });
                // 修改用户信息
                $("#table_live_list tbody").off("click.op_user_edit").on("click.op_user_edit", "a[data-sign=op_user_edit]", function () {

                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opUserEdit({
                        id: $op_video.data("name")
                    });
                });
                // 修改预约信息
                $("#table_live_list tbody").off("click.op_video_order_edit").on("click.op_video_order_edit", "a[data-sign=op_video_order_edit]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opOrderEdit({
                        id: $op_video.data("id")
                    });
                });
                // 取消置顶
                $("#table_live_list tbody").off("click.op_video_top_off").on("click.op_video_top_off", "a[data-sign=op_video_top_off]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opTop({
                        id: $op_video.data("id"),
                        op: "0"
                    });
                });
                // 置顶
                $("#table_live_list tbody").off("click.op_video_top_on").on("click.op_video_top_on", "a[data-sign=op_video_top_on]", function () {
                    var $op_video = $(this).closest("span[data-sign=op_video]");
                    that.opTop({
                        id: $op_video.data("id"),
                        op: "1"
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
            },
            getQueryParams: function () {
                var search_videoTitle = $("#form_video_search input[name=search_videoTitle]").val();
                var search_videoStatus = $("#form_video_search input[name=search_videoStatus]").val();
                var search_videoType = $("#form_video_search input[name=search_videoType]").val();
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
                            { sTitle: "ID", mData: "id"},
                            { sTitle: "userId", mData: "userId", bVisible:false},
                            { sTitle: "标题", sWidth: "20%", mData: "pname"},
                            { sTitle: "封面",sWidth: "5%", mData: null,
                                fnRender: function (obj) {
                                    var picture = obj.aData.picture;
                                    if (picture == undefined || "" == picture) {
                                        return "无"
                                    }
                                    return "<a id='a_content_pic' href='" + picture + "' target='_blank'><img style='width: 30px; height: 30px;' id='img_content_pic' src='" + picture + "' alt=''/></a>"
                                }
                            },

                            { sTitle: "预约时间", mData: "startTimeStr"},
                            { sTitle: "发起人", mData: "nickName"},
                            { sTitle: "发起人等级", mData: null,
                                fnRender: function (obj) {
                                    if(obj.aData.userLevel==null){
                                        return '0';
                                    }else{
                                        return obj.aData.userLevel;
                                    }
                                }
                            },
                            { sTitle: "预约人数", mData: "orderNum"},
                            { sTitle: "标签", mData: "tagValue"},
                            {
                                sTitle: "操作", mData: null,
                                fnRender: function (obj) {
                                    var color = "navy";
                                    var action = "取消置顶";
                                    var up_down = "op_video_top_off";
                                    var color_recommend = "navy";
                                    var action_recommend = "op_video_recommend_off";
                                    var recommend_on_off = "取消推荐";
                                    if (obj.aData.ptype != 0) {
                                        color = "red";
                                        action = "置顶";
                                        up_down = "op_video_top_on";
                                    }
                                    if (obj.aData.isShow != null && obj.aData.isShow == 0) {
                                        color_recommend = "green";
                                        action_recommend = "op_video_recommend_on";
                                        recommend_on_off = "推荐";
                                    }
                                    return _.template($("#temp_op").html(), {
                                        sign: "op_video",
                                        id: obj.aData.id,
                                        name: obj.aData.userId,
                                        pic: obj.aData.picture,
                                        ops: [
                                            {color: "blue", sign: "op_user_edit", id: "", name: "", btnName: "发起人信息"},
                                            {color: "yellow", sign: "op_video_order_edit", id: "", name: "", btnName: "修改预约"},
                                            {color: color, sign: up_down, id: "", name: "", btnName: action},
                                            {color: color_recommend, sign: action_recommend, id: "", name: "", btnName: recommend_on_off}
                                        ]
                                    });

                                }
                            }
                        ],
                        fnServerParams: function (aoData) {
                            aoData = $.merge(aoData, that.getQueryParams());
                            aoData = $.merge(aoData, [
                                {name: orderStr, value: 1},
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
                    this.initTable("order_pType");
                }
            },
            opUserEdit: function (data) {
                var that = this;
                this.user_edit.setOptions({
                    id: data.id,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.user_edit.show();
            },
            opOrderEdit: function(data){
                var that = this;
                this.order_edit.setOptions({
                    id: data.id,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.order_edit.show();
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
            opTop: function (data) {
                var that = this;
                that.confirm.show("d", function () {
                    $.ajax({
                        url: that.options.host + "/video/orderTop",
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
        };
        return Order;
    }
);