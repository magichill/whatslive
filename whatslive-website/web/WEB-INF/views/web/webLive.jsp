<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/webHeader.jsp" %>

    <title><fmt:message key="web.live.title"/>-${videoInfo.title}</title>

    <script>
        var __INFO__ = {
            pageid : 'web_live'
        }
    </script>
    <%--<script src="${ctx }/static/js/whatslive.js" type="text/javascript"></script>--%>
    <%--<script src="http://js.letvcdn.com/lc03_js/201509/15/09/41/3rd/abc.js" type="text/javascript"></script>--%>

    <script type="text/javascript">

        $(function () {

            $("[id^='item']").click(

                function() {

                    var selectedDivId = $(this).attr("id");
                    var index = selectedDivId.substr(5);
                    var selectedId = $("#id_list_" + index).val();
                    var id = $("#id").val();
                    if (selectedId != id) {

                        $("#id").val(selectedId);
                        var form = $("#form_web");
                        form.method = "post";
                        form.action = "${ctx }/web/webLive";
                        form.submit();
                    }
                });
        });
        <%--function callbackJs(type , value) {--%>

            <%--switch(type) {--%>

                <%--&lt;%&ndash;case "videoAuthValid":&ndash;%&gt;--%>
                    <%--&lt;%&ndash;alert("videoAuthValid");&ndash;%&gt;--%>
                    <%--&lt;%&ndash;$("#playerLoading").attr("style", "background:url(${videoInfo.coverPicture}) center no-repeat; background-size:100% 100%; display:none");&ndash;%&gt;--%>
                    <%--&lt;%&ndash;$("#player1").attr("style", "");&ndash;%&gt;--%>
                    <%--&lt;%&ndash;break;&ndash;%&gt;--%>
                <%--case "videoEmpty":--%>
                    <%--alert("videoEmpty");--%>
                    <%--$("#player1").attr("style", "position: absolute; margin-left: -800px");--%>
                    <%--$("#playerLoading").attr("style", "background:url(${videoInfo.coverPicture}) center no-repeat; background-size:100% 100%;");--%>
                    <%--break;--%>
                <%--case "videoFull":--%>
                    <%--alert("videoFull");--%>
                    <%--$("#playerLoading").attr("style", "background:url(${videoInfo.coverPicture}) center no-repeat; background-size:100% 100%; display:none");--%>
                    <%--$("#player1").attr("style", "");--%>
                    <%--break;--%>
                <%--case "videoStop":--%>
                    <%--alert("videoStop");--%>
                    <%--break;--%>
                <%--case "errorInConfig":--%>
                    <%--alert("errorInConfig");--%>
                    <%--break;--%>
                <%--case "errorInLoadPlugins":--%>
                    <%--alert("errorInLoadPlugins");--%>
                    <%--break;--%>
                <%--case "errorInKernel":--%>
                    <%--alert("errorInKernel");--%>
                    <%--break;--%>
            <%--}--%>

            <%--return {"status":"playerContinue"};--%>
        <%--}--%>

        function displayWatchedNum() {

            var id = $("#id").val();
            $.ajax({
                url: "${ctx }/web/webGetWatchedNum",
                type: "post",
                data: {"id": id},
                success: function (data) {

                    var data = $.parseJSON(data);
                    var showString;
                    if (($("#language").val() == "zh")) {

                        showString = data.watchedNum + "人已观看";
                    } else {

                        showString = data.watchedNum + "people watched";
                    }

                    $("#player1").attr("class", "player h5_wlive_hide");
                    $("#playend").attr("class", "play_end");
                    $("#watchInfo").html(showString);
                }
            });
        }
    </script>
