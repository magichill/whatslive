<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/mobileHeader.jsp" %>

    <title><fmt:message key="mobile.order.title"/>-${videoInfo.title}</title>

    <script>
        var __INFO__ = {
            pageid : 'm_ord'
        }
    </script>
    <%--<script src="http://js.letvcdn.com/lc03_js/201509/15/09/41/3rd/abc.js" type="text/javascript"></script>--%>

    <script type="text/javascript">

        $(function () {

            $(".little_img").click(

                function() {

                    var selectedDivId = $(this).attr("id");
                    var index = selectedDivId.substr(5);
                    var id = $("#id_list_" + index).val();
                    $("#id").val(id);
                    var form = $("#form_mobile");
                    form.method = "post";
                    form.action = "${ctx }/mobile/mobileLive";
                    form.submit();
                });
        });

        function startLive() {

            var form = $("#form_mobile");
            form.method = "post";
            form.action = "${ctx }/mobile/mobileLive";
            form.submit();
        }
    </script>
</head>
<body>
<form id="form_mobile">
    <div class="h5_wlive_wrap oplay_wrap">
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
        <div class="oplay_bg">
            <div class="oplay_blur play_bg_blur" style="height:640px; background:url(${videoInfo.coverPicture}) center no-repeat; background-size:100% 100%;"></div>
            <input type="hidden" id="id" name="id" value="${videoInfo.id}"/>
            <input type="hidden" id="currentTime" value="${currentTime}"/>
            <input type="hidden" id="startTime" value="${videoInfo.startTime}"/>
            <input type="hidden" id="programType" value="${videoInfo.programType}"/>
            <input type="hidden" id="status" value="${videoInfo.status}"/>
            <input type="hidden" id="language" value="${language}"/>
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
            <!--videotype 1:直播 2:预约 3:录播-->
            <!--status 0:直播已结束录播为完成 不可用 1:正常-->
            <c:choose>
                <c:when test="${videoInfo.status == '1'}">
                    <div class="count_down mask_layer"><!--倒计时-->
                        <p class="title"><span class="ellipsis">${videoInfo.nickName}</span> <fmt:message key="info.orderlive"/></p>
                        <p class="mess"><span><i></i></span></p>
                        <p class="sub"><fmt:message key="info.sbuscribe"/></p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="play_end" style="height:640px"><!--预约取消-->
                        <div class="mask_layer">
                            <p class="title"><fmt:message key="info.end"/></p>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        <!--播放部分 end-->
        <!--播放信息 start-->
        <div class="play_infro">
            <div class="infro"><!--会员信息-->
                <p class="infro_title">${videoInfo.title}</p>
                <dl>
                    <dt><img src="${videoInfo.headPortrait}"></dt>
                    <dd class="num">
                        <span class="mess">${videoInfo.nickName}</span>
                    </dd>
                </dl>
            </div>
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