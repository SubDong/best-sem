package com.perfect.dto.monitor;

/**
 * Created by SubDong on 2014/11/26.
 */
public class FolderMonitorDTO {
    //监控对象id
    private Long monitorId;
    //监控对象所属的监控文件夹id
    private Long folderId;
    //监控对象的实际id
    private Long aclid;
    //监控对象本身所属单元id （暂为  无效属性）
    private Long adgroupId;
    //监控对象本身所属计划id (暂为  无效属性)
    private Long campaignId;
    //监控对象的类型 (目前仅可监控关键词，所以该字段仅提供默认值11 3-计划； 5-单元； 7-创意； 11-关键词)
    private Integer type;

    private Long accountId;

    //是否删除
    private Integer localstatus;

    public Long getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(Long monitorId) {
        this.monitorId = monitorId;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Long getAclid() {
        return aclid;
    }

    public void setAclid(Long aclid) {
        this.aclid = aclid;
    }

    public Long getAdgroupId() {
        return adgroupId;
    }

    public void setAdgroupId(Long adgroupId) {
        this.adgroupId = adgroupId;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Integer getLocalstatus() {
        return localstatus;
    }

    public void setLocalstatus(Integer localstatus) {
        this.localstatus = localstatus;
    }

    @Override
    public String toString() {
        return "FolderMonitorDTO{" +
                "monitorId=" + monitorId +
                ", folderId=" + folderId +
                ", aclid=" + aclid +
                ", adgroupId=" + adgroupId +
                ", campaignId=" + campaignId +
                ", type=" + type +
                ", accountId=" + accountId +
                ", localstatus=" + localstatus +
                '}';
    }
}
