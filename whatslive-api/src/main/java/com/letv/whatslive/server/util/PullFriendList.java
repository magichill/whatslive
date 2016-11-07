package com.letv.whatslive.server.util;

import com.google.common.collect.Lists;
import com.letv.whatslive.common.utils.StringUtil;
import com.letv.whatslive.common.utils.ThreadDistribution;
import com.letv.whatslive.server.service.ThirdFriendService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Friend;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import weibo4j.Friendships;
import weibo4j.model.User;
import weibo4j.model.UserWapper;
import weibo4j.model.WeiboException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-8-4.
 */
@Getter
@Setter
public class PullFriendList implements Runnable{

    private Twitter twitter;
    private String token;
    private String tokenSecret;
    private Long userId;
    private ThirdFriendService thirdFriendService;
    private Facebook facebook;
    private Integer type;
    private String uid; //微博用户id

    @Override
    public void run() {
        if(type == 1){
            updateFbFriend(token,userId);
        }else if(type == 2){
            try {
                updateTwFriends(token,tokenSecret,userId);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }else if(type == 4){
            updateWeiboFriends(token,uid,userId);
//            updateWeiboFriends(token,uid);
        }
        Thread.currentThread().interrupt();
        System.out.println(Thread.currentThread().isInterrupted());
    }

    private void updateFbFriend(String token ,Long userId){
        if(StringUtils.isBlank(token)){
            return;
        }
        List<DBObject> userList = Lists.newArrayList();
        ResponseList<Friend> friendList = null;
        try {
            facebook.setOAuthAccessToken(new AccessToken(token));
            friendList = facebook.getFriends();
        } catch (FacebookException e) {
            e.printStackTrace();
            LogUtils.logError("fail to get friends");
        }
        for(Friend list : friendList){
            DBObject friendObj = new BasicDBObject();
            LogUtils.commonLog("facebook friend's name:"+list.getName());
            try{
                friendObj.put("_id",list.getId()+facebook.getId());
                friendObj.put("userId",userId);
                friendObj.put("nickName",list.getName());
                friendObj.put("introduce", list.getQuotes());
                friendObj.put("thirdId",list.getId());
                if(list.getPicture() != null){
                    friendObj.put("userIcon",list.getPicture().getURL().toString());
                }else{
                    friendObj.put("userIcon","");
                }
                friendObj.put("type",1);
            }catch (Exception e){
                LogUtils.logError("fail to get facebook friend #"+list.getId());
                continue;
            }

            userList.add(friendObj);
        }
        thirdFriendService.insertFriend(userList,1);
    }

    private void updateTwFriends(String token,String tokenSecret,Long userId) throws TwitterException {
        if(StringUtils.isBlank(token) || StringUtils.isBlank(tokenSecret)){
            return;
        }
        List<DBObject> friendList = Lists.newArrayList();
        twitter.setOAuthAccessToken(null);
        twitter4j.auth.AccessToken accessToken = new twitter4j.auth.AccessToken(token,tokenSecret);
        twitter.setOAuthAccessToken(accessToken);
        long id = twitter.getOAuthAccessToken().getUserId();
        long lCursor = -1;
        IDs friendsIDs = twitter.getFriendsIDs(id, lCursor);
        do
        {
            for (long i : friendsIDs.getIDs())
            {
                DBObject friendObj = new BasicDBObject();
                try{
                    friendObj.put("_id",i+id);
                    friendObj.put("userId",userId);
                    LogUtils.commonLog("twitter friend's id: #"+i);
                    friendObj.put("nickName",twitter.showUser(i).getScreenName());
                    friendObj.put("introduce", twitter.showUser(i).getDescription());
                    friendObj.put("thirdId",i);
                    friendObj.put("userIcon",twitter.showUser(i).getMiniProfileImageURL());
                    friendObj.put("type",2);
                    friendList.add(friendObj);
                }catch(Exception e){
                    LogUtils.logError("fail to get twitter friend #"+i);
                    continue;
                }

            }
        }while(friendsIDs.hasNext());
        thirdFriendService.insertFriend(friendList,2);
    }

    private void updateWeiboFriends(String token,String uid,Long userId) {
        if(StringUtils.isBlank(token) || StringUtils.isBlank(uid)){
            return;
        }
        List<DBObject> friendList = Lists.newArrayList();
        Friendships fm = new Friendships(token);
        try {
            Map<String,String> params = new HashMap<String,String>();
            params.put("uid",uid);
            Long cursor = 0l;
            UserWapper users;
            do {
                params.put("cursor",cursor.toString());
                users = fm.getFriends(params);
                LogUtils.commonLog("cursor===="+cursor);
                for (User u : users.getUsers()) {
                    DBObject friendObj = new BasicDBObject();
                    friendObj.put("_id",u.getId()+uid);
                    friendObj.put("userId",userId);
                    LogUtils.commonLog("weibo friend's id: #"+u.getId());
                    friendObj.put("nickName",u.getName());
                    friendObj.put("introduce", u.getDescription());
                    friendObj.put("thirdId",u.getId());
                    friendObj.put("userIcon",u.getAvatarLarge());
                    friendObj.put("type",4);
                    friendList.add(friendObj);
                }
                cursor = users.getNextCursor();
            }while (users.getNextCursor()>0);

        } catch (WeiboException e) {
            e.printStackTrace();
        }
        thirdFriendService.insertFriend(friendList,4);
    }

//    public static void main(String[] args){
//        PullFriendList pullFriendList = new PullFriendList();
//        pullFriendList.setType(4);
//        pullFriendList.setToken("2.00Imv2SCimduIDb226c487c10sHfKc");
//        pullFriendList.setUid("2103466692");
////        ThreadDistribution.getInstance().doWork(pullFriendList);
//        pullFriendList.run();
//    }
}
