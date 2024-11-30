package org.irs.dto;

public class UserRolesDTO {
    public String role_name;
    public String permission_name;

    public UserRolesDTO(String role_name, String permission_name){
        this.role_name = role_name;
        this.permission_name = permission_name;
    }
}
