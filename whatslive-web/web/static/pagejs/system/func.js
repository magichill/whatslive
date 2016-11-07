/**
 * Created by wangruifeng on 14-4-23.
 */
define(["./func_new"], function (FuncNew) {

        function Func(options) {
            this.init(options);
        }

        Func.prototype = {
            init: function (options) {
                this.options = options;
                this.func_new = new FuncNew({host: this.options.host});
            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_func_list").show();
                this.initEvents();
                this.refreshTable();
            },
            initEvents: function () {
                var that = this;
                // 搜索
                $("#btn_func_search").off("click.search").on("click.search", function () {
                    that.initTable();
                });
                // 新增
                $("#btn_func_new").off("click.func_new").on("click.func_new", function () {
                    that.opNew();
                });
                $("#table_func_list tbody").off("click.op_func_edit").on("click.op_func_edit", "a[data-sign=op_func_edit]", function () {
                    var $op_func= $(this).closest("span[data-sign=op_func]");
                    that.opEdit({
                        id: $op_func.data("id")
                    });
                });
                $("#table_func_list tbody").off("click.op_func_child").on("click.op_func_child", "a[data-sign=op_func_child]", function () {
                    var $op_func= $(this).closest("span[data-sign=op_func]");
                    that.opChild({
                        id: $op_func.data("id")
                    });
                });

            },
            getQueryParams: function () {
                var search_funcName = $("#form_func_search input[name=search_funcName]").val();
                return [
                    {name: "search_funcName", value: search_funcName}
                ];
            },
            initTable: function () {
                var that = this;
                this.dataTable = $("#table_func_list").dataTable({
//                    iDisplayLength: 20,
                    bPaginate :false,
                    bProcessing: true,
                    bServerSide: true,
                    bInfo:false,
                    bSort: false,
                    bFilter: false,
                    bAutoWidth: false,
                    bDestroy: true,
                    sDom: "tr",
                    sAjaxSource: this.options.host + "/system/func/list.json",
                    sServerMethod: "POST",
                    aoColumns: [
                        { sTitle: "ID", mData: "id"},
                        { sTitle: "资源名称", mData: "funcName"},
                        { sTitle: "父节点ID", mData: "parentFuncId"},
                        { sTitle: "是否叶子节点", mData: null,
                              fnRender : function(obj){
                                  return obj.aData.isLeaf ==1 ? '<span class="label label-info" >叶子</span>':
                                      '<span class="label label-important">非</span>';
                              }
                        },
                        { sTitle: "资源URL", mData: "actionUrl"},
                        { sTitle: "图标标识", mData: "iconUrl"},
                        { sTitle: "排序号", mData: "funcOrder"},
                        { sTitle: "操作", mData: null,
                            fnRender: function (obj) {

                                var isLeaf = obj.aData.isLeaf;
                                if(isLeaf){
                                    return _.template($("#temp_op").html(), {
                                        sign: "op_func",
                                        id: obj.aData.id,
                                        name: obj.aData.loginName,
                                        ops: [
                                            {color: "blue", sign: "op_func_edit", id: "", name: "", btnName: "修改"}
                                        ]
                                    });
                                }else{
                                    return _.template($("#temp_op").html(), {
                                        sign: "op_func",
                                        id: obj.aData.id,
                                        name: obj.aData.loginName,
                                        ops: [
                                            {color: "blue", sign: "op_func_edit", id: "", name: "", btnName: "修改"},
                                            {color: "blue", sign: "op_func_child", id: "", name: "", btnName: "添加子资源"}
                                        ]
                                    });
                                }
                            }
                        }
                    ],
                    fnServerParams: function (aoData) {
                        aoData = $.merge(aoData, that.getQueryParams());
                    }
                });
            },
            refreshTable: function () {
                if (this.dataTable) {
                    this.dataTable.fnDraw();
                } else {
                    this.initTable();
                }
            },
            opNew: function () {
                var that = this;
                this.func_new.setOptions({
                    opt:'new',
                    callback_btnSave: function() {
                        that.show();
                    }
                });
                this.func_new.show();
            },
            opEdit: function (data) {
                var that = this;
                this.func_new.setOptions({
                    opt:'edit',
                    id:data.id,
                    callback_btnSave: function() {
                        that.show();
                    }
                });
                this.func_new.show();
            },
            opChild: function (data) {
                var that = this;
                this.func_new.setOptions({
                    opt:'child',
                    id:data.id,
                    callback_btnSave: function() {
                        that.show();
                    }
                });
                this.func_new.show();
            }
        };

        return Func;
    });