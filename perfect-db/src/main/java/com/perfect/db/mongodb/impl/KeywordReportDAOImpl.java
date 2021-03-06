package com.perfect.db.mongodb.impl;

import com.perfect.core.AppContext;
import com.perfect.dao.KeywordReportDAO;
import com.perfect.db.mongodb.base.AbstractUserBaseDAOImpl;
import com.perfect.db.mongodb.base.BaseMongoTemplate;
import com.perfect.dto.keyword.KeywordReportDTO;
import com.perfect.entity.keyword.KeywordEntity;
import com.perfect.entity.report.KeywordReportEntity;
import com.perfect.utils.DateUtils;
import com.perfect.utils.ObjectUtils;
import com.perfect.utils.paging.PagerInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by XiaoWei on 2014/9/17.
 * 2014-12-3 refactor
 */
@SuppressWarnings("unchecked")
@Repository("keywordReportDAO")
public class KeywordReportDAOImpl extends AbstractUserBaseDAOImpl<KeywordReportDTO, Long> implements KeywordReportDAO {

    @Override
    public Class<KeywordReportEntity> getEntityClass() {
        return KeywordReportEntity.class;
    }

    @Override
    public Class<KeywordReportDTO> getDTOClass() {
        return KeywordReportDTO.class;
    }

