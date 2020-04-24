package com.ceshi;

import com.ceshi.dao.UserDOMapper;
import com.ceshi.dataobject.UserDO;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = {"com.ceshi"})
@RestController
@MapperScan("com.ceshi.dao")
public class App{

    @Resource
    private UserDOMapper userDOMapper;

    @RequestMapping("/")
    public String home(){

        UserDO userDO=userDOMapper.selectByPrimaryKey(1);

        if(userDO==null){
            return "用户不存在";
        }


        return userDO.getName();
    }



    public static void main( String[] args )
    {
        SpringApplication.run(App.class,args);
        System.out.println( "Hello World!" );
    }



}
