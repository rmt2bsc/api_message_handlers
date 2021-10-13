package org.rmt2.api.handler;

import java.util.ArrayList;
import java.util.List;

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
import org.dto.ApplicationDto;
import org.dto.CategoryDto;
import org.dto.ResourceDto;
import org.dto.UserDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;

/**
 * Security testing facility that is mainly responsible for setting up mock data.
 * <p>
 * All derived media related Api unit tests should inherit this class
 * to prevent duplicating common functionality.
 * 
 * @author rterrell
 * 
 */
public class SecurityMockDtoData {

    public static final List<AppRole> createAppRoleMockData() {
        List<AppRole> list = new ArrayList<>();
        int appRoleId = SecurityMockOrmDataFactory.TEST_APP_ROLE_ID;
        int roleId = SecurityMockOrmDataFactory.TEST_ROLE_ID;
        AppRole o = SecurityMockOrmDataFactory.createOrmAppRole(appRoleId,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID, roleId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmAppRole(++appRoleId,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID, ++roleId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmAppRole(++appRoleId,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID, ++roleId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmAppRole(++appRoleId,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID, ++roleId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmAppRole(++appRoleId,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID, ++roleId);
        list.add(o);
        
        return list;
    }

    
    public static final List<ApplicationDto> createApplicationMockData() {
        List<ApplicationDto> list = new ArrayList<>();
        int appId = SecurityMockOrmDataFactory.TEST_NEW_APP_ID;
        Application o = SecurityMockOrmDataFactory.createOrmApplication(appId);
        ApplicationDto d = Rmt2OrmDtoFactory.getAppDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmApplication(++appId);
        d = Rmt2OrmDtoFactory.getAppDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmApplication(++appId);
        d = Rmt2OrmDtoFactory.getAppDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmApplication(++appId);
        d = Rmt2OrmDtoFactory.getAppDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmApplication(++appId);
        d = Rmt2OrmDtoFactory.getAppDtoInstance(o);
        list.add(d);
        
        return list;
    }
    
    public static final List<ApplicationAccess> createApplicationAccessMockData() {
        List<ApplicationAccess> list = new ArrayList<>();
        int appAccessId = SecurityMockOrmDataFactory.TEST_APP_ACCESS_ID;
        int appId = SecurityMockOrmDataFactory.TEST_NEW_APP_ID;
        ApplicationAccess o = SecurityMockOrmDataFactory
                .createOrmApplicationAccess(appAccessId, appId,
                        SecurityMockOrmDataFactory.TEST_USER_ID, true);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmApplicationAccess(++appAccessId,
                ++appId, SecurityMockOrmDataFactory.TEST_USER_ID, true);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmApplicationAccess(++appAccessId,
                ++appId, SecurityMockOrmDataFactory.TEST_USER_ID, true);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmApplicationAccess(++appAccessId,
                ++appId, SecurityMockOrmDataFactory.TEST_USER_ID, true);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmApplicationAccess(++appAccessId,
                ++appId, SecurityMockOrmDataFactory.TEST_USER_ID, true);
        list.add(o);
        
        return list;
    }
    
    
    public static final List<GroupRoles> createGroupRolesMockData() {
        List<GroupRoles> list = new ArrayList<>();
        int grpRoleId = SecurityMockOrmDataFactory.TEST_GROUP_ROLD_ID;
        GroupRoles o = SecurityMockOrmDataFactory.createOrmGroupRoles(grpRoleId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID,
                SecurityMockOrmDataFactory.TEST_ROLE_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmGroupRoles(++grpRoleId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID,
                SecurityMockOrmDataFactory.TEST_ROLE_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmGroupRoles(++grpRoleId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID,
                SecurityMockOrmDataFactory.TEST_ROLE_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmGroupRoles(++grpRoleId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID,
                SecurityMockOrmDataFactory.TEST_ROLE_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmGroupRoles(++grpRoleId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID,
                SecurityMockOrmDataFactory.TEST_ROLE_ID);
        list.add(o);
        
        return list;
    }
    
    public static final List<CategoryDto> createRolesMockData() {
        List<CategoryDto> list = new ArrayList<>();
        int roleId = SecurityMockOrmDataFactory.TEST_ROLE_ID;
        Roles o = SecurityMockOrmDataFactory.createOrmRoles(roleId);
        CategoryDto d = Rmt2OrmDtoFactory.getRoleDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmRoles(++roleId);
        d = Rmt2OrmDtoFactory.getRoleDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmRoles(++roleId);
        d = Rmt2OrmDtoFactory.getRoleDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmRoles(++roleId);
        d = Rmt2OrmDtoFactory.getRoleDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmRoles(++roleId);
        d = Rmt2OrmDtoFactory.getRoleDtoInstance(o);
        list.add(d);
        
        return list;
    }
    
    public static final List<UserAppRole> createUserAppRoleMockData() {
        List<UserAppRole> list = new ArrayList<>();
        int userAppRoleId = SecurityMockOrmDataFactory.TEST_USER_APP_ROLE_ID;
        UserAppRole o = SecurityMockOrmDataFactory.createOrmUserAppRole(userAppRoleId,
                SecurityMockOrmDataFactory.TEST_APP_ROLE_ID,
                SecurityMockOrmDataFactory.TEST_USER_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserAppRole(++userAppRoleId,
                SecurityMockOrmDataFactory.TEST_APP_ROLE_ID,
                SecurityMockOrmDataFactory.TEST_USER_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserAppRole(++userAppRoleId,
                SecurityMockOrmDataFactory.TEST_APP_ROLE_ID,
                SecurityMockOrmDataFactory.TEST_USER_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserAppRole(++userAppRoleId,
                SecurityMockOrmDataFactory.TEST_APP_ROLE_ID,
                SecurityMockOrmDataFactory.TEST_USER_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserAppRole(++userAppRoleId,
                SecurityMockOrmDataFactory.TEST_APP_ROLE_ID,
                SecurityMockOrmDataFactory.TEST_USER_ID);
        list.add(o);
        
        return list;
    }
    
    
    public static final List<UserDto> createUserGroupMockData() {
        List<UserDto> list = new ArrayList<>();
        int groupId = SecurityMockOrmDataFactory.TEST_GROUP_ID;
        UserGroup o = SecurityMockOrmDataFactory.createOrmUserGroup(groupId);
        UserDto d = Rmt2OrmDtoFactory.getGroupDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmUserGroup(++groupId);
        d = Rmt2OrmDtoFactory.getGroupDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmUserGroup(++groupId);
        d = Rmt2OrmDtoFactory.getGroupDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmUserGroup(++groupId);
        d = Rmt2OrmDtoFactory.getGroupDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmUserGroup(++groupId);
        d = Rmt2OrmDtoFactory.getGroupDtoInstance(o);
        list.add(d);
        
        return list;
    }

    public static final List<UserLogin> createUserLoginMockData() {
        List<UserLogin> list = new ArrayList<>();
        int loginId = SecurityMockOrmDataFactory.TEST_USER_ID;
        UserLogin o = SecurityMockOrmDataFactory.createOrmUserLogin(loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "password", "2018-01-01");
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserLogin(++loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "password", "2018-01-01");
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserLogin(++loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "password", "2018-01-01");
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserLogin(++loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "password", "2018-01-01");
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserLogin(++loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "password", "2018-01-01");
        list.add(o);
        
        return list;
    }
    
    public static final List<ResourceDto> createUserResourceMockData() {
        List<ResourceDto> list = new ArrayList<>();
        int resourceId = SecurityMockOrmDataFactory.TEST_RESOURCE_ID;
        UserResource o = SecurityMockOrmDataFactory.createOrmUserResource(resourceId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID,
                "URL_" + resourceId, true);
        ResourceDto d = Rmt2OrmDtoFactory.getResourceDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmUserResource(++resourceId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID,
                "URL_" + resourceId, true);
        d = Rmt2OrmDtoFactory.getResourceDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmUserResource(++resourceId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID,
                "URL_" + resourceId, true);
        d = Rmt2OrmDtoFactory.getResourceDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmUserResource(++resourceId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID,
                "URL_" + resourceId, true);
        d = Rmt2OrmDtoFactory.getResourceDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmUserResource(++resourceId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID,
                "URL_" + resourceId, true);
        d = Rmt2OrmDtoFactory.getResourceDtoInstance(o);
        list.add(d);
        
        return list;
    }
    
    
    public static final List<UserResourceAccess> createUserResourceAccessMockData() {
        List<UserResourceAccess> list = new ArrayList<>();
        int resourceAccessId = SecurityMockOrmDataFactory.TEST_RESOURCE_ACCESS_ID;
        UserResourceAccess o = SecurityMockOrmDataFactory
                .createOrmUserResourceAccess(resourceAccessId,
                        SecurityMockOrmDataFactory.TEST_GROUP_ID,
                        SecurityMockOrmDataFactory.TEST_RESOURCE_ID,
                        SecurityMockOrmDataFactory.TEST_USER_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserResourceAccess(++resourceAccessId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_ID,
                SecurityMockOrmDataFactory.TEST_USER_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserResourceAccess(++resourceAccessId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_ID,
                SecurityMockOrmDataFactory.TEST_USER_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserResourceAccess(++resourceAccessId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_ID,
                SecurityMockOrmDataFactory.TEST_USER_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserResourceAccess(++resourceAccessId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_ID,
                SecurityMockOrmDataFactory.TEST_USER_ID);
        list.add(o);
        
        return list;
    }
    
    public static final List<ResourceDto> createUserResourceSubtypeMockData() {
        List<ResourceDto> list = new ArrayList<>();
        int userResourceSubtypeId = SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID;
        UserResourceSubtype o = SecurityMockOrmDataFactory.createOrmUserResourceSubtype(userResourceSubtypeId,
                        SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID);
        ResourceDto d = Rmt2OrmDtoFactory.getResourceDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmUserResourceSubtype(++userResourceSubtypeId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID);
        d = Rmt2OrmDtoFactory.getResourceDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmUserResourceSubtype(++userResourceSubtypeId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID);
        d = Rmt2OrmDtoFactory.getResourceDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmUserResourceSubtype(++userResourceSubtypeId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID);
        d = Rmt2OrmDtoFactory.getResourceDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmUserResourceSubtype(++userResourceSubtypeId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID);
        d = Rmt2OrmDtoFactory.getResourceDtoInstance(o);
        list.add(d);
        
        return list;
    }
    
    public static final List<ResourceDto> createSingleUserResourceTypeMockData() {
        List<ResourceDto> list = new ArrayList<>();
        int resourceTypeId = SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID;
        UserResourceType o = SecurityMockOrmDataFactory.createOrmUserResourceType(resourceTypeId);
        ResourceDto d = Rmt2OrmDtoFactory.getResourceDtoInstance(o);
        list.add(d);
        return list;
    }

    public static final List<ResourceDto> createUserResourceTypeMockData() {
        List<ResourceDto> list = new ArrayList<>();
        int resourceTypeId = SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID;
        UserResourceType o = SecurityMockOrmDataFactory.createOrmUserResourceType(resourceTypeId);
        ResourceDto d = Rmt2OrmDtoFactory.getResourceDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmUserResourceType(++resourceTypeId);
        d = Rmt2OrmDtoFactory.getResourceDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmUserResourceType(++resourceTypeId);
        d = Rmt2OrmDtoFactory.getResourceDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmUserResourceType(++resourceTypeId);
        d = Rmt2OrmDtoFactory.getResourceDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmUserResourceType(++resourceTypeId);
        d = Rmt2OrmDtoFactory.getResourceDtoInstance(o);
        list.add(d);
        
        return list;
    }
    
    public static final List<CategoryDto> createVwAppRolesMockData() {
        List<CategoryDto> list = new ArrayList<>();
        int appRoleId = SecurityMockOrmDataFactory.TEST_APP_ROLE_ID;
        int roleId = SecurityMockOrmDataFactory.TEST_ROLE_ID;
        VwAppRoles o = SecurityMockOrmDataFactory.createOrmVwAppRoles(appRoleId,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID, roleId);
        CategoryDto d = Rmt2OrmDtoFactory.getAppRoleDtoInstance(o, null);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmVwAppRoles(++appRoleId,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID, ++roleId);
        d = Rmt2OrmDtoFactory.getAppRoleDtoInstance(o, null);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmVwAppRoles(++appRoleId,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID, ++roleId);
        d = Rmt2OrmDtoFactory.getAppRoleDtoInstance(o, null);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmVwAppRoles(++appRoleId,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID, ++roleId);
        d = Rmt2OrmDtoFactory.getAppRoleDtoInstance(o, null);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmVwAppRoles(++appRoleId,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID, ++roleId);
        d = Rmt2OrmDtoFactory.getAppRoleDtoInstance(o, null);
        list.add(d);
        
        return list;
    }
  
    public static final List<VwResource> createVwResourceMockData() {
        List<VwResource> list = new ArrayList<>();
        int resourceId = SecurityMockOrmDataFactory.TEST_RESOURCE_ID;
        int resourceTypeId = SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID;
        VwResource o = SecurityMockOrmDataFactory.createOrmVwResource(resourceId, "URL_" + resourceId,
                resourceTypeId, SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID, true);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwResource(++resourceId, "URL_" + resourceId,
                ++resourceTypeId, SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID, true);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwResource(++resourceId, "URL_" + resourceId,
                ++resourceTypeId, SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID, true);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwResource(++resourceId, "URL_" + resourceId,
                ++resourceTypeId, SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID, true);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwResource(++resourceId, "URL_" + resourceId,
                ++resourceTypeId, SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID, true);
        list.add(o);
        
        return list;
    }
    
    public static final List<VwResourceType> createVwResourceTypeMockData() {
        List<VwResourceType> list = new ArrayList<>();
        int appRoleId = SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID;
        VwResourceType o = SecurityMockOrmDataFactory.createOrmVwResourceType(
                appRoleId, SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwResourceType(++appRoleId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwResourceType(++appRoleId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwResourceType(++appRoleId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwResourceType(++appRoleId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID);
        list.add(o);
        
        return list;
    }
    
    public static final List<VwUser> createVwUserSingleMockData() {
        List<VwUser> list = new ArrayList<>();
        int loginId = SecurityMockOrmDataFactory.TEST_USER_ID;
        VwUser o = SecurityMockOrmDataFactory.createOrmVwUser(loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "test1234" + loginId, "2018-01-01", "ShortName_" + loginId);
        list.add(o);
        return list;
    }
    
    public static final List<UserDto> createSingleUserMockData() {
        List<UserDto> list = new ArrayList<>();
        int loginId = SecurityMockOrmDataFactory.TEST_USER_ID;
        VwUser o = SecurityMockOrmDataFactory.createOrmVwUser(loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "test1234" + loginId, "2018-01-01", "ShortName_" + loginId);
        UserDto d = Rmt2OrmDtoFactory.getUserDtoInstance(o);
        list.add(d);
        return list;
    }
    public static final List<UserDto> createVwUserMockData() {
        List<UserDto> list = new ArrayList<>();
        int loginId = SecurityMockOrmDataFactory.TEST_USER_ID;
        VwUser o = SecurityMockOrmDataFactory.createOrmVwUser(loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "test1234" + loginId, "2018-01-01", "ShortName_" + loginId);
        UserDto d = Rmt2OrmDtoFactory.getUserDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmVwUser(++loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "test1234" + loginId, "2018-01-01", "ShortName_" + loginId);
        d = Rmt2OrmDtoFactory.getUserDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmVwUser(++loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "test1234" + loginId, "2018-01-01", "ShortName_" + loginId);
        d = Rmt2OrmDtoFactory.getUserDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmVwUser(++loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "test1234" + loginId, "2018-01-01", "ShortName_" + loginId);
        d = Rmt2OrmDtoFactory.getUserDtoInstance(o);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmVwUser(++loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "test1234" + loginId, "2018-01-01", "ShortName_" + loginId);
        d = Rmt2OrmDtoFactory.getUserDtoInstance(o);
        list.add(d);
        
        return list;
    }
    
    public static final List<CategoryDto> createVwUserAppRolesMockData() {
        List<CategoryDto> list = new ArrayList<>();
        int appRoleId = SecurityMockOrmDataFactory.TEST_APP_ROLE_ID;
        VwUserAppRoles o = SecurityMockOrmDataFactory.createOrmVwUserAppRoles(SecurityMockOrmDataFactory.TEST_USER_ID,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID,
                SecurityMockOrmDataFactory.TEST_ROLE_ID,
                appRoleId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "user_name",
                "2018-01-01");
        CategoryDto d = Rmt2OrmDtoFactory.getUserAppRoleDtoInstance(o, null);
        list.add(d);
        o = SecurityMockOrmDataFactory.createOrmVwUserAppRoles(SecurityMockOrmDataFactory.TEST_USER_ID,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID,
                SecurityMockOrmDataFactory.TEST_ROLE_ID,
                ++appRoleId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "user_name",
                "2018-01-01");
        d = Rmt2OrmDtoFactory.getUserAppRoleDtoInstance(o, null);
        list.add(d);
        // o =
        // SecurityMockOrmDataFactory.createOrmVwUserAppRoles(SecurityMockOrmDataFactory.TEST_USER_ID,
        // SecurityMockOrmDataFactory.TEST_NEW_APP_ID,
        // SecurityMockOrmDataFactory.TEST_ROLE_ID,
        // ++appRoleId,
        // SecurityMockOrmDataFactory.TEST_GROUP_ID, "user_name",
        // "2018-01-01");
        // list.add(o);
        // o =
        // SecurityMockOrmDataFactory.createOrmVwUserAppRoles(SecurityMockOrmDataFactory.TEST_USER_ID,
        // SecurityMockOrmDataFactory.TEST_NEW_APP_ID,
        // SecurityMockOrmDataFactory.TEST_ROLE_ID,
        // ++appRoleId,
        // SecurityMockOrmDataFactory.TEST_GROUP_ID, "user_name",
        // "2018-01-01");
        // list.add(o);
        // o =
        // SecurityMockOrmDataFactory.createOrmVwUserAppRoles(SecurityMockOrmDataFactory.TEST_USER_ID,
        // SecurityMockOrmDataFactory.TEST_NEW_APP_ID,
        // SecurityMockOrmDataFactory.TEST_ROLE_ID,
        // ++appRoleId,
        // SecurityMockOrmDataFactory.TEST_GROUP_ID, "user_name",
        // "2018-01-01");
        // list.add(o);
        
        return list;
    }
    
    public static final List<VwUserGroup> createVwUserGroupMockData() {
        List<VwUserGroup> list = new ArrayList<>();
        int loginId = SecurityMockOrmDataFactory.TEST_USER_ID;
        VwUserGroup o = SecurityMockOrmDataFactory.createOrmVwUserGroup(loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "2018-01-01");
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwUserGroup(++loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "2018-01-01");
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwUserGroup(++loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "2018-01-01");
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwUserGroup(++loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "2018-01-01");
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwUserGroup(++loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "2018-01-01");
        list.add(o);
        
        return list;
    }
    
    
    public static final List<VwUserResourceAccess> createVwUserResourceAccessMockData() {
        List<VwUserResourceAccess> list = new ArrayList<>();
        int loginId = SecurityMockOrmDataFactory.TEST_USER_ID;
        VwUserResourceAccess o = SecurityMockOrmDataFactory
                .createOrmVwUserResourceAccess(loginId, "UserName_" + loginId,
                        SecurityMockOrmDataFactory.TEST_GROUP_ID,
                        SecurityMockOrmDataFactory.TEST_RESOURCE_ID,
                        "URL_" + loginId,
                        SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID,
                        SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID, true);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwUserResourceAccess(++loginId,
                "UserName_" + loginId, SecurityMockOrmDataFactory.TEST_GROUP_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_ID, "URL_" + loginId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID, true);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwUserResourceAccess(++loginId,
                "UserName_" + loginId, SecurityMockOrmDataFactory.TEST_GROUP_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_ID, "URL_" + loginId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID, true);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwUserResourceAccess(++loginId,
                "UserName_" + loginId, SecurityMockOrmDataFactory.TEST_GROUP_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_ID, "URL_" + loginId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID, true);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwUserResourceAccess(++loginId,
                "UserName_" + loginId, SecurityMockOrmDataFactory.TEST_GROUP_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_ID, "URL_" + loginId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID, true);
        list.add(o);
        
        return list;
    }
    
}