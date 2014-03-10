package es.mgj.servidor.items;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import es.mgj.util.Constantes;

public class Cliente extends Thread{
	
	private String nick;
	private Socket socket;
	private PrintWriter salida;
	private BufferedReader entrada;
	private Servidor servidor;
	private boolean conectado;
	
	public String getNick(){
		return this.nick;
	}
	
	public Cliente(Socket socket, Servidor servidor) throws IOException {
		
		this.conectado = true;
		this.socket = socket;
		this.servidor = servidor;
		
		salida = new PrintWriter(socket.getOutputStream(), true);
		entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
	}
	
	public PrintWriter getSalida(){
		return salida;
	}
	
	private void desconectar(){	
		if(this.socket == null)
			return;
		
		servidor.enviaratodos("/s/desconectado" + this.nick + "/n/nick" + " está desconectado");
		
		this.servidor.elminarCliente(this, this.socket.getLocalSocketAddress().toString());
		this.servidor.enviaratodos("/s/servidorNicks" + servidor.getNicks());
		
		try {
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.socket = null;
		
	}
	
	public void run() {
		
		try {
			
			this.nick = entrada.readLine();
			
			if(!this.servidor.checkIp(this.socket.getLocalSocketAddress().toString()) 
					&& Constantes.ACCESO_UNICO){
				
				salida.println("/s/servidorMessage Su Ip está siendo usada");
				
				return;
			}
			
			if(!this.servidor.checkClient(this)){
				salida.println("/s/servidorMessage Usuario ya logueado");
				return;
			}
			
			this.conectado = true;
				
			String linea = "";
			
			this.servidor.putIp(this.socket.getLocalSocketAddress().toString());
			
			this.servidor.putCliente(this);
			
			this.servidor.enviaratodos("/s/servidorNicks" + servidor.getNicks());
			
			comprobarConectados();
			
			while ((linea = entrada.readLine()) != null) {
				
				if(linea.equals("/quit")){
					
					desconectar();
					break;
					
				}else if(linea.equals("/p/pong")){
					
					this.conectado = true;
					
				}else{
					
					String[] temp = linea.split("/n/nick");

					servidor.enviarMensaje(temp[0], temp[2], temp[1]);
				}
					
			}
			
		}catch(SocketException se){
			
			desconectar();	
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}

	private void comprobarConectados() {
		
		Thread hiloComprobar = new Thread(new Runnable(){
			@Override
			public void run() {
				while(conectado){
					try {
						conectado = false;
						servidor.enviaratodos("/p/ping");
						Thread.sleep(100);
						
						if(!conectado){
							
							desconectar();
						}
						
						sleep(10000);
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		});
		
		hiloComprobar.start();
	}
	
	
}
