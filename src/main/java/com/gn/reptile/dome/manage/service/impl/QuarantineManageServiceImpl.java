package com.gn.reptile.dome.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gn.reptile.dome.manage.entity.Quarantine;
import com.gn.reptile.dome.manage.mapper.QuarantineMapper;
import com.gn.reptile.dome.manage.service.QuarantineManageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gwk
 * @since 2021-12-07
 */
@Service
public class QuarantineManageServiceImpl extends ServiceImpl<QuarantineMapper, Quarantine> implements QuarantineManageService {

    @Override
    public  IPage<Quarantine> pageQuarantine(Page<Quarantine> page,Quarantine quarantine){

        page = Optional.ofNullable(page).orElse(new Page<>());
        QueryWrapper<Quarantine> queryWrapper = new QueryWrapper<>();

        return  this.page(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveQuarantine(Quarantine quarantine){
        Assert.notNull(quarantine, "为空");
        return this.save(quarantine);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeQuarantine(String id){
        Assert.hasText(id, "主键为空");
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeQuarantineByIds(List<String> ids){
        Assert.isTrue(!CollectionUtils.isEmpty(ids), "主键集合为空");
        return this.removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateQuarantine(Quarantine quarantine){
        Assert.notNull(quarantine, "为空");
        return this.updateById(quarantine);
    }

    @Override
    public Quarantine getQuarantineById(String id){
        return  this.getById(id);
    }
}
