package com.qiushangcheng.fastsqlbuilder.demo.repository;

import com.qiushangcheng.fastsqlbuilder.core.*;
import com.qiushangcheng.fastsqlbuilder.demo.po.Demo;
import com.qiushangcheng.fastsqlbuilder.demo.repository.entity.Demo1;
import com.qiushangcheng.fastsqlbuilder.demo.repository.entity.path.Demo1Path;
import com.qiushangcheng.fastsqlbuilder.demo.repository.entity.path.Demo2Path;
import org.springframework.stereotype.Repository;

import java.util.Arrays;

/**
 * @auther QiuShangcheng
 * @create 2023/9/14
 */
@Repository
public class DemoRepository extends SqlExecutor {

    public void demo(Demo1 demo1) {
        // sql构建器，sqlBuilder对象非线程安全，勿定义为线程共享对象
        SqlBuilder sqlBuilder = FastSqlBuilder.getSqlBuilder(Demo1.class);
        // 复杂语句生成器，使用方式在查询示例中查看
        SqlExpressionCreator expressionCreator = FastSqlBuilder.getSqlExpressionCreator();

        /*插入示例
        * insertField()默认表示插入所有字段，
        * insertField(XXX, XXX, XXX)仅插入指定字段，
        * insertField(true, XXX, XXX, XXX)忽略指定的字段，插入表中剩余字段
        * */

//        // insertObject()插入
//        SqlBuildResult insertDemo1 = sqlBuilder.insertField().insertObject(demo1).build();
//        System.out.println(update(insertDemo1));
//        SqlBuildResult insertDemo2 = sqlBuilder.insertField(true, Demo1Path.id).insertObject(demo1).build();
//        System.out.println(update(insertDemo2));
//        SqlBuildResult insertDemo3 = sqlBuilder.insertField(Demo1Path.name, Demo1Path.description, Demo1Path.val).insertObject(demo1).build();
//        System.out.println(update(insertDemo3));
//        // batchInsert()批量插入
//        SqlBuildResult batchInsertDemo1 = sqlBuilder.insertField(true, Demo1Path.id).batchInsert(Arrays.asList(demo1, demo1)).build();
//        System.out.println(update(batchInsertDemo1));
//        // insertParam()插入，追加insertParam()代表批量插入
//        SqlBuildResult paramInsert = sqlBuilder.insertField(Demo1Path.name, Demo1Path.description, Demo1Path.val)
//                .insertParam(demo1.getName(), demo1.getDescription(), demo1.getVal())
//                .insertParam(demo1.getName(), demo1.getDescription(), demo1.getVal())
//                .build();
//        System.out.println(update(paramInsert));

        /*查询示例
        * select()默认查询表中所有字段，select(XXX, XXX, XXX)仅查询指定字段，
        * select(List<SqlBuildResult> sqlBuildResults)查询嵌套语句
        * select(List<SqlBuildResult> sqlBuildResults, XXX, XXX, XXX)查询嵌套语句和指定的字段
        * */

//        // 简单查询
//        SqlBuildResult select1 = sqlBuilder.select().where().equal(Demo1Path.id, 5).build();
//        System.out.println(queryForObject(select1, Demo1.class));
//        SqlBuildResult select2 = sqlBuilder.select(Demo1Path.name, Demo1Path.description).build();
//        System.out.println(query(select2, Demo1.class));
//        // 嵌套查询
//        SqlBuildResult b = expressionCreator.select(Demo1Path.tableName, Demo1Path.val).where().equal(Demo1Path.id, 1).priority().as("b").build();
//        SqlBuildResult c = expressionCreator.sum(Demo1Path.val).as("c").build();
//        SqlBuildResult select3 = sqlBuilder.select(Arrays.asList(c, b)).build();
//        System.out.println(query(select3, Demo.class));
//        SqlBuildResult select4 = sqlBuilder.select(Arrays.asList(c, b), Demo1Path.name, Demo1Path.description).build();
//        System.out.println(query(select4, Demo.class));
//        String[] fields = sqlBuilder.getTableInfo().getFieldList().toArray(new String[0]); // 获取Demo1.class对应表的所有字段
//        SqlBuildResult select5 = sqlBuilder.select(Arrays.asList(c, b), fields).build();
//        System.out.println(query(select5, Demo.class));
//        // 联表查询
//        SqlBuildResult select6 = sqlBuilder.select(Demo1Path.name, Demo1Path.description, Demo2Path.beginTime).innerJoin(Demo2Path.tableName).on().fieldEqual(Demo1Path.name, Demo2Path.name).build();
//        System.out.println(query(select6, Demo1.class));

        /*更新示例
        * updateField()默认更新所有字段，
        * updateField(XXX, XXX, XXX)仅更新指定字段
        * */

//        // updateObject()更新
//        SqlBuildResult update1 = sqlBuilder.updateField(Demo1Path.name, Demo1Path.description).updateObject(demo1).where().equal(Demo1Path.id, 1).build();
//        System.out.println(update(update1));
//        // updateParam()更新
//        SqlBuildResult update2 = sqlBuilder.updateField(Demo1Path.name, Demo1Path.description).updateParam(demo1.getName(), demo1.getDescription()).where().equal(Demo1Path.id, 2).build();
//        System.out.println(update(update2));

        /*删除示例*/

//        SqlBuildResult delete1 = sqlBuilder.delete().where().equal(Demo1Path.id, 26).build();
//        System.out.println(update(delete1));
    }
}
