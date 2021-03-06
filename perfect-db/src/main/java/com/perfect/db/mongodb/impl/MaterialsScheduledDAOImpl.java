package com.perfect.db.mongodb.impl;

import com.perfect.commons.constants.MaterialsJobEnum;
import com.perfect.dao.MaterialsScheduledDAO;
import com.perfect.db.mongodb.base.AbstractSysBaseDAOImpl;
import com.perfect.dto.ScheduledJobDTO;
import com.perfect.entity.ScheduledJobEntity;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created on 2015-10-08.
 *
 * @author dolphineor
 */
@Repository("materialsScheduledDAO")
public class MaterialsScheduledDAOImpl extends AbstractSysBaseDAOImpl<ScheduledJobDTO, String> implements MaterialsScheduledDAO {

    @Override
    @SuppressWarnings("unchecked")
    public Class<ScheduledJobEntity> getEntityClass() {
        return ScheduledJobEntity.class;
    }

    @Override
    public ScheduledJobDTO findByJobId(String jobId) {
        return convertFromEntity(getSysMongoTemplate().findOne(Query.query(Criteria.where(JOB_ID).is(jobId)), getEntityClass()));
    }

    @Override
    public void addJob(ScheduledJobDTO scheduledJob) {
        ScheduledJobEntity scheduledJobEntity = convertFromDTO(scheduledJob);
        getSysMongoTemplate().save(scheduledJobEntity, TBL_MATERIALS_SCHEDULER);
    }

    @Override
    public void pauseJob(ScheduledJobDTO scheduledJob) {
        getSysMongoTemplate().updateFirst(
                buildQuery(scheduledJob),
                Update.update(JOB_STATUS, MaterialsJobEnum.PAUSE.value()),
                getEntityClass());
    }

    @Override
    public void resumeJob(ScheduledJobDTO scheduledJob) {
        getSysMongoTemplate().updateFirst(
                buildQuery(scheduledJob),
                Update.update(JOB_STATUS, MaterialsJobEnum.ACTIVE.value()),
                getEntityClass());
    }

    @Override
    public void deleteJob(ScheduledJobDTO scheduledJob) {
        getSysMongoTemplate().remove(buildQuery(scheduledJob), getEntityClass());
    }

    @Override
    public boolean isExists(String jobName, String jobGroup, int jobType) {
        return getSysMongoTemplate().exists(
                Query.query(Criteria.where(JOB_NAME).is(jobName).and(JOB_GROUP).is(jobGroup).and(JOB_TYPE).is(jobType)),
                getEntityClass());
    }

    @Override
    public List<ScheduledJobDTO> getAllScheduledJob() {
        List<ScheduledJobEntity> entityList = getSysMongoTemplate().findAll(getEntityClass());

        return entityList.stream().map(this::convertFromEntity).collect(Collectors.toList());
    }

    private ScheduledJobEntity convertFromDTO(ScheduledJobDTO scheduledJob) {
        ScheduledJobEntity scheduledJobEntity = new ScheduledJobEntity();
        scheduledJobEntity.setJobId(scheduledJob.getJobId());
        scheduledJobEntity.setJobName(scheduledJob.getJobName());
        scheduledJobEntity.setJobGroup(scheduledJob.getJobGroup());
        scheduledJobEntity.setJobType(scheduledJob.getJobType());
        scheduledJobEntity.setJobStatus(scheduledJob.getJobStatus());
        scheduledJobEntity.setCronExpression(scheduledJob.getCronExpression());

        return scheduledJobEntity;
    }

    private ScheduledJobDTO convertFromEntity(ScheduledJobEntity scheduledJob) {
        ScheduledJobDTO scheduledJobDTO = new ScheduledJobDTO();
        scheduledJobDTO.setId(scheduledJob.getId());
        scheduledJobDTO.setJobId(scheduledJob.getJobId());
        scheduledJobDTO.setJobName(scheduledJob.getJobName());
        scheduledJobDTO.setJobGroup(scheduledJob.getJobGroup());
        scheduledJobDTO.setJobType(scheduledJob.getJobType());
        scheduledJobDTO.setJobStatus(scheduledJobDTO.getJobStatus());
        scheduledJobDTO.setCronExpression(scheduledJob.getCronExpression());

        return scheduledJobDTO;
    }

    private Query buildQuery(ScheduledJobDTO scheduledJob) {
        Objects.requireNonNull(scheduledJob);

        return Query.query(Criteria
                .where(JOB_ID).is(scheduledJob.getJobId())
                .and(JOB_NAME).is(scheduledJob.getJobName())
                .and(JOB_GROUP).is(scheduledJob.getJobGroup()));
    }
}
