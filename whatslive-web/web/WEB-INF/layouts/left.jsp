<%@ page import="com.google.common.collect.Lists" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<!-- BEGIN SIDEBAR -->
<div class="page-sidebar nav-collapse collapse">
<!-- BEGIN SIDEBAR MENU -->
<ul class="page-sidebar-menu">
<li>
    <!-- BEGIN SIDEBAR TOGGLER BUTTON -->
    <div class="sidebar-toggler hidden-phone"></div>
    <!-- BEGIN SIDEBAR TOGGLER BUTTON -->
</li>
<%--<li>--%>
    <%--<!-- BEGIN RESPONSIVE QUICK SEARCH FORM -->--%>
    <%--<form class="sidebar-search">--%>
        <%--<div class="input-box">--%>
            <%--<a href="javascript:;" class="remove"></a>--%>
            <%--<input type="text" placeholder="Search..."/>--%>
            <%--<input type="button" class="submit" value=" "/>--%>
        <%--</div>--%>
    <%--</form>--%>
    <%--<!-- END RESPONSIVE QUICK SEARCH FORM -->--%>
<%--</li>--%>
<li>&nbsp;</li>
<%
    String FUNC_KEY_LEFTPAGE = "fId";
    int fIdLeftPage = 0;
    String fIdStringLeftPage = request.getParameter(FUNC_KEY_LEFTPAGE);
    if(StringUtils.isNumeric(fIdStringLeftPage)){
        fIdLeftPage = Integer.valueOf(fIdStringLeftPage);
    }
    List<SysFunction> f1ListLeftPage = null;
    //根据fId获取topFunc
    SysFunction topFuncLeftPage = null;
    if(fIdLeftPage > 0){
        topFuncLeftPage = WebUtils.getTopFunctionBySubFuncId(fIdLeftPage);
        f1ListLeftPage = WebUtils.getFunctionListByLoginUserAndParentFuncId(request, topFuncLeftPage.getId());
    }else {
        List<SysFunction> topFuncListLeftPage = WebUtils.getTopFunctionListByLoginUser(request);
        if(topFuncListLeftPage != null && topFuncListLeftPage.size() > 0){
            topFuncLeftPage = topFuncListLeftPage.get(0);
            SysFunction firstLeafFunc = WebUtils.getFirstLeafFunctionByLoginUserAndTopFunction(request, topFuncLeftPage.getId());
            if(firstLeafFunc != null){
                fIdLeftPage = firstLeafFunc.getId();
            }
            f1ListLeftPage = WebUtils.getFunctionListByLoginUserAndParentFuncId(request, topFuncLeftPage.getId());
        }else{
            f1ListLeftPage = Lists.newArrayList();
        }
    }

    for(int i = 0; i < f1ListLeftPage.size(); i++){
        SysFunction f1 = f1ListLeftPage.get(i);
        String liClazz = "";
        boolean isActiveF1 = WebUtils.isFuncActive(f1, fIdLeftPage);

        if(isActiveF1){
            liClazz += " active";
        }
        if(i == 0){
            liClazz += " start";
        }
        if(i == f1ListLeftPage.size() - 1){
            liClazz += " last";
        }
%>
<li class="<%=liClazz%>">
    <a href="${ctx}<%=WebUtils.addQueryParam(f1.getActionUrl(), FUNC_KEY_LEFTPAGE, f1.getId().toString())%>">
        <i class="<%=f1.getIconUrl()%>"></i>
        <span class="title"><%=f1.getFuncName()%></span>
    <%
        if(isActiveF1){
    %>
        <span class="selected"></span>

    <%
        }

        if(f1.getIsLeaf() == null || f1.getIsLeaf() != 1){
            String arrowClazz;
            if(isActiveF1){
                arrowClazz = "class=\"arrow open \"";
            }else{
                arrowClazz = "class=\"arrow \"";
            }
    %>
        <span <%=arrowClazz%>></span>
    <%
        }
    %>
    </a>
        <%
            if(f1.getIsLeaf() == null || f1.getIsLeaf() != 1){
                List<SysFunction> f2List = WebUtils.getFunctionListByLoginUserAndParentFuncId(request, f1.getId());

        %>

        <ul class="sub-menu">
        <%
                for(int j = 0; j < f2List.size(); j++){
                    SysFunction f2 = f2List.get(j);
                    boolean isActiveF2 = WebUtils.isFuncActive(f2, fIdLeftPage);
        %>
            <li <%=isActiveF2?"class=\"active\"":""%>>
                <a href="${ctx}<%=WebUtils.addQueryParam(f2.getActionUrl(), FUNC_KEY_LEFTPAGE, f2.getId().toString())%>"><%=f2.getFuncName()%></a>
            </li>
        <%
                }
        %>
        </ul>
        <%

            }
        %>
</li>
<%
    }
%>
<%--<li class="active">--%>
    <%--<a href="javascript:;">--%>
        <%--<i class="icon-folder-open"></i>--%>
        <%--<span class="title">专辑视频管理</span>--%>
        <%--<span class="arrow "></span>--%>
    <%--</a>--%>
    <%--<ul class="sub-menu">--%>
        <%--<li class="active">--%>
            <%--<a href="${ctx}/cont/contAlbum">专辑管理</a>--%>
        <%--</li>--%>
        <%--<li>--%>
            <%--<a href="${ctx}/cont/contVideoList/2/2332">视频管理</a>--%>
        <%--</li>--%>
    <%--</ul>--%>
<%--</li>--%>
<%--<li>--%>
    <%--<a class="active" href="javascript:;">--%>
        <%--<i class="icon-sitemap"></i>--%>
        <%--<span class="title">运营管理</span>--%>
        <%--<span class="arrow "></span>--%>
    <%--</a>--%>
    <%--<ul class="sub-menu">--%>
        <%--<li>--%>
            <%--<a href="javascript:;">--%>
                <%--栏目推荐管理--%>
            <%--</a>--%>

        <%--</li>--%>
        <%--<li>--%>
            <%--<a href="javascript:;">--%>
                <%--专题管理--%>
            <%--</a>--%>

        <%--</li>--%>
    <%--</ul>--%>
<%--</li>--%>
</ul>
<!-- END SIDEBAR MENU -->
</div>
<!-- END SIDEBAR -->