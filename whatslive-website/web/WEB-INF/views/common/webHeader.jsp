<%@ page language="java" errorPage="/WEB-INF/views/error/500.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="language" value="${sessionScope.language}"/>
<!DOCTYPE html>
<html>
<!-- BEGIN HEAD -->
<head>
    <base href="${ctx }">
    <meta http-equiv="Pragma" CONTENT="no-cache" />
    <meta http-equiv="Cache-Control" CONTENT="no-store, no-cache, must-revalidate" />
    <meta http-equiv="Expires" CONTENT="-1" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link rel="Shortcut Icon" href="http://i2.letvimg.com/lc02_iscms/201508/25/18/24/6897f7c1fa8a4c09b3838392a9067766.png"/>
    <link rel="stylesheet" href="${ctx }/static/css/whatslive_web.css"/>
    <script src="${ctx }/static/js/jquery-1.11.3.js" type="text/javascript"></script>
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
