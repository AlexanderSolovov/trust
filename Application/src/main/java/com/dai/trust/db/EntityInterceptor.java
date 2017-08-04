package com.dai.trust.db;

import com.dai.trust.common.SharedData;
import com.dai.trust.common.StringUtility;
import com.dai.trust.models.AbstractCodeEntity;
import com.dai.trust.models.AbstractEntity;
import com.dai.trust.models.AbstractIdEntity;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Hibernate interceptor class to exclude entity system fields from the dirty
 * checks. This intercepter have to be referenced in persistence.xml file as a
 * property.
 */
public class EntityInterceptor extends EmptyInterceptor {

    @Override
    public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        if (AbstractEntity.class.isAssignableFrom(entity.getClass()) && previousState != null) {
            // Override system fields in the current state with loaded values
            for (int i = 0; i < propertyNames.length; i++) {
                if (propertyNames[i].equalsIgnoreCase("actionUser") || propertyNames[i].equalsIgnoreCase("actionCode")) {
                    if (propertyNames[i].equalsIgnoreCase("actionCode") && currentState[i] != null && currentState[i].toString().equalsIgnoreCase("d")) {
                        continue;
                    }
                    currentState[i] = previousState[i];
                }
            }
        }
        return super.findDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        // Update before delete to record user name
        if (AbstractEntity.class.isAssignableFrom(entity.getClass()) && id != null) {

            //List<String> ids = new ArrayList();
            HashMap<String, String> ids = new HashMap<>();
            String idColumns = "";
            String table = entity.getClass().getName();
            Table t = entity.getClass().getAnnotation(Table.class);

            if (t != null && !StringUtility.isEmpty(t.name())) {
                table = t.name();
                //throw new TrustException(MessagesKeys.ERR_NO_TABLE_NAME, new Object[]{clazz.getName()});
            }

            if (AbstractCodeEntity.class.isAssignableFrom(entity.getClass())) {
                idColumns = "code = :code";
                ids.put("code", id.toString());
            } else if (AbstractIdEntity.class.isAssignableFrom(entity.getClass())) {
                idColumns = "id = :id";
                ids.put("id", id.toString());
            } else {
                // Look for ID fields
                for (Field f : entity.getClass().getDeclaredFields()) {
                    if (f.getAnnotation(Id.class) != null) {
                        try {
                            String fName = f.getName();
                            Method m = entity.getClass().getMethod("get" + fName.substring(0, 1).toUpperCase() + fName.substring(1), new Class[]{});

                            ids.put(f.getName(), m.invoke(entity, new Object[]{}).toString());

                            if (idColumns.equals("")) {
                                idColumns = fName + " = :" + fName;
                            } else {
                                idColumns = idColumns + " and " + fName + " = :" + fName;
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(EntityInterceptor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

            EntityManager em = DatabaseManager.getEntityManager();

            try {
                em.getTransaction().begin();
                Query q = em.createQuery(String.format("update %s set action_code = 'd', action_user='%s' where %s",
                        entity.getClass().getName(), SharedData.getUserName(), idColumns));
                for (Map.Entry<String, String> entry : ids.entrySet()) {
                    q.setParameter(entry.getKey(), entry.getValue());
                }
                q.executeUpdate();
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
            } finally {
                em.close();
            }
        }
    }
}
