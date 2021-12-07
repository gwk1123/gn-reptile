package com.gn.reptile.dome.manage.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gn.reptile.dome.manage.entity.Quarantine;
import com.gn.reptile.dome.manage.service.QuarantineManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author gwk
 * @since 2021-12-07
 */
@Api(tags = {""})
@RestController
@RequestMapping("/quarantine")
public class QuarantineManageController {

    private final QuarantineManageService uarantineService;

    public QuarantineManageController(QuarantineManageService uarantineService){this.uarantineService = uarantineService;}

    @ApiOperation(value = "新增")
    @PostMapping("/quarantine")
    public boolean saveQuarantine(@RequestBody Quarantine quarantine){
    return uarantineService.saveQuarantine(quarantine);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/quarantine/{id}")
    public boolean removeQuarantine(@PathVariable("id") String id){
    return uarantineService.removeQuarantine(id);
    }

    @ApiOperation(value = "批量删除")
    @DeleteMapping("/quarantines")
    public boolean removeQuarantineByIds(@RequestBody List <String> ids){
        return uarantineService.removeQuarantineByIds(ids);
        }


        @ApiOperation(value = "更新")
        @PutMapping("/quarantine")
        public boolean updateQuarantine(@RequestBody Quarantine quarantine){
        return uarantineService.updateQuarantine(quarantine);
        }

        @ApiOperation(value = "查询分页数据")
        @ApiImplicitParams({
        @ApiImplicitParam(name = "page", value = "分页参数"),
        @ApiImplicitParam(name = "quarantine", value = "查询条件")
        })
        @GetMapping("/quarantine/page")
        public IPage<Quarantine> pageQuarantine(Page<Quarantine> page,Quarantine quarantine){
        return uarantineService.pageQuarantine(page, quarantine);
        }

        @ApiOperation(value = "id查询")
        @GetMapping("/quarantine/{id}")
        public Quarantine getQuarantineById(@PathVariable String id){
        return uarantineService.getQuarantineById(id);
        }

        }
