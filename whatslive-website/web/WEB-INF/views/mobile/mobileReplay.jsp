<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/mobileHeader.jsp" %>

    <title>
        <c:choose>
            <c:when test="${videoInfo.status == '1'}">
                <fmt:message key="mobile.replay.title"/>-${videoInfo.title}
            </c:when>
            <c:otherwise>
                <fmt:message key="mobile.replay.title"/>
            </c:otherwise>
        </c:choose>
    </title>

    <script>
        var __INFO__ = {
            pageid : 'm_play'
        }
    </script>
    <%--<script src="http://js.letvcdn.com/lc03_js/201509/15/09/41/3rd/abc.js" type="text/javascript"></script>--%>

    <script type="text/javascript">

        $(function () {

            $(".little_img").click(

                function() {

                    var selectedDivId = $(this).attr("id");
                    var index = selectedDivId.substr(5);
                    var selectedId = $("#id_list_" + index).val();
                    var id = $("#id").val();
                    if (selectedId != id) {

                        $("#id").val(selectedId);
                        var form = $("#form_mobile");
                        form.method = "post";
                        form.action = "${ctx }/mobile/mobileLive";
                        form.submit();
                    }
                });

            $("#playerCoverImg").click(

                function() {

                    $("#playerCoverImg").attr("class", "player h5_wlive_hide");
                    $("#player").attr("class", "player");
                    $("video").get(0).play();
                });

//            var video = $("video").get(0);
//            //1.加载至可运行状态（当浏览器可以开始播放该音视频时产生该事件）
//            video.addEventListener("canplay", function () {
//                alert("canplay");
//            }, false);
//            //2.视频流在缓冲、连接中状态（当视频因缓冲下一帧而停止时产生该事件）
//            video.addEventListener("waiting", function () {
//                alert("waiting");
//            }, false);
//            //3.缓冲结束至可播放的状态（当媒体从因缓冲而引起的暂停和停止恢复到播放时产生该事件）
//            video.addEventListener("playing", function () {
//                alert("playing");
//            }, false);
//            //4.视频结束状态（当前播放列表结束时产生该事件）
//            video.addEventListener("ended", function () {
//                alert("ended");
//            }, false);
//            //5.异常状态（当加载媒体发生错误时产生该事件）
//            video.addEventListener("error", function () {
//                alert("error");
//            }, false);
        });
    </script>
