package com.puzzly.api.service;

import com.puzzly.api.dto.request.UserExRequestDto;
import com.puzzly.api.entity.User;
import com.puzzly.api.entity.UserEx;
import com.puzzly.api.enums.AccountAuthority;
import com.puzzly.api.repository.UserRepository;
import com.puzzly.api.dto.request.UserRequestDto;
import com.puzzly.api.dto.response.UserDTOResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    // NOTE 생성자주입 ( final <- ReqruiedArgsConstructor , bean 한개일경우 @Autowired 생략 가능)
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public UserDTOResponse insertUser(UserRequestDto userDTO){

        if(userDTO.getCreateDateTime() == null) userDTO.setCreateDateTime(LocalDateTime.now());
        if(userDTO.getAccountAuthority() == null) userDTO.setAccountAuthority(AccountAuthority.ROLE_USER);
        // TODO FE와 별도로 상의하여 통신구간 암호화를 구현하고, 복호화 > 암호화 혹은 그대로 때려박기 등을 구현해야 한다.
        userDTO.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));

        UserExRequestDto tmpEx = userDTO.getUserExRequestDto();
        userDTO.setUserExRequestDto(null);
        User user = modelMapper.map(userDTO, User.class);
        User saveEntity = userRepository.save(user);

        log.error(saveEntity.toString());
        return modelMapper.map(saveEntity, UserDTOResponse.class);
    }

    public UserDTOResponse getUser(String username){
        User user = userRepository.getUserByUserName(username);
        return modelMapper.map(user, UserDTOResponse.class);
    }

    public List<UserRequestDto> findAll(){
        List<User> entity = userRepository.findAll();
        List<UserRequestDto> results = entity.stream()
                .map(e -> modelMapper.map(e, UserRequestDto.class))
                .collect(Collectors.toList());
        return results;
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

}
