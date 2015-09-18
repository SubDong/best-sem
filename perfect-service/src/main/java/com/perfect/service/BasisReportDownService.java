package com.perfect.service;

import java.io.OutputStream;
import java.util.Date;

/**
 * Created by subdong on 15-9-18.
 */
public interface BasisReportDownService {
    /**
     * 下载已生成的数据报告
     *
     * @param os
     * @param redisKey   redisKey值
     * @param reportTime 报告分为时间
     * @param terminal   报告选择终端
     */
    void downReportCSV(OutputStream os, String redisKey, int reportTime, int terminal, int reportType, String dateHead);

    /**
     * 下载已生成的账户数据报告
     *
     * @param os
     */
    void downAccountReportCSV(OutputStream os, Date startDate, Date endDate, Date startDate1, Date endDate1, int dateType, int devices, String sortVS, int startVS, int limitVS);

}
