<%@ page language="java" errorPage="/WEB-INF/views/error/500.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="language" value="${sessionScope.language}"/>
<!DOCTYPE html>
<html class="iphone">
<head>
    <base href="${ctx }">
    <meta http-equiv="Pragma" CONTENT="no-cache" />
    <meta http-equiv="Cache-Control" CONTENT="no-store, no-cache, must-revalidate" />
    <meta http-equiv="Expires" CONTENT="-1" />
    <meta charset="utf-8">
    <link rel="dns-prefetch" href="//js.letvcdn.com">
    <link rel="dns-prefetch" href="//css.letvcdn.com">
    <link rel="dns-prefetch" href="//i0.letvimg.com">
    <link rel="dns-prefetch" href="//i1.letvimg.com">
    <link rel="dns-prefetch" href="//i2.letvimg.com">
    <link rel="dns-prefetch" href="//i3.letvimg.com">
    <link rel="Shortcut Icon" href=" http://i2.letvimg.com/lc02_iscms/201508/25/18/24/6897f7c1fa8a4c09b3838392a9067766.png" />
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta content="width=device-width,initial-scale=1,user-scalable=no" name="viewport">
    <meta name="apple-touch-fullscreen" content="yes">
    <meta content="black" name="apple-mobile-web-app-status-bar-style">
    <meta content="telephone=no" name="format-detection">
    <meta name="apple-mobile-web-app-title" content="手机乐视">
    <meta name="baidu-tc-cerfication" content="30f757b11897dc4f697eb568cb7eb2a3" />
    <meta name="baidu-site-verification" content="SgTbeKNm8i" />
    <link rel="apple-touch-icon-precomposed" href="${ctx }/static/css/whatslive_h5.css"/>
    <link rel="stylesheet" href="${ctx }/static/css/whatslive_h5.css"/>
    <script src="${ctx }/static/js/jquery-1.11.3.js" type="text/javascript"></script>
    <script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/whatslive.js" type="text/javascript"></script>
    <script>
        var _hmt = _hmt || [];
        (function() {
            var hm = document.createElement("script");
            hm.src = "//hm.baidu.com/hm.js?4a856bb88aa960c4976507a6610f51b5";
            var s = document.getElementsByTagName("script")[0];
            s.parentNode.insertBefore(hm, s);
        })();
    </script>
