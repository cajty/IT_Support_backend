package org.ably.it_support.ticket;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TicketMapper {

@Mapping(target = "categoryName", source = "category.name")
@Mapping(target = "creatorName", source = "createdBy.name")
@Mapping(target = "status", source = "status")
TicketResponse toResponse(Ticket ticket);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "category.id", source = "categoryId")
    Ticket toEntity(TicketRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "category.id", source = "categoryId")
    void updateEntityFromRequest(TicketRequest request, @MappingTarget Ticket ticket);
}