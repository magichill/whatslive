<%@ page language="java" errorPage="/WEB-INF/views/error/500.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%--<%@ page import="WebConstants" %>--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sitemesh" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
//    request.setAttribute("fileHost", WebConstants.UPLOAD_SERVER_HOST);
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>