package com.perfect.dao.sys;

import com.perfect.dao.base.HeyCrudRepository;
import com.perfect.dto.sys.SystemUserDTO;
import com.perfect.dto.baidu.BaiduAccountInfoDTO;
import com.perfect.dto.sys.SystemUserModuleDTO;

import java.util.Date;
import java.util.List;

/**
 * Created by vbzer_000 on 2014/6/18.
 */
public interface SystemUserDAO extends HeyCrudRepository<SystemUserDTO, String> {

    /**
     * 根据用户名查询
     * <br>------------------------------<br>
     *
     * @param userName
     * @return
     */
    SystemUserDTO findByUserName(String userName);

    /**
     * 添加百度账户
     *
     * @param list
     * @param currSystemUserName
     */
    void addBaiduAccount(List<BaiduAccountInfoDTO> list, String currSystemUserName);

    SystemUserDTO findByAid(long aid);

    Iterable<SystemUserDTO> getAllValidUser();

    void insertAccountInfo(String userName, BaiduAccountInfoDTO baiduAccountInfoDTO);

    int removeAccountInfo(List<BaiduAccountInfoDTO> baiduAccountInfoDTOs, String account);

    void clearAccountData(Long accountId);

    void clearCampaignData(Long accountId, List<Long> campaignIds);

    void clearAdgroupData(Long accountId, List<Long> adgroupIds);

    void clearKeywordData(Long accountId, List<Long> keywordIds);

    void clearCreativeData(Long accountId, List<Long> creativeIds);

    List<Long> getLocalAdgroupIds(Long accountId, List<Long> campaignIds);

    List<Long> getLocalKeywordIds(Long accountId, List<Long> adgroupIds);

    List<Long> getLocalCreativeIds(Long accountId, List<Long> adgroupIds);

    List<SystemUserDTO> findAll(int skip, int limit, String order, boolean asc);

    boolean updateAccountStatus(String id, Boolean accountStatus);

    boolean updateAccountTime(String id, Date startDate, Date endDate);

    boolean updateAccountPayed(String id, Boolean payed);

    List<SystemUserModuleDTO> getUserModules(String name);

    SystemUserDTO findByUserId(String id);

    boolean updateModuleMenus(String id, String modulename, String[] menus);

    boolean saveUserModule(String userid, SystemUserModuleDTO systemUserModuleDTO);

    boolean deleteModule(String userid, String moduleId);
}
