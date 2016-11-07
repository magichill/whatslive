<!-- 页面中必须添加如下内容-->
/**
 <div id="modal_album_con" class="modal hide fade" tabindex="-1" role="dialog"
 aria-labelledby="modal_album_con_title" aria-hidden="true">
 <div class="modal-header">
 <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
 <h3 id="modal_album_con_title">视频选择</h3>
 </div>
 <div id="modal_video_con_body" class="modal-body">
 </div>
 <div class="modal-footer">
 <button id="btn_album_close" class="btn" data-dismiss="modal" aria-hidden="true">关闭</button>
 </div>
 </div>
 */

define(function () {
    function VideoList(options) {
        this.init(options);

    }

    VideoList.prototype = {
        init: function (options) {
            this.options = options;
        },
        setOptions: function (options) {
            this.options = $.extend(this.options, options);
        },
        show: function (data) {
            var that = this;
            //data中可以加status参数表示查询视频的状态,默认状态为上线状态视频(1) 如:{status:1}
            //data中可以加tStatus参数表示查询视频的转码状态,默认状态为转码成功状态(2) 如:{tStatus:2}
            //data中可以加batchFlag参数表示是否支持批量添加,batchFlag如果为空表示不支持,为1表示支持
            //data中可以加标签nTagId参数,表示查询不含有该标签的视频。
            //data中可以加标签from参数,表示手机类型1--乐视手机 2--普通手机  如:{from:1}
            that.setOptions(data);
            var url = this.options.host + "/commonChoose/page/videoList";
            var params = {id: this.options.id};
            $("#modal_video_con_body").empty();
            $("#modal_video_con_body").load(url, params, function () {
                that.loadContentFinish();
            });
        },
        loadContentFinish: function () {
            var that = this;
            that.initModalEvents();
            that.initModalTable();
            that.showModal();
        },

        initModalEvents: function () {
            var that = this;
            // 查询组件
            $("#search_tagId").select2({width: "200", allowClear: true});
            // 搜索
            $("#btn_video_search").off("click.btn_video_search").on("click.btn_video_search", function () {
                that.initModalTable();
            });
            // 删除
            $("#table_album_video tbody").off("click.op_video_recommend_off").on("click.op_video_recommend_off", "a[data-sign=op_video_recommend_off]", function () {
                var $op_video = $(this).closest("span[data-sign=op_video]");
                that.opRecommend({
                    id: $op_video.data("id"),
                    op: "0"
                });
            });
            // 添加
            $("#table_album_video tbody").off("click.op_video_recommend_on").on("click.op_video_recommend_on", "a[data-sign=op_video_recommend_on]", function () {
                var $op_video = $(this).closest("span[data-sign=op_video]");
                that.opRecommend({
                    id: $op_video.data("id"),
                    op: "1"
                });
            });

            // 在线播放
            $("#table_album_video tbody").off("click.op_video_view").on("click.op_video_view", "a[data-sign=op_video_view]", function () {
                var $op_video = $(this).closest("span[data-sign=op_video]");
                that.opView({
                    id: $op_video.data("id"),
                    $a: $(this)
                });
            });

            // 关闭
            $("#btn_album_close").off("click.btn_album_close").on("click.btn_album_close", function () {
                that.options.callback_close();
            });

            // 全选与全不选
            $("#table_album_video thead").off("click.videos_select").on("click.videos_select", function () {
                var element = $("#table_album_video tbody :input[type=checkbox]");
                var checked = $(".videos_select").is(":checked");
                if (checked) {
                    element.attr("checked", true);
                } else {
                    element.attr("checked", false);
                }
                $.uniform.update(element);
            });


        },

        refreshModalVideo: function () {
            if (this.modalDataTable) {
                this.modalDataTable.fnDraw();
            } else {
                this.initModalTable();
            }
        },

        initModalTable: function () {
            var that = this;

            //batchFlag=1表示需要批量添加的功能
            var batch = "1";

            if (batch == "1") {
                this.modalDataTable = $("#table_album_video").dataTable({
                    iDisplayLength: 20,
                    bProcessing: true,
                    bServerSide: true,
                    bSort: false,
                    bFilter: false,
                    bAutoWidth: false,
                    bDestroy: true,
                    sDom: "tr<'row-fluid'<'span6'i><'span6'p>>",
                    sAjaxSource: this.options.host + "/commonChoose/list.json",
                    sServerMethod: "POST",
                    aoColumns: [
                        { sTitle: "id",sWidth: "5%", mData: "id"},
                        { sTitle: "userId", mData: "userId", bVisible:false},
                        { sTitle: "封面",sWidth: "5%", mData: null,
                            fnRender: function (obj) {
                                var picture = obj.aData.picture;
                                if (picture == undefined || "" == picture) {
                                    return "<a id='a_content_pic' href=''></a>"
                                }
                                return "<a id='a_content_pic' href='" + picture + "' target='_blank'><img style='width: 30px; height: 30px;' id='img_content_pic' src='" + picture + "' alt=''/></a>"
                            }
                        },
                        { sTitle: "类型", sWidth: "8%", mData: null,
                            fnRender: function (obj) {
                                switch (obj.aData.ptype) {
                                    case 1:
                                        return '直播';
                                    case 3:
                                        return '录播';
                                    default:
                                        return '未知';
                                }
                            }
                        },
                        { sTitle: "标题", sWidth: "20%", mData: "pname"},
                        { sTitle: "发起人", sWidth: "12%", mData: "nickName"},
                        { sTitle: "发起时间",sWidth: "8%", mData: "createTimeStr"},
                        {
                            sTitle: "操作", sWidth: "20%", mData: null,
                            fnRender: function (obj) {
                                var color_recommend = "red";
                                var action_recommend = "op_video_recommend_off";
                                var recommend_on_off = "删除";
                                if (obj.aData.status != null && obj.aData.status == 0) {
                                    color_recommend = "yellow";
                                    action_recommend = "op_video_recommend_on";
                                    recommend_on_off = "添加";
                                }
                                var ops = [
                                    {color: color_recommend, sign: action_recommend, id: "", name: "", btnName: recommend_on_off},
                                    {color: "green", sign: "op_video_view", id: "", name: "", btnName: "点击观看"},

                                ];
                                return _.template($("#temp_op").html(), {
                                    sign: "op_video",
                                    id: obj.aData.id,
                                    name: obj.aData.userId,
                                    ops: ops
                                });

                            }
                        }
                    ],
                    fnServerParams: function (aoData) {
                        aoData = $.merge(aoData, that.getModalQueryParams());
                    },
                    fnDrawCallback: function (oSettings) {
                        App.initUniform();
                    }
                });
            } else {

            }
        },
        getModalQueryParams: function () {
            var search_videoName = $("#form_video_search input[name=search_videoName]").val();
            return [
                {name: "search_videoTitle", value: search_videoName},
                {name: "search_videoStatus", value: 1},
                {name: "search_type", value: this.options.type},
                {name: "search_id", value: this.options._id}
            ];
        },
        refreshTable: function () {
            if (this.dataTable) {
                this.dataTable.fnDraw();
            } else {
                this.initModalTable();
            }
        },
        showModal: function () {
            var that = this;
            $("#modal_album_con").modal({
                width: 800,
                maxHeight: 500
            });
            //增加关闭回调函数
            $('#modal_album_con').on('hide.bs.modal', function () {
                that.options.callback_refresh();
            })
        },
        hideModal: function () {
            $("#modal_album_con").modal("hide");
        },
        opRecommend: function (data) {
            var that = this;
            $.ajax({
                url: that.options.host + "/commonChoose/recommend",
                type: "post",
                data: {type:this.options.type, id:this.options._id, programId: data.id, op: data.op},
                success: function (data) {
                    if (data.rsCode == "A00000") {
                        $.gritter.add({title: "提示信息：", text: "操作成功！", time: 1000});
                        that.refreshTable();
                    } else {
                        $.gritter.add({title: "提示信息：", text: data.rsMsg, time: 2000});
                    }
                }
            });
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
        getSelect: function () {
            var ids = "";
            $("#table_album_video tbody").find("input[type=checkbox]:checked").each(function () {
                ids += $(this).val() + ",";
            });
            return ids.length > 0 ? ids.substr(0, ids.length - 1) : ids;
        }
    }
    return VideoList;
});
