package com.hackhu.seckill.dao;

import com.hackhu.seckill.dto.UserDTO;
import java.util.List;

public interface UserDTOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info
     *
     * @mbggenerated Mon Feb 24 16:12:50 CST 2020
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info
     *
     * @mbggenerated Mon Feb 24 16:12:50 CST 2020
     */
    int insert(UserDTO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info
     *
     * @mbggenerated Mon Feb 24 16:12:50 CST 2020
     */
    UserDTO selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info
     *
     * @mbggenerated Mon Feb 24 16:12:50 CST 2020
     */
    List<UserDTO> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info
     *
     * @mbggenerated Mon Feb 24 16:12:50 CST 2020
     */
    int updateByPrimaryKey(UserDTO record);
}