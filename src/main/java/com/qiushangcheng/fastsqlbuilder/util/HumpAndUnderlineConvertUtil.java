package com.qiushangcheng.fastsqlbuilder.util;

import com.google.common.base.CaseFormat;

/**
 * @auther QiuShangcheng
 * @create 2023/8/12
 */
public class HumpAndUnderlineConvertUtil {

    /**
     * 驼峰转下划线
     *
     * @param str
     * @return
     */
    public static String humpToUnderline(String str) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str);
    }

    /**
     * 下划线转驼峰
     *
     * @param str
     * @return
     */
    public static String underlineToHump(String str) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
    }

}
