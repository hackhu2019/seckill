package com.hackhu.seckill.dao;

import com.hackhu.seckill.dto.ItemDTO;
import java.util.List;

public interface ItemMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbggenerated Wed Feb 26 21:42:09 CST 2020
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbggenerated Wed Feb 26 21:42:09 CST 2020
     */
    int insert(ItemDTO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbggenerated Wed Feb 26 21:42:09 CST 2020
     */
    ItemDTO selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbggenerated Wed Feb 26 21:42:09 CST 2020
     */
    List<ItemDTO> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbggenerated Wed Feb 26 21:42:09 CST 2020
     */
    int updateByPrimaryKey(ItemDTO record);
}