<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="/WEB-INF/views/common/webHeader.jsp" %>

    <title><fmt:message key="web.termsservice.title"/></title>
    <script>
        var __INFO__ = {
            pageid : 'web_ruler'
        }
    </script>
    <%--<script src="${ctx }/static/js/whatslive.js" type="text/javascript"></script>--%>
    <%--<script src="http://js.letvcdn.com/lc03_js/201509/15/09/41/3rd/abc.js" type="text/javascript"></script>--%>

</head>

<body>
<div class="wlive_wrap">
    <input type="hidden" id="language" value="${language}"/>
    <div class="play_bg play_bg01">
        <!--头部 start-->
        <div class="co_top">
            <div class="co_top_box">
                <div class="logo">
                    <a href="${ctx }/"><img src="<fmt:message key='logo.first'/>"></a>
                    <i><img src="<fmt:message key='logo.second'/>"></i>
                </div>
                <dl class="download">
                    <dt class="title"><fmt:message key="info.download"/></dt>
                    <dd><a class="app_btn01" target="_blank" href="https://itunes.apple.com/cn/app/le-hai-zhi-bo-yuan-chuang/id1028984886?mt=8"><img src="<fmt:message key='app.download.apple'/>"></a></dd>
                    <dd>
                        <c:choose>
                            <c:when test="${language == 'zh'}">
                                <a class="app_btn02" href="${ctx }/static/apk/lehi_download.apk">
                                    <img src="<fmt:message key='app.download.google'/>">
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a class="app_btn02" href="#" target="_blank">
                                    <img src="<fmt:message key='app.download.google'/>">
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </dd>
                </dl>
            </div>
        </div>
        <!--头部 end-->
        <c:choose>
            <c:when test="${language == 'zh'}">
                <div class="co_box word C_word">
                    <div class="back"><a href="${ctx }/">><fmt:message key="back"/></a></div>
                    <p class="title"><fmt:message key="termsservice.contentstitle"/></p>
                    <div class="detail">
                        <p>尊敬的用户您好，请您仔细阅读以下条款，如果您对本协议的任何条款表示异议，您可以选择不使用；使用则意味着您将同意遵守本协议下全部规定，以及我们后续对使用协议随时所作的任何修改，并完全服从于我们的统一管理。</p>
                        <h4><i>第一章 总则</i></h4>
                        <p><i>第1条</i> 乐嗨直播是乐嗨团队(以下统称"本团队")向用户提供的一种更方便的进行视频直播和观看视频直播服务的平台。</p>
                        <p><i>第2条</i> 乐嗨直播所有权、经营权、管理权均属本团队。</p>
                        <p><i>第3条</i> 本协议最终解释权归属本团队。</p>
                        <h4><i>第二章 用户说明</i></h4>
                        <p><i>第4条</i> 凡是下载、浏览和发布内容的用户均为乐嗨直播用户(以下统称"用户")。</p>
                        <p><i>第5条</i> 用户的个人信息受到保护，不接受任何个人或单位的查询。国家机关依法查询除外，用户的个人设置公开除外。</p>
                        <p><i>第6条</i> 用户享有言论自由的权利。</p>
                        <p><i>第7条</i> 用户的言行不得违反《计算机信息网络国际联网安全保护管理办法》、《互联网信息服务管理办法》、《互联网电子公告服务管理规定》、《维护互联网安全的决定》、《互联网新闻信息服务管理规定》等相关法律规定，不得在乐嗨直播上发布、传播或以其它方式传送含有下列内容之一的信息：</p>
                        <p>1.反对宪法所确定的基本原则的；</p>
                        <p>2.危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；</p>
                        <p>3.损害国家荣誉和利益的；</p>
                        <p>4.煽动民族仇恨、民族歧视、破坏民族团结的；</p>
                        <p>5.破坏国家宗教政策，宣扬邪教和封建迷信的；</p>
                        <p>6.散布谣言，扰乱社会秩序，破坏社会稳定的；</p>
                        <p>7.散布淫秽、色情、赌博、暴力、凶杀、恐怖或者教唆犯罪的；</p>
                        <p>8.侮辱或者诽谤他人，侵害他人合法权利的；
                        <p>9.煽动非法集会、结社、游行、示威、聚众扰乱社会秩序的；</p>
                        <p>10.以非法民间组织名义活动的；</p>
                        <p>11.含有虚假、有害、胁迫、侵害他人隐私、骚扰、侵害、中伤、粗俗、猥亵、或其它道德上令人反感的内容；</p>
                        <p>12.含有中国法律、法规、规章、条例以及任何具有法律效力之规范所限制或禁止的其它内容的。</p>
                        <p><i>第8条</i> 用户不得在乐嗨直播内发布任何形式的广告。</p>
                        <p><i>第9条</i> 用户应承担一切因其个人的行为而直接或间接导致的民事或刑事法律责任，因用户行为给本团队造成损失的，用户应负责赔偿。</p>
                        <p><i>第10条</i> 本团队拥有对违反协议的用户进行处理的权力，直至禁止其在乐嗨直播内发布信息。</p>
                        <p><i>第11条</i> 禁止任何使用非法软件进行刷内容、违规发布内容的行为。</p>
                        <p><i>第12条</i> 任何用户发现乐嗨直播上有内容涉嫌侮辱或者诽谤他人、侵害他人合法权益的或违反本协议的，有权进行直接举报。</p>
                        <p><i>第13条</i> 为了能够给广大用户提供一个优质的交流平台，同时使得乐嗨直播能够良性、健康的发展，将对涉及反动、色情和发布不良内容的用户，进行严厉处理。一经发现此类行为，将给予永久封禁并清空所有发言的处罚。</p>
                        <h4><i>第三章 权利声明</i></h4>
                        <p><i>第14条</i> 乐嗨直播发布的内容仅代表作者观点，与本团队无关。对于用户言论的真实性引发的全部责任，由用户自行承担，本团队不承担任何责任。</p>
                        <p><i>第15条</i> 用户之间因使用乐嗨直播而产生或可能产生的任何纠纷和/或损失，由用户自行解决并承担相应的责任，与本团队无关。</p>
                        <p><i>第16条</i> 用户在乐嗨直播上发布的内容，本团队有权转载或引用。</p>
                        <p><i>第17条</i> 用户在任何时间段在乐嗨直播上发表的任何内容的著作财产权，用户许可本团队在全世界范围内免费地、永久性地、不可撤销地、可分许可地和非独家地使用的权利，包括但不限于：复制权、发行权、出租权、展览权、表演权、放映权、广播权、信息网络传播权、摄制权、改编权、翻译权、汇编权以及《著作权法》规定的由著作权人享有的其他著作财产权利。并且，用户许可本团队有权利就任何主体侵权而单独提起诉讼，并获得全部赔偿。</p>
                        <h4><i>第四章 处罚规则</i></h4>
                        <p><i>第18条</i> 本团队郑重提醒用户，若出现下列情况任意一种或几种，将承担包括被关闭全部或者部分权限、被暂停或被禁止使用的后果，情节严重的，还将承担相应的法律责任。</p>
                        <p>1.使用不雅或不恰当昵称；</p>
                        <p>2.发布含有猥亵、色情、人身攻击和反政府言论等非法或侵权言论的；</p>
                        <p>3.从事非法商业活动；</p>
                        <p>4.假冒管理人员或破坏管理人员形象；</p>
                        <p>5.使用非法软件进行刷内容、违规发布内容的行为；</p>
                        <p>6.其他本团队认为不恰当的情况。</p>
                        <p><i>第19条</i> 凡内容出现以下情况之一，本团队管理人员有权不提前通知作者直接删除，并依照有关规定作相应处罚。情节严重者，本团队管理人员有权对其做出关闭部分权限、暂停直至禁止使用。</p>
                        <p>1.发表含有本协议第7条禁止发布、传播内容的文章；</p>
                        <p>2.发表无意义的灌水内容；</p>
                        <p>3.同一内容多次出现的；</p>
                        <p>4.违反本协议第8条的规定，发布广告的；</p>
                        <p>5.内容包含有严重影响用户浏览的内容或格式的；</p>
                        <p>6.其他本团队认为不恰当的情况。</p>
                        <p>7.侵害第三人知识产权或其他权利；</p>
                        <p>8.内容包含病毒或被认定为恶意软件、间谍软件。</p>
                        <p>9.其他乐视公司认为不恰当的内容。</p>
                        <p>如果发布者作品的内容是因为有瑕疵的或为恶意软件而不能自愿性移除时，乐视公司有权向发布者收取因移除有瑕疵的或为恶意软件的所有费用。</p>
                        <h4><i>第五章 隐私保护</i></h4>
                        <p><i>第20条</i> 乐嗨直播不对外公开或向第三方提供单个用户的注册资料及用户在使用网络服务时存储在本网站的非公开内容，但下列情况除外：</p>
                        <p>1.事先获得用户的明确授权；</p>
                        <p>2.根据有关的法律法规要求；</p>
                        <p>3.按照相关政府主管部门的要求；</p>
                        <p>4.该第三方同意承担与乐嗨直播同等的保护用户隐私的责任；</p>
                        <p>5.在不透露单个用户隐私资料的前提下，乐嗨直播有权对整个用户数据库进行分析并对用户数据库进行商业上的利用。</p>
                        <p>6.请尊重他人隐私。不要公布他人的私密信息。隐私信息包括下列例子：</p>
                        <p>&nbsp;&nbsp;&nbsp;&nbsp;•他人身份证号码、出生证明</p>
                        <p>&nbsp;&nbsp;&nbsp;&nbsp;•信用卡类信息</p>
                        <p>&nbsp;&nbsp;&nbsp;&nbsp;•住宅地址或其他视为隐私的地点信息</p>
                        <p>&nbsp;&nbsp;&nbsp;&nbsp;•非公开信息，个人电话号码、邮箱地址、微信、微博、QQ号等</p>
                        <p>&nbsp;&nbsp;&nbsp;&nbsp;•未经视频对象允许拍摄的亲热视频或照片</p>
                        <p>&nbsp;&nbsp;&nbsp;&nbsp;•在相关法律下被视为个人隐私的视频或照片</p>
                        <h4><i>第六章 附则</i></h4>
                        <p><i>第21条</i> 关于打击网络谣言的司法解释：《最高人民法院、最高人民检察院关于办理利用信息网络实施诽谤等刑事案件适用法律若干问题的解释》已于2013年9月5日由最高人民法院审判委员会第1589次会议、2013年9月2日由最高人民检察院第十二届检察委员会第9次会议通过，现予公布，自2013年9月10日起施行。</p>
                        <p><i>第22条</i> 所有用户发布的内容而引起的法律纠纷，与本团队无关。</p>
                        <p><i>第23条</i> 乐嗨直播如因系统维护或升级等而需暂停服务时，将事先公告。若因硬件故障或其它不可抗力而导致暂停服务，于暂停服务期间造成的一切不便与损失，本团队不负任何责任。由于乐嗨直播的产品调整导致信息丢失和/或其他结果的，本团队不承担任何责任。</p>
                        <p><i>第24条</i> 本协议未涉及的问题参见国家有关法律法规，当本协议与国家法律法规冲突时，以国家法律法规为准。</p>
                        <p><i>第25条</i> 任何权利人认为乐视公司应用商店提供的服务所涉及的作品、表演、录音录像制品，侵犯自己的信息网络传播权或者被删除、改变了自己的权利管理电子信息的，可以向该乐视公司提交书面通知，要求乐视公司删除该作品、表演、录音录像制品，或者断开与该作品、表演、录音录像制品的链接。通知书应当包含下列内容：</p>
                        <p>&nbsp;&nbsp;&nbsp;&nbsp;(一)权利人的姓名(名称)、联系方式和地址；</p>
                        <p>&nbsp;&nbsp;&nbsp;&nbsp;(二)要求删除或者断开链接的侵权作品、表演、录音录像制品的名称和网络地址；</p>
                        <p>&nbsp;&nbsp;&nbsp;&nbsp;(三)构成侵权的初步证明材料。</p>
                        <p>&nbsp;&nbsp;&nbsp;&nbsp;权利人应当对通知书的真实性负责。</p>
                        <p>如通知不含以上必备条款，乐视公司有权拒绝配合，并不承担任何责任。</p>
                        乐嗨团队联系方式：691101015@qq.com
                        <p>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="co_box word E_word">
                    <div class="back"><a href="${ctx }/">><fmt:message key="back"/></a></div>
                    <p class="title"><fmt:message key="termsservice.contentstitle"/></p>
                    <div class="detail">
                        <p>Effective: August 27, 2015</p>
                        <p>These Terms govern your access to and use of WhatsLIVE, operated and provided by WhatsLIVETeam., including any WhatsLIVE mobile applications and websites ("WhatsLIVE Services" or "WhatsLIVE") and any videos, information, text, graphics, photos or other materials uploaded, downloaded or appearing on WhatsLIVE (collectively referred to as "Content"). Your access to and use of WhatsLIVE is conditioned on your acceptance of and compliance with these Terms. By accessing or using WhatsLIVE Services you agree to be bound by these Terms.</p>
                        <h4>1. Basic Terms</h4>
                        <p>You are responsible for your use of WhatsLIVE Services, for any Content you post to WhatsLIVE, and for any consequences thereof. The Content you submit, post, or display will be able to be viewed by other users and through third party services and websites. You should only provide Content you are comfortable sharing with others under these Terms.</p>
                        <p>Tip: The Content you share with others may be viewed around the world, including on other services and websites.</p>
                        <p>You may use WhatsLIVE only if you can form a binding contract with WhatsLIVE Team and are not a person barred from receiving services under the laws of China or other applicable jurisdiction. If you are accepting these Terms and using WhatsLIVE on behalf of a company, organization, government, or other legal entity, you represent and warrant that you are authorized to do so. You may use WhatsLIVE only in compliance with these Terms and all applicable local, state, national, and international laws, rules and regulations.</p>
                        <p>The WhatsLIVE Services are always evolving and the form and nature of WhatsLIVE may change from time to time without prior notice to you. In addition, WhatsLIVE Team may stop (permanently or temporarily) providing WhatsLIVE (or any features within WhatsLIVE) to you or to users generally and may not be able to provide you with prior notice. We also retain the right to create limits on use and storage at our sole discretion at any time without prior notice to you.</p>
                        <h4>2. Privacy</h4>
                        <p>Our collection and use of your information is governed by the Twitter Privacy Policy , Facebook Privacy Policy and the WhatsLIVE Privacy Statement. You understand that through your use of WhatsLIVE you consent to the collection and use of this information, including the transfer of this information to the China and/or other countries for storage, processing and use by WhatsLIVE Team. As part of providing you the WhatsLIVE Services, we may need to provide you with certain communications, such as service announcements and administrative messages. These communications are considered part of the WhatsLIVE Services and your WhatsLIVE account, which you may not be able to opt-out from receiving.</p>
                        <h4>3. Access Credentials</h4>
                        <p>You are responsible for safeguarding the credentials you use to access WhatsLIVE and for any activities or actions under your account. We encourage you to use "strong" passwords (passwords that use a combination of upper and lower case letters, numbers and symbols) with your Twitter account or Facebook account that you may connect to your WhatsLIVE account. The WhatsLIVE  team cannot and will not be liable for any loss or damage arising from your failure to comply with the above requirements.</p>
                        <h4>4. Content on WhatsLIVE </h4>
                        <p>All Content, whether publicly posted or privately transmitted, is the sole responsibility of the person who originated such Content. We may, but are not required to monitor or control the Content posted via WhatsLIVE and we cannot take responsibility for such Content. Any use or reliance on any Content or materials posted via WhatsLIVE or obtained by you through WhatsLIVE is at your own risk.</p>
                        <p>We do not endorse, support, represent or guarantee the completeness, truthfulness, accuracy, or reliability of any Content or communications posted via Periscope or endorse any opinions expressed via WhatsLIVE. You understand that by using WhatsLIVE, you may be exposed to Content that might be offensive, harmful, inaccurate or otherwise inappropriate, or in some cases, postings that have been mislabeled or are otherwise deceptive. Under no circumstances will WhatsLIVE Team be liable in any way for any Content, including, but not limited to, any errors or omissions in any Content, or any loss or damage of any kind incurred as a result of the use of any Content posted, emailed, transmitted or otherwise made available via WhatsLIVE or broadcast elsewhere.</p>
                        <p>Tip: We work together with our community to ensure our content rules are respected. Report any content that violates our Community Guidelines.</p>
                        <h4>5. Your Rights</h4>
                        <p>You retain your rights to any Content you submit, post or display on or through WhatsLIVE. In order to make WhatsLIVE available to you and other users, WhatsLIVE Team needs a license from you. By submitting, posting or displaying Content on or through WhatsLIVE, you grant us a worldwide, non-exclusive, royalty-free license (with the right to sublicense) to use, copy, reproduce, process, adapt, modify, publish, transmit, display and distribute such Content in any and all media or distribution methods (now known or later developed).</p>
                        <p>You agree that this license includes the right for WhatsLIVE Team to provide, promote, and improve WhatsLIVE and to make Content submitted to or through WhatsLIVE available to other companies, organizations or individuals who partner with WhatsLIVE Team for the syndication, broadcast, distribution or publication of such Content on other media and services, subject to our terms and conditions for such Content use.</p>
                        <p>Tip: You own your Content, but this license allows us to make your Content available to the rest of the world and to let others do the same.</p>
                        <p>Such additional uses by WhatsLIVE Team., or other companies, organizations or individuals who partner with WhatsLIVE Team., may be made with no compensation paid to you with respect to the Content that you submit, post, transmit or otherwise make available through WhatsLIVE.</p>
                        <p>We may modify or adapt your Content in order to transmit, display or distribute it over computer networks and in various media and/or make changes to your Content as are necessary to conform and adapt that Content to any requirements or limitations of any networks, devices, services or media.</p>
                        <p>You are responsible for your use of WhatsLIVE, for any Content you provide, and for any consequences thereof, including the use of your Content by other users and our third party partners. You understand that your Content may be syndicated, broadcast, distributed, or published by our partners and if you do not have the right to submit Content for such use, it may subject you to liability. WhatsLIVE Team will not be responsible or liable for any use of your Content by WhatsLIVE Team in accordance with these Terms. You represent and warrant that you have all the rights, power and authority necessary to grant the rights granted herein to any Content that you submit.</p>
                        <h4>6. Your License to Use WhatsLIVE</h4>
                        <p>WhatsLIVE Team gives you a personal, worldwide, royalty-free, non-assignable and non-exclusive license to use the software that is provided to you by WhatsLIVE Team as part of the WhatsLIVE Services. This license is for the sole purpose of enabling you to use and enjoy the benefit of WhatsLIVE as provided by WhatsLIVE Team., in the manner permitted by these Terms.</p>
                        <h4>7. WhatsLIVE Rights</h4>
                        <p>All right, title, and interest in and to WhatsLIVE Services (excluding Content provided by users) are and will remain the exclusive property of WhatsLIVE Team and its licensors. WhatsLIVE is protected by copyright, trademark, and other laws of both the China and foreign countries. WhatsLIVE Team reserves all rights not expressly granted in these Terms. You acknowledge and agree that any feedback, comments, or suggestions you may provide regarding WhatsLIVE Team or WhatsLIVE Services is entirely voluntary and we will be free to use such feedback, comments or suggestions as we see fit and without any obligation to you.</p>
                        <h4>8. Restrictions on Content and Use of WhatsLIVE</h4>
                        <p>Please review the  WhatsLIVE Community Guidelines (which are part of these Terms) to better understand what is prohibited on WhatsLIVE. We reserve the right at all times (but will not have an obligation) to remove or refuse to distribute any Content on WhatsLIVE, to suspend or terminate users, and to reclaim usernames without liability to you. We also reserve the right to access, read, preserve, and disclose any information as we reasonably believe is necessary to (i) satisfy any applicable law, regulation, legal process or governmental request, (ii) enforce the Terms, including investigation of potential violations hereof, (iii) detect, prevent, or otherwise address fraud, security or technical issues, (iv) respond to user support requests, or (v) protect the rights, property or safety of Twitter, Inc., its users and the public.</p>
                        <p>Tip: We may remove your Content or disable your account if you violate our Community Guidelines or these Terms.</p>
                        <p>You may not do any of the following while accessing or using WhatsLIVE: (i) access, tamper with, or use non-public areas of Periscope Services, WhatsLIVE's computer systems, or the technical delivery systems of WhatsLIVE's providers; (ii) probe, scan, or test the vulnerability of any system or network or breach or circumvent any security or authentication measures; (iii) access or search or attempt to access or search WhatsLIVE Services by any means (automated or otherwise) other than through our currently available, published interfaces that are provided by WhatsLIVE Team (and only pursuant to those terms and conditions), unless you have been specifically allowed to do so in a separate agreement with WhatsLIVE Team (NOTE: crawling WhatsLIVE Services is permissible if done in accordance with the provisions of the robots.txt file, however, scraping WhatsLIVE Services without the prior consent of WhatsLIVE Team is expressly prohibited); (iv) forge any TCP/IP packet header or any part of the header information in any email or posting, or in any way use WhatsLIVE Services to send altered, deceptive or false source-identifying information; or (v) interfere with, or disrupt, (or attempt to do so), the access of any user, host or network, including, without limitation, sending a virus, overloading, flooding, spamming, mail-bombing WhatsLIVE Services, or by scripting the creation of Content in such a manner as to interfere with or create an undue burden on WhatsLIVE Services.</p>
                        <h4>9. Copyright Policy</h4>
                        <p>WhatsLIVE Team respects the intellectual property rights of others and expects users of WhatsLIVE Services to do the same. We will respond to notices of alleged copyright infringement that comply with applicable law and are properly provided to us. If you believe that your Content has been copied in a way that constitutes copyright infringement, please provide us with the following information: (i) a physical or electronic signature of the copyright owner or a person authorized to act on their behalf; (ii) identification of the copyrighted work claimed to have been infringed; (iii) identification of the material that is claimed to be infringing or to be the subject of infringing activity and that is to be removed or access to which is to be disabled, and information reasonably sufficient to permit us to locate the material; (iv) your contact information, including your address, telephone number, and an email address; (v) a statement by you that you have a good faith belief that use of the material in the manner complained of is not authorized by the copyright owner, its agent, or the law; and (vi) a statement that the information in the notification is accurate, and, under penalty of perjury, that you are authorized to act on behalf of the copyright owner.</p>
                        <p>We reserve the right to remove Content alleged to be infringing without prior notice and at our sole discretion. In appropriate circumstances, WhatsLIVE will also terminate a user's account if the user is determined to be a repeat infringer. Our designated copyright agent for notice of alleged copyright infringement appearing on WhatsLIVE is:</p>
                        <p>Copyright Agent – WhatsLIVE Team</p>
                        <p>105 Yaojiayuan St., Chaoyang District</p>
                        <p>Beijing, China</p>
                        <p>Email: </p>
                        <h4>10. Ending These Terms</h4>
                        <p>The Terms will continue to apply until terminated by either you or WhatsLIVE Team as follows.</p>
                        <p>You may end your legal agreement with Twitter, Inc. at any time for any reason by deactivating your accounts and discontinuing your use of WhatsLIVE.</p>
                        <p>We may suspend or terminate your accounts or cease providing you with all or part of WhatsLIVE Services at any time for any reason, including, but not limited to, if we reasonably believe: (i) you have violated these Terms, (ii) you create risk or possible legal exposure for us; or (iii) our provision of WhatsLIVE Services to you is no longer commercially viable. We will make reasonable efforts to notify you through the WhatsLIVE Services, or the next time you attempt to access your account, or by an email address you have provided us (if applicable).</p>
                        <p>In all such cases, the Terms shall terminate, including, without limitation, your license to use WhatsLIVE Services, except that the following sections shall continue to apply: 4, 5, 7, 8, 10, 11, and 12.</p>
                        <p>Nothing in this section shall affect the rights of WhatsLIVE Team to change, limit or stop the provision of WhatsLIVE Services without prior notice, as provided above in section 1.</p>
                        <h4>11.Disclaimers and Limitations of Liability</h4>
                        <p>Please read this section carefully since it limits the liability of WhatsLIVE Team and its parents, subsidiaries, affiliates, related companies, officers, directors, employees, agents, representatives, partners, and licensors (collectively, the "WhatsLIVE Entities"). Each of the subsections below only applies up to the maximum extent permitted under applicable law. Some jurisdictions do not allow the disclaimer of implied warranties or the limitation of liability in contracts, and as a result the contents of this section may not apply to you. Nothing in this section is intended to limit any rights you may have which may not be lawfully limited.</p>
                        <p>A. WhatsLIVE Services Are Available "AS-IS"</p>
                        <p>Your access to and use of WhatsLIVE Services or any Content is at your own risk. You understand and agree that WhatsLIVE Services are provided to you on an "AS IS" and "AS AVAILABLE" basis. Without limiting the foregoing, WhatsLIVE ENTITIES DISCLAIM ALL WARRANTIES AND CONDITIONS, WHETHER EXPRESS OR IMPLIED, OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT.</p>
                        <p>WhatsLIVE Entities make no warranty and disclaim all responsibility and liability for: (i) the completeness, accuracy, availability, timeliness, security or reliability of WhatsLIVE or any Content; (ii) any harm to your computer system, loss of data, or other harm that results from your access to or use of WhatsLIVE, or any Content; (iii) the deletion of, or the failure to store or to transmit, any Content and other communications maintained by WhatsLIVE; (iv) whether Periscope will meet your requirements or be available on an uninterrupted, secure, or error-free basis. No advice or information, whether oral or written, obtained from WhatsLIVE Entities or through WhatsLIVE, will create any warranty not expressly made herein.</p>
                        <p>B. Links</p>
                        <p>WhatsLIVE Services may contain links to third-party websites or resources. You acknowledge and agree that we are not responsible or liable for: (i) the availability or accuracy of such websites or resources; or (ii) the content, products, or services on or available from such websites or resources. Links to such websites or resources do not imply any endorsement by WhatsLIVE Entities of such websites or resources or the content, products, or services available from such websites or resources. You acknowledge sole responsibility for and assume all risk arising from your use of any such websites or resources.</p>
                        <p>C. Limitation of Liability</p>
                        <p>TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW, THE WhatsLIVE ENTITIES SHALL NOT BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, CONSEQUENTIAL OR PUNITIVE DAMAGES, OR ANY LOSS OF PROFITS OR REVENUES, WHETHER INCURRED DIRECTLY OR INDIRECTLY, OR ANY LOSS OF DATA, USE, GOOD-WILL, OR OTHER INTANGIBLE LOSSES, RESULTING FROM (i) YOUR ACCESS TO OR USE OF OR INABILITY TO ACCESS OR USE WhatsLIVE; (ii) ANY CONDUCT OR CONTENT OF ANY THIRD PARTY ON WhatsLIVE, INCLUDING WITHOUT LIMITATION, ANY DEFAMATORY, OFFENSIVE OR ILLEGAL CONDUCT OF OTHER USERS OR THIRD PARTIES; (iii) ANY CONTENT OBTAINED FROM WhatsLIVE; OR (iv) UNAUTHORIZED ACCESS, USE OR ALTERATION OF YOUR TRANSMISSIONS OR CONTENT.</p>
                        <p>IN NO EVENT SHALL THE AGGREGATE LIABILITY OF THE WhatsLIVE ENTITIES EXCEED THE GREATER OF ONE HUNDRED U.S. DOLLARS (U.S. $100.00) OR THE AMOUNT YOU PAID WhatsLIVE Team, IF ANY, IN THE PAST SIX MONTHS FOR WhatsLIVE SERVICES GIVING RISE TO THE CLAIM.</p>
                        <p>THE LIMITATIONS OF THIS SUBSECTION SHALL APPLY TO ANY THEORY OF LIABILITY, WHETHER BASED ON WARRANTY, CONTRACT, STATUTE, TORT (INCLUDING NEGLIGENCE) OR OTHERWISE, AND WHETHER OR NOT THE WhatsLIVE ENTITIES HAVE BEEN INFORMED OF THE POSSIBILITY OF ANY SUCH DAMAGE, AND EVEN IF A REMEDY SET FORTH HEREIN IS FOUND TO HAVE FAILED OF ITS ESSENTIAL PURPOSE.</p>
                        <h4>12. General Terms</h4>
                        <p>A. Waiver and Severability</p>
                        <p>The failure of T WhatsLIVE Team to enforce any right or provision of these Terms will not be deemed a waiver of such right or provision. In the event that any provision of these Terms is held to be invalid or unenforceable, then that provision will be limited or eliminated to the minimum extent necessary, and the remaining provisions of these Terms will remain in full force and effect.</p>
                        <p>B. Controlling Law and Jurisdiction</p>
                        <p>These Terms and any action related thereto will be governed by the laws of China without regard to or application of its conflict of law provisions or your state or country of residence. All claims, legal proceedings or litigation arising in connection with WhatsLIVE Services will be brought solely in the federal or state courts located in China, and you consent to the jurisdiction of and venue in such courts and waive any objection as to inconvenient forum.</p>
                        <p>If you are a federal, state, or local government entity in China using WhatsLIVE in your official capacity and legally unable to accept the controlling law, jurisdiction or venue clauses above, then those clauses do not apply to you. For such Chinese government entities, these Terms and any action related thereto will be governed by the laws of the China (without reference to conflict of laws) and, in the absence of law and to the extent permitted under law, the laws of China (excluding choice of law).</p>
                        <p>C. Entire Agreement</p>
                        <p>These Terms, the  WhatsLIVE Community Guidelines, the  WhatsLIVE Privacy Policy and our Privacy Statement are the entire and exclusive agreement between WhatsLIVE Team and you regarding WhatsLIVE Services (excluding any services for which you have a separate agreement with WhatsLIVE Team that is explicitly in addition or in place of these Terms), and these Terms supersede and replace any prior agreements between WhatsLIVE Team and you regarding WhatsLIVE Services.</p>
                        <p>We may revise these Terms from time to time, and the most current version will always be at www.whatsliveapp.com If the revision, in our sole discretion, is material we will notify you through WhatsLIVE Services. If you do not wish to be bound by any such revisions to the Terms, you must end these Terms with us as set forth in Section 10 above. By continuing to access or use WhatsLIVE Services after those revisions become effective, you agree to be bound by the revised Terms.</p>
                        <p>Either you live in or outside China, these Terms are an agreement between you and WhatsLIVE Team, 105 Yaojiayuan Street, Chaoyang District, Beijing,China. If you have any questions about these Terms, please contact us contact@whatslive.gmail（待补充） </p>
                        <h4>WhatsLIVE Privacy Statement</h4>
                        <p>WhatsLIVE is the easiest way to start or watch a live video broadcast. The following describes practices that are specific to WhatsLIVE.</p>
                        <p> WhatsLIVE is designed to help you broadcast video publicly all around the world.</p>
                        <p>When you broadcast a video, your broadcast is public by default. This includes the metadata provided with your broadcast, such as when and where you broadcast. When you watch others' broadcasts, information regarding when you watched the video (for example, either live or on replay) and any comments or hearts you make on the video is also public. If you save your broadcast for replay, the video and associated information will remain public for 24 hours or until you delete it. If you do not save for replay, the video will no longer be available but the associated information about your broadcast (for example, the title, when and where you broadcast, who watched) will remain public for 24 hours or until you delete it.</p>
                        <p>We provide you with the option to share your broadcast only with those WhatsLIVE followers you invite.</p>
                        <p>You do not have to create an account to watch public broadcasts on our website. You do need to create an account in order to broadcast video.</p>
                        <p>If you choose to create or update an account, you may provide some personal information, such as your profile information, phone number and email address. Your profile information on WhatsLIVE is publicly available and includes your name, username, Twitter or Facebook username, biography information, profile picture, the people you follow and who follows you, and the number of hearts you have received. You may choose to sign up for WhatsLIVE through another service, such as Twitter, and use information from that service to create your WhatsLIVE profile. When you sign up through Twitter, we make suggestions of who to follow on WhatsLIVE based on who you follow and who follows you on Twitter, and we offer the option to Tweet to your followers when you start a broadcast.</p>
                        <p>We use and store information about your location to provide features of our Services, such as broadcasting with your location, and to improve and customize the Services. We may infer your location based on information from your device. If you have turned on location services for WhatsLIVE, we may share your precise location with your video.</p>
                        <p>We may revise this Privacy Statement from time to time. The most current version of this Statement will always be at www.whatsliveapp.com. </p>
                        <p>Effective August 27, 2015</p>
                        <h4>Community Guidelines </h4>
                        <p>WhatsLIVE is about being in the moment, connected to a person and a place. This immediacy encourages direct and unfiltered participation in a story as it's unfolding. There are a few guidelines intended to keep WhatsLIVE open and safe:</p>
                        <p>Do not post pornographic or overtly sexual content</p>
                        <p>Do not publish explicitly graphic content or media that is intended to incite violent, illegal or dangerous activities</p>
                        <p>Respect one another. Do not abuse, harass or post others' private, confidential information</p>
                        <p>Do not impersonate to mislead or deceive</p>
                        <p>Do not spam</p>
                        <p>Have fun, and be decent to one another. WhatsLIVE reserves the right to allow sensitive content when it is artistic, educational, scientific or newsworthy. Please remember that WhatsLIVE is not to be used for any unlawful purposes or in furtherance of illegal activities.</p>
                        <h5>Graphic Content</h5>
                        <p>WhatsLIVE is intended to be open and safe. To maintain a healthy platform, explicit graphic content is not allowed.</p>
                        <p>Explicit graphic content includes, but is not limited to, depictions of child abuse, animal abuse, or bodily harm. WhatsLIVE is not for content that is intended to incite violence, or includes a direct and specific threat of violence to others.</p>
                        <p>WhatsLIVE reserves the right to allow sensitive content when it is artistic, educational, scientific or newsworthy.</p>
                        <h5>Private Information</h5>
                        <p>Please respect the privacy of others. Do not publish another person's private or confidential information. Some examples of private information include:</p>
                        <p>Social security or other national identity numbers</p>
                        <p>Credit card information</p>
                        <p>Addresses or locations that are considered and treated private</p>
                        <p>Non-public, personal phone numbers and emails</p>
                        <p>Intimate videos or photos taken or distributed without the subject's consent</p>
                        <p>Videos or images that are considered and treated as private under applicable laws</p>
                        <p>Keep in mind that although you may consider certain information to be private, not all postings of such information may be a violation of this policy. We may consider the context and nature of the information posted, local privacy laws, and other case-specific facts when determining if this policy has been violated.</p>
                        <p>Impersonation</p>
                        <p>Do not impersonate with the intent to mislead, confuse, or deceive others. Parody, commentary, or fan accounts should be clearly labeled. For example, in your account bio write, "This is a parody account" or use a descriptive username, e.g., "@NotBillMurray".</p>
                        <p>Spam</p>
                        <p>WhatsLIVE strives to protect its community from spam. Do not use WhatsLIVE to spam anyone. User abuse and technical abuse are not tolerated, and may result in permanent account suspension.</p>
                        <p>Some examples of abuse and spam:</p>
                        <p>Malware / Phishing: publishing or linking to malicious content intended to damage or disrupt another user's account or device, or to compromise a user's privacy or login information</p>
                        <p>Serial Accounts: creating serial accounts for disruptive or abusive purposes</p>
                        <p>Selling Accounts: purchasing or selling WhatsLIVE accounts</p>
                        <p>Invitation Spam: repeatedly following and unfollowing people</p>
                        <p>Broadcast Spam:</p>
                        <p>Broadcasting for the sole purpose of directing users to an external site or service</p>
                        <p>Posting deliberately misleading broadcast titles, especially with the intent to redirect the viewer to an external site or service</p>
                        <p>Purchasing 'hearts' or paying to have a broadcast 'shared' to increase the popularity of the content</p>
                        <p>Using serial accounts to auto 'share' broadcasts to increase popularity</p>
                        <p>Posting large numbers of unsolicited comments, especially in an attempt to advertise a service or link</p>
                        <p>Using or promoting third-party sites or services that claim to get you more followers</p>
                        <h5>WhatsLIVE Copyright & DMCA Policy</h5>
                        <p>We respect the intellectual property rights of others and expect WhatsLIVE users to do the same. We will respond to notices of alleged copyright infringement that comply with applicable law and are properly provided to us.</p>
                        <p>If you believe that your content has been copied in a way that constitutes copyright infringement, please provide us with the following information:</p>
                        <p>a physical or electronic signature of the copyright owner or a person authorized to act on their behalf;</p>
                        <p>identification of the copyrighted work claimed to have been infringed;</p>
                        <p>identification of the material that is claimed to be infringing or to be the subject of infringing activity and that is to be removed or access to which is to be disabled, and information reasonably sufficient to permit us to locate the material:</p>
                        <p>username,</p>
                        <p>broadcast title,</p>
                        <p>date,</p>
                        <p>time</p>
                        <p>your contact information, including your address, telephone number, and an email address;
                            a statement by you that you have a good faith belief that use of the material in the manner complained of is not authorized by the copyright owner, its agent, or the law; and a statement that the information in the notification is accurate, and, under penalty of perjury, that you are authorized to act on behalf of the copyright owner</p>
                        <p>We reserve the right to remove content alleged to be infringing without prior notice and at our sole discretion. In appropriate circumstances, WhatsLIVE will also terminate a user's account if the user is determined to be a repeat infringer. Our designated copyright agent for notice of alleged copyright infringement appearing on Periscope is:</p>
                        <p>Copyright Agent – WhatsLIVE Team</p>
                        <p>105 Yaojiayuan St., Chaoyang District</p>
                        <p>Beijing, China</p>
                        <p>Email: 691101015@qq.com</p>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
        <p class="copyright"><fmt:message key='copyright'/></p>
    </div>
</div>
<%--<script type="text/javascript">__loadjs('whatslive');</script>--%>
</body>
</html>