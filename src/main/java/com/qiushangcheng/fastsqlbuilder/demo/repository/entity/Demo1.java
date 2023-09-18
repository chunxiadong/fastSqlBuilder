package com.qiushangcheng.fastsqlbuilder.demo.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Table(name = "demo1")
@AllArgsConstructor
@NoArgsConstructor
public class Demo1 {
    @Id
    private Integer id;
    private String name;
    private String description;
    private Date beginTime;
    private Date endTime;
    private Integer val;
}
