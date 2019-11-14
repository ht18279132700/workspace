package org.gelivable.param;


public class OrParam {

	private StringBuilder sql = new StringBuilder();
	private Object value;
	
	public OrParam(String name, Object value){
		this(name, Relation.EQ, value);
	}
	
	public OrParam(String name, Relation relation){
		this(name, relation, null);
	}
	
	public OrParam(String name, Relation relation, Object value){
		if(Relation.IS_NULL == relation || Relation.IS_NOT_NULL == relation){
			sql.append(name).append(relation.getValue());
		}else if(value != null){
			sql.append(name).append(relation.getValue());
			this.value = value;
		}
	}
	
	public StringBuilder getSql(){
		return sql;
	}
	
	public Object getValue(){
		return value;
	}
	
}
