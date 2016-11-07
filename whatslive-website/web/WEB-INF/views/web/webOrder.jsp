<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/webHeader.jsp" %>

    <title><fmt:message key="web.order.title"/>-${videoInfo.title}</title>

    <script>
        var __INFO__ = {
            pageid : 'web_ord'
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
                        var id = $("#id_list_" + index).val();
                        $("#id").val(id);
                        var form = $("#form_web");
                        form.method = "post";
                        form.action = "${ctx }/web/webLive";
                        form.submit();
                    });
        });

        function startLive() {

            var form = $("#form_web");
            form.method = "post";
            form.action = "${ctx }/web/webLive";
            form.submit();
        }

    </script>
</head>
<body>
<form id="form_web">
    <input type="hidden" id="language" value="${language}"/>
    <div class="wlive_wrap">
        <div class="play_bg" style="<fmt:message key='web.backgroundstyle'/>">
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
            <!--videotype 1:直播 2:预约 3:录播-->
            <!--status 0:直播已结束录播为完成 不可用 1:正常-->
            <div class="co_box">
                <input type="hidden" id="id" name="id" value="${videoInfo.id}"/>
                <input type="hidden" id="currentTime" value="${currentTime}"/>
                <input type="hidden" id="startTime" value="${videoInfo.startTime}"/>
                <input type="hidden" id="programType" value="${videoInfo.programType}"/>
                <input type="hidden" id="status" value="${videoInfo.status}"/>
                <input type="hidden" id="coverPicture" value="${videoInfo.coverPicture}"/>
                <input type="hidden" id="title" value="${videoInfo.title}"/>
                <input type="hidden" id="shareUrl" value="${videoInfo.shareUrl}"/>
                <c:choose>
                    <c:when test="${language == 'zh'}">
                        <input type="hidden" id="describe" value="${videoInfo.nickName}会在@乐嗨直播 ${videoInfo.title}，到时一起看！"/>
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" id="describe" value="${videoInfo.nickName}'s upcoming stream on #WhatsLIVE! ${videoInfo.title}"/>
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
                            <dd class="num"></dd>
                            <dd class="mess">${videoInfo.nickName}</dd>
                        </dl>
                    </div>
                    <c:if test="${videoInfo.status == '1'}">
                        <div class="list03">
                            <p>${videoInfo.orderNum} <fmt:message key="info.subscribed"/></p>
                            <p><fmt:message key="info.watchmore"/></p>
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
                        <!--播放器 start-->
                        <div class="column02">
                            <div class="player">
                                <div class="oplay_blur play_bg_blur" style="background:url(${videoInfo.coverPicture}) center no-repeat; background-size:100% 100%;"></div>
                                <div class="count_down mask_layer"><!--倒计时-->
                                    <p class="title"><span class="ellipsis">${videoInfo.nickName}</span> <fmt:message key="info.orderlive"/></p>
                                    <p class="mess"><span><i></i></span></p>
                                    <p class="sub"><fmt:message key="info.sbuscribe"/></p>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!--播放器 start-->
                        <div class="column02">
                            <!--预约取消-->
                            <div class="play_end" style="background:url(${videoInfo.coverPicture}) center no-repeat; background-size:100% 100%;">
                                <div class="mask_layer">
                                    <p class="title"><fmt:message key="info.end"/></p>
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
                        <c:if test="${videoInfo_list.id == videoInfo.id}">
                            <c:set var="classType" value="current"/>
                        </c:if>
                        <c:set var="titleClassType" value="title"/>
                        <c:if test="${language == 'zh'}">
                            <c:set var="titleClassType" value="title title_f"/>
                        </c:if>
                        <dl id="item_${status.count}" class="${classType}">
                            <c:choose>
                                <c:when test="${videoInfo_list.programType == '1'}">
                                    <dt class="head_icon">
                                        <a href="javascript:void(0);"><img src="${videoInfo_list.coverPicture}"></a>
                                        <i class="live" style="background:url(<fmt:message key='icon.live'/>); background-size:100% 100%;"></i>
                                    </dt>
                                    <dd class="mess">
                                        <a class="${titleClassType}" href="javascript:void(0);">${videoInfo_list.title}</a>
                                        <div class="live_mess">
                                            <p class="f_title">${videoInfo_list.nickName} <fmt:message key="info.in"/></p>
                                            <p class="watch">${videoInfo_list.watchOnlineUserNum} <fmt:message key="info.watching"/></p>
                                        </div>
                                    </dd>
                                </c:when>
                                <c:otherwise>
                                    <dt class="head_icon">
                                        <a href="javascript:void(0);"><img src="${videoInfo_list.coverPicture}"></a>
                                        <i class="replay" style="background:url(<fmt:message key='icon.replay'/>) right no-repeat; background-size:auto 100%;"></i>
                                    </dt>
                                    <dd class="mess">
                                        <a class="${titleClassType}" href="javascript:void(0);">${videoInfo_list.title}</a>
                                        <div class="replay_mess">
                                            <p class="f_title">${videoInfo_list.nickName} <fmt:message key="info.in"/></p>
                                            <p class="infro">
                                                <i class="people">${videoInfo_list.watchNum}</i>
                                                <i class="like">${videoInfo_list.likeNum}</i>
                                                <i class="time">${videoInfo_list.liveTimeLength}</i></p>
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