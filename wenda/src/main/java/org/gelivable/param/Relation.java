package org.gelivable.param;

public enum Relation {
	EQ(" = ? "),                       //等于
	LT(" < ? "),                       //小于
	GT(" > ? "),                       //大于
	NEQ(" <> ? "),                     //不等于
	LEQ(" <= ? "),                     //小于等于
	GEQ(" >= ? "),                     //大于等于
	LIKE(" LIKE ? "),                  //like
	IS_NULL(" IS NULL "),              //is null
	IS_NOT_NULL(" IS NOT NULL ");      //is not null
	
	private String value;
	
	private Relation(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
}
