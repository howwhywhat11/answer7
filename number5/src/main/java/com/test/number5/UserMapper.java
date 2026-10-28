package com.test.number5;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO user (username,password) VALUES (#{username},#{password})")
    int insert(User user);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername (String username);

    @Select("SELECT * FROM user LIMIT #{offset},#{size}")
    List<User> findByPage (@Param("offset")int offset,@Param("size")int size);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById (Long id);
}
