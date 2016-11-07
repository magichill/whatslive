<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" isErrorPage="true"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="language" value="${sessionScope.language}"/>
<!DOCTYPE html>
<html>
<head>
    <title>404</title>
    <meta charset="utf-8"/>
</head>
<body>
<c:choose>
    <c:when test="${language == 'zh'}">
        <H1>您访问的页面不存在</H1>
    </c:when>
    <c:otherwise>
    <H1>This page does not exist</H1>
    </c:otherwise>
</c:choose>
</body>
</html>
