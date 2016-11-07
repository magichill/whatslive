package com.letv.whatslive.common.http.weibo;

import com.alibaba.fastjson.annotation.JSONField;
import com.letv.whatslive.common.http.weibo.data.*;

import java.util.List;

/**
 * Created by gaoshan on 15-9-7.
 */
public class RespBody {

    @JSONField(name = "display_name")
    private String displayName;

    private ImageData image;

    private AuthorData author;

    @JSONField(name = "embed_code")
    private String embedCode;

    private StreamData stream;

    private String summary;

    private String url;

    private LinksData links;

    private List<TagData> tags;

    @JSONField(name = "create_at")
    private String createAt;

    @JSONField(name = "custom_data")
    private CustomData customData;

    @JSONField(name = "object_type")
    private String objectType;


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ImageData getImage() {
        return image;
    }

    public void setImage(ImageData image) {
        this.image = image;
    }

    public AuthorData getAuthor() {
        return author;
    }

    public void setAuthor(AuthorData author) {
        this.author = author;
    }

    public String getEmbedCode() {
        return embedCode;
    }

    public void setEmbedCode(String embedCode) {
        this.embedCode = embedCode;
    }

    public StreamData getStream() {
        return stream;
    }

    public void setStream(StreamData stream) {
        this.stream = stream;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LinksData getLinks() {
        return links;
    }

    public void setLinks(LinksData links) {
        this.links = links;
    }

    public List<TagData> getTags() {
        return tags;
    }

    public void setTags(List<TagData> tags) {
        this.tags = tags;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public CustomData getCustomData() {
        return customData;
    }

    public void setCustomData(CustomData customData) {
        this.customData = customData;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
}
