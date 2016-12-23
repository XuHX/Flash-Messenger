package ClienteV2;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.apache.commons.codec.binary.Base64;

import ClienteV2.Encriptado.CesarRecursivo;

/*
 * Clase del cliente que gestiona la conexi�n
 */ 
public class Cliente {

	   public static String Ip_Servidor; //Direcci�n ip del servidor
	   private String nombre; //Nombre del usuario
	   private GUI_Cliente frame; //GUI del cliente
	   private ObjectInputStream in;
       private ObjectOutputStream out;
       private Socket socket;
	   private H_Cliente hilo;
	
	   //Crea una nueva instancia del cliente
	   public Cliente(GUI_Cliente frame) throws IOException{      
		   this.frame = frame;
	   }
	   
	   public void conexion() throws IOException{
		   Ip_Servidor = Principal_Cliente.Ip_Servidor;
		   
	      try {
	    	 socket = new Socket(Cliente.Ip_Servidor, 8000);
	 	     in = new ObjectInputStream(socket.getInputStream()); 
	 	     out = new ObjectOutputStream(socket.getOutputStream()); 
	   
	      }catch (IOException e) {
	    	  JOptionPane.showMessageDialog(frame,"Ning�n servidor activado", "Error de conexi�n", JOptionPane.ERROR_MESSAGE);
	      }
	     
	      hilo =  new H_Cliente(socket, in, out, frame);
	      hilo.start();
	   }
	
	/*
	 * M�todo que env�a los mensajes al servidor
	 */
	public void enviarTexto(String txt) {
		try {
			out.writeObject("1");
			out.writeObject(txt);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Has sido expulsado del servidor.", "Error",
					JOptionPane.ERROR_MESSAGE);
			
			System.out.println(e);
			frame.dispose();

		}
	}

	/**
	 * M�todo que env�a una imagen
	 * @param path direcci�n en la que se encuentra la fotograf�a
	 * @param tipo 1 si jpg, 2 si png
	 */
	public void enviarImagen(String path, String tipo){
		try {
		if(tipo.equals("png")){
			out.writeObject("2");
		}else{
			out.writeObject("3");
		}
	    BufferedImage imagen = ImageIO.read(new File(path));
	    out.writeObject(new ImageIcon(imagen));
		System.out.println("Imagen enviada!");
		
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "Has sido expulsado del servidor.", "Error",
					JOptionPane.ERROR_MESSAGE);
			System.out.println(e);
			frame.dispose();
		}
	}
}
