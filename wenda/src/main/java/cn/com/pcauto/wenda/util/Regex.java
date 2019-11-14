package cn.com.pcauto.wenda.util;

/**
 * Regex
 *
 * @author <a href="mailto:shuyaochen@sina.com">陈树钥</a>
 * @version 1.0
 * @since 2012-1-4 12:56:05
 */
public enum Regex {

    cellphone("^(13|14|15|18|17)[0-9]{9}$", ""),//电话号码验证
    email("^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$", ""),
    trueName("([\\u4e00-\\u9fa5\\·])*", "请输入中文名,允许带有·"),
    authorName("^[a-zA-Z0-9\\_\u4e00-\u9fa5]+$", "只能含有汉字、数字、字母、下划线"),
    carNum15("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$","身份证格式，支持15位，只验证格式，不验证准确性"),
    carNum18("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}(X|x|[0-9])$","身份证格式，支持18位，只验证格式，不验证准确性");
    
    
    public final String value;
    public final String tip;

    /**
     *
     * @param regex
     */
    Regex(String regex, String tip) {
        this.value = regex;
        this.tip = tip;
    }

    public String getTip() {
        return tip;
    }
}
