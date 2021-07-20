package org.rmt2.api.handler;

import java.security.NoSuchAlgorithmException;

import org.dao.mapping.orm.rmt2.AppRole;
import org.dao.mapping.orm.rmt2.Application;
import org.dao.mapping.orm.rmt2.ApplicationAccess;
import org.dao.mapping.orm.rmt2.GroupRoles;
import org.dao.mapping.orm.rmt2.Roles;
import org.dao.mapping.orm.rmt2.UserAppRole;
import org.dao.mapping.orm.rmt2.UserGroup;
import org.dao.mapping.orm.rmt2.UserLogin;
import org.dao.mapping.orm.rmt2.UserResource;
import org.dao.mapping.orm.rmt2.UserResourceAccess;
import org.dao.mapping.orm.rmt2.UserResourceSubtype;
import org.dao.mapping.orm.rmt2.UserResourceType;
import org.dao.mapping.orm.rmt2.VwAppRoles;
import org.dao.mapping.orm.rmt2.VwResource;
import org.dao.mapping.orm.rmt2.VwResourceType;
import org.dao.mapping.orm.rmt2.VwUser;
import org.dao.mapping.orm.rmt2.VwUserAppRoles;
import org.dao.mapping.orm.rmt2.VwUserGroup;
import org.dao.mapping.orm.rmt2.VwUserResourceAccess;
import org.modules.authentication.CryptoUtils;

import com.api.util.RMT2Date;

public class SecurityMockOrmDataFactory {
    public static final int TEST_NEW_APP_ID = 123450;
    public static final int TEST_EXISTING_APP_ID = 1000;
    public static final int TEST_USER_ID = 1000;
    public static final int TEST_RESOURCE_ID = 2000;
    public static final int TEST_RESOURCE_TYPE_ID = 3000;
    public static final int TEST_RESOURCE_SUBTYPE_ID = 4000;
    public static final int TEST_ROLE_ID = 5000;
    public static final int TEST_APP_ROLE_ID = 6000;
    public static final int TEST_USER_APP_ROLE_ID = 7000;
    public static final int TEST_APP_ACCESS_ID = 8000;
    public static final int TEST_GROUP_ROLD_ID = 9000;
    public static final int TEST_RESOURCE_ACCESS_ID = 5555;
    public static final int TEST_GROUP_ID = 500;
    public static final int TEST_UPDATE_RC = 1;
    
   /**
    * 
    * @param id
    * @return
    */
    public static final Application createOrmApplication(int id) {
        Application o = new Application();
        o.setAppId(id);
        o.setName("ApplicationName_" + id);
        o.setDescription("ApplicationDescription_" + id);
        return o;
    }
    
    /**
     * 
     * @param id
     * @param appId
     * @param loginId
     * @param loggedIn
     * @return
     */
    public static final ApplicationAccess createOrmApplicationAccess(int id, int appId, int loginId, boolean loggedIn) {
        ApplicationAccess o = new ApplicationAccess();
        o.setAppAccessId(id);
        o.setAppId(appId);
        o.setLoginId(loginId);
        o.setLoggedin(loggedIn ? 1 : 0);
        return o;
    }
    
    /**
     * 
     * @param id
     * @param appId
     * @param roleid
     * @return
     */
    public static final AppRole createOrmAppRole(int id, int appId, int roleid) {
        AppRole o = new AppRole();
        o.setAppRoleId(id);
        o.setAppId(appId);
        o.setRoleId(roleid);
        o.setCode("Code_" + id);
        o.setName("Name_" + id);
        o.setDescription("Description_" + id);
        return o;
    }
    
    /**
     * 
     * @param id
     * @param grpId
     * @param roleId
     * @return
     */
    public static final GroupRoles createOrmGroupRoles(int id, int grpId, int roleId) {
        GroupRoles o = new GroupRoles();
        o.setGrpRoleId(id);
        o.setGrpId(grpId);
        o.setRoleId(roleId);
        return o;
    }
    
