package com.perfect.service;

import com.perfect.autosdk.sms.v3.Folder;
import com.perfect.autosdk.sms.v3.FolderMonitor;
import com.perfect.autosdk.sms.v3.Monitor;
import com.perfect.entity.FolderEntity;
import com.perfect.entity.FolderMonitorEntity;

import java.util.List;

/**
 * Created by SubDong on 2014/9/10.
 */
public interface MonitoringService {

    /**
     * 获取监控文件夹
     * @return
     */
    public List<FolderEntity> getFolder();

    /**
     * 获取监控文件夹下的所有内容
     * @return
     */
    public List<FolderMonitorEntity> getMonitor();

}
