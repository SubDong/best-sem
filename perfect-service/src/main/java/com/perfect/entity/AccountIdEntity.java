package com.perfect.entity;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by yousheng on 2014/8/13.
 *
 * @author yousheng
 */
public class AccountIdEntity {

    @Field("aid")
    private long accountId;


    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }
}
