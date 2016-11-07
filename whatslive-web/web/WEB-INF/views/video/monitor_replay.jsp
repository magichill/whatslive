<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- BEGIN HEAD -->
<head>
    <title>录播监控</title>
    <!-- BEGIN GLOBAL MANDATORY STYLES -->
    <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/jquery.gritter/jquery.gritter.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/bootstrap.modal/bootstrap-modal.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/css/style-metro.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/css/pagination.css"/>

    <style>
        body {
            font-size:13px;
            font-family:\5FAE\8F6F\96C5\9ED1
        }
        body, div, p, ul, li, img, h1, h2, h3, h4, h5, dl, dt {
            padding:0px;
            margin:0px
        }
        ul li {
            list-style:none
        }
        #form_monitor {
            margin:0px;
        }
        .table_wrap {
            position:absolute;
            width:100%;
            height:100%;
        }
        .table_wrap h2{ font-size:22px; border-left:1px solid #999; border-top:1px solid #999; border-right:1px solid #999; padding:3px 9px;}
        .table_wrap .page_div{ height: 35px;border-left:1px solid #999; border-top:1px solid #999; border-right:1px solid #999; border-bottom:1px solid #999; padding:3px 9px;}
        .table_wrap .text_div{ font-size:13px; display: inline-block;float:left;margin: 6px 0px;}
        .table_wrap ul{ padding:13px; height:90%;}
        .table_wrap ul li{ width:25%; height:45%; float:left;}
        .table_wrap ul li table{width:100%; height:100%;}
        .table_wrap ul li table td{ border-left:1px solid #999; border-bottom:1px solid #999; border-right:1px solid #999; padding:0px 0px;}
        .table_wrap ul li table th{ border-left:1px solid #999; border-bottom:1px solid #999; border-right:1px solid #999; padding:3px 8px;}
        .table_wrap ul li table .foot{ height:30px;}
    </style>

    <script src="${ctx }/static/js/jquery-1.10.1.min.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/jquery-migrate-1.2.1.min.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/jquery-ui-1.10.1.custom.min.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/jquery.slimscroll.min.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/jquery.blockui.min.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/jquery.cookie.min.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/jquery.uniform.min.js" type="text/javascript"></script>
    <script type="text/javascript" src="${ctx}/static/lib/jquery.gritter/jquery.gritter.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/require/require.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/bootstrap.modal/bootstrap-modal.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/bootstrap.modal/bootstrap-modalmanager.js"></script>
    <script type="text/javascript" src="${ctx}/static/js/app.js"></script>
    <script type="text/javascript" src="${ctx}/static/js/pagination.js"></script>

    <script type="text/javascript">
        jQuery(function () {

            App.init({ctx: "${ctx}", fileHost: "${fileHost}"});

            require.config({
            baseUrl: "${ctx}/static/",
            paths: {'jquery': 'js/jquery-1.10.1.min'},
            shim: {}
            });

            require(['pagejs/video/monitor'], function (Monitor) {

            var monitor = new Monitor({
              host: "${ctx}"
            });
            monitor.show();
            });
        });
    </script>
</head>
<body>
<form id="form_monitor">
    <input type="hidden" id="videoType" name="videoType" value="${videoType}"/>
    <input type="hidden" id="startNo" name="startNo" value="${startNo}"/>
    <input type="hidden" id="pageNo" name="pageNo" value="${pageNo}"/>
    <input type="hidden" id="total" name="total" value="${total}"/>
    <div class="table_wrap">
        <ul>
            <h2>WhatsLIVE 可视化审核界面</h2>
            <div class="page_div">
                <div class="text_div">当前共有${total}个录播视频，${totalWatchNum}人参与，${reportedVideoCount}个视频被举报</div>
                <div class="pagination" style="text-align:center; padding:1px; display:inline-block; float:right; margin:1px;/">
                    <div class="pagin_go" style="float:right;margin-left: 1px;">共 ${total} 条记录</div>
                    <div pageDiv style="float:right;"></div>
                </div>
            </div>
            <c:forEach var="videoInfo" items="${videoInfoList}" varStatus="status">
                <li>
                    <table cellpadding="0" cellspacing="0">
                        <thead>
                        <tr align="center">
                            <td>录播</td>
                            <td>${videoInfo.watchNum}人&nbsp;&nbsp;&nbsp;${videoInfo.reportNum}次举报</td>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td colspan="2">
                                <object type="application/x-shockwave-flash" data="http://yuntv.letv.com/bcloud.swf" width="100%" height="100%">
                                    <param name="quality" value="high">
                                    <param name="align" value="middle">
                                    <param name="wmode" value="opaque">
                                    <param name="bgcolor" value="#000000">
                                    <param name="allowscriptaccess" value="always">
                                    <param name="allowfullscreen" value="true">
                                    <param name="flashvars" value="uu=${uuid}&vu=${videoInfo.vuid}&autoplay=1&gpcflag=1&autoReplay=0&loadingurl=0">
                                </object>
                            </td>
                        </tr>
                        <tr align="center" class="foot">
                            <td colspan="2">
                                <c:choose>
                                    <c:when test="${videoInfo.isShow == '1'}">
                                        <a href="javascript:void(0);" class="hide" id="op_video_recommend_on_${status.count}" data-oppid="op_video_recommend_off_${status.count}" data-id="${videoInfo.id}">推荐</a>
                                        <a href="javascript:void(0);" class="" id="op_video_recommend_off_${status.count}" data-oppid="op_video_recommend_on_${status.count}" data-id="${videoInfo.id}">取消推荐</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="javascript:void(0);" class="" id="op_video_recommend_on_${status.count}" data-oppid="op_video_recommend_off_${status.count}" data-id="${videoInfo.id}">推荐</a>
                                        <a href="javascript:void(0);" class="hide" id="op_video_recommend_off_${status.count}" data-oppid="op_video_recommend_on_${status.count}" data-id="${videoInfo.id}">取消推荐</a>
                                    </c:otherwise>
                                </c:choose>
                                &nbsp;&nbsp;&nbsp;
                                <a href="javascript:void(0);" id="op_video_verify_down_${status.count}" data-sign="op_video_verify_down" data-id="${videoInfo.id}">下线</a>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </li>
            </c:forEach>
        </ul>
    </div>
    <div class="page-content">
    </div>
</form>
<!-- BEGIN COMFIRM MODAL -->
</body>
</html>