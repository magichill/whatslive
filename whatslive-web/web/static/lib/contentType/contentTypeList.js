/**
 * Created by wangruifeng on 14-4-17.
 */
define(["../../pagejs/common/confirm","../../pagejs/video/live"],function(Confirm,Video) {

    function ContentType(options) {
        this.init(options);
    }
    ContentType.prototype = {

        _element:null,

        init: function (options) {
            this.options = options;
            this.confirm=new Confirm();
            this.video=new Video({host:this.options.host});//视频
        },
        setOptions: function (options) {
            this.options = $.extend(this.options, options);
        },
        show: function () {
            $("div[data-sign=content]").hide();
            this.initEvents();
        },
        initEvents: function () {
            var that = this;
            // 搜索
            $("#btn_video_search").off("click.search").on("click.search", function () {
                that.initTable();
            });

            $("#btn_typeId_selected").click(function(){
                that.getType_List();
            })

            $("#btn_add_back").click(function(){
                that.btnBack();
            })
            // 审核
            $("#content_video_list tbody").off("click.op_videoJoinTag_del").on("click.op_videoJoinTag_del", "a[data-sign=op_videoJoinTag_del]", function () {
                var $op_video = $(this).closest("span[data-sign=op_ContentType]");
                that.opVideJoinTagdel({
                    id: $op_video.data("id")
                });
            });
        },
        initTagAddVidoEvents: function () {
            var that = this;
            // 搜索
            $("#btn_add_video_search").off("click.search").on("click.search", function () {
                that.initTagAddVideoTable({ex:1});
            });

            $("#btn_add_back").click(function(){
                that.btnBack();
            })

            // 选择
            $("#content_add_video_list tbody").off("click.op_videoTag_selected").on("click.op_videoTag_selected", "a[data-sign=op_videoTag_selected]", function () {
                var $op_video = $(this).closest("span[data-sign=op_tagSelViedo]");
                that.opNew({
                    id: $op_video.data("id")
                });

            });

        },
        getType_List:function(){
            var that = this;
            var type = $("#contentType").val();
            switch(type)
            {
                case '1':
                    break;
                case '2':
/*
                    that.initTable();
*/
                    that.showModal()
                    break;
                default:
            }
        },
        initTable: function () {
            var that = this;
            this.dataTable = $("#table_add").dataTable({
                    iDisplayLength: 20,
                    bProcessing: true,
                    bServerSide: true,
                    bSort: false,
                    bFilter: false,
                    bAutoWidth: false,
                    bDestroy: true,
                    sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                    sAjaxSource: that.options.host + "/video/list.json",
                    sServerMethod: "POST",
                    aoColumns: [
                       { sTitle: "视频id", mData: "_id"},
                       { sTitle: "视频描述", mData: null,
                            fnRender: function (obj) {
                                return obj.aData.vDesc.length > 5 ? obj.aData.vDesc.substr(0, 5) + "..." : obj.aData.vDesc;
                            }
                        },
                        { sTitle: "视频类型", mData: null,
                            fnRender: function (obj) {
                                return obj.aData.vType == 1 ? '<span class="label label-info">长视频</span>' : '<span class="label label-important">短视频</span>'
                            }
                        },
                        { sTitle: "视频状态", mData: null,
                            fnRender: function (obj) {
                                switch (obj.aData.vType) {
                                    case 1:
                                        return '<span class="label label-info">上线(审核通过)</span>';
                                    case 2:
                                        return '<span class="label label-info">下线(审核不通过)</span>';
                                    case 0:
                                        return '<span class="label label-info">待审</span>';
                                    case -1:
                                        return '<span class="label label-info">视频初始化状态</span>';
                                }
                            }
                        },
                        { sTitle: "转码状态", mData: null,
                            fnRender: function (obj) {
                                switch (obj.aData.tStatus) {
                                    case -3:
                                        return '<span class="label label-info">转码失败</span>';
                                    case -2:
                                        return '<span class="label label-info">上传媒资失败</span>';
                                    case -1:
                                        return '<span class="label label-info">未上传</span>';
                                    case 0:
                                        return '<span class="label label-info">上传成功发起转码请求</span>';
                                    case 1:
                                        return '<span class="label label-info">上传媒资成功</span>';
                                    case 2:
                                        return '<span class="label label-info">转码成功</span>';
                                }
                            }
                        },
                        { sTitle: "权重", mData: "hotWeight"},
                        { sTitle: "赞数", mData: "goodNum"},
                        { sTitle: "累计赞数", mData: "totalGoodNum"},
                        { sTitle: "评论数", mData: "commentNum"},
                        { sTitle: "播放次数", mData: "playNum"},
                        { sTitle: "上传用户姓名", mData: null,
                            fnRender: function (obj) {
                                return obj.aData.userName + "(" + obj.aData.uid + ")"
                            }
                        },
                        { sTitle: "上传时间", mData: "createDateStr"},
                        { sTitle: "操作", mData: null,
                            fnRender: function (obj) {
                                var color = "red";
                                var action = "启用";
                                if (obj.aData.vType == 1) {
                                    color = "navy";
                                    action = "停用"
                                }
                                return _.template($("#temp_op").html(), {
                                    sign: "op_video",
                                    id: obj.aData._id,
                                    name: null,
                                    ops: [
                                        {color: "blue", sign: "op_video_edit", id: "", name: "", btnName: "修改"},
                                        {color: "blue", sign: "op_video_view", id: "", name: "", btnName: "预览"},
                                        {color: color, sign: "op_video_verify", id: "", name: "", btnName: action}
                                    ]
                                });
                            }
                        }
                    ],
                    fnServerParams: function (aoData) {
                        aoData = $.merge(aoData,[]);
                    }
                }
            )
            ;
        },
        btnBack: function () {//通用
            $("#modal_contentType").modal("hide");
        },
        btnManageBack: function () {
            $("div[data-sign=content]").hide();
            $("#content_videoTag_list").show();
            if ($.isFunction(this.options.callback_btnBack)) {
                this.options.callback_btnBack();
            }
        },
        loadContentFinish: function () {
            // 初始化事件
            this.initTagAddVidoEvents()
            this.initTagAddVideoTable({ex:1});
            this.showModal();
        },

        showModal:function(){
            $("#modal_contentType").modal({
                width: 1200,
                maxHeight: 1200
            });
        },

        getQueryParams: function () {
/*
            var search_videoId = $("#form_video_search input[name=search_videoId]").val();
            var search_videoName = $("#form_video_search select[name=search_videoName]").val();
            var tid = this.options.tid;
            return [
                {name: "search_videoId", value: search_videoId},
                {name: "search_videoName", value: search_videoName},
                {name:"tid",value:tid}
            ];
*/
        },
        getVideoQueryParams: function (data) {
            var search_videoId = $("#form_video_search input[name=search_add_videoId]").val();
            var search_videoName = $("#form_video_search select[name=search_add_videoName]").val();
            var tid = this.options.tid;
            var ex = data.ex;
            return [
                {name: "search_videoId", value: search_videoId},
                {name: "search_videoName", value: search_videoName},
                {name:"tid",value:tid},
                {name:"exists",value:ex}
            ];
        },

        initTagAddVideoTable: function (data) {
            var that = this;
            this.dataTable = $("#table_add_video_list").dataTable({
                    iDisplayLength: 20,
                    bProcessing: true,
                    bServerSide: true,
                    bSort: false,
                    bFilter: false,
                    bAutoWidth: false,
                    bDestroy: true,
                    sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                    sAjaxSource: that.options.host + "/video/list.json",
                    sServerMethod: "POST",
                    aoColumns: [
                        { sTitle: "视频id", mData: "_id"},
                        { sTitle: "标题", mData: "vTitle"},
                        { sTitle: "视频类型", mData: null,
                            fnRender: function (obj) {
                                return obj.aData.vType == 1 ? '<span class="label label-info">长视频</span>' : '<span class="label label-important">短视频</span>'
                            }
                        },
                        { sTitle: "视频状态", mData: null,
                            fnRender: function (obj) {
                                switch (obj.aData.vType) {
                                    case 1:
                                        return '<span class="label label-info">上线(审核通过)</span>';
                                    case 2:
                                        return '<span class="label label-info">下线(审核不通过)</span>';
                                    case 0:
                                        return '<span class="label label-info">待审</span>';
                                }
                            }
                        },
                        { sTitle: "权重", mData: "hotWeight"},
                        { sTitle: "赞数", mData: "goodNum"},
                        { sTitle: "累计赞数", mData: "totalGoodNum"},
                        { sTitle: "评论数", mData: "commentNum"},
                        { sTitle: "播放次数", mData: "playNum"},
                        { sTitle: "上传用户id", mData: "uid"},
                        { sTitle: "上传时间", mData: "CreateDateStr"},
                        { sTitle: "操作", mData: null,
                            fnRender: function (obj) {
                                return _.template($("#temp_op").html(), {
                                    sign: "op_tagSelViedo",
                                    id: obj.aData._id,
                                    name: obj.aData.videoTagName,
                                    ops: [
                                        {color: "green", sign: "op_videoTag_selected", id: "", name: "", btnName: "选择"}
                                    ]
                                });
                            }
                        }
                    ],
                    fnServerParams: function (aoData) {
                        aoData = $.merge(aoData, that.getVideoQueryParams(data));
                    }
                }
            )
            ;
        },
        refreshTable: function () {
            if (this.dataTable) {
                this.dataTable.fnDraw();
            } else {
                this.initTable();
            }
        },

        refreshModalTable: function () {
            if (this.dataTable) {
                this.dataTable.fnDraw();
            } else {
                this.initTagAddVideoTable();
            }
        },

        opNew: function (data) {
            var that = this;
                $.ajax({
                    url: that.options.host + "/videoTag/tagJoinVideo",
                    type: "post",
                    data: {tid: that.options.tid,vid:data.id},
                    success: function (data) {
                        if (data.rsCode == "A00000") {
                            $.gritter.add({title: "提示信息：", text: "操作成功！", time: 1000});
                            that.initTagAddVideoTable({ex:1});
                        } else {
                            $.gritter.add({title: "提示信息：", text: data.rsMsg, time: 2000});
                        }
                    }
                });
        },

        opVideJoinTagdel: function (data) {
            var that = this;
            this.confirm.show("删除",function () {
                $.ajax({
                    url: that.options.host + "/videoTag/tagJoinVideoDel",
                    type: "post",
                    data: {tid: that.options.tid, vid: data.id},
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
    }

    return ContentType;

});