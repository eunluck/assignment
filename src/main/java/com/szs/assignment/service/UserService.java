package com.szs.assignment.service;


import com.szs.assignment.model.entity.UserInfo;
import com.szs.assignment.repository.CanJoinUserRepository;
import com.szs.assignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CanJoinUserRepository  canJoinUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserInfo login(String loginId, String password) {
        checkArgument(!Strings.isBlank(password), "패스워드를 입력해주세요.");

        UserInfo userInfo = findByUserId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디입니다."));

        userInfo.login(passwordEncoder, password);
        return userInfo;
    }

    @Transactional
    public UserInfo join(UserInfo userInfo){
        checkArgument(!isDuplicatedId(userInfo.getUserId()), "중복된 아이디입니다.");
        checkArgument(isPossibleUser(userInfo.getName(), userInfo.getRegNo()), "가입이 허용된 사용자가 아닙니다.");

        userInfo.encodePassword(passwordEncoder);

        return userRepository.save(userInfo);
    }


    public boolean isDuplicatedId(String userId){
        return userRepository.findByUserId(userId).isPresent();
    }

    public boolean isPossibleUser(String name, String regNo){
        return canJoinUserRepository.findByNameAndRegNo(name, regNo).isPresent();
    }

    @Transactional(readOnly = true)
    public Optional<UserInfo> findByUserId(String userId) {

        return userRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<UserInfo> findBySeq(Long seq) {

        return userRepository.findById(seq);
    }
}
