package com.puzzly.api.dto.response;

import com.puzzly.api.entity.User;
import com.puzzly.api.entity.UserEx;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTOResponse extends User {
    private UserEx userEx;
}
