package com.puzzly.api.entity;

//import com.puzzly.api.dto.request.UserExRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name="tb_users_ex")
public class UserEx {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long userExId;
    /* TODO 1:1  FK를 PK로 사용하는 방법을 알아내서 적용해야한다.*/
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column boolean firstTermAgreement;
    @Column boolean secondTermAgreement;
    @Column private String statusMessage;
    @Column private String profileFilePath;

    /*
    public UserEx(UserExRequestDto exDto){
        this.firstTermAgreement = exDto.isFirstTermAgreement();
        this.secondTermAgreement = exDto.isSecondTermAgreement();
        this.statusMessage = exDto.getStatusMessage();
        this.profileFilePath = exDto.getProfileFilePath();
    }

     */
}
