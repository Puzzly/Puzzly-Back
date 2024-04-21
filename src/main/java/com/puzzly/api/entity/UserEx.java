package com.puzzly.api.entity;

//import com.puzzly.api.dto.request.UserExRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name="tb_users_ex")
public class UserEx {
    @Id
    private Long userExId;
    /* 1:1  FK를 PK로 사용하는 방법*/
    @OneToOne(fetch=FetchType.LAZY)
    @MapsId
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