    /**
     * 
     * @param id
     * @return
     */
    public static final Roles createOrmRoles(int id) {
        Roles o = new Roles();
        o.setRoleId(id);
        o.setName("RoleName_" + id);
        o.setDescription("RoleDescription_" + id);
        return o;
    }
 
    /**
     * 
     * @param id
     * @param appRoleId
     * @param loginId
     * @return
     */
    public static final UserAppRole createOrmUserAppRole(int id, int appRoleId, int loginId) {
        UserAppRole o = new UserAppRole();
        o.setUserAppRoleId(id);
        o.setAppRoleId(appRoleId);
        o.setLoginId(loginId);
        return o;
    }
    
    /**
     * 
     * @param id
     * @return
     */
    public static final UserGroup createOrmUserGroup(int id) {
        UserGroup o = new UserGroup();
        o.setGrpId(id);
        o.setDescription("UserGroupDescription_" + id);
        return o;
    }
    
    /**
     * 
     * @param loginId
     * @param grpId
     * @param userName
     * @param password
     * @param startDate
     * @return
     */
    public static final UserLogin createOrmUserLogin(int loginId, int grpId, String userName, String password, String startDate) {
        UserLogin o = new UserLogin();
        o.setLoginId(loginId);
        o.setGrpId(grpId);
        o.setUsername(userName);
        o.setPassword(password);
        o.setStartDate(RMT2Date.stringToDate(startDate));
        o.setBirthDate(RMT2Date.stringToDate("1966-01-01"));
        o.setFirstname("firstname_" + loginId);
        o.setLastname("lastname_" + loginId);
        o.setEmail(o.getFirstname() + "." + o.getLastname() + "@gte.net");
        o.setDescription("description_" + loginId);
        o.setActive(1);
        o.setTotalLogons(5);
        o.setSsn("111-11-1111");
        o.setLoggedIn(1);
        
        return o;
    }
    
    /**
     * 
     * @param rsrcId
     * @param rsrcTypeId
     * @param rsrcSubtypeId
     * @param url
     * @param secured
     * @return
     */
    public static final UserResource createOrmUserResource(int rsrcId, int rsrcTypeId, int rsrcSubtypeId, 
            String url, Boolean secured) {
        UserResource o = new UserResource();
        o.setRsrcId(rsrcId);
        o.setRsrcTypeId(rsrcTypeId);
        o.setRsrcSubtypeId(rsrcSubtypeId);
        o.setUrl(url);
        o.setName("name_" + rsrcId);
        o.setDescription("description_" + rsrcId);
        o.setSecured(secured != null && secured ? 1 : 0);
        return o;
    }
    
    /**
     * 
     * @param rsrcAccessId
     * @param grpId
     * @param rsrcId
     * @param loginId
     * @return
     */
    public static final UserResourceAccess createOrmUserResourceAccess(
            int rsrcAccessId, int grpId, int rsrcId, int loginId) {
        UserResourceAccess o = new UserResourceAccess();
        o.setRsrcAccessId(rsrcAccessId);
        o.setGrpId(grpId);
        o.setRsrcId(rsrcId);
        o.setLoginId(loginId);
        return o;
    }
    
    /**
     * 
     * @param rsrcSubtypeId
     * @param rsrcTypeId
     * @param name
     * @return
     */
    public static final UserResourceSubtype createOrmUserResourceSubtype(int rsrcSubtypeId, int rsrcTypeId) {
        UserResourceSubtype o = new UserResourceSubtype();
        o.setRsrcSubtypeId(rsrcSubtypeId);
        o.setRsrcTypeId(rsrcTypeId);
        o.setName("ResourceSubtypeName_" + rsrcSubtypeId);
        o.setDescription("ResourceSubtypeDescription_" + rsrcSubtypeId);
        return o;
    }
    
