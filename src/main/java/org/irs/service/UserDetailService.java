package org.irs.service;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.irs.QueryStore.Users;
import org.irs.database.Datasources;
import org.irs.dto.UserRequestDTO;
import org.irs.dto.UserResponseDTO;
import org.irs.dto.UserRolesDTO;
import org.irs.util.PasswordHashUtil;



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
                // response.setPassword(rs.getString("password_hash"));
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

    public void createUser(UserRequestDTO userDto) throws SQLException {
        Connection connection = null;
        try {
            String roleName="citizen";
            connection = datasource.getConnection();
            connection.setAutoCommit(false);
            if (mobileExists(connection,userDto.mobileNumber)) {
                throw new IllegalArgumentException("Mobile number already registered");
            }
    
             String passwordHash = PasswordHashUtil.hashPassword(userDto.password); // Hash the password
            // Insert user and get the generated user ID
            Long userId = insertUser(connection, userDto.mobileNumber, userDto.username, passwordHash, userDto.email);

            // Find role ID by role name
            Long roleId = findRoleIdByName(connection, roleName);
            if (roleId == null) {
                throw new SQLException("Role not found: " + roleName);
            }

            // Insert into user_roles table
            assignRoleToUser(connection, userId, roleId);

            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }


    private Long insertUser(Connection connection, String mobileNumber, String username, String passwordHash, String email) throws SQLException {
        String insertUserSQL = user.insertUser(mobileNumber, username, passwordHash, email);
        try (PreparedStatement pstmt = connection.prepareStatement(insertUserSQL)) {
            pstmt.setString(1, mobileNumber);
            pstmt.setString(2, username);
            pstmt.setString(3, passwordHash);
            pstmt.setString(4, email);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("id");
            } else {
                throw new SQLException("Failed to insert user.");
            }
        }
    }

    private Long findRoleIdByName(Connection connection, String roleName) throws SQLException {
        String selectRoleSQL = user.findRoleIdByName(roleName);
        try (PreparedStatement pstmt = connection.prepareStatement(selectRoleSQL)) {
            pstmt.setString(1, roleName);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("id");
            }
        }
        return null;
    }

    private void assignRoleToUser(Connection connection, Long userId, Long roleId) throws SQLException {
        String insertUserRoleSQL = user.assignRoleToUser(userId, roleId);
        try (PreparedStatement pstmt = connection.prepareStatement(insertUserRoleSQL)) {
            pstmt.setLong(1, userId);
            pstmt.setLong(2, roleId);
            pstmt.executeUpdate();
        }
    }

    private boolean mobileExists(Connection connection, String mobileNumber) throws SQLException {
        String sql = user.mobileExists(mobileNumber);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, mobileNumber);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count")>0;
            }
        }
        return false;
    }



}
