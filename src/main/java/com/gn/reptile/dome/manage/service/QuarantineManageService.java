package com.gn.reptile.dome.manage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gn.reptile.dome.manage.entity.Quarantine;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gwk
 * @since 2021-12-07
 */
public interface QuarantineManageService extends IService<Quarantine> {

    /**
     * 查询分页数据
     *
     * @param page      分页参数
     * @param quarantine 查询条件
     * @return IPage<Quarantine>
     */
     IPage<Quarantine> pageQuarantine(Page<Quarantine> page, Quarantine quarantine);

    /**
     * 新增
     *
     * @param quarantine 
     * @return boolean
     */
    boolean saveQuarantine(Quarantine quarantine);

    /**
     * 删除
     *
     * @param id 主键
     * @return boolean
     */
    boolean removeQuarantine(String id);

    /**
     * 批量删除
     *
     * @param ids 主键集合
     * @return boolean
     */
    boolean removeQuarantineByIds(List<String> ids);

    /**
     * 修改
     *
     * @param quarantine 
     * @return boolean
     */
    boolean updateQuarantine(Quarantine quarantine);

    /**
     * id查询数据
     *
     * @param id id
     * @return Quarantine
     */
    Quarantine getQuarantineById(String id);
}
