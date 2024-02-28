package com.puzzly.service;

import com.puzzly.dto.UserDTORequest;
import com.puzzly.dto.UserDTOResponse;
import com.puzzly.entity.User;
import com.puzzly.enums.Authority;
import com.puzzly.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;

    public UserDTOResponse insertUser(UserDTORequest userDTO){
        User user = modelMapper.map(userDTO, User.class);
        if(user.getCreateDateTime() == null) user.setCreateDateTime(LocalDateTime.now());
        if(user.getAuthority() == null) user.setAuthority(Authority.ROLE_USER);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        User saveEntity = userRepository.save(user);

        return modelMapper.map(saveEntity, UserDTOResponse.class);
    }

    public UserDTOResponse getUser(String userName){
        User user = userRepository.getUserByName(userName);
        return modelMapper.map(user, UserDTOResponse.class);
    }

    public List<UserDTORequest> findAll(){
        List<User> entity = userRepository.findAll();
        List<UserDTORequest> results = entity.stream()
                .map(e -> modelMapper.map(e, UserDTORequest.class))
                .collect(Collectors.toList());
        return results;
    }

    //dev
    public User obtainUser(String email, String password){
        User user = userRepository.findByEmail(email);
        if(user.getPassword().equals(password)){
            return user;
        }
        return null;
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

}
