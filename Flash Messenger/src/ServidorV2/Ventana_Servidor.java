package ServidorV2;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.event.ActionEvent;

/*
 * Clase principal del Servidor
 */
public class Ventana_Servidor extends JFrame {

 
	private static final long serialVersionUID = 930849024921895057L;
	private JPanel contentPane;
	private JLabel lblPanelDeControl;
	private JButton btnDesconectar, btnUsuarios, btnRegistroDeMensajes;
	private String ip;


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
				JOptionPane.showMessageDialog(null,"Próximamente...", "Not today", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnRegistroDeMensajes.setBounds(163, 75, 89, 48);
		contentPane.add(btnRegistroDeMensajes);

	}
	public void runServer() {

		ServerSocket servidor1 = null;// para establecer la conexión
		ServerSocket servNick = null; // Para enviar el nombre
		ServerSocket servidor2 = null;// para enviar mensajes
		boolean activo = true;
		
		try {
			
			servidor1 = new ServerSocket(8080);
			servNick = new ServerSocket(8081);
			servidor2 = new ServerSocket(8083);
			
			while (activo) {
			
				Socket socket1 = null;
				Socket socketNick = null;
				Socket socket2 = null;
			
				try {
				
					socket1 = servidor1.accept();
					socketNick = servNick.accept();
					socket2 = servidor2.accept();
				} catch (IOException e) {
				    JOptionPane.showInputDialog("Error al unirse al servidor : " + e.getMessage());
					continue;
				}
				// Activa el servidor
				ip = Inet4Address.getLocalHost().getHostAddress();
				System.out.println("La ip del servidor es: " + ip);
				HiloServidor user = new HiloServidor(socket1, socketNick, socket2, this, ip);
				user.start();
			}

		} catch (IOException e) {
			JOptionPane.showInputDialog("Error: " + e.getMessage());
		}
	}
	
	public static void main(String[] args) throws IOException {
		// https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/nimbus.html
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
		Hilo_Enviar hilo = new Hilo_Enviar();
		hilo.start();
		
		Ventana_Servidor servidor = new Ventana_Servidor();
		servidor.setVisible(true);
		servidor.runServer();
	}

}
