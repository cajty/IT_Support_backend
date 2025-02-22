package org.ably.it_support.user;



import org.ably.it_support.auth.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {

@Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
AppUser toEntity(RegisterRequest registerRequest);






}