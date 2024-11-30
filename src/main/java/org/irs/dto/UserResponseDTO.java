package org.irs.dto;

public class UserResponseDTO {
    public String id;
    public String password;
    public String username;
    public String email;
    public String fullName;
    public String mobileNumber;
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getMobileNumber() {
        return mobileNumber;
    }
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    @Override
    public String toString() {
        return "UserResponseDTO [id=" + id + ", password=" + password + ", username=" + username + ", email=" + email
                + ", fullName=" + fullName + ", mobileNumber=" + mobileNumber + "]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
