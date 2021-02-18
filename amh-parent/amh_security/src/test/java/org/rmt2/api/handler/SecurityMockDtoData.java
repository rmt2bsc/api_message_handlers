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
    // protected List<AppRole> mockAppRoleData;
    // protected List<Application> mockApplicationData;
    // protected List<ApplicationAccess> mockApplicationAccessData;
    // protected List<GroupRoles> mockGroupRolesData;
    // protected List<Roles> mockRolesData;
    // protected List<UserAppRole> mockUserAppRoleData;
    // protected List<UserGroup> mockUserGroupData;
    // protected List<UserLogin> mockUserLoginData;
    // protected List<UserResource> mockUserResourceData;
    // protected List<UserResourceAccess> mockUserResourceAccessData;
    // protected List<UserResourceSubtype> mockUserResourceSubtypeData;
    // protected List<UserResourceType> mockUserResourceTypeData;
    // protected List<VwAppRoles> mockVwAppRolesData;
    // protected List<VwResource> mockVwResourceData;
    // protected List<VwResourceType> mockVwResourceTypeData;
    // protected List<VwUser> mockVwUserData;
    // protected List<VwUser> mockVwUserSingleData;
    // protected List<VwUserAppRoles> mockVwUserAppRolesData;
    // protected List<VwUserGroup> mockVwUserGroupData;
    // protected List<VwUserResourceAccess> mockVwUserResourceAccessData;

    // /**
    // * @throws java.lang.Exception
    // */
    // @Before
    // public void setUp() throws Exception {
    //
    // this.mockAppRoleData = this.createAppRoleMockData();
    // this.mockApplicationData = this.createApplicationMockData();
    // this.mockApplicationAccessData = this.createApplicationAccessMockData();
    // this.mockGroupRolesData = this.createGroupRolesMockData();
    // this.mockRolesData = this.createRolesMockData();
    // this.mockUserAppRoleData = this.createUserAppRoleMockData();
    // this.mockUserGroupData = this.createUserGroupMockData();
    // this.mockUserLoginData = this.createUserLoginMockData();
    // this.mockUserResourceData = this.createUserResourceMockData();
    // this.mockUserResourceAccessData =
    // this.createUserResourceAccessMockData();
    // this.mockUserResourceSubtypeData =
    // this.createUserResourceSubtypeMockData();
    // this.mockUserResourceTypeData = this.createUserResourceTypeMockData();
    // this.mockVwAppRolesData = this.createVwAppRolesMockData();
    // this.mockVwResourceData = this.createVwResourceMockData();
    // this.mockVwResourceTypeData = this.createVwResourceTypeMockData();
    // this.mockVwUserData = this.createVwUserMockData();
    // this.mockVwUserSingleData = this.createVwUserSingleMockData();
    // this.mockVwUserAppRolesData = this.createVwUserAppRolesMockData();
    // this.mockVwUserGroupData = this.createVwUserGroupMockData();
    // this.mockVwUserResourceAccessData =
    // this.createVwUserResourceAccessMockData();
    // return;
    // }

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
    
    public static final List<Roles> createRolesMockData() {
        List<Roles> list = new ArrayList<>();
        int roleId = SecurityMockOrmDataFactory.TEST_ROLE_ID;
        Roles o = SecurityMockOrmDataFactory.createOrmRoles(roleId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmRoles(++roleId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmRoles(++roleId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmRoles(++roleId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmRoles(++roleId);
        list.add(o);
        
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
    
    
    public static final List<UserGroup> createUserGroupMockData() {
        List<UserGroup> list = new ArrayList<>();
        int groupId = SecurityMockOrmDataFactory.TEST_GROUP_ID;
        UserGroup o = SecurityMockOrmDataFactory.createOrmUserGroup(groupId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserGroup(++groupId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserGroup(++groupId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserGroup(++groupId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserGroup(++groupId);
        list.add(o);
        
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
    
    public static final List<UserResource> createUserResourceMockData() {
        List<UserResource> list = new ArrayList<>();
        int resourceId = SecurityMockOrmDataFactory.TEST_RESOURCE_ID;
        UserResource o = SecurityMockOrmDataFactory.createOrmUserResource(resourceId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID,
                "URL_" + resourceId, true);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserResource(++resourceId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID,
                "URL_" + resourceId, true);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserResource(++resourceId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID,
                "URL_" + resourceId, true);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserResource(++resourceId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID,
                "URL_" + resourceId, true);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserResource(++resourceId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID,
                SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID,
                "URL_" + resourceId, true);
        list.add(o);
        
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
    
    public static final List<UserResourceSubtype> createUserResourceSubtypeMockData() {
        List<UserResourceSubtype> list = new ArrayList<>();
        int userResourceSubtypeId = SecurityMockOrmDataFactory.TEST_RESOURCE_SUBTYPE_ID;
        UserResourceSubtype o = SecurityMockOrmDataFactory.createOrmUserResourceSubtype(userResourceSubtypeId,
                        SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserResourceSubtype(++userResourceSubtypeId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserResourceSubtype(++userResourceSubtypeId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserResourceSubtype(++userResourceSubtypeId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserResourceSubtype(++userResourceSubtypeId,
                SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID);
        list.add(o);
        
        return list;
    }
    
    public static final List<UserResourceType> createUserResourceTypeMockData() {
        List<UserResourceType> list = new ArrayList<>();
        int resourceTypeId = SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID;
        UserResourceType o = SecurityMockOrmDataFactory.createOrmUserResourceType(resourceTypeId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserResourceType(++resourceTypeId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserResourceType(++resourceTypeId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserResourceType(++resourceTypeId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmUserResourceType(++resourceTypeId);
        list.add(o);
        
        return list;
    }
    
    public static final List<VwAppRoles> createVwAppRolesMockData() {
        List<VwAppRoles> list = new ArrayList<>();
        int appRoleId = SecurityMockOrmDataFactory.TEST_APP_ROLE_ID;
        int roleId = SecurityMockOrmDataFactory.TEST_ROLE_ID;
        VwAppRoles o = SecurityMockOrmDataFactory.createOrmVwAppRoles(appRoleId,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID, roleId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwAppRoles(++appRoleId,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID, ++roleId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwAppRoles(++appRoleId,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID, ++roleId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwAppRoles(++appRoleId,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID, ++roleId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwAppRoles(++appRoleId,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID, ++roleId);
        list.add(o);
        
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
    
    public static final List<VwUser> createVwUserMockData() {
        List<VwUser> list = new ArrayList<>();
        int loginId = SecurityMockOrmDataFactory.TEST_USER_ID;
        VwUser o = SecurityMockOrmDataFactory.createOrmVwUser(loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "test1234" + loginId, "2018-01-01", "ShortName_" + loginId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwUser(++loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "test1234" + loginId, "2018-01-01", "ShortName_" + loginId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwUser(++loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "test1234" + loginId, "2018-01-01", "ShortName_" + loginId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwUser(++loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "test1234" + loginId, "2018-01-01", "ShortName_" + loginId);
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwUser(++loginId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "UserName_" + loginId,
                "test1234" + loginId, "2018-01-01", "ShortName_" + loginId);
        list.add(o);
        
        return list;
    }
    
    public static final List<VwUserAppRoles> createVwUserAppRolesMockData() {
        List<VwUserAppRoles> list = new ArrayList<>();
        int appRoleId = SecurityMockOrmDataFactory.TEST_APP_ROLE_ID;
        VwUserAppRoles o = SecurityMockOrmDataFactory.createOrmVwUserAppRoles(SecurityMockOrmDataFactory.TEST_USER_ID,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID,
                SecurityMockOrmDataFactory.TEST_ROLE_ID,
                appRoleId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "user_name",
                "2018-01-01");
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwUserAppRoles(SecurityMockOrmDataFactory.TEST_USER_ID,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID,
                SecurityMockOrmDataFactory.TEST_ROLE_ID,
                ++appRoleId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "user_name",
                "2018-01-01");
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwUserAppRoles(SecurityMockOrmDataFactory.TEST_USER_ID,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID,
                SecurityMockOrmDataFactory.TEST_ROLE_ID,
                ++appRoleId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "user_name",
                "2018-01-01");
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwUserAppRoles(SecurityMockOrmDataFactory.TEST_USER_ID,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID,
                SecurityMockOrmDataFactory.TEST_ROLE_ID,
                ++appRoleId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "user_name",
                "2018-01-01");
        list.add(o);
        o = SecurityMockOrmDataFactory.createOrmVwUserAppRoles(SecurityMockOrmDataFactory.TEST_USER_ID,
                SecurityMockOrmDataFactory.TEST_NEW_APP_ID,
                SecurityMockOrmDataFactory.TEST_ROLE_ID,
                ++appRoleId,
                SecurityMockOrmDataFactory.TEST_GROUP_ID, "user_name",
                "2018-01-01");
        list.add(o);
        
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