package com.perfect.service.impl;

import com.perfect.dao.CensusDAO;
import com.perfect.dto.count.CensusCfgDTO;
import com.perfect.dto.count.CensusDTO;
import com.perfect.dto.count.ConstantsDTO;
import com.perfect.dto.count.CountDTO;
import com.perfect.entity.CensusEntity;
import com.perfect.service.CensusService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by XiaoWei on 2014/11/11.
 */
@Service("censusService")
public class CensusServiceImpl  implements CensusService {
    @Resource private CensusDAO censusDAO;
    private SimpleDateFormat allFormat=new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss z", Locale.ENGLISH);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);


    @Override
    public String saveParams(String[] osAnBrowser) {
        String uuid=osAnBrowser[0];
        String system=osAnBrowser[1];
        String browser=osAnBrowser[2];
        String resolution=osAnBrowser[3];
        boolean supportCookie= Boolean.parseBoolean(osAnBrowser[4]);
        boolean supportJava= Boolean.parseBoolean(osAnBrowser[5]);
        String bit=osAnBrowser[6];
        String flash=((osAnBrowser[7].equals(""))?"deny":((osAnBrowser[7]==null?"deny":osAnBrowser[7])));
        Date d=null;
        try {
            d=allFormat.parse(osAnBrowser[8]);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        String intoPage=((osAnBrowser[9].equals(""))?"direct":((osAnBrowser[9]==null?"":osAnBrowser[9])));
        String ip=osAnBrowser[10];
        String area=osAnBrowser[11];
        Integer operate= Integer.valueOf(osAnBrowser[12]);
        String lastPage=osAnBrowser[13];
        String sessionId=osAnBrowser[14];
        CensusDTO censusDTO=new CensusDTO();
        censusDTO.setUuid(uuid);
        censusDTO.setSystem(system);
        censusDTO.setBrowser(browser);
        censusDTO.setResolution(resolution);
        censusDTO.setSupportCookie(supportCookie);
        censusDTO.setSupportJava(supportJava);
        censusDTO.setBit(bit);
        censusDTO.setFlash(flash);
        censusDTO.setDate(d);
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC+08:00"));
        censusDTO.setTime(timeFormat.format(d));
        censusDTO.setIntoPage(intoPage);
        censusDTO.setLastPage(lastPage);
        censusDTO.setIp(ip);
        censusDTO.setArea(area);
        censusDTO.setOperate(operate);
        if(intoPage.indexOf("www.baidu.com") != -1) censusDTO.setSearchEngine("百度");
        else if(intoPage.indexOf("www.google.cn") != -1) censusDTO.setSearchEngine("Google");
        else if(intoPage.indexOf("www.sogou.com") != -1) censusDTO.setSearchEngine("搜狗");
        else if(intoPage.indexOf("www.so.com") != -1) censusDTO.setSearchEngine("360搜索引擎");
        else if(intoPage.indexOf("www.soso.com") != -1) censusDTO.setSearchEngine("SOSO搜搜");
        else censusDTO.setSearchEngine("其他");
        censusDTO.setSessionId(sessionId);
        censusDAO.saveParams(censusDTO);
        return censusDTO.getId();
    }

    @Override
    public Map<String, ConstantsDTO> getTodayTotal(String url) {
        return censusDAO.getTodayTotal(url);
    }

    @Override
    public List<ConstantsDTO> getVisitCustom(Map<String, Object> q) {
        return censusDAO.getVisitCustom(q);
    }


    @Override
    public int saveConfig(CensusCfgDTO censusCfgDTO) {
        return censusDAO.saveConfig(censusCfgDTO);
    }

    @Override
    public List<CensusCfgDTO> getCfgList(String ip) {
        return censusDAO.getCfgList(ip);
    }

    @Override
    public void delete(String id) {
        censusDAO.delete(id);
    }

    @Override
    public CountDTO getVisitPage(String ip,int datStatus) {
        switch (datStatus){
            case 2:
                return censusDAO.getVisitPage(ip, ConstantsDTO.CensusStatus.LAST_DAY);
            case 3:
                return censusDAO.getVisitPage(ip, ConstantsDTO.CensusStatus.LAST_WEEK);
            case 4:
                return censusDAO.getVisitPage(ip, ConstantsDTO.CensusStatus.LAST_MONTH);
            default:
                return censusDAO.getVisitPage(ip, ConstantsDTO.CensusStatus.TO_DAY);
        }
    }


}
