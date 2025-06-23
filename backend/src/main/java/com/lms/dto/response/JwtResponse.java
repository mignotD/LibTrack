package com.lms.dto.response;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Integer memberId;
    private String name;
    private String email;
    private String role;

    public JwtResponse(String token, Integer memberId, String name, String email, String role) {
        this.token = token;
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getToken() { return token; }
    public String getType() { return type; }
    public Integer getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