    /**
     * 
     * @param rsrcTypeId
     * @param description
     * @return
     */
    public static final UserResourceType createOrmUserResourceType(int rsrcTypeId) {
        UserResourceType o = new UserResourceType();
        o.setRsrcTypeId(rsrcTypeId);
        o.setDescription("ResourceTypeDescription_" + rsrcTypeId);
        return o;
    }
    
    /**
     * 
     * @param appRoleId
     * @param appId
     * @param roleId
     * @return
     */
    public static final VwAppRoles createOrmVwAppRoles(int appRoleId, int appId, int roleId) {
        VwAppRoles o = new VwAppRoles();
        o.setAppRoleId(appRoleId);
        o.setApplicationId(appId);
        o.setRoleId(roleId);
        o.setAppRoleCode("AppRoleCode_" + appRoleId);
        o.setAppRoleName("AppRoleName_" + appRoleId);
        o.setRoleName("RoleName_" + roleId);
        o.setAppName("AppName_" + appId);
        o.setAppRoleDescription("AppRoleDescription_" + appRoleId);
        return o;
    }
    
    /**
     * 
     * @param rsrcId
     * @param url
     * @param rsrcTypeId
     * @param rsrcSubtypeId
     * @param secured
     * @return
     */
    public static final VwResource createOrmVwResource(int rsrcId, String url, int rsrcTypeId, 
            int rsrcSubtypeId,  boolean secured) {
        VwResource o = new VwResource();
        o.setRsrcId(rsrcId);
        o.setRsrcTypeId(rsrcTypeId);
        o.setRsrcSubtypeId(rsrcSubtypeId);
        o.setUrl(url);
        o.setDescription("ResourceDescription_" + rsrcId);
        o.setSecured(secured ? 1 : 0);
        o.setName("ResourceName_" + rsrcId);
        o.setTypeDescr("ResourceTypeDescription_" + rsrcTypeId);
        o.setSubtypeName("ResourceSubTypeDescription_" + rsrcSubtypeId);
        o.setSubtypeName("ResourceSubTypeName_" + rsrcSubtypeId);

        return o;
    }
    
    /**
     * 
     * @param rsrcTypeId
     * @param rsrcSubtypeId
     * @return
     */
    public static final VwResourceType createOrmVwResourceType(int rsrcTypeId, int rsrcSubtypeId) {
        VwResourceType o = new VwResourceType();
        o.setResrcTypeId(rsrcTypeId);
        o.setResrcTypeName("ResourceTypeName_" + rsrcTypeId);
        o.setResrcSubtypeId(rsrcSubtypeId);
        o.setResrcSubtypeName("ResourceSubtypeName_" + rsrcSubtypeId);
        o.setResrcSubtypeDesc("ResourceSubtypeDescription_" + rsrcSubtypeId);
        return o;
    }
    
