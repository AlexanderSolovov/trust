package com.dai.trust.services.system;

import com.dai.trust.common.MessagesKeys;
import com.dai.trust.common.SharedData;
import com.dai.trust.common.StringUtility;
import com.dai.trust.exceptions.MultipleTrustException;
import com.dai.trust.exceptions.TrustException;
import com.dai.trust.models.search.ApplicationSearchResult;
import com.dai.trust.models.system.AppRole;
import com.dai.trust.models.system.User;
import com.dai.trust.models.system.UserGroup;
import com.dai.trust.services.AbstractService;
import java.security.MessageDigest;
import java.util.List;
import java.util.UUID;
import javax.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Contains methods, related to managing languages.
 */
public class UserService extends AbstractService {
    
    public static final String SESSION_USER_FULLNAME = "userFullName";
    private static final Logger logger = LogManager.getLogger(UserService.class.getName());

    public UserService() {
        super();
    }

    /** 
     * Returns user by user name. 
     * @param userName User name
     * @return 
     */
    public User getUser(String userName) {
        if (StringUtility.isEmpty(userName)) {
            return null;
        }
        try {
            return getEM().createQuery(
                    "FROM User WHERE userName = :userName", User.class)
                    .setParameter("userName", userName)
                    .getSingleResult();
        } catch (Exception e) {
            logger.error("Failed to get user", e);
            return null;
        }
    }
    
    /**
     * Returns all users.
     *
     * @return
     */
    public List<User> getUsers() {
        return getAll(User.class, "userName");
    }
    
    /**
     * Saves user to the database
     * @param user User object to save
     * @return Returns saved user
     */
    public User saveUser(User user){
        // Make validations
        MultipleTrustException errors = new MultipleTrustException();
        
        if(StringUtility.isEmpty(StringUtility.empty(user.getUserName()).trim())){
            errors.addError(new TrustException(MessagesKeys.ERR_USER_USER_NAME_EMPTY));
        } else {
            user.setUserName(user.getUserName().trim());
        }
        
        if(StringUtility.isEmpty(StringUtility.empty(user.getPasswd()).trim())){
            errors.addError(new TrustException(MessagesKeys.ERR_USER_PASSWORD_EMPTY));
        } else {
            user.setPasswd(user.getPasswd().trim());
        }
        
        if(StringUtility.isEmpty(StringUtility.empty(user.getFirstName()).trim())){
            errors.addError(new TrustException(MessagesKeys.ERR_USER_FIRST_NAME_EMPTY));
        } else {
            user.setFirstName(user.getFirstName().trim());
        }
        
        if(StringUtility.isEmpty(StringUtility.empty(user.getLastName()).trim())){
            errors.addError(new TrustException(MessagesKeys.ERR_USER_LAST_NAME_EMPTY));
        } else {
            user.setLastName(user.getLastName().trim());
        }
        
        if(user.getGroupCodes() == null || user.getGroupCodes().size() < 1){
            errors.addError(new TrustException(MessagesKeys.ERR_USER_NO_GROUPS));
        }
        
        if(errors.getErrors().size() > 0){
            throw errors;
        }
        
        // Get existing password
        String passwd = null;
        if (!StringUtility.isEmpty(user.getId())) {
            passwd = (String) getEM()
                    .createNativeQuery("select passwd from appuser where id = :id")
                    .setParameter("id", user.getId())
                    .getSingleResult();
        }
        
        // Generate new password if new or password doesn't match
        if(!StringUtility.empty(user.getPasswd()).equals(StringUtility.empty(passwd))){
            user.setPasswd(getPasswordHash(user.getPasswd()));
        }
        
        // Assign user id if missing
        if (StringUtility.isEmpty(user.getId())) {
            user.setId(UUID.randomUUID().toString());
        }

        // Assign user id to groups
        if (user.getGroupCodes()!= null) {
            for (UserGroup userGroup : user.getGroupCodes()) {
                if (StringUtility.isEmpty(userGroup.getUserId())) {
                    userGroup.setUserId(user.getId());
                }
            }
        }
        
        return save(user, true);
    }
    
    /**
     * Returns SHA-256 hash for the password.
     *
     * @param password Password string to hash.
     */
    private String getPasswordHash(String password) {
        String hashString = null;

        if (password != null && password.length() > 0) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(password.getBytes("UTF-8"));
                byte[] hash = md.digest();

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < hash.length; i++) {
                    String hex = Integer.toHexString(0xff & hash[i]);
                    if (hex.length() == 1) {
                        sb.append('0');
                    }
                    sb.append(hex);
                }

                hashString = sb.toString();

            } catch (Exception e) {
                logger.error("Failed to generate password hash", e);
                return null;
            }
        }
        return hashString;
    }
    
    /**
     * Returns User roles
     * @param userName User name
     * @return 
     */
    public List<AppRole> getUserRoles(String userName){
        Query q = getEM().createNativeQuery("select r.* "
                + "from public.approle r inner join public.user_role ur on r.code = ur.rolename "
                + "where ur.username = :userName", AppRole.class);
        q.setParameter("username", userName);
        return q.getResultList();
    }
}
