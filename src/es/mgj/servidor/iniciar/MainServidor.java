package es.mgj.servidor.iniciar;

import java.io.IOException;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectServer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.ServerConfiguration;

import es.mgj.servidor.items.Cliente;
import es.mgj.servidor.items.Servidor;
import es.mgj.util.Constantes;


public class MainServidor {
	public static void main(String args[]) {

		final Servidor servidor = new Servidor(Constantes.PUERTOSOCKET);
		Cliente cliente = null;
		
		try {
			servidor.conectar();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		try {
			
			Thread bd = new Thread(new Runnable(){
				
				@Override
				public void run() {
					new MainServidor().iniciarServidor();
					
				}
				
			});
			
			bd.start();
			
			while (servidor.isConected()) {
				
				cliente = new Cliente(servidor.escuchar(), servidor);
					
				cliente.start();
			}
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void iniciarServidor() {
	
		synchronized (this) {
			
			ServerConfiguration configuration = Db4oClientServer.newServerConfiguration();
			configuration.common().updateDepth(2);
			
			final ObjectServer servidor = Db4oClientServer.openServer(configuration, Constantes.DB4O_FILENAME, Constantes.PUERTODB4O);
			servidor.grantAccess(Constantes.USUARIO, Constantes.CONTRASENA);
			
			try {
				wait(Long.MAX_VALUE);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