</head>
<body>
<form id="form_web">
    <div class="wlive_wrap">
        <div class="play_bg" style="<fmt:message key='web.backgroundstyle'/>">
            <input type="hidden" id="language" value="${language}"/>
            <!--头部 start-->
            <div class="co_top">
                <div class="co_top_box">
                    <div class="logo">
                        <a href="${ctx }/"><img src="<fmt:message key='logo.first'/>"></a>
                        <i><img src="<fmt:message key='logo.second'/>"></i>
                    </div>
                    <dl class="download">
                        <dt class="title"><fmt:message key="info.download"/></dt>
                        <dd><a class="app_btn01" target="_blank" href="https://itunes.apple.com/cn/app/le-hai-zhi-bo-yuan-chuang/id1028984886?mt=8"><img src="<fmt:message key='app.download.apple'/>"></a></dd>
                        <dd>
                            <c:choose>
                                <c:when test="${language == 'zh'}">
                                    <a class="app_btn02" href="${ctx }/static/apk/lehi_download.apk">
                                        <img src="<fmt:message key='app.download.google'/>">
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <a class="app_btn02" href="#" target="_blank">
                                        <img src="<fmt:message key='app.download.google'/>">
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </dd>
                    </dl>
                </div>
            </div>
            <!--头部 end-->
            <!--programType 1:直播 2:预约 3:录播-->
            <!--status 0:直播已结束录播为完成 不可用 1:正常-->
            <div class="co_box">
                <input type="hidden" id="id" name="id" value="${videoInfo.id}"/>
                <input type="hidden" id="programType" value="${videoInfo.programType}"/>
                <input type="hidden" id="status" value="${videoInfo.status}"/>
                <input type="hidden" id="activityId" value="${videoInfo.activityId}"/>
                <input type="hidden" id="coverPicture" value="${videoInfo.coverPicture}"/>
                <input type="hidden" id="title" value="${videoInfo.title}"/>
                <input type="hidden" id="shareUrl" value="${videoInfo.shareUrl}"/>
                <c:choose>
                    <c:when test="${language == 'zh'}">
                        <input type="hidden" id="describe" value="我在@乐嗨直播 速来观看！ @${videoInfo.nickName}: ${videoInfo.title}"/>
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" id="describe" value="【LIVE NOW】@${videoInfo.nickName} on #WhatsLIVE ${videoInfo.title}"/>
                    </c:otherwise>
                </c:choose>
                <!--信息 start-->
                <div class="column01">
                    <div class="list01">
                        ${videoInfo.title}
                    </div>
                    <div class="list02">
                        <dl>
                            <dt><img src="${videoInfo.headPortrait}"></dt>
                            <dd class="num"><i class="people">${videoInfo.watchNum}</i><i class="like">${videoInfo.likeNum}</i></dd>
                            <dd class="mess">${videoInfo.nickName} <fmt:message key="info.in"/> </dd>
                        </dl>
                    </div>
                    <c:if test="${videoInfo.status == '1'}">
                        <div class="list03">
                            <p>${videoInfo.watchOnlineUserNum} <fmt:message key="info.watching"/></p>
                            <p><fmt:message key="info.join"/></p>
                        </div>
                    </c:if>
                    <div class="list04 bdsharebuttonbox"><!--分享-->
                        <c:choose>
                            <c:when test="${language == 'zh'}">
                                <a data-cmd='sqq' class="f_btn01" style="background:url(<fmt:message key='logo1'/>) center no-repeat; background-size:100% auto;" href="javascript:;"></a>
                                <a data-cmd='tsina' class="f_btn02" style="background:url(<fmt:message key='logo2'/>) center no-repeat; background-size:100% auto;" href="javascript:;"></a>
                                <a data-cmd='weixin' class="f_btn03" style="background:url(<fmt:message key='logo3'/>) center no-repeat; background-size:100% auto;" href="javascript:;"></a>
                                <div class="erweima wlive_hide">
                                    <img src="http://i3.letvimg.com/lc03_iscms/201508/21/11/04/3e20ff787ccf4ecda8bc96c0a98236a0.jpg">
                                    <p>打开微信，点击底部的"发现"，使用扫一扫即可关注</p>
                                    <a class="close">x</a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <a class="f_btn01" style="background:url(<fmt:message key='logo1'/>) center no-repeat; background-size:100% auto;" href="javascript:;"></a>
                                <a class="f_btn02" style="background:url(<fmt:message key='logo2'/>) center no-repeat; background-size:100% auto;" href="javascript:;"></a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <!--信息 end-->
                <c:choose>
                    <c:when test="${videoInfo.status == '1'}">
                        <div class="column02">
                            <!--播放器 start-->
                            <%--<div id="playerLoading" class="player" style="background:url(${videoInfo.coverPicture}) center no-repeat; background-size:100% 100%;"></div>--%>
                            <div id="player1" class="player"><!--放置播放器-->
                                <object width="100%" id="player" height="100%" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000">
                                    <param name="allowFullScreen" value="true">
                                    <param name="allowScriptAccess" value="always" />
                                    <param name="flashVars" value="autoplay=1&loadingurl=0&activityId=${videoInfo.activityId}&callbackJs=callbackJs" />
                                    <param name="movie" value="http://sdk.lecloud.com/live.swf" />
                                    <embed src="http://sdk.lecloud.com/live.swf" flashVars="autoplay=1&loadingurl=0&activityId=${videoInfo.activityId}&callbackJs=callbackJs" name="player" width="100%" height="100%" allowFullScreen="true" type="application/x-shockwave-flash"  allowScriptAccess="always"/>
                                </object>
                                <div title="Error message area" id="errorMsg" style="color:Red"></div>
                            </div>
                            <!--播放结束-->
                            <div id="playend" class="play_end wlive_hide" style="background:url(${videoInfo.coverPicture}) center no-repeat; background-size:100% 100%;">
                                <div class="mask_layer">
                                    <p class="title" style="font-size: 21px"><fmt:message key="info.replaymaking1"/></p>
                                    <p class="title01"><fmt:message key="info.replaymaking2"/></p>
                                    <div class="mess">
                                        <p id="watchInfo">${videoInfo.watchNum} <fmt:message key="info.watched"/></p>
                                        <p><fmt:message key="info.invite"/></p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!--播放器 start-->
                        <div class="column02">
                            <!--播放结束-->
                            <div class="play_end" style="background:url(${videoInfo.coverPicture}) center no-repeat; background-size:100% 100%;">
                                <div class="mask_layer">
                                    <c:choose>
                                        <c:when test="${videoInfo.programType == '-1' && videoInfo.status == '0'}">
                                            <!--直播或预约未启动-->
                                            <p class="title title01 fs18"><fmt:message key="info.notready"/></p>
                                        </c:when>
                                        <c:when test="${videoInfo.programType == '3' && videoInfo.status == '-1'}">
                                            <!--直播已结束录播未形成-->
                                            <p class="title" style="font-size: 21px"><fmt:message key="info.replaymaking1"/></p><!--直播已结束录播未形成 3秒后自动播放列表第一条-->
                                            <p class="title01"><fmt:message key="info.replaymaking2"/></p>
                                        </c:when>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
                <!--播放器 end-->
                <!--缩略图 start-->
                <div class="column03">
                    <c:forEach var="videoInfo_list" items="${videoInfoList}" varStatus="status">
                        <input type="hidden" id="id_list_${status.count}" value="${videoInfo_list.id}"/>
                        <c:set var="classType" value=""/>
                        <c:set var="hrefType" value="href=\"javascript:void(0);\""/>
                        <c:if test="${videoInfo_list.id == videoInfo.id}">
                            <c:set var="classType" value="current"/>
                            <c:set var="hrefType" value=""/>
                        </c:if>
                        <c:set var="titleClassType" value="title"/>
                        <c:if test="${language == 'zh'}">
                            <c:set var="titleClassType" value="title title_f"/>
                        </c:if>
                        <dl id="item_${status.count}" class="${classType}">
                            <c:choose>
                                <c:when test="${videoInfo_list.programType == '1'}">
                                    <dt class="head_icon">
                                        <a ${hrefType}><img src="${videoInfo_list.coverPicture}"></a>
                                        <i class="live" style="background:url(<fmt:message key='icon.live'/>); background-size:100% 100%;"></i>
                                    </dt>
                                    <dd class="mess">
                                        <a class="${titleClassType}" ${hrefType}>${videoInfo_list.title}</a>
                                        <div class="live_mess">
                                            <p class="f_title">${videoInfo_list.nickName} <fmt:message key="info.in"/></p>
                                            <p class="watch">${videoInfo_list.watchOnlineUserNum} <fmt:message key="info.watching"/></p>
                                        </div>
                                    </dd>
                                </c:when>
                                <c:otherwise>
                                    <dt class="head_icon">
                                        <a ${hrefType}><img src="${videoInfo_list.coverPicture}"></a>
                                        <i class="replay" style="background:url(<fmt:message key='icon.replay'/>) right no-repeat; background-size:auto 100%;"></i>
                                    </dt>
                                    <dd class="mess">
                                        <a class="${titleClassType}" ${hrefType}>${videoInfo_list.title}</a>
                                        <div class="replay_mess">
                                            <p class="f_title">${videoInfo_list.nickName} <fmt:message key="info.in"/></p>
                                            <p class="infro">
                                                <i class="people">${videoInfo_list.watchNum}</i>
                                                <i class="like">${videoInfo_list.likeNum}</i>
                                                <i class="time">${videoInfo_list.liveTimeLength}</i>
                                            </p>
                                        </div>
                                    </dd>
                                </c:otherwise>
                            </c:choose>
                        </dl>
                    </c:forEach>
                </div>
                <!--缩略图 end-->
            </div>
            <p class="copyright"><fmt:message key='copyright'/></p>
        </div>
    </div>
</form>
<%--<script type="text/javascript">__loadjs('whatslive');</script>--%>
</body>
</html>