<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<!--[if IE 8]> <html lang="en" class="ie8"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9"> <![endif]-->
<!--[if !IE]><!--> <html lang="en"> <!--<![endif]-->
<!-- BEGIN HEAD -->
<head>
    <meta charset="utf-8" />
    <title>WhatsLive后台管理系统</title>
    <meta content="width=device-width, initial-scale=1.0" name="viewport" />
    <meta content="" name="description" />
    <meta content="" name="author" />
    <!-- BEGIN GLOBAL MANDATORY STYLES -->
    <link href="${ctx }/static/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx }/static/css/bootstrap-responsive.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx }/static/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx }/static/css/style-metro.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx }/static/css/style.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx }/static/css/style-responsive.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx }/static/css/default.css" rel="stylesheet" type="text/css" id="style_color"/>
    <link href="${ctx }/static/css/uniform.default.css" rel="stylesheet" type="text/css"/>
    <!-- END GLOBAL MANDATORY STYLES -->
    <!-- BEGIN PAGE LEVEL STYLES -->
    <link href="${ctx }/static/css/login.css" rel="stylesheet" type="text/css"/>
    <!-- END PAGE LEVEL STYLES -->
    <link rel="shortcut icon" href="${ctx }/static/image/favicon.ico" />
</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<body class="login">
<!-- BEGIN LOGO -->
<div class="logo">
    <img src="${ctx }/static/image/logo-letv.png" alt="" />
</div>
<!-- END LOGO -->
<!-- BEGIN LOGIN -->
<div class="content">
<!-- BEGIN LOGIN FORM -->
<form class="form-vertical login-form" action="${ctx }/loginsubmit" method="post">
    <h3 class="form-title">WhatsLive后台管理系统</h3>
    <div id="empty-alert" class="alert alert-error hide">
        <button class="close" data-dismiss="alert"></button>
        <span>请输入用户名密码</span>
    </div>
    <c:if test="${message != null}">
    <div class="alert alert-error">
        <button class="close" data-dismiss="alert"></button>
        <span>${message}</span>
    </div>
    </c:if>
    <div class="control-group">
        <!--ie8, ie9 does not support html5 placeholder, so we just show field title for that-->
        <label class="control-label visible-ie8 visible-ie9">用户名</label>
        <div class="controls">
            <div class="input-icon left">
                <i class="icon-user"></i>
                <input class="m-wrap placeholder-no-fix" type="text" placeholder="用户名" id="loginname" name="loginname"/>
            </div>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label visible-ie8 visible-ie9">密码</label>
        <div class="controls">
            <div class="input-icon left">
                <i class="icon-lock"></i>
                <input class="m-wrap placeholder-no-fix" type="password" placeholder="密码" id="password" name="password"/>
            </div>
        </div>
    </div>
    <div class="form-actions">
        <%--<label class="checkbox">--%>
            <%--<input type="checkbox" name="remember" value="1"/> Remember me--%>
        <%--</label>--%>
        <button type="submit" class="btn green pull-right">
            登录 <i class="m-icon-swapright m-icon-white"></i>
        </button>
    </div>
    <%--<div class="forget-password">--%>
        <%--<h4>Forgot your password ?</h4>--%>
        <%--<p>--%>
            <%--no worries, click <a href="javascript:;" class="" id="forget-password">here</a>--%>
            <%--to reset your password.--%>
        <%--</p>--%>
    <%--</div>--%>
    <%--<div class="create-account">--%>
        <%--<p>--%>
            <%--Don't have an account yet ?&nbsp;--%>
            <%--<a href="javascript:;" id="register-btn" class="">Create an account</a>--%>
        <%--</p>--%>
    <%--</div>--%>
</form>
<!-- END LOGIN FORM -->
</div>
<!-- END LOGIN -->
<!-- BEGIN COPYRIGHT -->
<div class="copyright">
    2015 &copy; LETV.
</div>
<!-- END COPYRIGHT -->
<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
<!-- BEGIN CORE PLUGINS -->
<script src="${ctx }/static/js/jquery-1.10.1.min.js" type="text/javascript"></script>
<script src="${ctx }/static/js/jquery-migrate-1.2.1.min.js" type="text/javascript"></script>
<!-- IMPORTANT! Load jquery-ui-1.10.1.custom.min.js before bootstrap.min.js to fix bootstrap tooltip conflict with jquery ui tooltip -->
<script src="${ctx }/static/js/jquery-ui-1.10.1.custom.min.js" type="text/javascript"></script>
<script src="${ctx }/static/js/bootstrap.min.js" type="text/javascript"></script>
<!--[if lt IE 9]>
<script src="${ctx }/static/js/excanvas.min.js"></script>
<script src="${ctx }/static/js/respond.min.js"></script>
<![endif]-->
<script src="${ctx }/static/js/jquery.slimscroll.min.js" type="text/javascript"></script>
<script src="${ctx }/static/js/jquery.blockui.min.js" type="text/javascript"></script>
<script src="${ctx }/static/js/jquery.cookie.min.js" type="text/javascript"></script>
<script src="${ctx }/static/js/jquery.uniform.min.js" type="text/javascript" ></script>
<!-- END CORE PLUGINS -->
<!-- BEGIN PAGE LEVEL PLUGINS -->
<script type="text/javascript" src="${ctx}/static/lib/jquery.validation/jquery.validate.min.js"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="${ctx }/static/js/app.js" type="text/javascript"></script>
<script src="${ctx }/static/js/login.js" type="text/javascript"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<script>
    jQuery(document).ready(function() {
        App.init();
        Login.init();
    });
</script>
<!-- END JAVASCRIPTS -->
<!-- END BODY -->
<body>
</html>