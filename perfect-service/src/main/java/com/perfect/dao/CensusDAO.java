package com.perfect.dao;

import com.perfect.dto.ConstantsDTO;
import com.perfect.entity.CensusEntity;

/**
 * Created by XiaoWei on 2014/11/11.
 */
public interface CensusDAO extends  MongoCrudRepository<CensusEntity,Long> {
    /**
     * 添加数据源
     * @param censusEntity
     * @return
     */
    CensusEntity saveParams(CensusEntity censusEntity);

    /**
     *根据某个url地址获取今日统计数据
     * @return
     */
    public ConstantsDTO getTodayTotal(String url);

    /**
     * 根据某个url地址获取昨日统计数据
     * @param url
     * @return
     */
    public ConstantsDTO getLastDayTotal(String url);

    /**
     * 根据某个url地址获取上周统计数据
     * @param url
     * @return
     */
    public ConstantsDTO getLastWeekTotal(String url);

    /**
     * 根据某个url地址获取上个月统计数据
     * @param url
     * @return
     */
    public ConstantsDTO getLastMonthTotal(String url);
}
