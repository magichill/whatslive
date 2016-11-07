/*
 * 注入权限验证配置
 *
 * @param debug 调试模式
 * @param appId 公众号的唯一标识
 * @param timestamp 生成签名的时间戳
 * @param nonceStr 生成签名的随机串
 * @param signature 签名
 * @param jsApiList 需要使用的JS接口列表
 */
wx.config({
        debug: false,
        appId: $("#appid").val(),
        timestamp: $("#signTimestamp").val(),
        nonceStr: $("#signNonceStr").val(),
        signature: $("#signature").val(),
        jsApiList: [
            'onMenuShareTimeline',
            'onMenuShareAppMessage',
            'onMenuShareQQ'
        ]
    });

/*
 * 接口注入
 *
 */
wx.ready(function () {

	/*
	 * 朋友分享接口注入
	 *
	 * @param title 标题
	 * @param desc 文案
	 * @param link 链接
	 * @param imgUrl 图片
	 */
	wx.onMenuShareAppMessage({

		title: $("#title").val(),
		desc: $("#describe").val(),
		link: $("#shareUrl").val(),
		imgUrl: $("#coverPicture").val(),
		success: function (res) {
		},
		cancel: function (res) {
		},
		fail: function (res) {
			alert(JSON.stringify(res));
		}
	});

	/*
	 * 朋友圈分享接口注入
	 *
	 * @param title 标题
	 * @param link 链接
	 * @param imgUrl 图片
	 */
	wx.onMenuShareTimeline({

		title: $("#describe").val(),
		link: $("#shareUrl").val(),
		imgUrl: $("#coverPicture").val(),
		success: function (res) {
		},
		cancel: function (res) {
		},
		fail: function (res) {
			alert(JSON.stringify(res));
		}
	});

	/*
	 * QQ分享接口注入
	 *
	 * @param title 标题
	 * @param desc 文案
	 * @param link 链接
	 * @param imgUrl 图片
	 */
	wx.onMenuShareQQ({
		title: $("#title").val(),
		desc: $("#describe").val(),
		link: $("#shareUrl").val(),
		imgUrl: $("#coverPicture").val(),
		success: function (res) {
		},
		cancel: function (res) {
		},
		fail: function (res) {
			alert(JSON.stringify(res));
		}
	});
});
	


