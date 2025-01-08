package com.lms.service;

import com.lms.dto.response.MemberResponse;
import com.lms.entity.Member;
import com.lms.exception.BadRequestException;
import com.lms.exception.ResourceNotFoundException;
import com.lms.repository.MemberRepository;
import com.lms.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {
    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private final MemberRepository memberRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileService(MemberRepository memberRepository, TransactionRepository transactionRepository,
                          PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public MemberResponse getProfile(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        long activeLoans = transactionRepository.countByMemberMemberIdAndReturnDateIsNull(memberId);
        return MemberResponse.from(member, activeLoans);
    }

    @Transactional
    public void changePassword(Integer memberId, String currentPassword, String newPassword) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
        log.info("Password changed for member {}", memberId);
    }

    @Transactional
    public void updateEmail(Integer memberId, String newEmail) {
        if (memberRepository.findByEmail(newEmail).isPresent()) {
            throw new BadRequestException("Email already in use");
        }
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        member.setEmail(newEmail);
        memberRepository.save(member);
        log.info("Email updated for member {}", memberId);
    }
}
