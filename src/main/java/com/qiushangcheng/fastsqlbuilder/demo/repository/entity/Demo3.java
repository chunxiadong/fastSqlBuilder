package com.qiushangcheng.fastsqlbuilder.demo.repository.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @auther QiuShangcheng
 * @create 2023/8/15
 */

@Data
@Entity
@Table(name = "demo3")
public class Demo3 {
    @Id
    private Integer id;
    private String name;
    private String description;
    private Date beginTime;
    private Date endTime;
    private Integer val;
}
