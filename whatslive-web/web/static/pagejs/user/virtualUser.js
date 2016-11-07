define(["./virtualUser_edit","../common/confirm"], function (UserEdit,Confirm) {

        function User(options) {
            this.init(options);
        }

        User.prototype = {
            init: function (options) {
                this.options = options;
                this.user_edit = new UserEdit({host: this.options.host});
                this.confirm = new Confirm({host: this.options.host});
                //this.user_new = new UserNew({host: this.options.host});
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
                // 搜索
                $("#btn_user_search").off("click.search").on("click.search", function () {
                    that.initTable();
                });
                // 新增
                $("#btn_user_new").off("click.search").on("click.search", function () {
                    that.opEdit({
                        id: null
                    });
                });
                // 批量添加
                $("#btn_user_addBatch").off("click.addBatch").on("click.addBatch", function () {
                    that.batchAdd();
                });
                // 批量更新
                $("#btn_user_updateBatch").off("click.search").on("click.search", function () {
                    that.batchUpdate();
                });
                // 打开机器人
                $("#btn_user_openRobot").off("click.openRobot").on("click.openRobot", function () {
                    that.opRobot({
                        op: "1"
                    });
                });

                // 关闭机器人
                $("#btn_user_closeRobot").off("click.closeRobot").on("click.closeRobot", function () {
                    that.opRobot({
                        op: "0"
                    });
                });
                // 打开机器人评论
                $("#btn_user_openRobotComment").off("click.openRobot").on("click.openRobot", function () {
                    that.opRobotComment({
                        op: "1"
                    });
                });

                // 关闭机器人评论
                $("#btn_user_closeRobotComment").off("click.closeRobot").on("click.closeRobot", function () {
                    that.opRobotComment({
                        op: "0"
                    });
                });

                // 修改
                $("#table_user_list tbody").off("click.op_user_edit").on("click.op_user_edit", "a[data-sign=op_user_edit]", function () {
                    var $op_user = $(this).closest("span[data-sign=op_user]");
                    that.opEdit({
                        id: $op_user.data("id")
                    });
                });

                // 删除
                $("#table_user_list tbody").off("click.op_user_delete").on("click.op_user_delete", "a[data-sign=op_user_delete]", function () {
                    var $op_user = $(this).closest("span[data-sign=op_user]");
                    that.opDelete({
                        id: $op_user.data("id"),
                    });
                });

            },
            getQueryParams: function () {
                var search_nickName = $("#form_user_search input[name=search_nickName]").val();
                return [
                    {name: "search_nickName", value: search_nickName}
                ];
            },
            initTable: function () {
                var that = this;
                that.queryUserNumber();
                this.dataTable = $("#table_user_list").dataTable({
                        iDisplayLength: 20,
                        bProcessing: true,
                        bServerSide: true,
                        bSort: false,
                        bFilter: false,
                        bAutoWidth: false,
                        bDestroy: true,
                        sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                        sAjaxSource: this.options.host + "/user/virtualUser/list.json",
                        sServerMethod: "POST",
                        aoColumns: [
                            { sTitle: "ID", mData: "userId"},
                            { sTitle: "头像", mData: null,
                                fnRender: function (obj) {
                                    var picture = obj.aData.picture;
                                    if (picture == undefined || "" == picture) {
                                        return "<a id='a_content_pic' href=''></a>"
                                    }
                                    var pictureList = picture.split(",");
                                    var len = pictureList.length <= 0 ? 0 : pictureList.length - 1;
                                    return "<a id='a_content_pic' href='" + pictureList[len] + "' target='_blank'><img style='width: 30px; height: 30px;' id='img_content_pic' src='" + pictureList[len] + "' alt=''/></a>"
                                }
                            },
                            { sTitle: "昵称", mData: "nickName"},
                            { sTitle: "身份", mData: null,
                                fnRender: function (obj) {
                                    switch (obj.aData.role) {
                                        case 0:
                                            return '普通';
                                        case 1:
                                            return '认证';
                                        case 2:
                                            return '管理员';
                                        default:
                                            return '';
                                    }
                                }
                            },
                            { sTitle: "自我介绍", sWidth: "50%", mData: "introduce"},
                            {
                                sTitle: "操作", mData: null,
                                fnRender: function (obj) {
                                    var ops = [
                                        {color: "blue", sign: "op_user_edit", id: "", name: "", btnName: "编辑"},
                                        {color: "red", sign: "op_user_delete", id: "", name: "", btnName: "删除"}

                                    ];
                                    //2表示是系统管理员，不允许操作
                                    if(obj.aData.role == 2){
                                        ops = [];
                                    }
                                    return _.template($("#temp_op").html(), {
                                        sign: "op_user",
                                        id: obj.aData.userId,
                                        name: obj.aData.nickName,
                                        ops: ops
                                    });
                                }
                            }
                        ],
                        fnServerParams: function (aoData) {
                            aoData = $.merge(aoData, that.getQueryParams());
                        },
                        fnDrawCallback: function (oSettings) {
                            App.initUniform();
                        }
                    }
                );
            },
            queryUserNumber: function () {
                var that = this;
                $.ajax({
                    url: that.options.host + "/user/virtualUser/countUser",
                    type: "post",
                    data: null,
                    success: function (data) {
                        var data = $.parseJSON(data);
                        var showString = "当前共"+data.allUser+"个虚拟用户";
                        $("#userNum").html(showString);
                    }
                });
            },
            refreshTable: function () {
                if (this.dataTable) {
                    this.queryUserNumber();
                    this.dataTable.fnDraw();
                } else {
                    this.initTable();
                }
            },
            opEdit: function (data) {
                var that = this;
                this.user_edit.setOptions({
                    id: data.id,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.user_edit.show();
            },
            opDelete: function (data) {
                var that = this;
                that.confirm.show("d", function () {
                    $.ajax({
                        url: that.options.host + "/user/virtualUser/delete",
                        type: "post",
                        data: {id: data.id},
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
            batchAdd: function () {
                var that = this;
                $.ajax({
                    url: that.options.host + "/user/virtualUser/saveBatchUser",
                    type: "post",
                    data: null,
                    success: function (data) {

                    }
                });
            },
            batchUpdate: function () {
                var that = this;
                $.ajax({
                    url: that.options.host + "/user/virtualUser/batchUpdateUser",
                    type: "post",
                    data: null,
                    success: function (data) {

                    }
                });
            },
            opRobot: function (data) {
                var that = this;
                that.confirm.show("d", function () {
                    $.ajax({
                        url: that.options.host + "/user/virtualUser/opRobot",
                        type: "post",
                        data: {op: data.op},
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
            opRobotComment: function (data) {
                var that = this;
                that.confirm.show("d", function () {
                    $.ajax({
                        url: that.options.host + "/user/virtualUser/opRobotComment",
                        type: "post",
                        data: {op: data.op},
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
            }
        };
        return User;
    }
);