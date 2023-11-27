package com.qiushangcheng.fastsqlbuilder.demo.controller;

import com.qiushangcheng.fastsqlbuilder.demo.repository.primary.DemoRepository;
import com.qiushangcheng.fastsqlbuilder.demo.repository.entity.Demo1;
import com.qiushangcheng.fastsqlbuilder.demo.repository.secondary.Demo2Repository;
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

    @Resource
    private Demo2Repository demo2Repository;

    @GetMapping("/test")
    public String test() {
        Demo1 demo1 = new Demo1(null, "name111", "des", new Date(), new Date(), 1);
        Demo1 demo2 = new Demo1(null, "name222", "des", new Date(), new Date(), 1);
        demoRepository.demo(demo1);
        demo2Repository.demo(demo2);
        return "200";
    }
}
