package com.puzzly.dto;

import com.puzzly.entity.User;
import com.puzzly.enums.JoinType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserDTOResponse extends User {
}
