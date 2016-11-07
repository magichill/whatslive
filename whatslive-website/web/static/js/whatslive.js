/**
 *  @createBy   baiyu3@letv.com
 *  @date       2015-08-25
 *  @desc    	whatslive
*/
//define(function(require, exports, module){
$(function(){
	var whatsLive = {
		/**初始化入口**/
		init: function(){
			this.initParam(); //获取页面相关参数
			this.initModule(); //载入依赖模块
			this.initEvent(); //绑定事件
		},
		/*初始化参数*/
		initParam: function(){
        	this.language = $('#language').val();//语言种类
        	this.videoTitle = $('#title').val();//视频名
        	this.videoDesc = $('#describe').val();//视频描述
        	this.videoUrl = $('#shareUrl').val();//视频地址
        	this.videoCoverPic = $('#coverPicture').val();//视频封面
        	this.currentTime = $("#currentTime").val(); //当前时间戳
        	this.startTime = $("#startTime").val(); // 预约开始时间戳
        	this.ua = navigator.userAgent; //浏览器信息
			this.status = $("#status").val(); // 视频状态
		},
		/**初始化模块**/
		initModule: function(){
			// this.wlPlayer = require('bz/whatsLive/js/wlPlayer');//模块渲染
		},
		/**初始化事件**/
		initEvent: function(){
			this.initRender(); //结构渲染
			this.initBind();
			this.initInteractive(); //页面交互
		},
		/**初始化结构渲染**/
		initRender: function(){
			var _this = this,
				pageId = __INFO__.pageid;
			// 只有在微信浏览器里面显示分享到微信按钮
			if (/m_/i.test(pageId) && !/MicroMessenger/i.test(_this.ua)) {
				$('.bdsharebuttonbox .f_btn03').hide();
			}
			if (/m_/i.test(pageId) && pageId != 'm_index' && pageId != 'web_ruler') {
				if (/iPhone/i.test(_this.ua)) {
					$('.no_download a').prop('href', 'http://lehi.letv.com/mobile');
				} else {
					if (_this.language == 'zh') {
						$('.no_download a').prop('href', 'http://lehi.letv.com/mobile');
					} else{
						$('.no_download a').prop('href', 'http://lehi.letv.com/mobile');
					}
				}
			}
			if (/MicroMessenger/i.test(_this.ua)) {
				$('.download a').eq(0).prop('href', 'javascript:;');
				$('.download a').eq(1).prop('href', 'javascript:;');
			}
		},
		initBind: function(){
			var _this = this,
				pageId = __INFO__.pageid;
			if (/MicroMessenger/i.test(_this.ua)) {
				$('.bdsharebuttonbox .f_btn03').on('click',function(){
					$("#dialogShare").show();
				});
				$("#dialogShare").on('click',function(){
					$(this).hide();
				});

				$('.download').on('click',function(){
					$("#dialog").show();
				});
				$("#dialog").on('click',function(){
					$(this).hide();
				});
			}
			if (/index/i.test(pageId)) {
				$(".follow a").eq(1).on('click',function() {
					$(".follow .erweima").show();
				});
				$(".follow .close").on('click',function() {
					$(".follow .erweima").hide();
				});
			}
		},
		/**初始化交互**/
		initInteractive: function(){
			// 预约页倒计时且视频状态为1时
			if (/_ord/i.test(__INFO__.pageid) && this.status == '1') {

				this.countDown($(".count_down"),startLive);
			}
			this.bdShare();
		},
		/**倒计时**/
		countDown: function (obj,callback) {
			var _this = this;
		    var nowTime = _this.currentTime,  // 当前时间
		        endTime = _this.startTime,  // 结束时间
		        daysStr = ''; // 区分中英文版倒计时显示“天”/“days”
		        time = 0; //存储剩余时间各时间单位的数
		    var interval = endTime - nowTime;
		    if (_this.language == 'zh') {
		    	daysStr = '<i>天</i>';
		    } else{
		    	daysStr = '<i>days</i>';
		    }
		    // 存储各时间单位的秒数
		    var seconds = [
		        24 * 60 * 60 * 1000,   //一天的秒数
		        60 * 60 * 1000,        //一小时的秒数
		        60 * 1000,             //一分钟的秒数
		        1000                   //一秒的秒数
		    ]
		    function remainTime() {
		    	var timeStr = '';
		        if (interval <= 0){ // 根据需求看是最后到0s触发还是到1s触发
		            callback();
		            return;
		        }
		        var temTime = interval;
		        for (var i = 0; i < seconds.length; i++) {
		            // 剩余时间各时间单位的数字
		            time = Math.floor(temTime / seconds[i]);
		            if (time < 10) {
		                time = "0" + time;
		            }
		            if (i == 0) {
		            	if (time == '00') {
		            		obj.find('.mess').css('font-size', '58px');
		            	}else{
		            		obj.find('.mess').css('font-size', '36px');
		            	 	timeStr += '<span>'+ time + daysStr +'</span>'; 
		            	}
		            } else if(i == seconds.length-1) {
		            	timeStr += time;
		            }else{
		            	timeStr += time + ':';
		            }
		            // 剩余时间中去除已计算出的时间后所剩余的秒数
		            temTime = temTime % seconds[i];
		        }
		        obj.find('.mess').html(timeStr); // 显示倒计时
		        interval -= 1000;
		        setTimeout(remainTime, 1000);
		    }
	    	remainTime();
		},
		/**分享功能-引用操蛋的百度分享2.0**/
		bdShare: function () {
			var _this = this;
			var shareTitle = _this.videoDesc,
				shareDesc = '',
				shareUrl = _this.videoUrl,
				sharePic = _this.videoCoverPic;
			window._bd_share_config = {
		        common : {
		        	// onBeforeClick百度分享2.0方法：自定义分享内容（多用于同页面多个分享）
		        	onBeforeClick : function (cmd,config) {
		        		config.bdText = shareTitle;
			            config.bdDesc = shareDesc;
			            config.bdUrl = shareUrl;
			            config.bdPic = sharePic;
			            return config;
		        	},
		            bdText : "",
	                bdDesc : "",
	                bdUrl : "",
	                bdPic : ""
		        },
		        share : [{
		        	bdSize: 32
		        }]
		    }
			with(document)0[(getElementsByTagName('head')[0]||body).appendChild(createElement('script')).src='http://bdimg.share.baidu.com/static/api/js/share.js?cdnversion='+~(-new Date()/36e5)];
		}
	};
	/**入口**/
	whatsLive.init();
});
//});