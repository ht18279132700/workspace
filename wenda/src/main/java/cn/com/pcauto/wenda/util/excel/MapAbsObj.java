package cn.com.pcauto.wenda.util.excel;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * AbsObj的Map实现. 可以将其直接转成{@code Map<String, Object>}
 *
 * @author chensy
 */
public class MapAbsObj implements AbsObj, Map<String, Object> {

    final static private NumberFormat lessFractalNumberFormat;

    static {
        lessFractalNumberFormat = NumberFormat.getNumberInstance();
        lessFractalNumberFormat.setGroupingUsed(false);
        lessFractalNumberFormat.setMinimumFractionDigits(0);
    }

    private MapAbsObj(Map<String, Object> map) {
        this.map = map;
    }
    private Map<String, Object> map;

    static public MapAbsObj fromMap(Map<String, Object> map) {
        return new MapAbsObj(map);
    }

    static public MapAbsObj newInstance() {
        return fromMap(new HashMap<String, Object>());
    }

    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        map.putAll(m);
    }

    @Override
    public Object put(String key, Object value) {
        return map.put(key, value);
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) {
        return map.equals(o);
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Object get(String name) {
        return map.get(name);
    }

    @Override
    public Number getNumber(String name) {
        Object objectValue = map.get(name);
        if (objectValue instanceof Number) {
            return (Number) objectValue;
        }
        if (objectValue instanceof String) {
            String stringValue = (String) objectValue;
            if (stringValue == null || stringValue.length() == 0) {
                return 0;
            }
            double doubleValue = Double.parseDouble(stringValue);
            return doubleValue;
        }
        return 0;
    }

    @Override
    public String getString(String name) {
        Object objectValue = map.get(name);
        if (objectValue instanceof String) {
            return (String) objectValue;
        }
        if (objectValue instanceof Number) {
            return lessFractalNumberFormat.format(objectValue);
        }
        return objectValue != null ? objectValue.toString() : null;
    }

    @Override
    public Boolean getBoolean(String name) {
        Object objectValue = map.get(name);
        if (objectValue instanceof Boolean) {
            return (Boolean) objectValue;
        }
        if (objectValue instanceof Number) {
            Number numberValue = (Number) objectValue;
            if (numberValue.doubleValue() == 0) {
                return false;
            } else {
                return true;
            }
        }
        if (objectValue instanceof String) {
            String stringValue = (String) objectValue;
            return Boolean.parseBoolean(stringValue);
        }
        throw new RuntimeException(
                "can not convert " + objectValue + " to boolean");
    }

    @Override
    public AbsObj getAbsObj(String name) {
        return (AbsObj) map.get(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAs(String name, Class<T> clazz) {
        return (T) map.get(name);
    }

    @Override
    public void set(String name, Object value) {
        map.put(name, value);
    }

    @Override
    public Object delete(String name) {
        return map.remove(name);
    }

    @Override
    public boolean exists(String name) {
        return map.containsKey(name);
    }

    @Override
    public Set<String> nameSet() {
        return map.keySet();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    @Override
    public Date getDate(String name) {
        double d = getNumber(name).doubleValue();
        if (d > 0) {
            return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(d);
        }
        return null;
    }
}
