define(function () {

    function UserEdit(options) {
        this.init(options);
    }

    UserEdit.prototype = {

        init: function (options) {
            this.options = options;
        },
        setOptions: function (options) {
            this.options = $.extend(this.options, options);
        },
        show: function () {
            $("div[data-sign=content]").hide();
            $("#content_user_edit").show();
            //$("#content_video_list").show();
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
            // 初始化上传组件
            //this.initContentPic("picture");
            // 初始化UI组件
            //App.initUniform();
            // 初始化事件
            this.initEvents();
            // 初始化校验组件
            //this.initValidate();
            // 初始化日期时间组件
            this.initDatetime();
            this.initTable();
        },
        initDatetime: function () {
            $(".m-wrap[name=isAllowCommentEndDate],[name=isAllowUploadEndDate]").datetimepicker();
        },
        initEvents: function () {
            var that = this;
            // 保存
            $("#btn_user_save").off("click").on("click", function () {
                that.btnSave();
            });
            // 返回
            $("#btn_user_back").off("click").on("click", function () {
                that.btnBack();
            });
            // 查看直播或者录播
            $("#table_video_list tbody").off("click.op_video_view").on("click.op_video_view", "a[data-sign=op_video_view]", function () {
                var $op_video = $(this).closest("span[data-sign=op_video]");
                that.opView({
                    id: $op_video.data("name"),
                    $a: $(this)
                });
            });
            // 在线播放下线视频
            $("#table_video_list tbody").off("click.op_video_view_offLine").on("click.op_video_view_offLine", "a[data-sign=op_video_view_offLine]", function () {
                var $op_video = $(this).closest("span[data-sign=op_video]");
                that.opViewOffLine({
                    id: $op_video.data("name"),
                    $a: $(this)
                });
            });
        },
        initValidate: function () {
            this.validate = $("#form_user").validate({
                onsubmit: false,
                errorElement: "span",
                errorClass: "help-inline",
                rules: {
                    nickName: {required: true}
                },
                messages: {
                    nickName: {required: "昵称必填！"}
                },
                highlight: function (element) {
                    $(element).closest(".control-group").removeClass("success").addClass("error");
                },
                unhighlight: function (element) {
                    $(element).closest(".control-group").removeClass("error");
                },
                errorPlacement: function (error, element) {
                    error.insertAfter(element);
                }
            });
        },
        initContentPic: function (str) {
            var that = this;
            // 初始化图片上传
            $("#file_content_" + str).uploadify({
                method: "post",
                swf: App.ctx + "/static/lib/uploadify/swf/uploadify.swf",
                uploader: App.ctx + "/user/user/uploadPic",
                width: "100",
                height: "30",
                buttonClass: "btn blue",
                buttonText: "选择图片",
                fileTypeDesc: "请选择图片",
                fileTypeExts: "*.png;*.jpg",
                fileObjName: "uploadFile",//服务器接收文件时的参数名
                //formData: {apkVersion: "1.0"},
                onUploadSuccess: function (file, data, response) {
                    // 注意此处返回的是string类型的json，需要转换
                    var data = $.parseJSON(data);
                    // 将返回的上传路径设置到隐藏属性
                    $("#form_user input[name=" + str + "Path]").val(data.filePath);
                    // 显示图片预览
                    that.setContentPicUrl(str, data.fileUrl);
                },
                overrideEvents: ["onUploadSuccess"]
            });
            // 去掉原组件的样式
            $("#file_content_" + str + "-button").removeClass("uploadify-button").attr("style", "");
        },
        setContentPicUrl: function (str, url) {
            var src = url || "";
            $("#img_content_" + str).attr("src", src);
            $("#a_content_" + str).attr("href", src);
        },
        getFormData: function () {
            var data = {};
            // 获取表单的值
            $("#form_user").find("input[type=hidden],input[type=text],textarea,select").each(function () {
                data[this.name] = $(this).val();
            });
            return data;
        },
        btnSave: function () {
            //if (!this.validate.form()) {
            //    this.validate.focusInvalid();
            //    return;
            //}
            var that = this;
            $("#btn_user_save").attr("disabled", true);
            // 保存请求
            $.ajax({
                url: that.options.host + "/user/realUser/userSave",
                type: "post",
                data: that.getFormData(),
                success: function (data) {
                    $("#btn_user_save").attr("disabled", false);
                    if (data.rsCode == "A00000") {
                        $.gritter.add({title: "提示信息：", text: "保存成功！", time: 1000});
                        if ($.isFunction(that.options.callback_btnSave)) {
                            that.options.callback_btnSave(data);
                        }
                    } else {
                        $.gritter.add({title: "提示信息：", text: data.rsMsg, time: 2000});
                    }
                }
            });
        },
        btnBack: function () {
            $("div[data-sign=content]").hide();
            $("#content_user_list").show();
            if ($.isFunction(this.options.callback_btnSave)) {
                this.options.callback_btnSave();
            }
        },

        getQueryParams: function () {
            return [
                {name: "userid", value: this.options.id}
            ];
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
        initTable: function () {
            var that = this;
            this.dataTable = $("#table_video_list").dataTable({
                    iDisplayLength: 10,
                    bProcessing: true,
                    bServerSide: true,
                    bSort: false,
                    bFilter: false,
                    bAutoWidth: false,
                    bDestroy: true,
                    sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                    sAjaxSource: this.options.host + "/user/realUser/videoList",
                    sServerMethod: "POST",
                    aoColumns: [
                        { sTitle: "id", mData: "id"},
                        { sTitle: "标题", mData: "pname"},
                        { sTitle: "开始时间", mData: "startTimeStr"},
                        { sTitle: "参与人数", mData: "watchNum"},
                        { sTitle: "点赞数", mData: "likeNum"},
                        { sTitle: "直播类型", mData: null,
                            fnRender: function (obj) {
                                switch (obj.aData.ptype) {
                                    case 1:
                                        return '直播中';
                                    case 3:
                                        return '已结束';
                                    case 2:
                                        return '预约中';
                                    default:
                                        return '';
                                }
                            }
                        },
                        { sTitle: "状态", mData: null,
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
                            sTitle: "操作", mData: null,
                            fnRender: function (obj) {
                                switch (obj.aData.ptype) {
                                    case 2:
                                        return '';
                                    case 3:
                                        switch (obj.aData.status){
                                            case -2:
                                                return '';
                                            case -1:
                                                return '';
                                            case 0:
                                                return _.template($("#temp_op").html(), {
                                                    sign: "op_video",
                                                    id: obj.aData.userId,
                                                    name: obj.aData.id,
                                                    ops: [
                                                        {color: "blue", sign: "op_video_view_offLine", id: "", name: "", btnName: "点击观看"},
                                                    ]
                                                });
                                            default:
                                                return _.template($("#temp_op").html(), {
                                                    sign: "op_video",
                                                    id: obj.aData.userId,
                                                    name: obj.aData.id,
                                                    ops: [
                                                        {color: "blue", sign: "op_video_view", id: "", name: "", btnName: "点击观看"},
                                                    ]
                                                });
                                        }
                                    default:
                                        return _.template($("#temp_op").html(), {
                                            sign: "op_video",
                                            id: obj.aData.userId,
                                            name: obj.aData.id,
                                            ops: [
                                                {color: "blue", sign: "op_video_view", id: "", name: "", btnName: "点击观看"},
                                            ]
                                        });
                                }

                            }
                        }
                    ],
                    fnServerParams: function (aoData) {
                        aoData = $.merge(aoData, that.getQueryParams());
                    }
                }
            );
        },
    };

    return UserEdit;

});