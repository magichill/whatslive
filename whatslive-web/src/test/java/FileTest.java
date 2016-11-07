import com.letv.whatslive.web.constant.WebConstants;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjian7 on 2015/7/31.
 */
public class FileTest {

    private String saveKeyToFile(List<String> keywordList){
        String message = null;
        String fileNameTmp = "D:/develop/soft/apache-tomcat-8.0.23/webapps/ROOT/static/staticfile/" + WebConstants.UPLOAD_KEYWORD_TMP;
        String fileName = "D:/develop/soft/apache-tomcat-8.0.23/webapps/ROOT/static/staticfile/" + WebConstants.UPLOAD_KEYWORD;
        try {
            String lineBreak = getLineBreak();
            StringBuilder keywordString = new StringBuilder();
            for(String keyword: keywordList){
                keywordString.append(keyword).append(lineBreak);
            }
            if(keywordString.length()>0){
                keywordString.delete(keywordString.lastIndexOf(lineBreak),keywordString.length());
            }
            File keywordFileTmp = new File(fileNameTmp);
            FileUtils.write(keywordFileTmp, keywordString.toString(),"UTF-8");

            File keywordFile = new File(fileName);
            FileUtils.copyFile(keywordFileTmp, keywordFile);

        } catch (IOException e) {
            e.printStackTrace();
            message = e.getMessage();
        }
        return message;
    }

    private String getLineBreak(){
        return System.getProperty("os.name").toLowerCase().startsWith("win")?"\r\n":"\n";
    }
    public static void  main(String[] args){
        FileTest fileTest = new FileTest();
        List<String> keyList = new ArrayList<String>();
        keyList.add("测试1");
        keyList.add("测试2");
        fileTest.saveKeyToFile(keyList);
    }
}
