package es.mgj.servidor.items;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import com.db4o.ObjectSet;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.ClientConfiguration;

import es.mgj.ra2.base.Log;
import es.mgj.util.Constantes;
import es.mgj.util.Util;



public class Servidor {
	
	private int puerto;
	private ServerSocket socket;
	private ArrayList<String> ipClientes;
	private ArrayList<Cliente> listaClientes;
	
	public Servidor(int puerto){
		
		listaClientes = new ArrayList<Cliente>();
		ipClientes = new ArrayList<String>();
		this.puerto = puerto;
	}
	
	public void startDb4o(){
		
		ClientConfiguration configuration = Db4oClientServer.newClientConfiguration();
		configuration.common().updateDepth(2);
		
		Util.db = Db4oClientServer.openClient(configuration, Constantes.HOST, Constantes.PUERTODB4O,
			Constantes.USUARIO, Constantes.CONTRASENA);
	}
	
	public void putIp(String ip){
		this.ipClientes.add(ip);
	}
	
	public boolean checkIp(String ip){
		
		if(this.ipClientes.contains(ip))
			return false;
		
		return true;
	}
	
	public void conectar() throws IOException {
		socket = new ServerSocket(this.puerto);
	}
	
	public void desconectar() throws IOException {
		socket.close();
	}
	
	public boolean isConected() {
		return !socket.isClosed();
	}
	
	public Socket escuchar() throws IOException {
		return socket.accept();
	}
	
	public void putCliente(Cliente cliente){
		
		this.listaClientes.add(cliente);
		
	}
	
	public boolean checkClient(Cliente cliente){
		
		for(Cliente cliente2 : this.listaClientes){
			
			if(cliente2.getNick().equals(cliente.getNick()))
				return false;
		}
		
		return true;
	}
	
	public String getNicks(){
		
		String nicks = "";
		
		for(Cliente cliente : this.listaClientes){
			nicks += cliente.getNick() + "/";
		}
		
		return nicks.substring(0 , nicks.length());
	}
	
	public void enviaratodos(String mensaje){
		
		for(Cliente c : listaClientes){
			c.getSalida().println(mensaje);
		}
	}
	
	public void elminarCliente(Cliente cliente, String ip){
		
		startDb4o();
		
		Log.getLogObject().addEntrada( new GregorianCalendar().getTime()  
				+ "/" + ip
				+ "/" + cliente.getNick()
				+ "/" +"Log out");
		
		System.out.println(cliente.getNick() + " eliminado, log guardado");
		
		this.ipClientes.remove(ip);
		this.listaClientes.remove(cliente);
		
	}

	public void enviarMensaje(String emisor, String receptor, String mensaje) {
		
		for(Cliente c : listaClientes){
			if(c.getNick().equals(receptor)){
				
				c.getSalida().println(emisor + "/n/nick" + mensaje);
				
			}
	
		}
		
	}
	
	

}
