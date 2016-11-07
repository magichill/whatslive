package com.letv.whatslive.mongo.dao;

import com.letv.whatslive.model.Voice;
import com.letv.whatslive.mongo.BaseMediaDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.springframework.stereotype.Repository;

/**
 * Created by zoran on 15-8-21.
 */
@Repository
public class VoiceDAO extends BaseMediaDAO {

    protected String collectionName = "voice";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }


    public long insertVoice(Voice voice) {
        try {
            DBObject dbObject = new BasicDBObject();
            if (voice != null) {
                long id = getAutoIncrementId();
                dbObject.put("_id", id);
                dbObject.put("time", voice.getTime());
                dbObject.put("voice", voice.getVoice());
                dbObject.put("createTime", voice.getCreateTime());
                WriteResult result = this.dbCollection.insert(dbObject);
                return id;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    public Voice getVoiceById(long id){
        try{
            if(id>0){
                DBObject query = new BasicDBObject("_id",id);
                DBObject dbo = this.dbCollection.findOne(query);
                if(dbo!=null){
                    String time = (String)dbo.get("time");
                    byte[] voice = (byte[])dbo.get("voice");
                    long createTime =(Long)dbo.get("createTime");
                    return Voice.newVoice(time,voice,createTime);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;

    }





}
