package ClienteV2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JOptionPane;

/*
 * Clase del cliente que gestiona la conexi�n
 */ 
public class Cliente {

	   public static String Ip_Servidor; //Direcci�n ip del servidor
	   private String nombre; //Nombre del usuario
	   private GUI_Cliente frame; //GUI del cliente
	   private DataOutputStream salida;
	   private DataInputStream entrada;
	   private Socket comunicacion; //para la conectarse
	   private Socket comunicacion2;//para recibir el mensaje
	   private H_Cliente hilo;
	
	   //Crea una nueva instancia del cliente
	   public Cliente(GUI_Cliente frame) throws IOException{      
		   this.frame = frame;
	   }
	   
	   public void conexion() throws IOException{
		   Ip_Servidor = Principal_Cliente.Ip_Servidor;
		   
	      try {
	         comunicacion = new Socket(Cliente.Ip_Servidor, 8080);
	         comunicacion2 = new Socket(Cliente.Ip_Servidor, 8083);
	         
	         salida = new DataOutputStream(comunicacion.getOutputStream());
	         entrada = new DataInputStream(comunicacion2.getInputStream());     
	   
	      }catch (IOException e) {
	    	  JOptionPane.showMessageDialog(frame,"Ning�n servidor activado", "Error de conexi�n", JOptionPane.ERROR_MESSAGE);
	      }
	     
	      hilo =  new H_Cliente(entrada, frame);
	      hilo.start();
	   }
	
	   /*
	    * M�todo que env�a los mensajes al servidor
	    */
	   public void flujo(String txt){
	      try {             
	         salida.writeInt(1);
	         salida.writeUTF(txt);
	      } catch (IOException e) {
	    	  JOptionPane.showMessageDialog(frame,"Has sido expulsado del servidor.", "Error", JOptionPane.ERROR_MESSAGE);
	    	  frame.dispose();
	    	 
	      }
	   }
}
