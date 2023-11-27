package com.qiushangcheng.fastsqlbuilder.demo;

import com.qiushangcheng.fastsqlbuilder.config.FastSqlBuilderConfig;
import com.qiushangcheng.fastsqlbuilder.pathclass.PathClassCreator;

/**
 * @auther QiuShangcheng
 * @create 2023/8/15
 */
public class Test {
    public static void main(String[] args) {
//        SqlExpressionCreator expressionCreator = FastSqlBuilder.getSqlExpressionCreator();
//        SqlBuilder sqlBuilder = FastSqlBuilder.getSqlBuilder(Demo1.class);
//
//        ArrayList<Demo1> list = new ArrayList<>();
//        Demo1 demo1 = new Demo1(null, "name", "des", null, null, 1);
//        list.add(new Demo1(null, "name", "des", null, null, 1));
//        list.add(new Demo1(null, "name", "des", null, null, 2));
//        SqlBuildResult insert = sqlBuilder.insertField().insertObject(demo1).build(false);
//        SqlBuildResult insert1 = sqlBuilder.insertField(true, Demo1Path.id, Demo1Path.val).insertObject(demo1).build();
//        SqlBuildResult insert2 = sqlBuilder.insertField(Demo1Path.name, Demo1Path.description).insertObject(demo1).build();
//        SqlBuildResult insert3 = sqlBuilder.insertField().batchInsert(list).build();
//        SqlBuildResult insert4 = sqlBuilder.insertField(Demo1Path.name, Demo1Path.description).batchInsert(list).build();
//        SqlBuildResult insert5 = sqlBuilder.insertField(Demo1Path.name, Demo1Path.description).insertParam(demo1.getName(), demo1.getDescription()).build();
//
//        SqlBuildResult b = expressionCreator.select(Demo1Path.tableName, Demo1Path.val).where().equal(Demo1Path.id, 2355).priority().as("b").build();
//        SqlBuildResult c = expressionCreator.sum(Demo1Path.val).as("c").build();
//        String[] fields = sqlBuilder.getTableInfo().getFieldList().toArray(new String[0]);
//        SqlBuildResult select1 = sqlBuilder.select().build();
//        SqlBuildResult select2 = sqlBuilder.select(Demo1Path.name, Demo1Path.description).build();
//        SqlBuildResult select3 = sqlBuilder.select(Arrays.asList(c, b)).build();
//        SqlBuildResult select4 = sqlBuilder.select(Arrays.asList(c, b), Demo1Path.name, Demo1Path.description).build();
//        SqlBuildResult select5 = sqlBuilder.select(Arrays.asList(c, b), fields).build();
//        SqlBuildResult select6 = sqlBuilder.select(Demo1Path.name, Demo1Path.description, Demo2Path.beginTime).innerJoin(Demo2Path.tableName).on().fieldEqual(Demo1Path.name, Demo2Path.name).build();
//
//        SqlBuildResult update1 = sqlBuilder.updateField(Demo1Path.name, Demo1Path.description).updateObject(demo1).where().equal(Demo1Path.id, 1).build();
//        SqlBuildResult update2 = sqlBuilder.updateField(Demo1Path.name, Demo1Path.description).updateParam(demo1.getName(), demo1.getDescription()).where().equal(Demo1Path.id, 1).build();
//
//        SqlBuildResult delete1 = sqlBuilder.delete().where().equal(Demo1Path.id, 1).build();

    }


    public static void refresh() {
        PathClassCreator pathClassCreator = new PathClassCreator();
        PathClassCreator.Configuration pathClassConfiguration = PathClassCreator.Configuration.getInstance(FastSqlBuilderConfig.entityPackage);
        pathClassCreator.setProperties(pathClassConfiguration, true);
    }
}
