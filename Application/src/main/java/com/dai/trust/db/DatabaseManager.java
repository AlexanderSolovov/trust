package com.dai.trust.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Contains methods to connect and open database
 */
public class DatabaseManager {
    private static EntityManagerFactory entityManagerFactory;
 
    public static EntityManager getEntityManager() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory("trust");
        }
        return entityManagerFactory.createEntityManager();
    }
 
    public static void killEntityManagerFactory() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }
}
