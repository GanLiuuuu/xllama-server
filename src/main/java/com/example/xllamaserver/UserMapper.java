package com.example.xllamaserver;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user")
    List<User> getAllUser();

    @Insert("INSERT INTO user(username, password, bio) VALUES(#{username}, #{password} , #{bio})")
    void insertUser(User user);
}