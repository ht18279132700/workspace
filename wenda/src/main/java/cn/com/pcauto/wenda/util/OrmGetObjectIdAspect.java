package cn.com.pcauto.wenda.util;

import java.lang.reflect.Field;

import org.aspectj.lang.ProceedingJoinPoint;
import org.gelivable.dao.Mid;

public class OrmGetObjectIdAspect {

	public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
		Object returnValue = joinPoint.proceed();
		if(returnValue == null || !(returnValue instanceof Mid)){
			return returnValue;
		}
		Mid mid = (Mid)returnValue;
		Field f = mid.getClass().getDeclaredField("ids");
		f.setAccessible(true);
		Object[] ids = (Object[])f.get(mid);
		returnValue = new DateMid(ids);
		return returnValue;
	}
	
}
