package com.letv.whatslive.website.util.util;

import net.coobird.thumbnailator.Thumbnails;

import java.io.IOException;

/**
 * Created by wangruifeng on 14-11-21.
 */
public class ThumbnailsUtils {
    /**
     * 图片按比例缩略
     *
     * @param wid
     * @param height
     * @param imgUrl
     * @return
     */
    public static void compressScale(Integer wid, Integer height, String imgUrl, String toFileUrl) {
        try {
            Thumbnails.of(imgUrl).size(wid, height).toFile(toFileUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] agrs){
        ThumbnailsUtils.compressScale(50,50,"/letv/upload/staticfile/20140902/9d377b10ce778c4938b3c7e2c63a229a.png","/letv/upload/staticfile/20140902/50.png");
    }

}
