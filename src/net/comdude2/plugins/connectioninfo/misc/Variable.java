package net.comdude2.plugins.connectioninfo.misc;

public class Variable {
	
	private String name = null;
	private Object variable = null;
	private String className = null;
	
	public Variable(String name, Object variable, String className){
		this.name = name;
		this.variable = variable;
		this.className = className;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Object getVariable(){
		return this.variable;
	}
	
	public String getClassName(){
		return this.className;
	}
	
}
