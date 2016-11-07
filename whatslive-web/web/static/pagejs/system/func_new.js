/**
 * Created by zhuminghua on 14-4-17.
 */
define(function () {

    function FuncNew(options) {
        this.init(options);
    }

    FuncNew.prototype = {

        init: function (options) {
            this.options = options;
        },
        setOptions: function (options) {
            this.options = $.extend(this.options, options);
        },
        show: function () {
            $("div[data-sign=content]").hide();
            $("#content_func_edit").show();
            this.loadContent();
        },
        loadContent: function () {
            var that = this;
            var url ='';
            var opt = this.options.opt;
            var url ='';
            if(opt=='new'){
                 url = this.options.host + "/system/func/page/funcNew";
            }else if(opt=='edit'){
                 url = this.options.host + "/system/func/page/funcEdit";
            }else{
                 url = this.options.host + "/system/func/page/funcChild";
            }
            var params = {id: this.options.id,opt:opt};
            $("#div_func_edit").empty();
            $("#div_func_edit").block({message: "Loading..."});
            $("#div_func_edit").load(url + " #form_func", params, function (responseText, textStatus, XMLHttpRequest) {
                $("#div_func_edit").unblock();
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
        },
        initEvents: function () {
            var that = this;
            $("#btn_func_save").off("click").on("click", function () {
                that.btnSave();
            });
            $("#btn_func_back").off("click").on("click", function () {
                that.btnBack();
            });
        },
        initValidate: function () {
            this.validate = $("#form_func").validate({
                onsubmit: false,
                errorElement: "span",
                errorClass: "help-inline",
                rules: {
                    funcName: {required: true},
                    isLeaf: {required: true},
                    actionUrl: {required: true},
                    funcOrder: {required: true}
                },
                messages: {
                    funcName: {required: "资源名称必填！"},
                    isLeaf: {required: "是否叶子节点必选！"},
                    actionUrl: {required: "资源链接必填！"},
                    funcOrder: {required: "排序号必填！"}
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
            $("#form_func").find("input[type=hidden],input[type=text],input[type=radio]:checked,textarea,select").each(function () {
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
            $("#btn_func_save").attr("disabled", true);
            var url ='';
            var opt = this.options.opt;
            if(opt=='new')
                url = that.options.host + "/system/func/funcSave";
            else if(opt=='edit')
                url = that.options.host + "/system/func/funcUpdate";
            else
                url = that.options.host + "/system/func/funcSaveChild";
            // 保存请求
            $.ajax({
                url: url,
                type: "post",
                data: that.getFormData(),
                success: function (data) {
                    $("#btn_func_save").attr("disabled", false);
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
            $("#content_func_list").show();
            if ($.isFunction(this.options.callback_btnBack)) {
                this.options.callback_btnBack();
            }
        }
    };

    return FuncNew;

});