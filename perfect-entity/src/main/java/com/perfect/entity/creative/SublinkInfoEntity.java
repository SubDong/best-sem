package com.perfect.entity.creative;

/**
 * Created by XiaoWei on 2015/2/10.
 * 蹊径子链描述
 */
public class SublinkInfoEntity {
    private String description;     //蹊径子链描述
    private String destinationUrl;  //蹊径子链描述对应访问URL

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public void setDestinationUrl(String destinationUrl) {
        this.destinationUrl = destinationUrl;
    }

    @Override
    public String toString() {
        return "SublinkInfoEntity{" +
                "description='" + description + '\'' +
                ", destinationUrl='" + destinationUrl + '\'' +
                '}';
    }
}
