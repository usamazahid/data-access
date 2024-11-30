package org.irs.QueryStore;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class Users {
    public String getUserDetails(String userId){
        String query = " select * from users where mobile_number='"+userId+"'";
        System.out.println(query);
        return query;
    }

    public String getUserRoles(String userId){
        String query = "SELECT " 
                        + "r.role_name, " 
                        + "p.permission_name " 
                        + "FROM " 
                        + "users u " 
                        + "JOIN " 
                        + "user_roles ur ON u.id = ur.user_id " 
                        + "JOIN " 
                        + "roles r ON ur.role_id = r.id " 
                        + "LEFT JOIN " 
                        + "role_permissions rp ON r.id = rp.role_id " 
                        + "LEFT JOIN " 
                        + "permissions p ON rp.permission_id = p.id " 
                        + "WHERE " 
                        + "u.id = '"+userId+"' " 
                        + "UNION ALL " 
                        + "SELECT " 
                        + "NULL AS role_name, " 
                        + "p.permission_name " 
                        + "FROM " 
                        + "users u " 
                        + "JOIN " 
                        + "user_permissions up ON u.id = up.user_id " 
                        + "JOIN " 
                        + "permissions p ON up.permission_id = p.id " 
                        + "WHERE " 
                        + "u.id = '"+userId+"'"  ;

                        System.out.println(query);
                        return query;

    }
}
