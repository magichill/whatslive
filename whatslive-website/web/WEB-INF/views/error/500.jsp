<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" isErrorPage="true"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="language" value="${sessionScope.language}"/>
<!DOCTYPE html>
<html>
<head>
    <title>500</title>
    <meta charset="utf-8"/>
</head>
<body>
<c:choose>
    <c:when test="${language == 'zh'}">
        <H1>页面暂时不能访问，请稍后重试</H1>
    </c:when>
    <c:otherwise>
    <H1>Page can not access, please try again later</H1>
    </c:otherwise>
</c:choose>

</body>
</html>
