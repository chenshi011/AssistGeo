package com.hikvision.energy.energis.fxtool.AssitTool.init;

import com.hikvision.energy.energis.exportsql.datastore.DataStoreManager;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by GOT.hodor on 2017/9/28.
 */

@Component
public class DataStorage {

    private DataStoreManager dataStoreManager;

    @PostConstruct
    public void init() {
        dataStoreManager = new DataStoreManager();
    }

    public DataStoreManager getDataStoreManager() {
        return dataStoreManager;
    }

}
