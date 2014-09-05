package com.perfect.mongodb.dao.impl;

import com.mongodb.WriteResult;
import com.perfect.constants.LogStatusConstant;
import com.perfect.core.AppContext;
import com.perfect.dao.AdgroupBackUpDAO;
import com.perfect.dao.AdgroupDAO;
import com.perfect.dao.LogDAO;
import com.perfect.dao.LogProcessingDAO;
import com.perfect.entity.*;
import com.perfect.entity.backup.AdgroupBackUpEntity;
import com.perfect.mongodb.base.AbstractUserBaseDAOImpl;
import com.perfect.mongodb.base.BaseMongoTemplate;
import com.perfect.mongodb.utils.EntityConstants;
import com.perfect.mongodb.utils.Pager;
import com.perfect.utils.LogUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.perfect.mongodb.utils.EntityConstants.*;

/**
 * Created by vbzer_000 on 2014-07-02.
 */
@Repository("adgroupDAO")
public class AdgroupDAOImpl extends AbstractUserBaseDAOImpl<com.perfect.entity.AdgroupEntity, Long> implements AdgroupDAO {

    @Override
    public String getId() {
        return ADGROUP_ID;
    }

    public String get_id() {
        return "_id";
    }

    @Resource
    private LogProcessingDAO logProcessingDAO;
    @Resource
    private AdgroupBackUpDAO adgroupBackUpDAO;

    public List<Long> getAllAdgroupId() {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        Query query = new BasicQuery("{}", "{" + ADGROUP_ID + " : 1}");
        List<com.perfect.entity.AdgroupEntity> list = mongoTemplate.find(query, com.perfect.entity.AdgroupEntity.class, EntityConstants.TBL_ADGROUP);
        List<Long> adgroupIds = new ArrayList<>(list.size());
        for (com.perfect.entity.AdgroupEntity type : list)
            adgroupIds.add(type.getAdgroupId());
        return adgroupIds;
    }

