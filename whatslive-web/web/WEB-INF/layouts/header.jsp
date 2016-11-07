<%@ page import="com.letv.whatslive.model.mysql.system.SysFunction" %>
<%@ page import="com.letv.whatslive.web.util.WebUtils" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<!-- BEGIN HEADER -->
<div class="header navbar navbar-inverse navbar-fixed-top">
<!-- BEGIN TOP NAVIGATION BAR -->
<div class="navbar-inner">
<div class="container-fluid">
<!-- BEGIN LOGO -->
<a class="brand" href="${ctx }/">
    <img src="${ctx }/static/image/logo-letv.png" alt="logo"/>
</a>
<!-- END LOGO -->

<!-- BEGIN HORIZANTAL MENU -->

<div class="navbar hor-menu hidden-phone hidden-tablet">
    <div class="navbar-inner">
        <ul class="nav">
<%
    String FUNC_KEY_HEADERPAGE = "fId";
    int fIdHeaderPage = 0;
    String fIdStringHeaderPage = request.getParameter(FUNC_KEY_HEADERPAGE);
    if(StringUtils.isNumeric(fIdStringHeaderPage)){
        fIdHeaderPage = Integer.valueOf(fIdStringHeaderPage);
    }
    List<SysFunction> topFuncListHeaderPage = WebUtils.getTopFunctionListByLoginUser(request);
    if(fIdHeaderPage <= 0){
        if(topFuncListHeaderPage != null && topFuncListHeaderPage.size() > 0){
            SysFunction firstLeafFunc = WebUtils.getFirstLeafFunctionByLoginUserAndTopFunction(request, topFuncListHeaderPage.get(0).getId());
            if(firstLeafFunc != null){
                fIdHeaderPage = firstLeafFunc.getId();
                //跳转到第一个有权限的功能页面
//                String actionUrl = WebUtils.addQueryParam(firstLeafFunc.getActionUrl(), FUNC_KEY_HEADERPAGE, firstLeafFunc.getId().toString());
//                response.sendRedirect(request.getContextPath()+actionUrl);
            }
        }
    }

    for(SysFunction func : topFuncListHeaderPage){
        boolean isActiveTop = WebUtils.isFuncActive(func, fIdHeaderPage);
        SysFunction firstLeafFunc = WebUtils.getFirstLeafFunctionByLoginUserAndTopFunction(request, func.getId());
        String actionUrl = "/";
        if(firstLeafFunc != null){
            actionUrl = WebUtils.addQueryParam(firstLeafFunc.getActionUrl(), FUNC_KEY_HEADERPAGE, firstLeafFunc.getId().toString());
        }
%>
            <li <%=isActiveTop?"class=\"active\"":""%>>
                <a href="${ctx }<%=actionUrl%>">
                    <%=func.getFuncName()%>
<%
        if(isActiveTop){
%>
                    <span class="selected"></span>
<%
        }
%>
                </a>
            </li>
<%
    }
%>
            <%--<li>--%>
                <%--<a href="">统计分析子系统</a>--%>
            <%--</li>--%>
            <%--<li>--%>
                <%--<a href="">设备管理子系统</a>--%>
            <%--</li>--%>
            <%--<li>--%>
                <%--<a href="">用户反馈子系统</a>--%>
            <%--</li>--%>
            <%--<li>--%>
                <%--<a href="">系统设置</a>--%>
            <%--</li>--%>

        </ul>
    </div>
</div>

<!-- END HORIZANTAL MENU -->



<!-- BEGIN RESPONSIVE MENU TOGGLER -->
<a href="javascript:;" class="btn-navbar collapsed" data-toggle="collapse" data-target=".nav-collapse">
    <img src="${ctx }/static/image/menu-toggler.png" alt=""/>
</a>
<!-- END RESPONSIVE MENU TOGGLER -->
<!-- BEGIN TOP NAVIGATION MENU -->
<ul class="nav pull-right">
<!-- BEGIN USER LOGIN DROPDOWN -->
<li class="dropdown user">
    <a href="#" class="dropdown-toggle" data-toggle="dropdown">
        <img alt="" src="${ctx }/static/image/user-32.png"/>
<%
    String userName = WebUtils.getLoginUserNameNotNull(request);
%>
        <span class="username"><%=userName%></span>
        <i class="icon-angle-down"></i>
    </a>
    <ul class="dropdown-menu">
        <li><a href="${ctx }/"><i class="icon-user"></i>我的账号</a></li>
        <%--<li><a href="page_calendar.html"><i class="icon-calendar"></i> My Calendar</a></li>--%>
        <li><a href="${ctx }/"><i class="icon-envelope"></i>收件箱(1)</a></li>
        <%--<li><a href="#"><i class="icon-tasks"></i> My Tasks</a></li>--%>
        <li class="divider"></li>
        <li><a href="#logoutModal" data-toggle="modal"><i class="icon-key"></i> 退出</a></li>
    </ul>
</li>
<!-- END USER LOGIN DROPDOWN -->
</ul>

<!-- END TOP NAVIGATION MENU -->
</div>
</div>
<!-- END TOP NAVIGATION BAR -->
</div>
<!-- END HEADER -->
<div id="logoutModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="logoutModalLabel" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
        <h3 id="logoutModalLabel">操作确认</h3>
    </div>
    <div class="modal-body">
        <p>确定退出系统吗？</p>
    </div>
    <div class="modal-footer">
        <button class="btn" data-dismiss="modal" aria-hidden="true">取消</button>
        <button  class="btn blue" onclick="window.location.href = '${ctx }/logout';">确定</button>
    </div>
</div>