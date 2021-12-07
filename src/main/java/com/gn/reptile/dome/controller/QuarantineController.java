package com.gn.reptile.dome.controller;

import com.gn.reptile.dome.service.QuarantineService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Semaphore;

@RestController
@RequestMapping(value = "/jy")
public class QuarantineController {

    @Autowired
    private QuarantineService quarantineService;


    private final static Logger log = LoggerFactory.getLogger(QuarantineController.class);

    private Semaphore semaphore = new Semaphore(5);
    private Semaphore count = new Semaphore(1);


    @ApiOperation("开始预约")
    @RequestMapping(value = "/ksyy")
    public void search() {

        int availablePermits = semaphore.availablePermits();
        if (availablePermits > 0) {
            log.info("抢到资源");
        } else {
            log.info("资源已被占用，稍后再试");
            throw new RuntimeException("资源已被占用，稍后再试");

        }
        try {
            semaphore.acquire(1);
            if (count.availablePermits() > 0) {
                count.acquire(1);
            } else {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    if (count.availablePermits() > 0) {
                        count.acquire(1);
                        break;
                    }
                    if (i == 9) {
                        log.info("资源已被占用，稍后再试");
                        throw new RuntimeException("资源已被占用，稍后再试");
                    }
                }
            }
            quarantineService.asynProcessing();
        } catch (Exception e) {
            log.error("开始预约异常:{}",e);
            throw new RuntimeException("searchError System anomaly");
        } finally {
            count.release(1);
            semaphore.release(1);//释放一个资源
        }


    }


    @ApiOperation("初始化浏览器")
    @GetMapping("/init/chrome")
    public String initChromeDriver(@RequestParam(required = false) String dataDir, @RequestParam(defaultValue = "3") Integer num) {
        semaphore = new Semaphore(5 * num);
        count = new Semaphore(num);
        quarantineService.initChromeDriver(dataDir, num);
        return "初始化成功";
    }


}
