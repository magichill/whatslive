package com.letv.whatslive.server.util;

import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.User;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Picture;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import java.util.Date;

/**
 * Created by gaoshan on 15-7-1.
 */
public class UserConverter {

    public static User facebook2User(Facebook facebook) throws FacebookException {
        User user = new User();
        facebook4j.User facebookUser = null;
        try {
            facebookUser = facebook.users().getMe();
        } catch (FacebookException e) {
            LogUtils.ERROR_LOG.error("fail to login facebook");
            e.printStackTrace();
        }
        user.setUserName(facebookUser.getName());
        user.setNickName(facebookUser.getName());
        user.setEmail(facebookUser.getEmail());
        user.setThirdId(facebookUser.getId());
        user.setIntroduce(facebookUser.getQuotes());
        Picture pic = facebookUser.getPicture();
        if(pic != null){
            user.setPicture(pic.getURL().toString());
        }
        user.setUserType(Constant.LOGIN_TYPE_FACEBOOK);
        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(System.currentTimeMillis());
        user.setLastLoginTime(System.currentTimeMillis());
        return user;
    }

    public static User twitter2User(Twitter twitter,AccessToken accessToken) throws TwitterException {
        User user = new User();
        twitter4j.User twitterUser = null;
        long id = accessToken.getUserId();
        twitterUser = twitter.showUser(id);
        user.setNickName(twitterUser.getScreenName());
        user.setUserName(twitterUser.getName());
        user.setIntroduce(twitterUser.getDescription());
        user.setTokenSecret(accessToken.getTokenSecret());
        user.setThirdId(ObjectUtils.toString(id));
        user.setPicture(twitterUser.getBiggerProfileImageURL());
        user.setUserType(Constant.LOGIN_TYPE_TWITTER);
        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(System.currentTimeMillis());
        user.setLastLoginTime(System.currentTimeMillis());
        return user;
    }
}
