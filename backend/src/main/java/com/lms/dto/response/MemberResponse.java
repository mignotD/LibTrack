package com.lms.dto.response;

import com.lms.entity.Member;

import java.time.LocalDate;

public class MemberResponse {
    private Integer memberId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDate joinDate;
    private String status;
    private String role;
    private long activeLoans;

    public static MemberResponse from(Member m) {
        MemberResponse r = new MemberResponse();
        r.memberId = m.getMemberId();
        r.name = m.getName();
        r.email = m.getEmail();
        r.phone = m.getPhone();
        r.address = m.getAddress();
        r.joinDate = m.getJoinDate();
        r.status = m.getStatus();
        r.role = m.getRole();
        return r;
    }

    public static MemberResponse from(Member m, long activeLoans) {
        MemberResponse r = from(m);
        r.activeLoans = activeLoans;
        return r;
    }

    public Integer getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public LocalDate getJoinDate() { return joinDate; }
    public String getStatus() { return status; }
    public String getRole() { return role; }
    public long getActiveLoans() { return activeLoans; }
}
