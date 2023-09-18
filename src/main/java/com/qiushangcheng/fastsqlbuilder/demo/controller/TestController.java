package com.qiushangcheng.fastsqlbuilder.demo.controller;

import com.qiushangcheng.fastsqlbuilder.demo.repository.DemoRepository;
import com.qiushangcheng.fastsqlbuilder.demo.repository.entity.Demo1;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @auther QiuShangcheng
 * @create 2023/9/14
 */
@RestController
@RequestMapping("/fastSqlBuilder")
public class TestController {
    @Resource
    private DemoRepository demoRepository;

    @GetMapping("/test")
    public String test() {
        Demo1 demo1 = new Demo1(null, "name", "des", new Date(), new Date(), 1);
        demoRepository.demo(demo1);
        return "200";
    }
}
