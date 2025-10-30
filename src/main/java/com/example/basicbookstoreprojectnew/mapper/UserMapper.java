package com.example.basicbookstoreprojectnew.mapper;

import com.example.basicbookstoreprojectnew.dto.UserRegistrationRequestDto;
import com.example.basicbookstoreprojectnew.dto.UserRegistrationResponseDto;
import com.example.basicbookstoreprojectnew.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toModel(UserRegistrationRequestDto dto);

    UserRegistrationResponseDto toDto(User user);
}
