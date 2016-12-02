package ServidorV2;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ServidorV2.BD.BD_Local;
import ServidorV2.BD.BD_Remota;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Level;
import java.awt.event.ActionEvent;

/*
 * Clase principal del Servidor
 */
public class Ventana_Servidor extends JFrame {

 
	private static final long serialVersionUID = 930849024921895057L;
	private JPanel contentPane;
	private JLabel lblPanelDeControl, lblEstado;
	private JButton btnDesconectar, btnUsuarios, btnRegistroDeMensajes;
	private String ip;
	private BD_Remota remota = new BD_Remota();
	private BD_Local local = new BD_Local();
	private Connection con = null;
	private Statement stat = null;


	public Ventana_Servidor() {
		setTitle("Flash Messenger");
		setAutoRequestFocus(false);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 273, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		lblPanelDeControl = new JLabel("Panel de control del servidor");
		lblPanelDeControl.setBounds(5, 5, 247, 36);
		lblPanelDeControl.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblPanelDeControl.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblPanelDeControl);
		
		btnDesconectar = new JButton("Desconectar");
		btnDesconectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnDesconectar.setBounds(74, 227, 113, 23);
		contentPane.add(btnDesconectar);
		
		btnUsuarios = new JButton("Usuarios activos");
		btnUsuarios.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				ListaActivos lista = new ListaActivos();
				lista.agregarNombre();
				lista.setVisible(true);
			}
		});
		btnUsuarios.setBounds(10, 75, 143, 48);
		contentPane.add(btnUsuarios);
		
		btnRegistroDeMensajes = new JButton("Log");
		btnRegistroDeMensajes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null,"Pr�ximamente...", "Not today", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnRegistroDeMensajes.setBounds(163, 75, 89, 48);
		contentPane.add(btnRegistroDeMensajes);
		
		lblEstado = new JLabel("Conectado a internet => " + Test());
		lblEstado.setBounds(55, 183, 166, 23);
		contentPane.add(lblEstado);
		
		JButton btnBD = new JButton("Base de datos");
		btnBD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
			}
		});
		btnBD.setBounds(74, 134, 136, 38);
		contentPane.add(btnBD);

	}
	
	//M�todo que busca el usuario y la contrase�a en la BD
	public boolean buscarUsuario(String usuario){
		
		boolean hay = local.existeUsuario(usuario, stat, con);
		System.out.println("Hay : " + hay);
		
		return hay;
	}
	public void responder(boolean hay) throws SocketException, UnknownHostException{
		String existe = " ";
		
		if(hay == true){
			existe = "si";
		}else{
			existe = "no";
		}
		byte[] b = existe.getBytes(Charset.forName("UTF-8"));
		
		DatagramSocket socket = new DatagramSocket(5001 ,InetAddress.getByName("localhost"));
		DatagramPacket dato = new DatagramPacket(b, b.length,InetAddress.getByName("localhost"), 5000); 

		try {
			System.out.println("Enviando respuesta...");
			socket.send(dato);
			System.out.println("Respuesta enviada");
		} catch (IOException e) {
			e.printStackTrace();
		}
			socket.close();
		
	}
	
	public void recibirRegistro() throws SocketException, UnknownHostException{
		
		byte [] b = new byte [15];
		DatagramSocket socket = new DatagramSocket(5001, InetAddress.getByName("localhost"));
		DatagramPacket dato = new DatagramPacket(b, b.length);
		
		
		try {
			System.out.println("Esperando dato...");
			socket.receive(dato);
			System.out.println("Dato recibido");
		} catch (IOException e) {
			e.printStackTrace();
		}
		socket.close();
		String registro = " ";
		try {
			registro = new String(b, "UTF-8");
			System.out.println(registro);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		dividir(registro);
	}
	
	/*
	 * M�todo que divide la cadena del usuario
	 */
	public void dividir(String algo){
		
		String nombre = "";
		String contrase�a = "";
		String correo = "";
		
		//Encontar la posici�n de los espacios
		int espacio = 0;
		int espacio2 = 0;
		
		for (int i = 0; i < algo.length(); i++) {

			if (algo.charAt(i) == ' ') {
				if(espacio == 0){
					espacio= i;
				}else{
					espacio2 = i;
				}		
			}
		}
		//Formamos las palabras
		int x = 0;
		while(x < espacio){
			
			nombre = nombre + algo.charAt(x);
			x++;
		}
		espacio = espacio + 1;
		while(espacio < espacio2){
			
			contrase�a = contrase�a + algo.charAt(espacio);
			espacio++;
		}
		espacio2 = espacio2 + 1;
		while(espacio2 < algo.length()){
			
			correo = correo + algo.charAt(espacio2);
			espacio2++;
		}
		
		local.clienteInsert(stat, nombre, contrase�a, correo);		
	}
	public void runServer() {

		ServerSocket servidor1 = null;// para establecer la conexi�n
		ServerSocket servidor2 = null;// para enviar mensajes
		boolean activo = true;
		
		try {
			
			servidor1 = new ServerSocket(8080);
			servidor2 = new ServerSocket(8083);
			
			while (activo) {
			
				Socket socket1 = null;
				Socket socket2 = null;
			
				try {
				
					socket1 = servidor1.accept();
					socket2 = servidor2.accept();
				} catch (IOException e) {
					Registro.log( Level.SEVERE, "Error al unirse al servidor: " + e.getMessage(), e );
				    JOptionPane.showInputDialog("Error al unirse al servidor : " + e.getMessage());
					continue;
				}
		
				ip = (((InetSocketAddress)socket1.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
				System.out.println("La ip del cliente es: " + ip);
				
				//Activamos el usuario
				HiloServidor user = new HiloServidor(socket1, socket2, this, ip);
				user.start();
			}

		} catch (IOException e) {
			Registro.log( Level.SEVERE, "Error " + e.getMessage(), e );
			JOptionPane.showInputDialog("Error: " + e.getMessage());
		}
	}
	
	public boolean Test(){
		
		if(remota.TestInternet() == true){
		//	remota.Conectar();
			return true;
		}else{
			con = local.initBD();
			stat = local.usarCrearTablasBD(con);
			return false;
		}
		
	}
	/*
	 * Main del programa Servidor
	 */
	public static void main(String[] args) throws IOException {

			try {
					for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
						if ("Nimbus".equals(info.getName())) {
							UIManager.setLookAndFeel(info.getClassName());
							break;
						}
					}
				} catch (Exception e) {
					Registro.log( Level.SEVERE, "Nimbus no est� operativo.", e );
					// If Nimbus is not available, you can set the GUI to another look
					// and feel.
				}
		Hilo_EnviarIp enviar = new Hilo_EnviarIp();
		enviar.start();
		
		Ventana_Servidor servidor = new Ventana_Servidor();
		servidor.setVisible(true);
		
		Hilo_Recibir recibir = new Hilo_Recibir();
		recibir.start();
		
		servidor.runServer();
	}
}