    public List<Long> getAdgroupIdByCampaignId(Long campaignId) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        Query query = new BasicQuery("{}", "{" + ADGROUP_ID + " : 1}");
        query.addCriteria(Criteria.where(CAMPAIGN_ID).is(campaignId));
        List<com.perfect.entity.AdgroupEntity> list = mongoTemplate.find(query, com.perfect.entity.AdgroupEntity.class, EntityConstants.TBL_ADGROUP);
        List<Long> adgroupIds = new ArrayList<>(list.size());
        for (com.perfect.entity.AdgroupEntity type : list)
            adgroupIds.add(type.getAdgroupId());
        return adgroupIds;
    }

    @Override
    public List<String> getAdgroupIdByCampaignId(String campaignId) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        Query query = new BasicQuery("{}", "{_id : 1}");
        query.addCriteria(Criteria.where(OBJ_CAMPAIGN_ID).is(campaignId));
        List<com.perfect.entity.AdgroupEntity> list = mongoTemplate.find(query, com.perfect.entity.AdgroupEntity.class, EntityConstants.TBL_ADGROUP);
        List<String> adgroupIds = new ArrayList<>(list.size());
        for (com.perfect.entity.AdgroupEntity type : list)
            adgroupIds.add(type.getId());
        return adgroupIds;
    }


    public List<com.perfect.entity.AdgroupEntity> getAdgroupByCampaignId(Long campaignId, Map<String, Object> params, int skip, int limit) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        Query query = new Query();
        Criteria criteria = Criteria.where(CAMPAIGN_ID).is(campaignId);
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, Object> entry : params.entrySet())
                criteria.and(entry.getKey()).is(entry.getValue());
        }
        query.addCriteria(criteria);
        query.with(new PageRequest(skip, limit));
        List<com.perfect.entity.AdgroupEntity> _list = mongoTemplate.find(query, com.perfect.entity.AdgroupEntity.class, EntityConstants.TBL_ADGROUP);
        return _list;
    }


    public List<AdgroupEntity> getAdgroupByCampaignObjId(String campaignObjId) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        return mongoTemplate.find(Query.query(Criteria.where(OBJ_CAMPAIGN_ID).is(campaignObjId)), getEntityClass(), TBL_ADGROUP);
    }

    public AdgroupEntity findOne(Long adgroupId) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        com.perfect.entity.AdgroupEntity _adgroupEntity = mongoTemplate.findOne(
                new Query(Criteria.where(ADGROUP_ID).is(adgroupId)), com.perfect.entity.AdgroupEntity.class, EntityConstants.TBL_ADGROUP);
        return _adgroupEntity;
    }

    public List<com.perfect.entity.AdgroupEntity> findAll() {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        List<com.perfect.entity.AdgroupEntity> adgroupEntities = mongoTemplate.find(Query.query(Criteria.where(ACCOUNT_ID).is(AppContext.getAccountId())), getEntityClass());
        return adgroupEntities;
    }

    /**
     * 条件查询, 分页
     *
     * @param params
     * @param skip
     * @param limit
     * @return
     */
    public List<com.perfect.entity.AdgroupEntity> find(Map<String, Object> params, int skip, int limit) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        Query query = new Query();
        if (params != null && params.size() > 0) {
            Criteria criteria = new Criteria();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                criteria.and(entry.getKey()).is(entry.getValue());
            }
            query.addCriteria(criteria);
        }
        query.with(new PageRequest(skip, limit, new Sort(Sort.Direction.DESC, "price")));
        List<com.perfect.entity.AdgroupEntity> list = mongoTemplate.find(query, com.perfect.entity.AdgroupEntity.class, EntityConstants.TBL_ADGROUP);
        return list;
    }

    /**
     * 条件查询
     *
     * @param query
     * @return
     */
    public List<com.perfect.entity.AdgroupEntity> findByQuery(Query query) {
        return BaseMongoTemplate.getUserMongo().find(query, com.perfect.entity.AdgroupEntity.class);
    }

    @Override
    public List<com.perfect.entity.AdgroupEntity> findByCampaignId(Long cid) {
        return getMongoTemplate().find(Query.query(Criteria.where(CAMPAIGN_ID).is(cid)), getEntityClass());
    }

    @Override
    public List<com.perfect.entity.AdgroupEntity> findIdByCampaignId(Long cid) {
        Query query = new BasicQuery("{}", "{ " + ADGROUP_ID + " : 1 }");

        return getMongoTemplate().find(query.addCriteria(Criteria.where(CAMPAIGN_ID).is(cid))
                , com.perfect.entity.AdgroupEntity.class);
    }

    @Override
    public com.perfect.entity.AdgroupEntity findByObjId(String oid) {
        return getMongoTemplate().findOne(Query.query(Criteria.where(SYSTEM_ID).is(oid)), getEntityClass());
    }

    @Override
    public Object insertOutId(com.perfect.entity.AdgroupEntity adgroupEntity) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        mongoTemplate.insert(adgroupEntity, EntityConstants.TBL_ADGROUP);
       logDAO.insertLog(adgroupEntity.getId(),LogStatusConstant.ENTITY_ADGROUP);
        return adgroupEntity.getId();
    }

    @Override
    public void deleteByObjId(final String oid) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        mongoTemplate.remove(Query.query(Criteria.where(get_id()).is(oid)), getEntityClass());
        deleteSubOid(new ArrayList<String>(1) {{
            add(oid);
        }});
    }

    @Override
    public void deleteByObjId(Long adgroupId) {
        MongoTemplate mongoTemplate=BaseMongoTemplate.getUserMongo();
        Update update=new Update();
        update.set("ls",3);
        mongoTemplate.updateFirst(new Query(Criteria.where(EntityConstants.ADGROUP_ID).is(adgroupId)),update, com.perfect.entity.AdgroupEntity.class,EntityConstants.TBL_ADGROUP);
        //以前是直接删除拉取到本地的数据，是硬删除，现在改为软删除，以便以后还原操作
//        mongoTemplate.remove(new Query(Criteria.where(EntityConstants.ADGROUP_ID).is(adgroupId)),AdgroupEntity.class,EntityConstants.TBL_ADGROUP);
        logDAO.insertLog(adgroupId,LogStatusConstant.ENTITY_ADGROUP,LogStatusConstant.OPT_DELETE);
    }

    @Override
    public void updateCampaignIdByOid(String oid, Long campaignId) {
        WriteResult wr = getMongoTemplate().updateMulti(Query.query(Criteria.where(OBJ_CAMPAIGN_ID).is(oid)),
                Update.update(CAMPAIGN_ID, campaignId).set(OBJ_CAMPAIGN_ID, null), getEntityClass());
    }

    @Override

    public void updateByObjId(AdgroupEntity adgroupEntity) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        Update up = new Update();
        up.set("name", adgroupEntity.getAdgroupName());
        up.set("max", adgroupEntity.getMaxPrice());
        up.set("neg", adgroupEntity.getNegativeWords());
        up.set("exneg", adgroupEntity.getExactNegativeWords());
        up.set("p", adgroupEntity.getPause());
        up.set("s", adgroupEntity.getStatus());
        up.set("m", adgroupEntity.getMib());
        mongoTemplate.updateFirst(new Query(Criteria.where(get_id()).is(adgroupEntity.getId())), up, AdgroupEntity.class, EntityConstants.TBL_ADGROUP);
        logDAO.insertLog(adgroupEntity.getId(), LogStatusConstant.ENTITY_ADGROUP);
    }

    @Override
    public void update(com.perfect.entity.AdgroupEntity adgroupEntity, com.perfect.entity.AdgroupEntity bakAdgroupEntity) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        Long id = adgroupEntity.getAdgroupId();
        Query query = new Query();
        query.addCriteria(Criteria.where(ADGROUP_ID).is(id));
        Update update = new Update();
        try {
            Class _class = adgroupEntity.getClass();
            Field[] fields = _class.getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                if ("adgroupId".equals(fieldName))
                    continue;
                StringBuilder fieldGetterName = new StringBuilder("get");
                fieldGetterName.append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1));
                Method method = _class.getDeclaredMethod(fieldGetterName.toString());
                Object after = method.invoke(adgroupEntity);
                if (after != null) {
                    update.set(field.getName(), after);
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        mongoTemplate.updateFirst(query, update, com.perfect.entity.AdgroupEntity.class, EntityConstants.TBL_ADGROUP);
        AdgroupBackUpEntity adgroupBakcUpEntityFind=adgroupBackUpDAO.findOne(adgroupEntity.getId());
        if(adgroupBakcUpEntityFind==null){
            AdgroupBackUpEntity adgroupBakcUpEntity=new AdgroupBackUpEntity();
            BeanUtils.copyProperties(bakAdgroupEntity,adgroupBakcUpEntity);
            adgroupBackUpDAO.insert(adgroupBakcUpEntity);
        }
        logDAO.insertLog(id,LogStatusConstant.ENTITY_ADGROUP,LogStatusConstant.OPT_UPDATE);
    }

    @Override
    public void insertReBack(AdgroupEntity adgroupEntity) {
        MongoTemplate mongoTemplate=BaseMongoTemplate.getUserMongo();
        mongoTemplate.remove(new Query(Criteria.where(get_id()).is(adgroupEntity.getId())),AdgroupEntity.class,EntityConstants.TBL_ADGROUP);
        mongoTemplate.insert(adgroupEntity,EntityConstants.TBL_ADGROUP);
    }

    public void insert(com.perfect.entity.AdgroupEntity adgroupEntity) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        mongoTemplate.insert(adgroupEntity, EntityConstants.TBL_ADGROUP);
        DataOperationLogEntity logEntity = LogUtils.getLog(adgroupEntity.getAdgroupId(), com.perfect.entity.AdgroupEntity.class, null, adgroupEntity);
        logProcessingDAO.insert(logEntity);
    }

    public void insertAll(List<com.perfect.entity.AdgroupEntity> entities) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        mongoTemplate.insertAll(entities);
        List<DataOperationLogEntity> logEntities = new LinkedList<>();
        for (com.perfect.entity.AdgroupEntity entity : entities) {
            DataOperationLogEntity logEntity = LogUtils.getLog(entity.getAdgroupId(), com.perfect.entity.AdgroupEntity.class, null, entity);
            logEntities.add(logEntity);
        }
        logProcessingDAO.insertAll(logEntities);
    }

    @SuppressWarnings("unchecked")
    public void update(com.perfect.entity.AdgroupEntity adgroupEntity) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        Long id = adgroupEntity.getAdgroupId();
        Query query = new Query();
        query.addCriteria(Criteria.where(ADGROUP_ID).is(id));
        Update update = new Update();
        DataOperationLogEntity log = null;
        try {
            Class _class = adgroupEntity.getClass();
            Field[] fields = _class.getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                if ("adgroupId".equals(fieldName))
                    continue;
                StringBuilder fieldGetterName = new StringBuilder("get");
                fieldGetterName.append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1));
                Method method = _class.getDeclaredMethod(fieldGetterName.toString());
                Object after = method.invoke(adgroupEntity);
                if (after != null) {
                    update.set(field.getName(), after);
                    Object before = method.invoke(findOne(id));
                    log = LogUtils.getLog(id, com.perfect.entity.AdgroupEntity.class,
                            new DataAttributeInfoEntity(field.getName(), before, after), null);
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        mongoTemplate.updateFirst(query, update, com.perfect.entity.AdgroupEntity.class, EntityConstants.TBL_ADGROUP);
        logProcessingDAO.insert(log);
    }

    public void update(List<com.perfect.entity.AdgroupEntity> entities) {
        for (com.perfect.entity.AdgroupEntity entity : entities)
            update(entity);
    }

    public void deleteById(final Long adgroupId) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        mongoTemplate.remove(new Query(Criteria.where(ADGROUP_ID).is(adgroupId)), com.perfect.entity.AdgroupEntity.class, EntityConstants.TBL_ADGROUP);
        deleteSub(new ArrayList<Long>(1) {{
            add(adgroupId);
        }});
    }

    public void deleteByIds(List<Long> adgroupIds) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        mongoTemplate.remove(new Query(Criteria.where(ADGROUP_ID).in(adgroupIds)), com.perfect.entity.AdgroupEntity.class, EntityConstants.TBL_ADGROUP);
        deleteSub(adgroupIds);
    }

    @Override
    public Class<com.perfect.entity.AdgroupEntity> getEntityClass() {
        return com.perfect.entity.AdgroupEntity.class;
    }

    public void delete(com.perfect.entity.AdgroupEntity adgroupEntity) {
        deleteById(adgroupEntity.getAdgroupId());
    }

    public void deleteAll() {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        mongoTemplate.dropCollection(com.perfect.entity.AdgroupEntity.class);
        deleteSub(getAllAdgroupId());
    }

    @Override
    public Pager findByPager(int start, int pageSize, Map<String, Object> q, int orderBy) {
        return null;
    }

