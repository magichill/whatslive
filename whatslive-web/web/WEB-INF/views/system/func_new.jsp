<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>资源新增界面</title>
</head>
<body>
<!-- BEGIN PAGE CONTAINER-->
<div class="container-fluid">
    <!-- BEGIN PAGE CONTENT LIST-->
    <div id="contentList" class="row-fluid">
        <!-- BEGIN FORM-->
        <form id="form_func" action="#" class="form-horizontal">
            <input type="hidden" name="id" value="${func.id}"/>
            <input type="hidden" name="parentFuncId" value="${func.parentFuncId}"/>
            <div class="control-group">
                <label class="control-label">资源名称<span class="required">*</span></label>
                <div class="controls">
                    <input type="text" name="funcName" class="span6 m-wrap" value="${func.funcName}"/>
                </div>
            </div>
            <c:if test="${opt != 'new'}">
                <div class="control-group">
                    <label class="control-label">父节点<span class="required">*</span></label>
                    <div class="controls">
                        <span class="text">${func.parentFuncId}</span>
                    </div>
                </div>
            </c:if>
            <div class="control-group">
                <label class="control-label">是否叶子节点<span class="required">*</span></label>
                <div class="controls">
                    <c:if test="${opt != 'edit'}">
                        <label class="radio"><input type="radio"  name="isLeaf" value="0" checked="true"/>非</label>
                        <label class="radio"><input type="radio"  name="isLeaf" value="1" />叶子</label>
                    </c:if>
                    <c:if test="${opt == 'edit'}">
                        <c:if test="${func.isLeaf == '0'}"><!--

                            <label class="radio"><input type="radio"  name="isLeaf" value="0" checked/>非</label>
                            <label class="radio"><input type="radio"  name="isLeaf" value="1" />叶子</label>
                            -->
                            <span class="text">非</span>
                            <input type="hidden" name="isLeaf" value="0">
                        </c:if>
                        <c:if test="${func.isLeaf == '1'}">
                            <label class="radio"><input type="radio"  name="isLeaf" value="0" />非</label>
                            <label class="radio"><input type="radio"  name="isLeaf" value="1" checked/>叶子</label>
                        </c:if>

                    </c:if>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">资源链接<span class="required">*</span></label>
                <div class="controls">
                    <input type="text" name="actionUrl" class="span6 m-wrap" value="${func.actionUrl}"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">图标标识</label>
                <div class="controls">
                    <input type="text" name="iconUrl" class="span6 m-wrap" value="${func.iconUrl}"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">排序号<span class="required">*</span></label>
                <div class="controls">
                    <input type="text" name="funcOrder" class="span6 m-wrap" value="${func.funcOrder}"/>
                </div>
            </div>
            <div class="form-actions">
                <button id="btn_func_save" type="button" class="btn green">保存</button>
                <button id="btn_func_back" type="button" class="btn">返回</button>
            </div>
        </form>
        <!-- END FORM-->
    </div>
    <!-- END PAGE CONTENT LIST-->

</div>
<!-- END PAGE CONTAINER-->
</body>
</html>