package com.dai.trust.common;

import com.dai.trust.db.DatabaseManager;
import com.dai.trust.filters.ContextFilter;
import java.util.HashMap;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

/**
 * This class is used as a shared data storage, which can be accessed between
 * different layers of the application. It will be created per request, using
 * {@link ContextFilter} and hence all data will live only during the request
 * time.
 */
public final class SharedData implements AutoCloseable {

    public static final String KEY_USER_NAME = "USER_NAME";
    public static final String KEY_APP_URL = "APP_URL";
    public static final String KEY_ENTITY_MANAGER = "ENTITY_MANAGER";
    public static final String KEY_SESSION = "SESSION";
    public static final String KEY_APP_PATH = "APP_PATH";

    private static ThreadLocal<SharedData> localThread = new ThreadLocal<>();
    private HashMap<String, Object> data;

    private SharedData() {
        this.data = new HashMap();
    }

    /**
     * Initializes SharedData storage. Call it only once in the servlet filter.
     *
     * @return
     */
    public static SharedData init() {
        SharedData sharedData = new SharedData();
        localThread.set(sharedData);
        return sharedData;
    }

    /**
     * Returns value from the shared data storage. If key not found or storage
     * is not initialized, null value will be returned.
     *
     * @param key Value key in the storage
     * @return
     */
    public static Object get(String key) {
        if (localThread.get() != null && !StringUtility.isEmpty(key)) {
            return localThread.get().data.get(key);
        }
        return null;
    }

    /**
     * Records value to the shared data storage, under provided key name.
     *
     * @param key Key name for the value
     * @param value Value to record into storage
     * @param replace Boolean flag indicating whether to replace existing key or
     * not
     * @return Returns true if the value was successfully recorded
     */
    public static boolean set(String key, Object value, boolean replace) {
        if (localThread.get() == null || StringUtility.isEmpty(key)) {
            return false;
        }

        if (localThread.get().data.containsKey(key)) {
            if (replace) {
                localThread.get().data.remove(key);
                localThread.get().data.put(key, value);
            }
        } else {
            localThread.get().data.put(key, value);
        }
        return true;
    }

    /**
     * Records value to the shared data storage, under provided key name. If key
     * already exists in the storage, its value will be overridden.
     *
     * @param key Key name for the value
     * @param value Value to record into storage
     * @return Returns true if the value was successfully recorded
     */
    public static boolean set(String key, Object value) {
        return set(key, value, true);
    }

    /**
     * Shortcut to the get method to return user name.
     *
     * @return
     */
    public static String getUserName() {
        return getStringValue(KEY_USER_NAME);
    }
    
    /**
     * Shortcut to the get method to return physical path of the application.
     *
     * @return
     */
    public static String getAppPath() {
        return getStringValue(KEY_APP_PATH);
    }

    /** 
     * Returns EntityManager instance for the current request. 
     * @return 
     */
    public static EntityManager getEm(){
        Object val = get(KEY_ENTITY_MANAGER);
        if(val == null){
            val = DatabaseManager.getEntityManager();
            set(KEY_ENTITY_MANAGER, val);
        } 
        return (EntityManager) val;
    }
    
    /** 
     * Returns Session instance. 
     * @return 
     */
    public static HttpSession getSession(){
        Object val = get(KEY_SESSION);
        if(val == null){
            return null;
        } 
        return (HttpSession) val;
    }
    
    /**
     * Shortcut to the get method to return application URL.
     *
     * @return
     */
    public static String getAppUrl() {
        return getStringValue(KEY_APP_URL);
    }

    /**
     * Returns String value from the storage
     *
     * @param key Value key.
     * @return
     */
    public static String getStringValue(String key) {
        Object val = get(key);
        if (val == null) {
            return null;
        }
        return val.toString();
    }

    @Override
    public void close() {
        Object val = get(KEY_ENTITY_MANAGER);
        if(val != null){
            if(((EntityManager)val).isOpen()){
                ((EntityManager)val).close();
            }
        } 
        localThread.remove();
    }
}
