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
	
	public void unregisterVariables(){
		variables = new ConcurrentLinkedQueue <Variable> ();
	}
	
	public boolean printToLog(){
		if (variables.size() > 0){
			LinkedList <Variable> lvariables = sort();
			log.info("###### DEBUGGER VARIABLE DUMP ######");
			for (Variable v : lvariables){
				log.info("Class name: " + v.getClassName() + " Variable name: " + v.getName() + " Variable value: " + value(v.getVariable()));
			}
			log.info("###### DEBUGGER END OF DUMP ######");
			return true;
		}else{
			return false;
		}
	}
	
	public boolean printToFile(){
		if (variables.size() > 0){
			
			//Setup file
			if (file.exists()){
				boolean deleted = file.delete();
				if (!deleted){
					return false;
				}
				try{
					boolean created = file.createNewFile();
					if (!created){
						return false;
					}
				}catch (IOException e){
					return false;
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
				return false;
			}finally{
				if (x != null){
					try{x.close();}catch(Exception e){}
				}
			}
			return true;
		}else{
			return false;
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
			String s = "State: NOTNULL ";
			@SuppressWarnings("unchecked")
			LinkedList <Object> list = (LinkedList <Object>)o;
			s = s + "Size: " + list.size();
			return s;
		}else if (o instanceof ConcurrentLinkedQueue){
			String s = "State: NOTNULL ";
			@SuppressWarnings("unchecked")
			ConcurrentLinkedQueue <Object> list = (ConcurrentLinkedQueue <Object>)o;
			s = s + "Size: " + list.size();
			return s;
		}else{
			return null;
		}
	}
	
}
