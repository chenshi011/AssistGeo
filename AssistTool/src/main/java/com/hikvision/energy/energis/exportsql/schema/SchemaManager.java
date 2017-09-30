package com.hikvision.energy.energis.exportsql.schema;

import com.hikvision.energy.energis.exportsql.loginfo.LogInfoDefinition;
import org.geotools.data.DataStore;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * manage schema in DataStore
 * Created by GOT.hodor on 2017/9/23.
 */
public class SchemaManager {

    protected static final Logger log = LoggerFactory.getLogger(SchemaManager.class);

    private Map<String, SimpleFeatureType> schemaMap;

    private DataStore dataStore;

    private String[] schemaNames;

    /**
     * construct
     * @param dataStore DataStore
     */
    public SchemaManager(DataStore dataStore) {
        this.dataStore = dataStore;
        schemaMap = new HashMap<>();
    }

    /**
     * load schema name
     * @return
     */
    public boolean preloadSchemaNames() {
        return this.reloadSchemaNames();
    }

    /**
     * load schema name
     * @return
     */
    public boolean reloadSchemaNames() {
        if (dataStore == null) {
            log.error(LogInfoDefinition.S_DATA_STORE_NOT_EXIST);
            return false;
        }

        try{
            schemaNames = dataStore.getTypeNames();
            return true;
        }catch (IOException e) {
            log.error(LogInfoDefinition.S_SCHEMA_GET_EXCEPTION, e.getMessage(), e);
        }

        return false;
    }

    /**
     * fetch schema from SchemaManager
     * @param schemaName schema name
     * @return
     */
    public SimpleFeatureType fetchSchema(String schemaName) {
        if (!dataStoreExist()) {
            log.error(LogInfoDefinition.S_DATA_STORE_NOT_EXIST);
            return null;
        }

        if (schemaMap.containsKey(schemaName)) {
            return schemaMap.get(schemaName);
        }

        if (schemaNameExist(schemaName)) {
            SimpleFeatureType schema = getSchema(schemaName);
            if (schema != null) {
                schemaMap.put(schemaName, schema);
                return schema;
            }
        }

        return null;

    }

    /**
     *
     * @param schema schema
     */
    public void addSchema(SimpleFeatureType schema) {
        if (schema == null) {
            return;
        }

        if (schema.getTypeName() == null) {
            return;
        }

        if (!dataStoreExist()) {
            return;
        }

        try{
            dataStore.createSchema(schema);

            StringBuffer sb = new StringBuffer();
            String sep = ",";
            for (int i=0; i < schemaNames.length; i++) {
                sb.append(schemaNames[i])
                        .append(sep);
            }
            sb.append(schema.getTypeName());

            schemaNames = sb.toString().split(sep);

        }catch (IOException e) {

        }

    }

    /**
     *
     * @param schemaName
     */
    public void removeSchema(String schemaName) {
        if (!schemaNameExist(schemaName)) {
            return;
        }

        try{
            dataStore.removeSchema(schemaName);

            List<String> nameList = Arrays.asList(schemaNames).stream()
                    .filter(name -> !name.equals(schemaName))
                    .collect(Collectors.toList());
            schemaNames = nameList.toArray(new String[nameList.size()]);

        }catch (IOException e) {

        }


    }

    /**
     * get schema from DataStore by name
     * @param schemaName
     * @return
     */
    private SimpleFeatureType getSchema(String schemaName) {
        try{
            SimpleFeatureType schema = dataStore.getSchema(schemaName);
            return schema;
        }catch (IOException e) {
            log.error(LogInfoDefinition.S_SCHEMA_GET_EXCEPTION, schemaName, e);
        }
        return null;
    }

    /**
     * Dose Schema exist
     * @param schemaName schema name
     * @return
     */
    public boolean schemaNameExist(String schemaName) {
        if (!dataStoreExist()) {
            log.error(LogInfoDefinition.S_SCHEMA_NOT_EXIST, schemaName);
            return false;
        }

        if (schemaNames == null) {
            return false;
        }

        return (Arrays.asList(schemaNames)).contains(schemaName);
    }

    private boolean dataStoreExist() {
        return dataStore!= null;
    }


    public DataStore getDataStore() {
        return dataStore;
    }

    public void setDataStore(DataStore dataStore) {
        this.dataStore = dataStore;
    }
}
