package cn.com.pcauto.wenda.util;

public enum TagType {

	BRAND("B",1),
	SERIAL("S",2),
	LEVEL1("1",3),
	LEVEL2("2",4)
	;
	
	private String name;
	private int priority;
	
	private TagType(String name, int priority){
		this.name = name;
		this.priority = priority;
	}
	
	public String getName(){
		return name;
	}
	
	public int getPriority(){
		return priority;
	}
}
