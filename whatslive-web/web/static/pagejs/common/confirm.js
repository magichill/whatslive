define(function () {

    'use strict';

    function Confirm() {
        this.init();
    }

    Confirm.prototype = {
        init: function () {
            this.data = {
                info: "是否继续操作？",
                width: 400,
                maxHeight: 300
            };
            if ($("#modal_confirm").length == 0 && $(".page-content").length > 0) {
                var modal_confirm = '<!-- BEGIN MODAL COMFIRM -->'
                    +'<div id="modal_confirm" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="modal_confirm_title" aria-hidden="true">'
                    +'  <div class="modal-header">'
                    +'      <button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>'
                    +'      <h3 id="modal_confirm_title">确认信息</h3>'
                    +'  </div>'
                    +'  <div class="modal-body">'
                    +'      <p id="modal_confirm_info" style="word-break:break-all"></p>'
                    +'  </div>'
                    +'  <div class="modal-footer">'
                    +'      <button id="btn_modal_confirm" data-dismiss="modal" class="btn blue">确定</button>'
                    +'      <button class="btn" data-dismiss="modal" aria-hidden="true">取消</button>'
                    +'  </div>'
                    +'</div>'
                    +'<!-- END MODAL COMFIRM -->';
                $(".page-content").append(modal_confirm);
            }
        },
        show: function (data, callback) {
            $.extend(this.data, data);
            $("#modal_confirm_info").html(this.data.info);
            $("#modal_confirm").modal({
                width: this.data.width,
                maxHeight: this.data.maxHeight
            });
            $("#btn_modal_confirm").off("click").on("click", function () {
                $("#modal_confirm").modal("hide");
                if ($.isFunction(callback)) {
                    callback();
                }
            });
        }
    }

    return Confirm;
});