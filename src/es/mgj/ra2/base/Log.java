package es.mgj.ra2.base;

import java.util.ArrayList;

import com.db4o.ObjectSet;

import es.mgj.util.Util;

public class Log {
	
	private ArrayList<String> entradasLog;
	
	public void addEntrada(String entrada){
		
		if(entradasLog == null){
			
			entradasLog = new ArrayList<String>();
		}
			
		entradasLog.add(entrada);
		
		Util.db.store(this);
		Util.db.commit();
		
	}
	
	public ArrayList<String> getLog() {
		
		if(entradasLog == null)
			entradasLog = new ArrayList<String>();
		
		return this.entradasLog;
	}
	
	public static Log getLogObject(){
		
		return Util.db.query(Log.class).get(0);
		
	}
}
