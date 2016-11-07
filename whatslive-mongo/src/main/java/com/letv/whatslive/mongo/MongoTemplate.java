package com.letv.whatslive.mongo;

import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zoran on 14-8-11.
 */
@Repository
public class MongoTemplate {

    private static final Logger logger = LoggerFactory.getLogger(MongoTemplate.class);

    @Value("${mongo.replica.set}")
    protected String host;

    @Value("${mongo.database}")
    protected String db;

    @Value("${mongo.auth}")
    protected String auth;

    @Value("${mongo.connections}")
    protected String connections;

    @Value("${mongo.password}")
    protected String password;

    @Value("${mongo.user}")
    protected String user;


    private MongoClient mongoClient;

    protected DB DB;


    @PostConstruct
    public void init() {
        try {
            String[] hosts = host.split(",");
            MongoCredential credential = null;
            ServerAddress serverAddress = null;
            MongoClientOptions options =null;
            List<ServerAddress> serverAddressList = null;
            if(StringUtils.isNotBlank(user) && StringUtils.isNotBlank(password)){
                 credential = MongoCredential.createMongoCRCredential(user, auth, password.toCharArray());
            }
            if (hosts.length > 1) {
                serverAddressList = new ArrayList<ServerAddress>();
                for (String hostStr : hosts) {
                    String ip = hostStr.split(":")[0];
                    String port = hostStr.split(":")[1];
                    ServerAddress sa = new ServerAddress(ip, Integer.parseInt(port));
                    serverAddressList.add(sa);
                }
                options = MongoClientOptions.builder()
                        .connectionsPerHost(Integer.parseInt(connections))
                        .readPreference(ReadPreference.primaryPreferred())
                        .build();
            }
            if (hosts.length == 1) {
                String ip = hosts[0].split(":")[0];
                String port = hosts[0].split(":")[1];
                serverAddress = new ServerAddress(ip, Integer.parseInt(port));
            }

            if (options != null && serverAddressList!=null && serverAddressList.size()>0) {
                if(credential!=null){
                    mongoClient = new MongoClient(serverAddressList, Arrays.asList(credential), options);
                }else{
                    mongoClient = new MongoClient(serverAddressList, options);
                }
            }

            if(serverAddress!=null){
                if(credential!=null){
                    mongoClient = new MongoClient(serverAddress,  Arrays.asList(credential));
                }else{
                    mongoClient = new MongoClient(serverAddress);
                }
            }

            DB = mongoClient.getDB(db);
        } catch (Exception e) {
            logger.error("Init MongoTemplate Failure! " + e.getMessage(),e);
            System.exit(-1);
        }
    }

    public DB getDB() {
        return DB;
    }


    public DBCollection getCollection(String collectionName) {
        return DB.getCollection(collectionName);
    }




}
