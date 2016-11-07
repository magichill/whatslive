define(function () {

    function ActivityEdit(options) {
        this.init(options);
    }

    ActivityEdit.prototype = {

        init: function (options) {
            this.options = options;
        },
        setOptions: function (options) {
            this.options = $.extend(this.options, options);
        },
        show: function () {
            $("div[data-sign=content]").hide();
            $("#content_activity_edit").show();
            this.loadContent();
        },
        loadContent: function () {
            var that = this;
            var url = this.options.host + "/activity/page/editActivity";
            var params = {aid: this.options.id};
            $("#div_activity_edit")[0].innerHTML = "";
            $("#div_activity_edit").block({message: "Loading..."});
            $("#div_activity_edit").load(url + " #form_activity", params, function (responseText, textStatus, XMLHttpRequest) {
                $("#div_activity_edit").unblock();
                that.loadContentFinish();
            });
        },
        loadContentFinish: function () {
            // 初始化上传组件
            this.initContentPic();
            // 初始化UI组件
            App.initUniform();
            // 初始化事件
            this.initEvents();
            // 初始化校验组件
            this.initValidate();
            // 初始化日期时间组件
            //this.initDatetime();
        },
        initValidate: function () {
            this.validate = $("#form_activity").validate({
                onsubmit: false,
                errorElement: "span",
                errorClass: "help-inline",
                rules: {
                    title: {required: true},
                    tag: {required: true}

                },
                messages: {
                    title: {required: "标题必填！"},
                    tag: {required: "标签必填！"}
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
        initDatetime: function () {
            $(".m-wrap[name=isAllowCommentEndDate],[name=isAllowUploadEndDate]").datetimepicker();
        },
        initEvents: function () {
            var that = this;
            // 保存
            $("#btn_activity_save").off("click").on("click", function () {
                that.btnSave();
            });
            // 返回
            $("#btn_activity_back").off("click").on("click", function () {
                that.btnBack();
            });
        },
        initContentPic: function () {
            var that = this;
            // 初始化图片上传
            $("#file_content_picture").uploadify({
                method: "post",
                swf: that.options.host + "/static/lib/uploadify/swf/uploadify.swf",
                uploader: that.options.host + "/activity/uploadImg",
                width: "100",
                height: "30",
                buttonClass: "btn blue",
                buttonText: "选择图片",
                fileTypeDesc: "请选择图片",
                fileTypeExts: "*.png;*.jpg",
                fileObjName: "uploadFile",//服务器接收文件时的参数名
                onUploadSuccess: function (file, data, response) {
                    // 注意此处返回的是string类型的json，需要转换
                    var data = $.parseJSON(data);
                    // 将返回的上传路径设置到隐藏属性
                    $("#form_activity input[name=vPic]").val(data.localUrl);
                    $("#form_activity input[name=sPic]").val(data.fileUrl);

                    // 显示图片预览
                    that.setContentPicUrl(data.fileUrl);
                },
                overrideEvents: ["onUploadSuccess"]
            });
            // 去掉原组件的样式
            $("#file_content_picture").removeClass("uploadify-button").attr("style", "");
        },
        setContentPicUrl: function (url) {
            var src = url || "";
            $("#img_content_picture").attr("src", src);
            $("#a_content_picture").attr("href", src);
        },
        btnSave: function () {
            debugger;
            if (!this.validate.form()) {
                this.validate.focusInvalid();
                return;
            }
            var that = this;
            $("#btn_activity_save").attr("disabled", true);
            // 保存请求
            $.ajax({
                url: that.options.host + "/activity/saveActivity",
                type: "post",
                data: that.getFormData(),
                success: function (data) {
                    $("#btn_activity_save").attr("disabled", false);
                    if (data.rsCode == "A00000") {
                        $.gritter.add({title: "提示信息：", text: "保存成功！", time: 1000});
                        that.btnBack();
                    } else {
                        $.gritter.add({title: "提示信息：", text: data.rsMsg, time: 2000});
                    }
                }
            });
        },
        getFormData: function () {
            var data = {};
            // 获取表单的值
            $("#form_activity").find("input[type=hidden],input[type=text],textarea,select").each(function () {
                data[this.name] = $(this).val();
            });

            return data;
        },
        btnBack: function () {
            $("div[data-sign=content]").hide();
            $("#content_activity_list").show();
            $("#div_activity_edit")[0].innerHTML = "";
            if ($.isFunction(this.options.callback_btnSave)) {
                //IE下如果DIV中有jquery的uploadify组件，则不兼容
                $("#div_activity_edit")[0].innerHTML = "";
                this.options.callback_btnSave();
            }
        },

        getQueryParams: function () {
            return [
                {name: "uid", value: this.options.id}
            ];
        }
    };

    return ActivityEdit;

});