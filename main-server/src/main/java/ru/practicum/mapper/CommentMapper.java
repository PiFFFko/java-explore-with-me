package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.dto.CommentDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "event", source = "comment.event.title")
    @Mapping(target = "creator", source = "comment.creator.name")
    CommentDto commentToCommentDto(Comment comment);

    @Mapping(target = "event", source = "comment.event.title")
    @Mapping(target = "creator", source = "comment.creator.name")
    List<CommentDto> listCommentsToListCommentDto(List<Comment> commentList);

}
