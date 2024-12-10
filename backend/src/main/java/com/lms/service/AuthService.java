package com.lms.service;

import com.lms.dto.request.LoginRequest;
import com.lms.dto.request.RegisterRequest;
import com.lms.dto.response.JwtResponse;
import com.lms.entity.Member;
import com.lms.entity.PasswordResetToken;
import com.lms.exception.BadRequestException;
import com.lms.exception.ResourceNotFoundException;
import com.lms.repository.MemberRepository;
import com.lms.repository.PasswordResetTokenRepository;
import com.lms.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final MemberRepository memberRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final EmailService emailService;

    public AuthService(MemberRepository memberRepository, PasswordResetTokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider, EmailService emailService) {
        this.memberRepository = memberRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.emailService = emailService;
    }

    public JwtResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = tokenProvider.generateToken(member.getMemberId(), member.getEmail(), member.getRole());
        log.info("User {} logged in", member.getEmail());
        return new JwtResponse(token, member.getMemberId(), member.getName(), member.getEmail(), member.getRole());
    }

    public JwtResponse register(RegisterRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        Member member = new Member();
        member.setName(request.getName());
        member.setEmail(request.getEmail());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setPhone(request.getPhone());
        member.setAddress(request.getAddress());
        member.setRole("member");
        member = memberRepository.save(member);

        String token = tokenProvider.generateToken(member.getMemberId(), member.getEmail(), member.getRole());
        log.info("User {} registered", member.getEmail());
        return new JwtResponse(token, member.getMemberId(), member.getName(), member.getEmail(), member.getRole());
    }

    public void forgotPassword(String email) {
        Optional<Member> memberOpt = memberRepository.findByEmail(email);
        if (memberOpt.isEmpty()) {
            return; // Don't reveal whether email exists
        }

        Member member = memberOpt.get();
        tokenRepository.deleteByMemberMemberId(member.getMemberId());

        String resetToken = UUID.randomUUID().toString();
        PasswordResetToken prt = new PasswordResetToken(member, resetToken, LocalDateTime.now().plusMinutes(30));
        tokenRepository.save(prt);

        emailService.sendPasswordResetEmail(member.getEmail(), resetToken);
        log.info("Password reset token sent for {}", email);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken prt = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired token"));

        if (prt.isExpired()) {
            tokenRepository.delete(prt);
            throw new BadRequestException("Token expired");
        }

        Member member = prt.getMember();
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
        tokenRepository.delete(prt);
        log.info("Password reset for {}", member.getEmail());
    }
}
