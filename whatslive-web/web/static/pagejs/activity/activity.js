define(["./activity_edit","./activityVideo_manage","../common/confirm"], function (ActivityEdit,ActivityVideoManage,Confirm) {

        function Activity(options) {
            this.init(options);
        }

        Activity.prototype = {
            init: function (options) {
                this.options = options;
                this.activity_edit = new ActivityEdit({host: this.options.host});
                this.activityVideo_manage = new ActivityVideoManage({host: this.options.host});
                this.confirm = new Confirm({host: this.options.host});

            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_activity_list").show();
                // 初始化UI组件
                App.initUniform();
                this.initEvents();
                this.refreshTable();
            },
            initEvents: function () {
                var that = this;
                // 新增
                $("#btn_activity_new").off("click.search").on("click.search", function () {
                    that.opEdit({
                        id: null
                    });
                });
                // 下线
                $("#table_activity_list tbody").off("click.opEffect").on("click.op_activity_verify_down", "a[data-sign=op_activity_verify_down]", function () {
                    var $op_activity = $(this).closest("span[data-sign=op_activity]");
                    that.opEffect({
                        id: $op_activity.data("id"),
                        op: "0"
                    });
                });
                // 上线
                $("#table_activity_list tbody").off("click.op_activity_verify_up").on("click.op_activity_verify_up", "a[data-sign=op_activity_verify_up]", function () {
                    var $op_activity = $(this).closest("span[data-sign=op_activity]");
                    that.opEffect({
                        id: $op_activity.data("id"),
                        op: "1"
                    });
                });
                // 优先
                $("#table_activity_list tbody").off("click.op_activity_priority_on").on("click.op_activity_priority_on", "a[data-sign=op_activity_priority_on]", function () {
                    var $op_activity = $(this).closest("span[data-sign=op_activity]");
                    that.opPriority({
                        id: $op_activity.data("id"),
                        op: "1"
                    });
                });
                // 取消优先
                $("#table_activity_list tbody").off("click.op_activity_priority_off").on("click.op_activity_priority_off", "a[data-sign=op_activity_priority_off]", function () {
                    var $op_activity = $(this).closest("span[data-sign=op_activity]");
                    that.opPriority({
                        id: $op_activity.data("id"),
                        op: "0"
                    });
                });
                // 取消推荐
                $("#table_activity_list tbody").off("click.op_activity_recommend_off").on("click.op_activity_recommend_off", "a[data-sign=op_activity_recommend_off]", function () {
                    var $op_activity = $(this).closest("span[data-sign=op_activity]");
                    that.opRecommend({
                        id: $op_activity.data("id"),
                        op: "0"
                    });
                });
                // 推荐
                $("#table_activity_list tbody").off("click.op_activity_recommend_on").on("click.op_activity_recommend_on", "a[data-sign=op_activity_recommend_on]", function () {
                    var $op_activity = $(this).closest("span[data-sign=op_activity]");
                    that.opRecommend({
                        id: $op_activity.data("id"),
                        op: "1"
                    });
                });
                // 修改
                $("#table_activity_list tbody").off("click.op_activity_change").on("click.op_activity_change", "a[data-sign=op_activity_change]", function () {
                    var $op_activity = $(this).closest("span[data-sign=op_activity]");
                    that.opEdit({
                        id: $op_activity.data("id")
                    });
                });
                // 管理
                $("#table_activity_list tbody").off("click.op_manager").on("click.op_manager", "a[data-sign=op_manager]", function () {
                    var $op_activity = $(this).closest("span[data-sign=op_activity]");
                    that.opManage({
                        id: $op_activity.data("id")
                    });
                });
            },
            getQueryParams: function () {
                //var search_activityTitle = $("#form_live_search input[name=search_activityTitle]").val();
                //var search_activityStatus = $("#form_live_search input[name=search_activityStatus]").val();
                //var search_activityType = $("#form_live_search input[name=search_activityType]").val();
                return [
                    //{name: "search_activityTitle", value: search_activityTitle},
                    //{name: "search_activityStatus", value: search_activityStatus},
                    //{name: "search_activityType", value: search_activityType}
                ];
            },
            initTable: function (orderStr) {
                var that = this;
                this.dataTable = $("#table_activity_list").dataTable({
                        iDisplayLength: 20,
                        bProcessing: true,
                        bServerSide: true,
                        bSort: false,
                        bFilter: false,
                        bAutoWidth: false,
                        bDestroy: true,
                        sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                        sAjaxSource: this.options.host + "/activity/list.json",
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
                            { sTitle: "标题", mData: "title"},
                            { sTitle: "标签", mData: "tag"},
                            { sTitle: "创建时间", mData: "createTimeStr"},
                            { sTitle: "状态", mData: null,
                                fnRender: function (obj) {
                                    switch (obj.aData.status) {
                                        case 1:
                                            return '<span class="label label-success">已上线</span>';
                                        case 0:
                                            return '<span class="label label-warning">未上线</span>';
                                        default:
                                            return '';
                                    }
                                }
                            },
                            {
                                sTitle: "操作", mData: null,
                                fnRender: function (obj) {
                                    var color = "navy";
                                    var action = "下线"
                                    var up_down = "op_activity_verify_down";
                                    var priority_color = "blue";
                                    var priority_action = "优先"
                                    var priority_on_off = "op_activity_priority_on";
                                    if (obj.aData.priority != null && obj.aData.priority > 0) {
                                        priority_color = "yellow";
                                        priority_action = "取消优先";
                                        priority_on_off = "op_activity_priority_off";
                                    }
                                    var color_recommend = "red";
                                    var action_recommend = "op_activity_recommend_off";
                                    var recommend_on_off = "取消推荐";
                                    if (obj.aData.isRecommend != null && obj.aData.isRecommend == 0) {
                                        color_recommend = "yellow";
                                        action_recommend = "op_activity_recommend_on";
                                        recommend_on_off = "推荐";
                                    }
                                    if (obj.aData.status == 0) {
                                        color = "blue";
                                        action = "上线";
                                        up_down = "op_activity_verify_up";
                                    }
                                    var ops = [
                                        {color: "green", sign: "op_activity_change", id: "", name: "", btnName: "修改活动"},
                                        {color: priority_color, sign: priority_on_off, id: "", name: "", btnName: priority_action},
                                        {color: color_recommend, sign: action_recommend, id: "", name: "", btnName: recommend_on_off},
                                        {color: color, sign: up_down, id: "", name: "", btnName: action},
                                        {color: "purple", sign: "op_manager", id: "", name: "", btnName: "管理"}
                                    ];
                                    return _.template($("#temp_op").html(), {
                                        sign: "op_activity",
                                        id: obj.aData.id,
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
                this.activity_edit.setOptions({
                    id: data.id,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.activity_edit.show();
            },
            opManage: function (data) {
                var that = this;
                this.activityVideo_manage.setOptions({
                    _id: data.id,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.activityVideo_manage.show();
            },
            opEffect: function (data) {
                var that = this;
                that.confirm.show("d", function () {
                    $.ajax({
                        url: that.options.host + "/activity/effect",
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
                    url: that.options.host + "/activity/priority",
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
                    url: that.options.host + "/activity/recommend",
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
        return Activity;
    }
);