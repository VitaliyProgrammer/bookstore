package com.example.basicbookstoreprojectnew.mapper;

import com.example.basicbookstoreprojectnew.dto.UserLoginResponseDto;
import com.example.basicbookstoreprojectnew.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthenticationMapper {

    @Mapping(target = "token", source = "token")
    UserLoginResponseDto loginResponse(User user, String token);
}
