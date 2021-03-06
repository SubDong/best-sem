package com.perfect.service;

import com.perfect.dto.backup.KeywordBackUpDTO;
import com.perfect.dto.keyword.KeywordDTO;

import java.util.List;

/**
 * Created by XiaoWei on 2014/9/9.
 * 2014-11-28 refactor
 */
public interface KeywordBackUpService {

    KeywordDTO reducUpdate(String id);

    void reducDel(String id);
    void myInsertAll(List<KeywordBackUpDTO> list);
}
