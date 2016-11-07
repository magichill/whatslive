package com.letv.whatslive.server.http;

import redis.clients.jedis.Jedis;

import java.io.*;

/**
 * Created by gaoshan on 15-7-14.
 */
public class FileRequestHandler {


    Jedis redis = new Jedis("localhost");
    //序列化方法
    public byte[] object2Bytes(Object value) {
        if (value == null)
            return null;
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream;
        try {
            outputStream = new ObjectOutputStream(arrayOutputStream);
            outputStream.writeObject(value);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                arrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return arrayOutputStream.toByteArray();
    }
    //反序列化方法
    public Object byte2Object(byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            return null;
        try {
            ObjectInputStream inputStream;
            inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Object obj = inputStream.readObject();
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    //保存文件方法
    public void setFile(String key,String path){
        File fr = new File(path);
        redis.set(key.getBytes(), object2Bytes(fr));
    }
    //读取文件对象方法
    public File getFile(String key){
        Jedis redis = new Jedis("localhost");
        File file = (File)byte2Object(redis.get(key.getBytes()));
        return file;
    }

    public void testFile(String key,String path)throws Exception{
        setFile("test", "D:\\test.txt");
        File file = getFile("test");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String   record   =   null;
        while ((record   =   br.readLine())   !=   null)   {
            System.out.println("record:"+record);
        }

    }

    public static void main(String[] args) throws Exception{
        FileRequestHandler os = new FileRequestHandler();
        os.testFile("test", "D:\\test.txt");
    }

}
