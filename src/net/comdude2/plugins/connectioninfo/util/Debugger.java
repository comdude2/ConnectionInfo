package net.comdude2.plugins.connectioninfo.util;

import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.comdude2.plugins.connectioninfo.misc.Variable;

public class Debugger {
	
	private Log log = null;
	private ConcurrentLinkedQueue <Variable> variables = new ConcurrentLinkedQueue <Variable> ();
	private File file = null;
	
	//TODO Change return type from boolean to a new class called DebuggerResponse
	
	public Debugger(Log log, File file){
		this.log = log;
		this.file = file;
	}
	
	public void registerVariable(Variable v){
		variables.add(v);
	}
	
	public int updateVariable(Variable v){
		Variable var = this.findVariable(v);
		if (var != null){
			var.setVariable(v.getVariable());
			return DebuggerResponse.UPDATED;
		}
		return DebuggerResponse.FAILED_TO_FIND;
	}
	
	public void unregisterVariables(){
		variables = new ConcurrentLinkedQueue <Variable> ();
	}
	
	public int printToLog(){
		if (variables.size() > 0){
			LinkedList <Variable> lvariables = sort();
			log.info("###### DEBUGGER VARIABLE DUMP ######");
			for (Variable v : lvariables){
				log.info("Class name: " + v.getClassName() + " Variable name: " + v.getName() + " Variable value: " + value(v.getVariable()));
			}
			log.info("###### DEBUGGER END OF DUMP ######");
			return DebuggerResponse.COMPLETE;
		}else{
			return DebuggerResponse.NO_VARIABLES;
		}
	}
	
	public int printToFile(){
		if (variables.size() > 0){
			
			//Setup file
			if (file.exists()){
				boolean deleted = file.delete();
				if (!deleted){
					return DebuggerResponse.FILE_NOT_DELETED;
				}
				try{
					boolean created = file.createNewFile();
					if (!created){
						return DebuggerResponse.FILE_NOT_CREATED;
					}
				}catch (IOException e){
					return DebuggerResponse.EXCEPTION;
				}
			}
			
			Formatter x = null;
			
			try{
				x = new Formatter(file);
				LinkedList <Variable> lvariables = sort();
				x.format("%s", "###### DEBUGGER VARIABLE DUMP ######");
				for (Variable v : lvariables){
					x.format("%s", "Class name: " + v.getClassName() + " Variable name: " + v.getName() + " Variable value: " + value(v.getVariable()));
				}
				x.format("%s", "###### DEBUGGER END OF DUMP ######");
			}catch (Exception e){
				e.printStackTrace();
				if (x != null){
					try{x.close();}catch(Exception e1){}
				}
				return DebuggerResponse.EXCEPTION;
			}finally{
				if (x != null){
					try{x.close();}catch(Exception e){}
				}
			}
			return DebuggerResponse.COMPLETE;
		}else{
			return DebuggerResponse.NO_VARIABLES;
		}
	}
	
	private LinkedList <Variable> sort(){
		LinkedList <String> classes = new LinkedList <String> ();
		for (Variable v : variables){
			if (!classes.contains(v.getClassName())){
				classes.add(v.getClassName());
			}
		}
		LinkedList <Variable> lvariables = new LinkedList <Variable> ();
		for (String className : classes){
			for (Variable v : variables){
				if (v.getClassName().equals(className)){
					lvariables.add(v);
				}
			}
		}
		if (lvariables.size() > 0){
			return lvariables;
		}else{
			return null;
		}
	}
	
	private String value(Object o){
		if (o instanceof String){
			return (String)o;
		}else if (o instanceof Integer){
			return String.valueOf((Integer)o);
		}else if (o instanceof Boolean){
			return String.valueOf((Boolean)o);
		}else if (o instanceof Long){
			return String.valueOf((Long)o);
		}else if (o instanceof File){
			return String.valueOf((File)o);
		}else if (o instanceof UUID){
			return String.valueOf((UUID)o);
		}else if (o instanceof LinkedList){
			try{
				String s = "State: NOTNULL ";
				@SuppressWarnings("unchecked")
				LinkedList <Object> list = (LinkedList <Object>)o;
				s = s + "Size: " + list.size();
				return s;
			}catch (Exception e){return null;}
		}else if (o instanceof ConcurrentLinkedQueue){
			try{
				String s = "State: NOTNULL ";
				@SuppressWarnings("unchecked")
				ConcurrentLinkedQueue <Object> list = (ConcurrentLinkedQueue <Object>)o;
				s = s + "Size: " + list.size();
				return s;
			}catch (Exception e){return null;}
		}else{
			return null;
		}
	}
	
	public Variable findVariable(Variable f){
		for (Variable v : variables){
			if (v.getName().equals(f.getName())){
				if (v.getClassName().equals(f.getClassName())){
					return v;
				}
			}
		}
		return null;
	}
	
}
