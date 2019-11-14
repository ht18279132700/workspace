package cn.com.pcauto.wenda.util.excel;

import java.util.Date;
import java.util.Set;

/**
 * 表示“抽象对象”.
 *
 * @author chensy
 */
public interface AbsObj {

    public Object get(String name);

    public Number getNumber(String name);
    
    public Date getDate(String name);

    public String getString(String name);

    public Boolean getBoolean(String name);

    public AbsObj getAbsObj(String name);

    public <T> T getAs(String name, Class<T> clazz);

    public void set(String name, Object value);

    public Object delete(String name);

    public boolean exists(String name);

    public Set<String> nameSet();
}
