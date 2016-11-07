define(function () {

    function DocumentEdit(options) {
        this.init(options);
    }

    DocumentEdit.prototype = {

        init: function (options) {
            this.options = options;
        },
        setOptions: function (options) {
            this.options = $.extend(this.options, options);
        },
        show: function () {
            $("div[data-sign=content]").hide();
            $("#content_document_edit").show();
            this.loadContent();
        },
        loadContent: function () {
            var that = this;
            var url = this.options.host + "/document/page/documentNew";
            var params = {did: this.options.id};
            //IE下如果DIV中有jquery的uploadify组件，则不兼容
            $("#div_document_edit")[0].innerHTML = "";
            $("#div_document_edit").block({message: "Loading..."});
            $("#div_document_edit").load(url + " #form_document", params, function (responseText, textStatus, XMLHttpRequest) {
                $("#div_document_edit").unblock();
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
            // 初始化上传组件
        },
        initDatetime: function () {
            $(".m-wrap[name=isAllowCommentEndDate],[name=isAllowUploadEndDate]").datetimepicker();
        },
        initEvents: function () {
            var that = this;
            // 保存
            $("#btn_document_save").off("click").on("click", function () {
                that.btnSave();
            });
            // 返回
            $("#btn_document_back").off("click").on("click", function () {
                that.btnBack();
            });
        },
        initValidate: function () {
            this.validate = $("#form_document").validate({
                onsubmit: false,
                errorElement: "span",
                errorClass: "help-inline",
                rules: {
                    version: {required: true},
                    comment: {required: true},

                },
                messages: {
                    version: {required: "版本号必填！"},
                    comment: {required: "文案内容必填！"},
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
        getFormData: function () {
            var data = {};
            // 获取表单的值
            $("#form_document").find("input[type=hidden],input[type=text],textarea,select").each(function () {
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
            $("#btn_document_save").attr("disabled", true);
            // 保存请求
            $.ajax({
                url: that.options.host + "/document/documentSave",
                type: "post",
                data: that.getFormData(),
                success: function (data) {
                    $("#btn_document_save").attr("disabled", false);
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
            $("#content_document_list").show();
            if ($.isFunction(this.options.callback_btnSave)) {
                //IE下如果DIV中有jquery的uploadify组件，则不兼容
                $("#div_document_edit")[0].innerHTML = "";
                this.options.callback_btnSave();
            }
        }

    };

    return DocumentEdit;

});