    @Override
    public PagerInfo findByPagerInfo(Map<String, Object> params) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserReportMongo();
        MongoTemplate mongoTemplateNormal = BaseMongoTemplate.getUserMongo();
        List<KeywordReportEntity> findlList = new ArrayList<>();
        Map<Long, KeywordReportEntity> imptMap = new HashMap<>();
        List<String> dateList = getCurrDate(params);
        List<Long> kwdIds = new ArrayList<>();
        if (params.containsKey("kwdIds")) {
            kwdIds = (List<Long>) params.get("kwdIds");
        }
        String orderBy = params.get("orderBy").toString();
        if (dateList.size() > 0 && kwdIds.size() > 0) {
            for (String str : dateList) {
                for (Long l : kwdIds) {
                    KeywordReportEntity list = mongoTemplate.findOne(new Query(Criteria.where(ACCOUNT_ID).is(AppContext.getAccountId()).and(KEYWORD_ID).is(l)), KeywordReportEntity.class, str + "-" + TBL_KEYWORD);
                    if (list != null) {
                        KeywordEntity matchType = mongoTemplateNormal.findOne(new Query(Criteria.where(ACCOUNT_ID).is(AppContext.getAccountId()).and(KEYWORD_ID).is(l)), KeywordEntity.class);
                        list.setOrderBy(orderBy);
                        list.setMatchType(matchType.getMatchType());
                        findlList.add(list);
                    }
                }
            }
        }
        if (findlList.size() > 0) {
            for (int i = 0; i < findlList.size(); i++) {
                if (imptMap.get(findlList.get(i).getKeywordId()) == null) {
                    imptMap.put(findlList.get(i).getKeywordId(), findlList.get(i));
                } else {
                    KeywordReportEntity tmp = imptMap.get(findlList.get(i).getKeywordId());
                    Integer tMobileImpression = tmp.getMobileImpression() != null ? tmp.getMobileImpression() : 0;
                    Integer fMobileImpression = findlList.get(i).getMobileImpression() != null ? findlList.get(i).getMobileImpression() : 0;
                    Integer tClick = tmp.getMobileClick() != null ? tmp.getMobileClick() : 0;
                    Integer fClick = findlList.get(i).getMobileClick() != null ? findlList.get(i).getMobileClick() : 0;
                    Double tCtr = tmp.getMobileCtr() != null ? tmp.getMobileCtr() : 0.0;
                    Double fCtr = findlList.get(i).getMobileCtr() != null ? findlList.get(i).getMobileCtr() : 0.0;
                    BigDecimal tCost = tmp.getMobileCost() != null ? tmp.getMobileCost() : BigDecimal.ZERO;
                    BigDecimal fCost = findlList.get(i).getMobileCost() != null ? findlList.get(i).getMobileCost() : BigDecimal.ZERO;
                    BigDecimal tCpc = tmp.getMobileCpc() != null ? tmp.getMobileCpc() : BigDecimal.ZERO;
                    BigDecimal fCpc = findlList.get(i).getMobileCpc() != null ? findlList.get(i).getMobileCpc() : BigDecimal.ZERO;
                    BigDecimal tCpm = tmp.getMobileCpm() != null ? tmp.getMobileCpm() : BigDecimal.ZERO;
                    BigDecimal fCpm = findlList.get(i).getMobileCpm() != null ? findlList.get(i).getMobileCpm() : BigDecimal.ZERO;
                    Double tConversion = tmp.getMobileConversion() != null ? tmp.getMobileConversion() : 0.0;
                    Double fConversion = findlList.get(i).getMobileConversion() != null ? findlList.get(i).getMobileConversion() : 0.0;
                    Double tPosition = tmp.getMobilePosition() != null ? tmp.getMobilePosition() : 0.0;
                    Double fPosition = findlList.get(i).getMobilePosition() != null ? findlList.get(i).getMobilePosition() : 0.0;

                    Integer mibPcImpression = tMobileImpression + fMobileImpression;
                    Integer mibClick = tClick + fClick;
                    Double mibCtr = tCtr + fCtr;
                    BigDecimal mibCost = tCost.add(fCost);
                    BigDecimal mibCpc = tCpc.add(fCpc);
                    BigDecimal mibCpm = tCpm.add(fCpm);
                    Double mibConversion = tConversion + fConversion;
                    Double mibPosition = tPosition + fPosition;

                    Double needMibCtr = 0.0;
                    if (mibClick > 0.0 && mibPcImpression > 0.0) {
                        needMibCtr = Double.parseDouble(mibClick / mibPcImpression + "");
                    }
                    BigDecimal needMibCpc = BigDecimal.ZERO;
                    if (mibCost.compareTo(BigDecimal.ZERO) == 1 && mibClick > 0.0) {
                        needMibCpc = mibCost.divide(BigDecimal.valueOf(mibClick), 2, BigDecimal.ROUND_UP);
                    }
                    BigDecimal tmpPcpc = BigDecimal.ONE;
                    if (tmp.getPcClick() != 0) {
                        tmpPcpc = tmp.getPcCost().divide(BigDecimal.valueOf(tmp.getPcClick()), 2, BigDecimal.ROUND_UP);
                    }
                    BigDecimal fmpPcpc = BigDecimal.ONE;
                    if (findlList.get(i).getPcClick() != 0) {
                        fmpPcpc = findlList.get(i).getPcCost().divide(BigDecimal.valueOf(findlList.get(i).getPcClick()), 2, BigDecimal.ROUND_UP);
                    }
                    tmp.setPcImpression(tmp.getPcImpression() + findlList.get(i).getPcImpression() + mibPcImpression);
                    tmp.setPcClick(tmp.getPcClick() + findlList.get(i).getPcClick() + mibClick);
                    tmp.setPcCtr((tmp.getPcClick() / tmp.getPcImpression() + findlList.get(i).getPcClick() / findlList.get(i).getPcImpression()) + needMibCtr);
                    tmp.setPcCost(tmp.getPcCost().add(findlList.get(i).getPcCost()).add(mibCost));
                    tmp.setPcCpc((tmpPcpc.add(fmpPcpc)).add(needMibCpc));
                    tmp.setPcCpm(tmp.getPcCpm().add(findlList.get(i).getPcCpm()).add(mibCpm));
                    tmp.setPcConversion(tmp.getPcConversion() + findlList.get(i).getPcConversion() + mibConversion);
                    tmp.setPcPosition(tmp.getPcPosition() + findlList.get(i).getPcPosition() + mibPosition);

                    tmp.setMobileImpression(mibPcImpression);
                    tmp.setMobileClick(mibClick);
                    tmp.setMobileCtr(mibCtr);
                    tmp.setMobileCost(mibCost);
                    tmp.setMobileCpc(mibCpc);
                    tmp.setMobileCpm(mibCpm);
                    tmp.setMobileConversion(mibConversion);
                    tmp.setMobilePosition(mibPosition);
                }
            }
        }
        List<KeywordReportEntity> importList = new ArrayList<>(imptMap.values());
        for (KeywordReportEntity kwd : importList) {
            Double d = Double.parseDouble(kwd.getPcCost() + "");
            if (d == 0.0) {
                kwd.setQuality(0);
            } else {
                kwd.setQuality((int) (kwd.getPcPosition() / d));
            }
        }
        Integer nowPage = Integer.parseInt(params.get("nowPage").toString());
        Integer pageSize = Integer.parseInt(params.get("pageSize").toString());
        Integer totalCount = imptMap.size();
        PagerInfo p = new PagerInfo(nowPage, pageSize, totalCount);
        if (totalCount < 1) {
            p.setList(new ArrayList());
            return p;
        } else {
            if (pageSize > totalCount) {
                importList = importList.subList(p.getFirstStation(), totalCount);
            } else {
                importList = importList.subList(p.getFirstStation(), p.getPageSize());
            }
        }
        Collections.sort(importList);
        p.setList(importList);
        return p;
    }

    @Override
    public List<KeywordReportDTO> getAll(Map<String, Object> params) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserReportMongo();
        MongoTemplate mongoTemplateNormal = BaseMongoTemplate.getUserMongo();
        List<KeywordReportEntity> findlList = new ArrayList<>();
        Map<Long, KeywordReportEntity> imptMap = new HashMap<>();
        List<String> dateList = getCurrDate(params);
        List<Long> kwdIds = (List<Long>) params.get("kwdIds");
        if (dateList.size() > 0) {
            for (String str : dateList) {
                for (Long l : kwdIds) {
                    KeywordReportEntity list = mongoTemplate.findOne(new Query(Criteria.where(ACCOUNT_ID).is(AppContext.getAccountId()).and(KEYWORD_ID).is(l)), KeywordReportEntity.class, str + "-" + TBL_KEYWORD);
                    if (list != null) {
                        KeywordEntity matchType = mongoTemplateNormal.findOne(new Query(Criteria.where(ACCOUNT_ID).is(AppContext.getAccountId()).and(KEYWORD_ID).is(l)), KeywordEntity.class);
                        list.setMatchType(matchType.getMatchType());
                        findlList.add(list);
                    }
                }
            }
        }
        if (findlList.size() > 0) {
            for (int i = 0; i < findlList.size(); i++) {
                if (imptMap.get(findlList.get(i).getKeywordId()) == null) {
                    imptMap.put(findlList.get(i).getKeywordId(), findlList.get(i));
                } else {
                    KeywordReportEntity tmp = imptMap.get(findlList.get(i).getKeywordId());
                    Integer tMobileImpression = tmp.getMobileImpression() != null ? tmp.getMobileImpression() : 0;
                    Integer fMobileImpression = findlList.get(i).getMobileImpression() != null ? findlList.get(i).getMobileImpression() : 0;
                    Integer tClick = tmp.getMobileClick() != null ? tmp.getMobileClick() : 0;
                    Integer fClick = findlList.get(i).getMobileClick() != null ? findlList.get(i).getMobileClick() : 0;
                    Double tCtr = tmp.getMobileCtr() != null ? tmp.getMobileCtr() : 0.0;
                    Double fCtr = findlList.get(i).getMobileCtr() != null ? findlList.get(i).getMobileCtr() : 0.0;
                    BigDecimal tCost = tmp.getMobileCost() != null ? tmp.getMobileCost() : BigDecimal.ZERO;
                    BigDecimal fCost = findlList.get(i).getMobileCost() != null ? findlList.get(i).getMobileCost() : BigDecimal.ZERO;
                    BigDecimal tCpc = tmp.getMobileCpc() != null ? tmp.getMobileCpc() : BigDecimal.ZERO;
                    BigDecimal fCpc = findlList.get(i).getMobileCpc() != null ? findlList.get(i).getMobileCpc() : BigDecimal.ZERO;
                    BigDecimal tCpm = tmp.getMobileCpm() != null ? tmp.getMobileCpm() : BigDecimal.ZERO;
                    BigDecimal fCpm = findlList.get(i).getMobileCpm() != null ? findlList.get(i).getMobileCpm() : BigDecimal.ZERO;
                    Double tConversion = tmp.getMobileConversion() != null ? tmp.getMobileConversion() : 0.0;
                    Double fConversion = findlList.get(i).getMobileConversion() != null ? findlList.get(i).getMobileConversion() : 0.0;
                    Double tPosition = tmp.getMobilePosition() != null ? tmp.getMobilePosition() : 0.0;
                    Double fPosition = findlList.get(i).getMobilePosition() != null ? findlList.get(i).getMobilePosition() : 0.0;

                    Integer mibPcImpression = tMobileImpression + fMobileImpression;
                    Integer mibClick = tClick + fClick;
                    Double mibCtr = tCtr + fCtr;
                    BigDecimal mibCost = tCost.add(fCost);
                    BigDecimal mibCpc = tCpc.add(fCpc);
                    BigDecimal mibCpm = tCpm.add(fCpm);
                    Double mibConversion = tConversion + fConversion;
                    Double mibPosition = tPosition + fPosition;

                    Double needMibCtr = 0.0;
                    if (mibClick > 0.0 && mibPcImpression > 0.0) {
                        needMibCtr = Double.parseDouble(mibClick / mibPcImpression + "");
                    }
                    BigDecimal needMibCpc = BigDecimal.ZERO;
                    if (mibCost.compareTo(BigDecimal.ZERO) == 1 && mibClick > 0.0) {
                        needMibCpc = mibCost.divide(BigDecimal.valueOf(mibClick), 2, BigDecimal.ROUND_UP);
                    }
                    BigDecimal tmpPcpc = BigDecimal.ONE;
                    if (tmp.getPcClick() != 0) {
                        tmpPcpc = tmp.getPcCost().divide(BigDecimal.valueOf(tmp.getPcClick()), 2, BigDecimal.ROUND_UP);
                    }
                    BigDecimal fmpPcpc = BigDecimal.ONE;
                    if (findlList.get(i).getPcClick() != 0) {
                        fmpPcpc = findlList.get(i).getPcCost().divide(BigDecimal.valueOf(findlList.get(i).getPcClick()), 2, BigDecimal.ROUND_UP);
                    }
                    tmp.setPcImpression(tmp.getPcImpression() + findlList.get(i).getPcImpression() + mibPcImpression);
                    tmp.setPcClick(tmp.getPcClick() + findlList.get(i).getPcClick() + mibClick);
                    tmp.setPcCtr((tmp.getPcClick() / tmp.getPcImpression() + findlList.get(i).getPcClick() / findlList.get(i).getPcImpression()) + needMibCtr);
                    tmp.setPcCost(tmp.getPcCost().add(findlList.get(i).getPcCost()).add(mibCost));
                    tmp.setPcCpc((tmpPcpc.add(fmpPcpc)).add(needMibCpc));
                    tmp.setPcCpm(tmp.getPcCpm().add(findlList.get(i).getPcCpm()).add(mibCpm));
                    tmp.setPcConversion(tmp.getPcConversion() + findlList.get(i).getPcConversion() + mibConversion);
                    tmp.setPcPosition(tmp.getPcPosition() + findlList.get(i).getPcPosition() + mibPosition);


                    tmp.setMobileImpression(mibPcImpression);
                    tmp.setMobileClick(mibClick);
                    tmp.setMobileCtr(mibCtr);
                    tmp.setMobileCost(mibCost);
                    tmp.setMobileCpc(mibCpc);
                    tmp.setMobileCpm(mibCpm);
                    tmp.setMobileConversion(mibConversion);
                    tmp.setMobilePosition(mibPosition);
                }
            }
        }
        List<KeywordReportEntity> importList = new ArrayList<>(imptMap.values());
        for (KeywordReportEntity kwd : importList) {
            Double d = Double.parseDouble(kwd.getPcCost() + "");
            if (d == 0.0) {
                kwd.setQuality(0);
            } else {
                kwd.setQuality((int) (kwd.getPcPosition() / d));
            }
        }
        return ObjectUtils.convert(importList, KeywordReportDTO.class);
    }

    private List<String> getCurrDate(Map<String, Object> params) {
        List<String> dateList = new ArrayList<>();
        if (params.containsKey("startDate") && params.containsKey("endDate")) {
            dateList = DateUtils.getPeriod(params.get("startDate").toString(), params.get("endDate").toString());
        }
        return dateList;
    }
}
