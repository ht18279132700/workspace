package cn.com.pcauto.wenda.util;

import java.util.Date;

import org.gelivable.dao.Mid;

public class DateMid extends Mid {
	
    private Object[] ids;

    public DateMid(Object ...ids) {
    	super(ids);
        this.ids = ids;
    }
	
	@Override
	public String toString() {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < ids.length; i++) {
        	Object object = ids[i];
        	if(object instanceof Date){
        		object = ((Date)object).getTime();
        	}
            buf.append('-').append(object);
        }
        if(buf.length() > 0){
        	buf.deleteCharAt(0);
        }
        return buf.toString();
	}
	
}