    /**
     * 
     * @param loginId
     * @param grpId
     * @param userName
     * @param password
     * @param startDate
     * @param shortName
     * @return
     */
    public static final VwUser createOrmVwUser(int loginId, int grpId, String userName, String password, 
            String startDate, String shortName) {
        VwUser o = new VwUser();
        o.setLoginId(loginId);
        o.setGrpId(grpId);
        o.setUsername(userName);
        String hashPw = null;
        try {
            hashPw = CryptoUtils.byteArrayToHexString(CryptoUtils.computeHash(userName + password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        o.setPassword(hashPw);
        o.setStartDate(RMT2Date.stringToDate(startDate));
        o.setBirthDate(RMT2Date.stringToDate("1966-01-01"));
        o.setFirstname("firstname_" + loginId);
        o.setLastname("lastname_" + loginId);
        o.setEmail(o.getFirstname() + "." + o.getLastname() + "@gte.net");
        o.setDescription("description_" + loginId);
        o.setActive(1);
        o.setTotalLogons(5);
        o.setSsn("111-11-1111");
        o.setShortName(shortName);
        return o;
    }
    
    /**
     * 
     * @param loginId
     * @param appId
     * @param roleId
     * @param appRoleId
     * @param grpId
     * @param userName
     * @param startDate
     * @return
     */
    public static final VwUserAppRoles createOrmVwUserAppRoles(int loginId, int appId, int roleId, 
            int appRoleId, int grpId, String userName, String startDate) {
        VwUserAppRoles o = new VwUserAppRoles();
        o.setUserAppRoleId(loginId + appId + roleId);
        o.setLoginUid(loginId);
        o.setApplicationId(appId);
        o.setAppName("AppName_" + appId);
        o.setRoleId(roleId);
        o.setRoleName("RoleName_" + roleId);
        o.setAppRoleId(appRoleId);
        o.setAppRoleCode("AppRoleCode_" + appRoleId);
        o.setAppRoleName("AppRoleName_" + appRoleId);
        o.setAppRoleDescription("AppRoleDescription_" + appRoleId);
        o.setGroupId(grpId);
        o.setGroupDescription("GroupDescription_" + grpId);
        o.setUsername(userName);
        o.setStartDate(RMT2Date.stringToDate(startDate));
        o.setBirthDate(RMT2Date.stringToDate("1966-01-01"));
        o.setFirstname("firstname_" + loginId);
        o.setLastname("lastname_" + loginId);
        o.setEmail(o.getFirstname() + "." + o.getLastname() + "@gte.net");
        o.setUserDescription("UserDescription_" + loginId);
        o.setActive(1);
        o.setSsn("111-11-1111");
        return o;
    }
    
    /**
     * 
     * @param loginId
     * @param grpId
     * @param userName
     * @param startDate
     * @return
     */
    public static final VwUserGroup createOrmVwUserGroup(int loginId, int grpId, String userName, String startDate) {
        VwUserGroup o = new VwUserGroup();
        o.setLoginId(loginId);
        o.setGroupId(grpId);
        o.setGroupDescription("GroupDescription_" + grpId);
        o.setUsername(userName);
        o.setStartDate(RMT2Date.stringToDate(startDate));
        o.setBirthDate(RMT2Date.stringToDate("1966-01-01"));
        o.setFirstname("firstname_" + loginId);
        o.setLastname("lastname_" + loginId);
        o.setEmail(o.getFirstname() + "." + o.getLastname() + "@gte.net");
        o.setUserDescription("UserDescription_" + loginId);
        o.setActive(1);
        o.setSsn("111-11-1111");
        return o;
    }
    
    /**
     * 
     * @param loginId
     * @param userName
     * @param grpId
     * @param rsrcId
     * @param url
     * @param rsrcTypeId
     * @param rsrcSubtypeId
     * @param secured
     * @return
     */
    public static final VwUserResourceAccess createOrmVwUserResourceAccess(int loginId, String userName, int grpId, 
            int rsrcId, String url, int rsrcTypeId, int rsrcSubtypeId, boolean secured) {
        VwUserResourceAccess o = new VwUserResourceAccess();
        o.setUserUid(loginId);
        o.setUserGroupId(grpId);
        o.setUserGroupName("GroupDescription_" + grpId);
        o.setUsername(userName);
        o.setUserFirstname("firstname_" + loginId);
        o.setUserLastname("lastname_" + loginId);
        o.setUserEmail(o.getUserFirstname() + "." + o.getUserLastname() + "@gte.net");
        o.setUserActiveStatus(1);
        
        o.setRsrcId(rsrcId);
        o.setResrcName("ResourceName_" + rsrcId);
        o.setResrcDesc("ResourceDescription_" + rsrcId);
        o.setResrcUrl(url);
        o.setResrcSecured(secured ? 1 : 0);
        
        o.setRsrcTypeId(rsrcTypeId);
        o.setResrcTypeName("ResourceTypeName_" + rsrcTypeId);
        
        o.setRsrcSubtypeId(rsrcSubtypeId);
        o.setResrcSubtypeDesc("ResourceSubTypeDescription_" + rsrcSubtypeId);
        o.setResrcSubtypeName("ResourceSubTypeName_" + rsrcSubtypeId);
        
        o.setHost("HostValue");
        return o;
    }
}
