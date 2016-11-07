package com.letv.whatslive.web.service.storage.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.letv.whatslive.web.constant.ServiceConstants;
import com.letv.whatslive.web.service.storage.AbstractUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by wangjian7 on 2015/7/23.
 */
@Component
public class AmazonS3Service extends AbstractUploadService{

    private static final Logger logger = LoggerFactory.getLogger(AmazonS3Service.class);


    @Override
    public String getKey(String prefix, String fileMd5, String busiKey) {
        return prefix + fileMd5;
    }

    @Override
    public boolean uploadFile(String md5, Long fileSize, String localFileUrl, String serverFileUrl, String key){
        File file = new File(localFileUrl);
        logger.info("upLoad to bucketName:" + ServiceConstants.AWS_S3_URL_PREX);
        try{
            AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(ServiceConstants.AWS_S3_ACCESSKEYID, ServiceConstants.AWS_S3_SECRETKEY));
            PutObjectRequest putObject = new PutObjectRequest(ServiceConstants.AWS_S3_BUCKETNAME, key, file);
            AccessControlList accessControlList = new AccessControlList();
            accessControlList.grantPermission(GroupGrantee.AllUsers, Permission.Read);
            putObject.setAccessControlList(accessControlList);
            s3.putObject(putObject);
            logger.info("uri:" + ServiceConstants.AWS_S3_URL_PREX + key);
        } catch (AmazonServiceException ase) {
            logger.error("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            logger.error("Error Message:	" + ase.getMessage());
            logger.error("HTTP Status Code: " + ase.getStatusCode());
            logger.error("AWS Error Code:   " + ase.getErrorCode());
            logger.error("Error Type:	   " + ase.getErrorType());
            logger.error("Request ID:	   " + ase.getRequestId());
            logger.error("", ase);
            return false;
        } catch (AmazonClientException ace) {
            ace.printStackTrace();
            logger.error("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            logger.error("Error Message: " + ace.getMessage(), ace);
            return false;
        }catch (Exception e){
            logger.error("Error Message: " + e.getMessage(),e);
            return  false;
        }
        return true;
    }

    /*public String deleteFile(String deleteKey){
        try{
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(ServiceConstants.AWS_S3_BUCKETNAME, deleteKey);
            s3.deleteObject(deleteObjectRequest);
        } catch (AmazonServiceException ase) {
            logger.error("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            logger.error("Error Message:	" + ase.getMessage());
            logger.error("HTTP Status Code: " + ase.getStatusCode());
            logger.error("AWS Error Code:   " + ase.getErrorCode());
            logger.error("Error Type:	   " + ase.getErrorType());
            logger.error("Request ID:	   " + ase.getRequestId());
            logger.error("", ase);
            return null;
        } catch (AmazonClientException ace) {
            ace.printStackTrace();
            logger.error("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            logger.error("Error Message: " + ace.getMessage(), ace);
            return null;
        }catch (Exception e){
            logger.error("Error Message: " + e.getMessage(),e);
            return  null;
        }
        return deleteKey;
    }*/

}
