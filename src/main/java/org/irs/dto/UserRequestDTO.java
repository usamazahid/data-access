package org.irs.dto;

public class UserRequestDTO {
    public String userId;
    public String password;
    public String username;
    public String email;
    public String fullName;
    public String mobileNumber;
    
    @Override
    public String toString() {
        return "UserRequestDTO [userId=" + userId + ", password=" + password + ", username=" + username + ", email="
                + email + ", fullName=" + fullName + ", mobileNumber=" + mobileNumber + "]";
    }
    
}
