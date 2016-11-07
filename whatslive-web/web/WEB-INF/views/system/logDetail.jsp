<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<body>
<div class="span6" id="content_log_list">
    <div class="portlet">
        <div class="portlet-title line">
            <div class="caption"><i class="icon-comments"></i>操作内容</div>
        </div>
        <div class="portlet-body" id="chats">
            <div class="scroller" data-height="435px" data-always-visible="1" data-rail-visible1="1">
                <ul class="chats">
                    <li class="in">
                        <div class="message">
                            <span class="arrow"></span>
                            <a href="#" class="name">修改前</a>
                            <span class="label"></span>
                            <textarea class="span6 m-wrap" rows="4" style="width: 472px"> ${sysLog.opBefore}</textarea>
                        </div>
                    </li>
                    <li class="in">
                        <div class="message">
                            <span class="arrow"></span>
                            <a href="#" class="name">修改后</a>
                            <span class="label"></span>
                            <textarea class="span6 m-wrap" rows="4" style="width: 472px"> ${sysLog.opAfter}</textarea>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>
</body>
</html>