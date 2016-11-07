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
            this.loadContent();
        },
        loadContent: function () {
            var that = this;
            var url = this.options.host + "/user/virtualUser/page/userNew";
            var params = {uid: this.options.id};
            //IE下如果DIV中有jquery的uploadify组件，则不兼容
            //$("#div_user_edit").empty();
            $("#div_user_edit")[0].innerHTML = "";
            $("#div_user_edit").block({message: "Loading..."});
            $("#div_user_edit").load(url + " #form_user", params, function (responseText, textStatus, XMLHttpRequest) {
                $("#div_user_edit").unblock();
                that.loadContentFinish();
            });
        },
        loadContentFinish: function () {

            // 初始化UI组件
            App.initUniform();
            // 初始化事件
            this.initEvents();
            // 初始化校验组件
            this.initValidate();
            // 初始化日期时间组件
            this.initDatetime();
            // 初始化上传组件
            this.initContentPic("picture");
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
        },
        initValidate: function () {
            this.validate = $("#form_user").validate({
                onsubmit: false,
                errorElement: "span",
                errorClass: "help-inline",
                rules: {
                    nickName: {required: true},
                    introduce: {required: true},

                },
                messages: {
                    nickName: {required: "用户名必填！"},
                    introduce: {required: "自我介绍必填！"},
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
                uploader: App.ctx + "/user/virtualUser/uploadImg",
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
                    $("#form_user input[name=vPic]").val(data.localUrl);
                    $("#form_user input[name=sPic]").val(data.fileUrl);
                    // 将返回的上传路径设置到隐藏属性
                    $("#form_user input[name=" + str + "Path]").val(data.filePath);
                    // 显示图片预览
                    that.setContentPicUrl(str, data.fileUrl);
                },
                overrideEvents: ["onUploadSuccess"]
            });
            // 去掉原组件的样式
            //$("#file_content_" + str + "-button").removeClass("uploadify-button").attr("style", "");
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
            if (!this.validate.form()) {
                this.validate.focusInvalid();
                return;
            }

            var that = this;
            $("#btn_user_save").attr("disabled", true);
            // 保存请求
            $.ajax({
                url: that.options.host + "/user/virtualUser/userSave",
                type: "post",
                data: that.getFormData(),
                success: function (data) {
                    $("#btn_user_save").attr("disabled", false);
                    if (data.rsCode == "A00000") {
                        $.gritter.add({title: "提示信息：", text: "保存成功！", time: 1000});
                        that.btnBack();
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
                //IE下如果DIV中有jquery的uploadify组件，则不兼容
                $("#div_user_edit")[0].innerHTML = "";
                this.options.callback_btnSave();
            }
        }

    };

    return UserEdit;

});