<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/webHeader.jsp" %>

    <meta name="Keywords" content="乐嗨,乐嗨直播,乐嗨APP,个人直播,手机直播,直播APP" />
    <meta name="description" content="乐嗨直播是一款手机直播APP，下载乐嗨直播APP，一键开启直播，即时分享，让全世界陪你一起嗨！乐嗨直播，世界尽在眼前。" />
    <title><fmt:message key="web.index.title"/></title>
    <script>
        var __INFO__ = {
            pageid : 'web_index'
        }
    </script>
    <%--<script src="${ctx }/static/js/whatslive.js" type="text/javascript"></script>--%>
    <%--<script src="http://js.letvcdn.com/lc03_js/201509/15/09/41/3rd/abc.js" type="text/javascript"></script>--%>
</head>

<body>
<div class="wlive_wrap">
<input type="hidden" id="language" value="${language}"/>
    <div class="index_slide_bg">
        <div class="slide_bg_box i_new_bg">
            <div class="i_01" style="background:url(<fmt:message key='web.index.background.pic1'/>) center 0 no-repeat; background-size:auto 100%;"></div>
            <div class="i_02" style="background:url(<fmt:message key='web.index.background.pic2'/>) center 0 no-repeat; background-size:auto 100%;"></div>
            <div class="i_03" style="background:url(<fmt:message key='web.index.background.pic3'/>) center 0 no-repeat; background-size:auto 100%;"></div>
        </div>
        <div class="co_box">
            <div class="detail">
                <div class="list01">
                    <p class="logo">
                        <a><img src="<fmt:message key='logo.first'/>"></a>
                        <i><img src="<fmt:message key='logo.third'/>"></i>
                    </p>
                    <p class="p01"><fmt:message key="web.index.watchword.first"/></p>
                    <p class="p02"><fmt:message key="web.index.watchword.second"/></p>
                </div>
                <div class="list02">
                    <dl class="download">
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
                    <dl class="follow">
                        <dt class="title"><fmt:message key="web.index.follow"/></dt>
                        <c:choose>
                            <c:when test="${language == 'zh'}">
                                <dd><a class="f_btn02" style="background:url(<fmt:message key='web.index.logo2'/>) center no-repeat; background-size:100% auto;" href="http://weibo.com/u/5627124172" target="_blank"></a></dd>
                                <dd><a class="f_btn03" style="background:url(<fmt:message key='web.index.logo3'/>) center no-repeat; background-size:100% auto;" href="javascript:;"></a></dd>
                                <div class="erweima wlive_hide">
                                    <img src="http://i3.letvimg.com/lc03_iscms/201508/21/11/04/3e20ff787ccf4ecda8bc96c0a98236a0.jpg">
                                    <p>打开微信，点击底部的"发现"，使用扫一扫即可关注</p>
                                    <span class="close">x</span>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <dd><a class="f_btn01" style="background:url(<fmt:message key='logo1'/>) center no-repeat; background-size:100% auto;" href="https://twitter.com/WhatsLIVE_App" target="_blank"></a></dd>
                                <dd><a class="f_btn02" style="background:url(<fmt:message key='logo2'/>) center no-repeat; background-size:100% auto;" href="https://www.facebook.com/pages/Whatslive-App/1638505223038789?fref=ts" target="_blank"></a></dd>
                            </c:otherwise>
                        </c:choose>
                    </dl>
                </div>
            </div>
            <img class="icon01" src="<fmt:message key='web.index.watchword.third'/>">
        </div>
        <p class="index_copyright"><fmt:message key="copyright"/></p>
        <!--footer start-->
        <div class="footer">
            <a href="${ctx }/web/webTermsService"><fmt:message key="web.index.service"/></a>
            <span class="line">|</span>
            <a class="about" href="${ctx }/web/webAboutus"><fmt:message key="web.index.aboutus"/></a>
        </div>
        <!--footer end-->
    </div>
</div>
<%--<script type="text/javascript">__loadjs('whatslive');</script>--%>
</body>
</html>