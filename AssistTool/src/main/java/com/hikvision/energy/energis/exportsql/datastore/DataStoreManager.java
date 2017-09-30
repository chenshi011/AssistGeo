package com.hikvision.energy.energis.exportsql.datastore;

import com.hikvision.energy.energis.exportsql.loginfo.LogInfoDefinition;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * manage DataStore
 *
 * Created by GOT.hodor on 2017/9/23.
 */
public class DataStoreManager {
    protected static final Logger log = LoggerFactory.getLogger(DataStoreManager.class);

    private Map<String, DataStore> dataStoreMap;

    /**
     * construct
     */
    public DataStoreManager() {
        dataStoreMap = new HashMap<>();
    }

    /**
     * store DataStore
     * @param storeName store name
     * @param params store connect params
     */
    public boolean storeConnection(String storeName, Map<String, Object> params){
        // dose DataStore exist
        if (dataStoreMap.containsKey(storeName)) {
            return false;
        }
        try{
            // create connection
            DataStore dataStore = DataStoreFinder.getDataStore(params);
            if (dataStore != null) {
                dataStoreMap.put(storeName, dataStore);
                return true;
            }
        }catch (IOException e) {
            log.error(LogInfoDefinition.S_DATA_STORE_CONNECT_ERROR + "[{}]", storeName, e);
        }

        return false;
    }

    /**
     * fetch DataStore by key
     * @param storeName store name
     * @return
     */
    public DataStore fetchDataStore(String storeName) {
        if (!dataStoreMap.containsKey(storeName)) {
            log.debug(LogInfoDefinition.S_DATA_STORE_NOT_EXIST, storeName);
            return null;
        }
        return dataStoreMap.get(storeName);
    }

    /**
     * remove DataStore & dispose DataStore
     * @param storeName store name
     */
    public void removeConnection(String storeName) {
        // dose DataStore exist
        if (!dataStoreMap.containsKey(storeName)) {
            log.debug(LogInfoDefinition.S_DATA_STORE_NOT_EXIST, storeName);
            return;
        }

        // dispose the DataStore
        DataStore dataStore = dataStoreMap.get(storeName);
        dataStoreMap.remove(storeName);
        if (dataStore != null) {
            dataStore.dispose();
            log.info("DataStore has removed and disposed");
        }
    }



}