</head>
<body>
<form id="form_mobile">
    <div class="h5_wlive_wrap">
        <!--头部 start-->
        <div class="co_top">
            <div class="co_top_box">
                <div class="logo">
                    <a href="${ctx }/mobile"><img src="<fmt:message key='logo.first'/>"></a>
                    <i><img src="<fmt:message key='logo.third'/>"></i>
                </div>
                <div class="no_download">
                    <a class="app_btn03" href="${ctx }/mobile"><img src="<fmt:message key='app.download'/>"></a>
                </div>
            </div>
        </div>
        <!--头部 end-->
        <!--播放部分 start-->
        <div class="play_box">
            <div class="play_bg"><!--模糊效果class：play_bg_blur-->
            </div>
            <!--videotype 1:直播 2:预约 3:录播-->
            <!--status 0:直播已结束录播为完成 不可用 1:正常-->
            <div class="co_box">
                <input type="hidden" id="id" name="id" value="${videoInfo.id}"/>
                <input type="hidden" id="programType" value="${videoInfo.programType}"/>
                <input type="hidden" id="status" value="${videoInfo.status}"/>
                <input type="hidden" id="uuid" value="${videoInfo.uuid}"/>
                <input type="hidden" id="vuid" value="${videoInfo.vuid}"/>
                <input type="hidden" id="language" value="${language}"/>
                <input type="hidden" id="coverPicture" value="${videoInfo.coverPicture}"/>
                <input type="hidden" id="title" value="${videoInfo.title}"/>
                <input type="hidden" id="shareUrl" value="${videoInfo.shareUrl}"/>
                <input type="hidden" id="appid" value="${videoInfo.appid}"/>
                <input type="hidden" id="signTimestamp" value="${videoInfo.signTimestamp}"/>
                <input type="hidden" id="signNonceStr" value="${videoInfo.signNonceStr}"/>
                <input type="hidden" id="signature" value="${videoInfo.signature}"/>
                <input type="hidden" id="debugFlg" value="${videoInfo.debugFlg}"/>
                <c:choose>
                    <c:when test="${language == 'zh'}">
                        <input type="hidden" id="describe" value="我在@乐嗨直播 快来一起围观！@${videoInfo.nickName}: ${videoInfo.title}"/>
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" id="describe" value="Watched on #WhatsLIVE by ${videoInfo.nickName}: ${videoInfo.title}"/>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${videoInfo.status == '1'}">
                        <!--播放器 start-->
                        <div id="playerCoverImg" class="play_icon" style="height:640px; background:url(${videoInfo.coverPicture}) center no-repeat; background-size:100% 100%;"><a></a></div><!--播放小图标-->
                        <div id="player" class="player h5_wlive_hide" style="height:640px;">
                            <script type="text/javascript">
                                var uuId = $("#uuid").val();
                                var vuId = $("#vuid").val();
                                var letvcloud_player_conf = {
                                    "uu":uuId,
                                    "vu":vuId,
                                    "auto_play":1,
                                    "dbd":'LETV',
                                    "callback":'TTVideoInit',
                                    "width":"100%",
                                    "height":"100%",
                                    "check_code":""
                                };
                                function TTVideoInit(dom){
                                    dom.addEventListener("canplay", function () {
//                                        alert("canplay");
                                    }, false);
                                    dom.addEventListener("waiting", function () {
//                                        alert("waiting");
                                    }, false);
                                    dom.addEventListener("playing", function () {
//                                        alert("playing");
                                    }, false);
                                    dom.addEventListener("ended", function () {
//                                        alert("ended");
                                        this.play();
                                    }, false);
                                    dom.addEventListener("error", function () {
//                                        alert("error.code");
                                    }, false);
                                }
                            </script>
                            <script type="text/javascript" src="http://yuntv.letv.com/bcloud.js"></script>
                        </div><!--循环播放-->
                        <!--播放器 end-->
                    </c:when>
                    <c:otherwise>
                        <div class="play_end" style="height:640px; background:url(<fmt:message key='coverimg.default'/>) center no-repeat; background-size:100% auto;"><!--录播视频未找到-->
                            <p class="title"><fmt:message key="info.notfound1"/></p>
                            <p class="title01"><fmt:message key="info.notfound2"/></p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <!--播放部分 end-->
        <!--播放信息 start-->
        <c:choose>
            <c:when test="${videoInfo.status == '1'}">
                <div class="play_infro">
                    <div class="infro"><!--会员信息-->
                        <p class="infro_title">${videoInfo.title}</p>
                        <dl>
                            <dt><img src="${videoInfo.headPortrait}"></dt>
                            <dd class="num">
                                <span class="people"><i></i>${videoInfo.watchNum}</span>
                                <span class="like"><i></i>${videoInfo.likeNum}</span>
                                <span class="mess">${videoInfo.nickName} <fmt:message key="info.in"/> </span>
                            </dd>
                        </dl>
                    </div>
                    <a class="replay" style="background:url(<fmt:message key='icon.replay'/>) right no-repeat; background-size:auto 100%;"></a><!--replay小图标-->
                    <div class="follow bdsharebuttonbox"><!--分享-->
                        <c:choose>
                            <c:when test="${language == 'zh'}">
                                <a data-cmd='sqq' class="f_btn01" style="background:url(<fmt:message key='logo1'/>) center no-repeat; background-size:100% auto;" href="javascript:;"></a>
                                <a data-cmd='tsina' class="f_btn02" style="background:url(<fmt:message key='logo2'/>) center no-repeat; background-size:100% auto;" href="javascript:;"></a>
                                <a class="f_btn03" style="background:url(<fmt:message key='logo3'/>) center no-repeat; background-size:100% auto;" href="javascript:;"></a>
                                <div class="erweima h5_wlive_hide">
                                    <img src="http://i3.letvimg.com/lc03_iscms/201508/21/11/04/3e20ff787ccf4ecda8bc96c0a98236a0.jpg">
                                    <p>打开微信，点击底部的"发现"，使用扫一扫即可关注</p>
                                    <a class="close">x</a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <a class="f_btn01" style="background:url(<fmt:message key='logo1'/>) center no-repeat; background-size:100% auto;" href="javascript:;"></a>
                                <a class="f_btn02" style="background:url(<fmt:message key='logo1'/>) center no-repeat; background-size:100% auto;" href="javascript:;"></a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:when>
        </c:choose>
        <!--播放信息 end-->
        <!--列表 start-->
        <div class="list_box">
            <c:forEach var="videoInfo_list" items="${videoInfoList}" varStatus="status">
                <input type="hidden" id="id_list_${status.count}" value="${videoInfo_list.id}"/>
                <c:set var="classType" value="little_img"/>
                <c:if test="${videoInfo_list.id == videoInfo.id}">
                    <c:set var="classType" value="little_img current"/>
                </c:if>
                <div id="item_${status.count}" class="${classType}">
                    <c:choose>
                        <c:when test="${videoInfo_list.programType == '1'}">
                            <img class="img_box" src="${videoInfo_list.coverPicture}">
                            <a class="live" style="background:url(<fmt:message key='icon.live'/>); background-size:100% 100%;"></a>
                            <div class="infro">
                                <p class="title">${videoInfo_list.title}</p>
                                <p class="mess">${videoInfo_list.nickName} <fmt:message key="info.in"/> </p>
                            </div>
                            <p class="watch">${videoInfo_list.watchOnlineUserNum} <fmt:message key="info.watching"/></p>
                        </c:when>
                        <c:otherwise>
                            <img class="img_box" src="${videoInfo_list.coverPicture}">
                            <a class="replay" style="background:url(<fmt:message key='icon.replay'/>) right no-repeat; background-size:auto 100%;"></a>
                            <div class="infro">
                                <p class="title">${videoInfo_list.title}</p>
                                <p class="mess">${videoInfo_list.nickName} <fmt:message key="info.in"/> </p>
                            </div>
                            <p class="num num_position">
                                <span class="people"><i></i>${videoInfo_list.watchNum}</span>
                                <span class="like"><i></i>${videoInfo_list.likeNum}</span>
                                <span class="duration">${videoInfo_list.liveTimeLength}</span>
                            </p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:forEach>
        </div>
        <!--列表 end-->
    </div>
    <!--分享蒙层-->
    <div class="share_pop" id="dialogShare"><div class="bg"></div><img src="http://i3.letvimg.com/lc01_img/201506/16/17/27/ts.png"></div>
    <!--下载蒙层-->
    <div class="share_pop" id="dialog">
        <div class="bg">
            点击右上角菜单<br>
            在默认浏览器中打开，然后点击下载键即可</div>
        <img src="http://i0.letvimg.com/lc03_iscms/201509/25/17/06/8fa8b1586fd84796abd7f1e0278daced.png">
    </div>
</form>
<%--<script type="text/javascript">__loadjs('whatslive');</script>--%>
<script src="${ctx }/static/js/weixin_share.js" type="text/javascript"></script>
</body>
</html>