package internship.paymentSystem.backend.DTOs;

import internship.paymentSystem.backend.models.enums.RoleEnum;

public class JwtResponse {
    private String token;
    private Long id;
    private String username;
    private String email;
    private RoleEnum role;

    public JwtResponse(String accessToken, Long id, String username, String email, RoleEnum role) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

}