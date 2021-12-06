package com.gn.reptile;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


//@EnableEurekaClient
//@MapperScan({"com.gn.web.sys.mapper", "com.gn.web.manual.mapper"})
@SpringBootApplication
public class GnReptileApplication {

    public static void main(String[] args) {
        SpringApplication.run(GnReptileApplication.class, args);
    }

}
