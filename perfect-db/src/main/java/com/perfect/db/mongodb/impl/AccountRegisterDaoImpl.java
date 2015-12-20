package com.perfect.db.mongodb.impl;

import com.perfect.dao.account.AccountRegisterDAO;
import com.perfect.db.mongodb.base.AbstractSysBaseDAOImpl;
import com.perfect.dto.sys.SystemUserDTO;
import com.perfect.entity.sys.SystemUserEntity;
import com.perfect.utils.ObjectUtils;
import com.perfect.utils.SystemUserUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by SubDong on 2014/9/30.
 */
@Repository("accountRegisterDao")
public class AccountRegisterDaoImpl extends AbstractSysBaseDAOImpl<SystemUserDTO, Long> implements AccountRegisterDAO {
    @Override
    public void addAccount(SystemUserDTO systemUserDTO) {

        SystemUserEntity sys_user = new SystemUserEntity();
        BeanUtils.copyProperties(systemUserDTO, sys_user);

        getSysMongoTemplate().insert(sys_user, "sys_user");
    }

    @Override
    public void regAccount(SystemUserDTO systemUserDTO) {
        SystemUserEntity sys_user = new SystemUserEntity();


        /*List<SystemUserModuleEntity> systemUserModuleEntities = new ArrayList<>();

        systemUserDTO.getSystemUserModules().forEach(e -> {
            SystemUserModuleEntity systemUserModuleEntity = new SystemUserModuleEntity();
            List<ModuleAccountInfoEntity> moduleAccountInfoEntities = new ArrayList<>();
            List<SystemMenuEntity> systemMenuEntities = new ArrayList<>();

            BeanUtils.copyProperties(e, systemUserModuleEntity);

            e.getAccounts().forEach(f -> {
                ModuleAccountInfoEntity baiduAccountEntity = new ModuleAccountInfoEntity();
                BeanUtils.copyProperties(f, baiduAccountEntity);
                moduleAccountInfoEntities.add(baiduAccountEntity);
            });

            e.getMenus().forEach(g -> {
                SystemMenuEntity systemMenuEntity = new SystemMenuEntity();
                BeanUtils.copyProperties(g, systemMenuEntity);
                systemMenuEntities.add(systemMenuEntity);
            });
            BeanUtils.copyProperties(e, systemUserModuleEntity);
            systemUserModuleEntity.setAccounts(moduleAccountInfoEntities);
            systemUserModuleEntity.setMenus(systemMenuEntities);

            systemUserModuleEntities.add(systemUserModuleEntity);

        });

        BeanUtils.copyProperties(systemUserDTO,sys_user);
        sys_user.setSystemUserModules(systemUserModuleEntities);*/

        //Convert to entity
        SystemUserEntity systemUserEntity = SystemUserUtils.retrieveEntityFromDTO(systemUserDTO);


        getSysMongoTemplate().insert(systemUserEntity, "sys_user");
    }

    @Override
    public SystemUserDTO getAccount(String userName) {
        SystemUserEntity user = getSysMongoTemplate().findOne(Query.query(Criteria.where("userName").is(userName)), getEntityClass(), "sys_user");

        SystemUserDTO systemUserDTO = ObjectUtils.convertToObject(user, SystemUserDTO.class);

        return systemUserDTO;
    }


    @Override
    public Class<SystemUserEntity> getEntityClass() {
        return SystemUserEntity.class;
    }

    @Override
    public Class<SystemUserDTO> getDTOClass() {
        return SystemUserDTO.class;
    }
}
