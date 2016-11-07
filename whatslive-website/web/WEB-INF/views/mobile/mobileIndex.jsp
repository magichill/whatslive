<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/mobileHeader.jsp" %>

    <title><fmt:message key="mobile.index.title"/></title>

    <script>
        var __INFO__ = {
            pageid : 'm_index'
        }
    </script>
    <%--<script src="http://js.letvcdn.com/lc03_js/201509/15/09/41/3rd/abc.js" type="text/javascript"></script>--%>
</head>
<body>
<div class="h5_wlive_wrap">
    <div class="index_slide_bg" style="<fmt:message key='mobile.index.backgroundstyle'/>">
        <div class="follow">
            <input type="hidden" id="language" value="${language}"/>
            <c:choose>
                <c:when test="${language == 'zh'}">
                    <a class="f_btn01" style="background:url(<fmt:message key='logo2'/>) center no-repeat; background-size:100% auto;" href="http://weibo.com/u/5627124172" target="_blank"></a>
                    <a class="f_btn02" style="background:url(<fmt:message key='logo3'/>) center no-repeat; background-size:100% auto;" href="javascript:;"></a>
                    <div class="erweima h5_wlive_hide">
                        <img src="http://i3.letvimg.com/lc03_iscms/201508/21/11/04/3e20ff787ccf4ecda8bc96c0a98236a0.jpg">
                        <p>打开微信，点击底部的"发现"，使用扫一扫即可关注</p>
                        <a class="close">x</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <a class="f_btn01" style="background:url(<fmt:message key='logo1'/>) center no-repeat; background-size:100% auto;" href="https://twitter.com/WhatsLIVE_App" target="_blank"></a>
                    <a class="f_btn02" style="background:url(<fmt:message key='logo2'/>) center no-repeat; background-size:100% auto;" href="https://www.facebook.com/pages/Whatslive-App/1638505223038789?fref=ts" target="_blank"></a>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="list01">
            <p class="logo">
                <a href="javascript:;"><img src="<fmt:message key='logo.first'/>"></a>
                <i><img src="<fmt:message key='logo.third'/>"></i>
            </p>
            <p class="p01"><fmt:message key="mobile.index.watchword.first"/></p>
            <p class="p02"><fmt:message key="mobile.index.watchword.second"/></p>
        </div>

        <div class="list02">
            <dl class="download">
                <dd><a class="app_btn01" target="_blank" href="https://itunes.apple.com/cn/app/le-hai-zhi-bo-yuan-chuang/id1028984886?mt=8" ><img src="<fmt:message key='mobile.app.download.apple'/>"></a></dd>
                <dd>
                    <c:choose>
                        <c:when test="${language == 'zh'}">
                            <a class="app_btn02" href="${ctx }/static/apk/lehi_download.apk">
                                <img src="<fmt:message key='mobile.app.download.google'/>">
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a class="app_btn02" href="javascript:;" target="_blank">
                                <img src="<fmt:message key='mobile.app.download.google'/>">
                            </a>
                        </c:otherwise>
                    </c:choose>
                </dd>
            </dl>
        </div>
        <img class="icon01" src="<fmt:message key='mobile.index.watchword.third'/>">
        <a class="terms" href="${ctx }/mobile/mobileTermsService"><fmt:message key="mobile.index.service"/></a>
    </div>
</div>
<!--分享蒙层-->
<div class="share_pop" id="dialog">
    <div class="bg">
        点击右上角菜单<br>
        在默认浏览器中打开，然后点击下载键即可</div>
    <img src="http://i0.letvimg.com/lc03_iscms/201509/25/17/06/8fa8b1586fd84796abd7f1e0278daced.png">
</div>
<%--<script type="text/javascript">__loadjs('whatslive');</script>--%>
</body>
</html>