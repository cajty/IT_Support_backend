package org.ably.it_support.user;



import org.ably.it_support.auth.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {


@Mapping(target = "email", expression = "java(registerRequest.getEmail().toLowerCase())")
AppUser toEntity(RegisterRequest registerRequest);






}