package com.cskaoyan.www.mapper;

import com.cskaoyan.www.bean.User;
import org.apache.ibatis.annotations.Param;


public interface UserMapper {
    User[] selectUser();

    void insertUser(@Param("email")String email, @Param("password")String password);

    void resetPassword(@Param("email")String email, @Param("newPassword")String newPassword);
}
