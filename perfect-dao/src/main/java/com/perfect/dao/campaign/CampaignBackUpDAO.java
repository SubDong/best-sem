package com.perfect.dao.campaign;


import com.perfect.dao.MongoCrudRepository;
import com.perfect.dto.backup.CampaignBackUpDTO;

public interface CampaignBackUpDAO extends MongoCrudRepository<CampaignBackUpDTO, Long> {


    CampaignBackUpDTO findByObjectId(String id);

    void deleteByObjectId(String id);

    void deleteByCid(long cid);

    CampaignBackUpDTO findOne(long cid);
}