define(["./realUser_edit", "./realUser_device_list","../common/confirm"], function (UserEdit, UserDeviceList, Confirm) {

        function User(options) {
            this.init(options);
        }

        User.prototype = {
            init: function (options) {
                this.options = options;
                this.user_edit = new UserEdit({host: this.options.host});
                this.user_device_list = new UserDeviceList({host: this.options.host});
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
                // 搜索
                $("#btn_user_search").off("click.search").on("click.search", function () {
                    that.initTable();
                });
                // 修改
                $("#table_user_list tbody").off("click.op_user_edit").on("click.op_user_edit", "a[data-sign=op_user_edit]", function () {
                    var $op_user = $(this).closest("span[data-sign=op_user]");
                    that.opEdit({
                        id: $op_user.data("id")
                    });
                });
                // 查看设备
                $("#table_user_list tbody").off("click.op_user_device_list").on("click.op_user_device_list", "a[data-sign=op_user_device_list]", function () {
                    var $op_user = $(this).closest("span[data-sign=op_user]");
                    that.opDeviceList({
                        id: $op_user.data("id")
                    });
                });
                // 全选与全不选
                $("#table_user_list thead").off("change.user_select").on("change.user_select", function () {
                    var element = $("#table_user_list tbody :input[type=checkbox]");
                    var checked = $(".user_select").is(":checked");
                    if (checked) {
                        element.attr("checked", true);
                    } else {
                        element.attr("checked", false);
                    }
                    $.uniform.update(element);
                });
                // 屏蔽
                $("#table_user_list tbody").off("click.op_user_verify_down").on("click.op_user_verify_down", "a[data-sign=op_user_verify_down]", function () {
                    var $op_user = $(this).closest("span[data-sign=op_user]");
                    that.opEffect({
                        id: $op_user.data("id"),
                        op: "0"
                    });
                });
                // 恢复
                $("#table_user_list tbody").off("click.op_user_verify_up").on("click.op_user_verify_up", "a[data-sign=op_user_verify_up]", function () {
                    var $op_user = $(this).closest("span[data-sign=op_user]");
                    that.opEffect({
                        id: $op_user.data("id"),
                        op: "1"
                    });
                });
            },
            getQueryParams: function () {
                var search_nickName = $("#form_user_search input[name=search_nickName]").val();
                var search_userRole = $("#form_user_search select[name=search_userRole]").val();
                var search_userLevel = $("#form_user_search select[name=search_userLevel]").val();
                var search_broadCastNum = $("#form_user_search select[name=search_broadCastNum]").val();
                return [
                    {name: "search_nickName", value: search_nickName},
                    {name: "search_userRole", value: search_userRole},
                    {name: "search_userLevel", value: search_userLevel},
                    {name: "search_broadCastNum", value: search_broadCastNum}
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
                        sAjaxSource: this.options.host + "/user/realUser/list.json",
                        sServerMethod: "POST",
                        aoColumns: [
                            { sTitle: "<input id='allUser' class='user_select' type='checkbox' name='selUser'>", mData: null,
                                fnRender: function (obj) {
                                    return "<input type='checkbox' name='userId' value='" + obj.aData.userId + "'>";
                                }
                            },
                            { sTitle: "ID", mData: "userId"},
                            { sTitle: "头像", mData: null,
                                fnRender: function (obj) {
                                    var picture = obj.aData.picture;
                                    if (picture == undefined || "" == picture) {
                                        return "<a id='a_content_pic' href=''></a>"
                                    }
                                    var pictureList = picture.split(",");
                                    var len = pictureList.length <= 0 ? 0 : pictureList.length - 1;
                                    return "<a id='a_content_pic' href='" + pictureList[len]
                                        + "' target='_blank'><img style='width: 30px; height: 30px;' id='img_content_pic' src='"
                                        + pictureList[len] + "' alt=''/></a>"
                                }
                            },
                            { sTitle: "昵称", sWidth: "20%", mData: "nickName"},
                            { sTitle: "身份", mData: null,
                                fnRender: function (obj) {
                                    switch (obj.aData.role) {
                                        case 0:
                                            return '普通';
                                        case 1:
                                            return '认证';
                                        default:
                                            return '';
                                    }
                                }
                            },
                            { sTitle: "等级", mData: null,
                                fnRender: function (obj) {
                                    if(obj.aData.level==null){
                                        return '0';
                                    }else{
                                        return obj.aData.level;
                                    }
                                }
                            },
                            { sTitle: "系统", mData: null,
                                fnRender: function (obj) {
                                    /*(0=>'IOS',1=>'android');
                                     */
                                    switch (obj.aData.lastOS) {
                                        case 0:
                                            return 'IOS';
                                        case 1:
                                            return '安卓';
                                        default:
                                            return '未知';
                                    }
                                }
                            },
                            { sTitle: "绑定第三方", mData: null,
                                fnRender: function (obj) {
                                    switch (obj.aData.userType) {
                                        case 1:
                                            return 'facebook';
                                        case 2:
                                            return 'twitter';
                                        case 4:
                                            return '微博';
                                        case 5:
                                            return 'QQ';
                                        case 6:
                                            return '微信';
                                        default:
                                            return '未知';
                                    }
                                }
                            },
                            { sTitle: "最近登录", mData: "lastLoginTimeStr"},
                            { sTitle: "直播经历", mData: "broadCastNum"},
                            {
                                sTitle: "操作", mData: null,
                                fnRender: function (obj) {
                                    var color = "navy";
                                    var action = "屏蔽";
                                    var up_down = "op_user_verify_down";
                                    if (obj.aData.userStatus == 0) {
                                        color = "red";
                                        action = "恢复";
                                        up_down = "op_user_verify_up";
                                    }
                                    return _.template($("#temp_op").html(), {
                                        sign: "op_user",
                                        id: obj.aData.userId,
                                        name: obj.aData.nickName,
                                        ops: [
                                            {color: "blue", sign: "op_user_edit", id: "", name: "", btnName: "详细信息"},
                                            {color: "green", sign: "op_user_device_list", id: "", name: "", btnName: "查看设备"},
                                            {color: color, sign: up_down, id: "", name: "", btnName: action},
                                        ]
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
                    url: that.options.host + "/user/realUser/countUser",
                    type: "post",
                    data: null,
                    success: function (data) {
                        var data = $.parseJSON(data);
                        var showString = "当前注册用户"+data.allUser+"人,"+data.allBroadcastedUser+"人发起过直播";
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
            opEffect: function (data) {
                var that = this;
                that.confirm.show("d", function () {
                    $.ajax({
                        url: that.options.host + "/user/realUser/effect",
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
            opDeviceList: function (data) {
                var that = this;
                this.user_device_list.setOptions({
                    id: data.id,
                    callback_btnBack: function () {
                        that.show();
                    }
                });
                this.user_device_list.show();
            }
        };
        return User;
    }
);