/**
 * Created by zhuminghua on 14-4-17.
 */
define(function () {

    function RoleNew(options) {
        this.init(options);
    }

    RoleNew.prototype = {

        init: function (options) {
            this.options = options;
        },
        setOptions: function (options) {
            this.options = $.extend(this.options, options);
        },
        show: function () {
            $("div[data-sign=content]").hide();
            $("#content_role_edit").show();
            this.loadContent();
        },
        loadContent: function () {
            var that = this;
            var url = this.options.host + "/system/role/page/roleNew";
            var params = {id: this.options.id};
            $("#div_role_edit").empty();
            $("#div_role_edit").block({message: "Loading..."});
            $("#div_role_edit").load(url + " #form_role", params, function (responseText, textStatus, XMLHttpRequest) {
                $("#div_role_edit").unblock();
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
            $("#btn_role_save").off("click").on("click", function () {
                that.btnSave();
            });
            $("#btn_role_back").off("click").on("click", function () {
                that.btnBack();
            });
        },
        initValidate: function () {
            this.validate = $("#form_role").validate({
                onsubmit: false,
                errorElement: "span",
                errorClass: "help-inline",
                rules: {
                    roleName: {required: true}
                },
                messages: {
                    roleName: {required: "角色名必填！"}
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
            $("#form_role").find("input[type=hidden],input[type=text],textarea,select").each(function () {
                data[this.name] = $(this).val();
            });
            // 获取多选的角色
//            var roles = [];
//            $("#form_role input[name=roles]:checked").each(function () {
//                roles.push($(this).val());
//            });
//            data["roles"] = roles.join(",");
            return data;
        },
        btnSave: function () {
            if (!this.validate.form()) {
                this.validate.focusInvalid();
                return;
            }
            var that = this;
            $("#btn_role_save").attr("disabled", true);
            // 保存请求
            $.ajax({
                url: that.options.host + "/system/role/roleSave",
                type: "post",
                data: that.getFormData(),
                success: function (data) {
                    $("#btn_role_save").attr("disabled", false);
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
            $("#content_role_list").show();
            if ($.isFunction(this.options.callback_btnBack)) {
                this.options.callback_btnBack();
            }
        }
    };

    return RoleNew;

});