package com.javayh.advanced.mybatis.mapper;

import com.javayh.advanced.mybatis.vo.LogisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mybatis
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-04
 */
@Mapper
public interface TestMapper {

    /**
     * <p>
     * 查询所有数据并且进行json处理
     * </p>
     *
     * @param
     * @return java.util.List<com.javayh.advanced.mybatis.vo.LogisticsVO>
     * @version 1.0.0
     * @author hai ji
     * @since 2020/8/4
     */
    List<LogisticsVO> findAll();

    List<LogisticsVO> findListAndIf(@Param(value = "vos") List<LogisticsVO> vos);


    /**
     * 新增
     */
    void insert(String name);
}
