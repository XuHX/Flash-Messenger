package ClienteV2;

import java.awt.Toolkit;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import ClienteV2.BD.Conexion;
import ClienteV2.LogIn.Login;

/**
 * Clase principal del cliente
 * @author Aitor Ugarte, Joseba Garc�a, Javier P�rez
 */
public class Principal_Cliente {

	static String ip = "";
	public static ArrayList<String> direcciones = new ArrayList<String>();

	public void LanzarLogin() throws UnknownHostException, IOException {

		try {
			Login inicio = new Login();
			inicio.setIconImage(Toolkit.getDefaultToolkit().getImage("images/logo.jpg"));
			inicio.setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/*
	 * M�todo que que recibe la ip del servidor en el Broadcast
	 */
	public static void DireccionIp() throws UnknownHostException, IOException {

		// Reseteamos las variables para evitar acumulaciones
		DatagramPacket dgp = null;

		// El mismo puerto que se uso en la parte de enviar.
		MulticastSocket escucha = new MulticastSocket(50000);

		// Nos ponemos a la escucha de la misma IP de Multicast que se uso en la
		// parte de enviar.
		escucha.joinGroup(InetAddress.getByName("230.0.0.1"));

		// Un array de bytes con tama�o suficiente para recoger el mensaje
		// enviado
		byte[] dato = new byte[15];

		// Se espera la recepci�n. La llamada a receive() se queda bloqueada
		// hasta que llegue un mesnaje.
		dgp = new DatagramPacket(dato, dato.length);
		escucha.receive(dgp);
		dato = dgp.getData();
		// escucha.close();
		ip = new String(dato, "UTF-8");
		direcciones.add(ip);
	}

	public String getIp() {
		return ip;
	}


	public static void main(String args[]) throws IOException {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look
			// and feel.
		}

		RayoProgreso carga = new RayoProgreso();
		carga.setIconImage(Toolkit.getDefaultToolkit().getImage("images/logo.jpg"));
		carga.setVisible(true);
		Conexion conexion = new Conexion();
		
		if (conexion.TestInternet() == true) {
			Connection con = conexion.initBD();
			Statement stat = conexion.usarBD(con);
			/*if(conexion.hayNuevaVersion(stat) == true){//TODO a�adir pr�ximamente
				JOptionPane.showMessageDialog(null,"�Hay un nueva versi�n del cliente disponible!");
			}*/ 
			do{
				conexion.servidorObtener(stat);
			}while(conexion.ips.size() == 0);
			ip = conexion.ips.get(0);
		} else {
			DireccionIp();
			ip = direcciones.get(0);
		}
		carga.StopSimulacion(null);
		carga.dispose();
	
		Login inicio = new Login();
		
		inicio.setIconImage(Toolkit.getDefaultToolkit().getImage("images/logo.jpg"));
		inicio.setVisible(true);
			
	}

}
