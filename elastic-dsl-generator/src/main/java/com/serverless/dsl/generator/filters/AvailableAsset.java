package com.serverless.dsl.generator.filters;


import java.io.Serializable;
import java.util.List;

import org.elasticsearch.common.lang3.builder.EqualsBuilder;
import org.elasticsearch.common.lang3.builder.HashCodeBuilder;
import org.elasticsearch.common.lang3.builder.ToStringBuilder;


public class AvailableAsset implements Serializable {

    private static final long serialVersionUID = 1L;
    private String format;
    private List<String> assetTypes;
    private List<String> releases;
    private String url;
    private Integer height;
    private Integer width;
    private String duration;
    private Integer bitrate;
    private String streamingUrl;

    public Integer getBitrate() {
        return bitrate;
    }

    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }

    public List<String> getAssetTypes() {
        return assetTypes;
    }

    public void setAssetTypes(List<String> assetTypes) {
        this.assetTypes = assetTypes;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getReleases() {
        return releases;
    }

    public void setReleases(List<String> releases) {
        this.releases = releases;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStreamingUrl() {
        return streamingUrl;
    }

    public void setStreamingUrl(String streamingUrl) {
        this.streamingUrl = streamingUrl;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !getClass().equals(obj.getClass())) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        AvailableAsset otherAvailableAsset = (AvailableAsset) obj;

        return new EqualsBuilder().append(format, otherAvailableAsset.format)
                .append(assetTypes, otherAvailableAsset.assetTypes).append(url, otherAvailableAsset.url).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(format).append(assetTypes).append(url).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, null, false, AvailableAsset.class);
    }

}