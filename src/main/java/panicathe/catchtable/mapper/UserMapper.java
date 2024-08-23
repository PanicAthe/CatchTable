package panicathe.catchtable.mapper;

import panicathe.catchtable.dto.UserDTO;
import panicathe.catchtable.model.User;

public class UserMapper {

    public static User toEntity(UserDTO userDTO) {
        return User.builder()
                .phone(userDTO.getPhone())
                .nickname(userDTO.getNickname())
                .password(userDTO.getPassword())
                .build();
    }

    public static UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .password(user.getPassword())
                .build();
    }
}
