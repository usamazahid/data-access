package org.irs.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.irs.QueryStore.Users;
import org.irs.database.Datasources; 
import org.irs.dto.UserRequestDTO;
import org.irs.dto.UserResponseDTO;
import org.irs.dto.UserRolesDTO;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class UserDetailService {
     @Inject 
    Users user;

    @Inject
    Datasources datasource;
    
    public UserResponseDTO getUserData(UserRequestDTO request){
        System.out.println("request :" + request.toString());
        UserResponseDTO response = new UserResponseDTO();
        String query = user.getUserDetails(request.userId);
        try(Connection con = datasource.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query)){
            if(rs.next()){
                response.setId(rs.getString("id"));
                response.setPassword(rs.getString("password_hash"));
                response.setUsername(rs.getString("username"));
                response.setEmail(rs.getString("email"));
                response.setFullName(rs.getString("full_name"));
                response.setMobileNumber(rs.getString("mobile_number"));
            }
            System.out.println("response :" + response.toString());
        }
        catch(Exception ex){

        }


        return response;
    }

     public List<UserRolesDTO> getUserRoles(UserRequestDTO request) {
        List<UserRolesDTO> response = new ArrayList<UserRolesDTO>();

        String query = user.getUserRoles(request.userId);
        try (Connection con = datasource.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                response.add(new UserRolesDTO(
                rs.getString("role_name"), 
                rs.getString("permission_name")));
            }
        } catch (Exception ex) {

        }

        return response;
    }
}
