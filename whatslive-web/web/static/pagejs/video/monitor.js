define(["../common/confirm"], function (Confirm) {

        function Monitor(options) {
            this.init(options);
        }

        Monitor.prototype = {
            init: function (options) {
                this.options = options;
                this.confirm = new Confirm({host: this.options.host});

            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                // 初始化UI组件
                App.initUniform();
                this.initEvents();
            },
            initEvents: function () {
                var that = this;
                // 下线
                $("[id^='op_video_verify_down']").off("click.op_video_verify_down").on("click.op_video_verify_down", function () {

                    that.opEffect({
                        id: $(this).data("id"),
                        op: "0",
                        itemId: $(this).attr("id")
                    });
                });
                /// 取消优先
                $("[id^='op_video_priority_off']").off("click.op_video_priority_off").on("click.op_video_priority_off", function () {

                    $(this).attr("class", "hide");
                    $("#" + $(this).data("oppid")).attr("class", "");
                    that.opPriority({
                        id: $(this).data("id"),
                        op: "0"
                    });
                });
                // 优先
                $("[id^='op_video_priority_on']").off("click.op_video_priority_on").on("click.op_video_priority_on", function () {

                    $(this).attr("class", "hide");
                    $("#" + $(this).data("oppid")).attr("class", "");
                    that.opPriority({
                        id: $(this).data("id"),
                        op: "1"
                    });
                });
                // 取消推荐
                $("[id^='op_video_recommend_off']").off("click.op_video_recommend_off").on("click.op_video_recommend_off", function () {

                    $(this).attr("class", "hide");
                    $("#" + $(this).data("oppid")).attr("class", "");
                    that.opRecommend({
                        id: $(this).data("id"),
                        op: "0"
                    });
                });
                // 推荐
                $("[id^='op_video_recommend_on']").off("click.op_video_recommend_on").on("click.op_video_recommend_on", function () {

                    $(this).attr("class", "hide");
                    $("#" + $(this).data("oppid")).attr("class", "");
                    that.opRecommend({
                        id: $(this).data("id"),
                        op: "1"
                    });
                });

                // 分页处理
                $("[pageDiv]").pagination({

                    pageNo: $("#pageNo").val(),
                    total: $("#total").val(),
                    pageSize: 8,
                    callback: function(selectPage) {

                        var videoType = $("#pType").val();
                        var startNo = (selectPage - 1) * 8;

                        $("#pageNo").val(selectPage);
                        $("#startNo").val(startNo);

                        var form = $("#form_monitor");
                        form.method = "post";
                        form.action = that.options.host + "/video/monitor";
                        form.submit();
                    }
                });
            },
            opEffect: function (data) {
                var itemId = data.itemId;
                var that = this;
                that.confirm.show("d", function () {
                    $.ajax({
                        url: that.options.host + "/video/effect",
                        type: "post",
                        data: {id: data.id, op: data.op},
                        success: function (data) {
                            if (data.rsCode == "A00000") {
                                $.gritter.add({title: "提示信息：", text: "操作成功！", time: 1000});
                                $("#" + itemId).parent().html("<p style='font-size:18px; color: #FF0000; font-weight:bold; text-align: center'>已下线</p>");
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
                        } else {
                            $.gritter.add({title: "提示信息：", text: data.rsMsg, time: 2000});
                        }
                    }
                });
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
            }
        };
        return Monitor;
    }
);