//    private Update getUpdate(AdgroupEntity adgroupEntity) {
//        Update update = new Update();
//
//        if (adgroupEntity.getAdgroupName() != null) {
//            update = update.addToSet(ADGROUP_NAME, adgroupEntity.getAdgroupName());
//        }
//
//        if (adgroupEntity.getCampaignId() != null) {
//            update = update.addToSet(CAMPAIGN_ID, adgroupEntity.getCampaignId());
//        }
//
//        if (adgroupEntity.getPause() != null) {
//            update = update.addToSet(PAUSE, adgroupEntity.getPause());
//        }
//
//        if (adgroupEntity.getExactNegativeWords() != null) {
//            update = update.addToSet(EXA_NEG_WORDS, adgroupEntity.getExactNegativeWords());
//        }
//
//        if (adgroupEntity.getNegativeWords() != null) {
//            update = update.addToSet(NEG_WORDS, adgroupEntity.getNegativeWords());
//        }
//
//        if (adgroupEntity.getReserved() != null) {
//            update = update.addToSet(RESERVED, adgroupEntity.getReserved());
//        }
//
//        if (adgroupEntity.getMaxPrice() != null) {
//            update = update.addToSet(MAX_PRICE, adgroupEntity.getMaxPrice());
//        }
//
//        return update;
//    }

    private void deleteSub(List<Long> adgroupIds) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        mongoTemplate.remove(new Query(Criteria.where(ADGROUP_ID).in(adgroupIds)), KeywordEntity.class);
        mongoTemplate.remove(new Query(Criteria.where(ADGROUP_ID).in(adgroupIds)), CreativeEntity.class);
        List<DataOperationLogEntity> logEntities = new LinkedList<>();
        for (Long id : adgroupIds) {
            DataOperationLogEntity log = LogUtils.getLog(id, com.perfect.entity.AdgroupEntity.class, null, null);
            logEntities.add(log);
        }
        logProcessingDAO.insertAll(logEntities);
    }

    private void deleteSubOid(List<String> oids) {
        MongoTemplate mongoTemplate = BaseMongoTemplate.getUserMongo();
        mongoTemplate.remove(new Query(Criteria.where(get_id()).in(oids)), KeywordEntity.class);
        mongoTemplate.remove(new Query(Criteria.where(get_id()).in(oids)), CreativeEntity.class);
        List<LogEntity> logEntities = new ArrayList<>();
        for (String id : oids) {
            logDAO.insertLog(id, LogStatusConstant.ENTITY_ADGROUP);
        }

    }

    @Resource
    LogDAO logDAO;
}
