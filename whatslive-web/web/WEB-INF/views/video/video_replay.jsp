<!DOCTYPE html>
<head>
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <title>录播视频</title>
</head>
<body>
<div id="fla_player" style="text-align:center;">
    <script type="text/javascript">

    </script>
    <div class="column02">
        <div id="player1" class="player"><!--放置播放器 循环播放-->
            <object type="application/x-shockwave-flash" id="player" data="http://yuntv.letv.com/bcloud.swf" width="600px" height="600px">
                <param name="quality" value="high">
                <param name="align" value="middle">
                <param name="wmode" value="opaque">
                <param name="bgcolor" value="#000000">
                <param name="allowscriptaccess" value="always">
                <param name="allowfullscreen" value="true">
                <param name="flashvars" value="uu=${program.videoId}&vu=${program.vuid}&autoplay=1&gpcflag=1&autoReplay=0&loadingurl=0">
            </object>
        </div>
    </div>
</div>

</body>
</html>