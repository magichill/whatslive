<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/mobileHeader.jsp" %>

    <title><fmt:message key="mobile.aboutus.title"/></title>

    </head>

<body>
<div class="h5_wlive_wrap word_wrap">
    <div class="word_bg word">
        <!--头部 start-->
        <div class="co_top">
            <p><img src="<fmt:message key='logo.third'/>"></p>
        </div>
        <!--头部 end-->
        <input type="hidden" id="language" value="${language}"/>
        <c:choose>
            <c:when test="${language == 'zh'}">
                <div class="co_box word about">
                    <p class="title"><fmt:message key="aboutus.contentstitle"/></p>
                    <div class="detail">
                        <p>当我们开始习惯于在社交网站上分享自己的所见所闻，表达感慨时，期望能获得认可，也能获得更多人的共鸣。但有的时候，简单的图文分享已无法满足这种期望。忙碌的工作，繁杂的事情，并不会磨灭人们想展示各自生活的欲望：想带你一起在演唱会摇摆，想带你一起感受赛场的热情，想带你一起等待日出领略海底模样……</p>
                        <p>有一天当大家讨论这件事的时候，发现没有什么比视频来的更直接简单，也没有什么比直播来的更贴近和有参与感。在经过一段时间调研后，"如何更好的展示直播，即时体验，让人与人能迅速拉近彼此的距离，甚至产生化学反应"成为我们的议题——乐嗨直播由此诞生。</p>
                        <p>它是健康积极的，同时更是快乐好玩的。我们决定用简洁又带有冲击的设计作为展现精彩生活的载体，加入像语音评论这样的功能让人们脱离只能输入文字的冷清。可以直播你正在做的任何事，可以选择观看你感兴趣的任何直播，随时随地，分享自己的生活，感受手机直播带来的另一番精彩。</p>
                        <p>乐嗨直播，世界近在眼前！</p></br>
                        <p>乐嗨团队</p>
                        <p>E-mail: 691101015@qq.com</p>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="co_box word E_word">
                    <p class="title"><fmt:message key="aboutus.contentstitle"/></p>
                    <div class="detail">
                        <p>We have been used to share what we saw and heard on the social networking sites, we expressed our feelings; expect to gain recognition and resonate with more people. </p>
                        <p>However, sometimes, simple graphic sharing has been unable to meet this expectation. </p>
                        <p>On the other hand, demanding work, complicated to-do lists could not be the reason for us to kill our desire to express ourselves: want to show you round about a wonderful concert, want to take you feel the game with enthusiasm, want you to watch the sunrise or enjoy the underwater world just see through of my eyes...</p>
                        <p>One day, when we talk about this, we realized among many ways to discover events and places, live video is the best one. It is direct and simple; it may be the best solution to record moments and let others to participate. So, we had been thinking, "how to better show a live video, how to emphasize 'here and now'， how to bridge the gaps among people, even to produce a chemical reaction" -- WhatsLIVE was born on threse thinking.</p>
                        <p>WhatsLIVE is healthy and positive; also, it's full of fun. We decided to use a minimal design style as a way to highlight video content, adding function such as voice comments so that users could experience real-time participate. You could broadcast what you are doing now; you could choose to watch any of your interest in 'LIVE' or 'Featured', anytime and anywhere, to share your life, feeling the wonderful of go live now!</p>
                        <p>WhatsLIVE, it's a world!</p></br>
                        <p>WhatsLIVE Team</p>
                        <p>E-mail：691101015@qq.com</p>
                        <p>Facebook: Whatslive App</p>
                        <p>Twitter: WhatsLIVE_App</p>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>