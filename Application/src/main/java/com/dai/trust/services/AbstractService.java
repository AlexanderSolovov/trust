package com.dai.trust.services;

import com.dai.trust.common.SharedData;
import com.dai.trust.common.StringUtility;
import com.dai.trust.models.AbstractEntity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Generic class for all services
 */
public abstract class AbstractService implements Serializable {

    private static final Logger logger = LogManager.getLogger(AbstractService.class.getName());

    /**
     * Returns application URL
     *
     * @param request Request object
     * @return
     */
    public String getApplicationUrl(HttpServletRequest request) {
        return request.getRequestURL().substring(0, request.getRequestURL().length() - request.getRequestURI().length()) + request.getContextPath();
    }

    public EntityManager getEM() {
        return SharedData.getEm();
    }

    /**
     * Returns entity by ID.
     *
     * @param <T> Entity class
     * @param clazz Actual entity class
     * @param id Entity ID
     * @param lock If true, LockModeType.WRITE will be used
     * @return
     */
    public <T extends AbstractEntity> T getById(Class<T> clazz, String id, boolean lock) {
        if (lock) {
            return (T) getEM().find(clazz, id, LockModeType.WRITE);
        } else {
            return (T) getEM().find(clazz, id);
        }
    }

    /**
     * Returns all entities of the given type.
     *
     * @param <T> Entity class
     * @param clazz Actual entity class
     * @param orderBy Column or comma separated list of column for ordering. If
     * null or empty is provided, then no order command will be added into the
     * query.
     * @return
     */
    public <T extends AbstractEntity> List<T> getAll(Class<T> clazz, String orderBy) {
        String order = "";
        if (!StringUtility.isEmpty(orderBy)) {
            order = " order by " + orderBy;
        }
        return getEM().createQuery("from " + clazz.getSimpleName() + order).getResultList();
    }

    /**
     * Saves the entity.
     *
     * @param <T> Entity class
     * @param entity Entity to save
     * @param refresh Boolean flag indicating if saved entity have to be
     * refreshed (selected from the database) after saving.
     * @return Saved entity
     */
    public <T extends AbstractEntity> T save(T entity, boolean refresh) {
        EntityTransaction tx = null;
        try {
            tx = getEM().getTransaction();
            tx.begin();
            T savedEntity = getEM().merge(entity);
            tx.commit();
            
            if (refresh) {
                getEM().refresh(savedEntity);
            }
            return savedEntity;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            if (getEM().isOpen()) {
                getEM().close();
            }
            throw e;
        }
    }

    /**
     * Deletes the entity.
     *
     * @param <T> Entity class
     * @param entity Entity to save
     */
    public <T extends AbstractEntity> void delete(T entity) {
        EntityTransaction tx = null;
        try {
            tx = getEM().getTransaction();
            tx.begin();
            getEM().remove(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            if (getEM().isOpen()) {
                getEM().close();
            }
            throw e;
        }
    }

    /**
     * Deletes entity by ID.
     *
     * @param <T> Entity class
     * @param id Entity ID
     * @param clazz Entity class
     */
    public <T extends AbstractEntity> void deleteById(String id, Class<T> clazz) {
        EntityTransaction tx = null;
        try {
            tx = getEM().getTransaction();
            tx.begin();
            T entity = getById(clazz, id, false);
            getEM().remove(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            if (getEM().isOpen()) {
                getEM().close();
            }
            throw e;
        }
    }

    /**
     * Counts all entities in the table
     *
     * @param <T> Entity class
     * @param clazz Entity class
     * @return
     */
    public <T extends AbstractEntity> long countAll(Class<T> clazz) {
        return (Long) getEM().createQuery(
                new StringBuilder("select count(*) from ")
                .append(clazz.getName()).toString())
                .getSingleResult();
    }

    /**
     * Saves all changes to the database.
     */
    public void flush() {
        getEM().flush();
    }

    /**
     * Cleans all changes from the entity manager.
     */
    public void clear() {
        getEM().clear();
    }

    /**
     * Returns list of entities in the given range
     *
     * @param <T> Entity class
     * @param clazz Entity class
     * @param firstResult Row number from which to start fetching
     * @param maxResults Number of row to return
     * @return
     */
    public <T extends AbstractEntity> List<T> getByRange(Class<T> clazz, int firstResult, int maxResults) {
        return getEM()
                .createQuery(
                        new StringBuilder("select entity from ")
                        .append(clazz.getSimpleName())
                        .append(" as entity").toString())
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .getResultList();
    }

    /**
     * Returns comma separated list of system columns, which exists for every
     * entity.
     *
     * @param prefix Table name or it's alias. If null or empty, nothing will be
     * prefixed
     * @return
     */
    public String getSystemColumns(String prefix) {
        if (!StringUtility.isEmpty(prefix)) {
            prefix = prefix + ".";
        } else {
            prefix = "";
        }
        return String.format("%s, %s, %s, %s", prefix + "action_code", prefix + "action_user", prefix + "action_time", "rowversion");
    }

    /**
     * Returns comma separated list of system columns, which exists for every
     * entity.
     *
     * @return
     */
    public String getSystemColumns() {
        return getSystemColumns("");
    }

    /**
     * Returns comma separated list of columns for reference data tables. It
     * includes system columns as well.
     *
     * @param prefix Table name or it's alias. If null or empty, nothing will be
     * prefixed
     * @param langCode Language code for localization of "val" column.
     * @return
     */
    public String getRefDataColumns(String prefix, String langCode) {
        if (!StringUtility.isEmpty(prefix)) {
            prefix = prefix + ".";
        } else {
            prefix = "";
        }
        if (StringUtility.isEmpty(langCode)) {
            langCode = "null";
        } else {
            langCode = "'" + langCode + "'";
        }
        return String.format("%s, %s, %s, %s", prefix + "code", prefix + "get_translation(val, " + langCode + ") as val", prefix + "active", getSystemColumns(prefix));
    }

    /**
     * Returns comma separated list of columns for reference data tables. It
     * includes system columns as well.
     *
     * @param langCode Language code for localization of "val" column.
     * @return
     */
    public String getRefDataColumns(String langCode) {
        return getRefDataColumns("", langCode);
    }

    /**
     * Returns comma separated list of columns for reference data tables. It
     * includes system columns as well. The difference to
     * {@link AbstractService#getRefDataColumns(java.lang.String, java.lang.String)}
     * is that returned values are not localized.
     *
     * @param prefix Table name or it's alias. If null or empty, nothing will be
     * prefixed
     * @return
     */
    public String getRefDataColumnsUnlocalized(String prefix) {
        if (!StringUtility.isEmpty(prefix)) {
            prefix = prefix + ".";
        } else {
            prefix = "";
        }
        return String.format("%s, %s, %s, %s", prefix + "code", prefix + "val", prefix + "active", getSystemColumns(prefix));
    }
}
