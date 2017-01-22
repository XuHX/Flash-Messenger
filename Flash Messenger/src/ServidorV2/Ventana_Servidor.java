package ServidorV2;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.sqlite.SQLiteConfig.SynchronousMode;

import ServidorV2.Ventana_Servidor;
import ServidorV2.BD.BD_Local;
import ServidorV2.BD.BD_Padre;
import ServidorV2.BD.BD_Remota;
import ServidorV2.Hilos.H_Comunicacion;
import ServidorV2.Hilos.H_EnviarIp;
import ServidorV2.Hilos.H_Servidor;
import ServidorV2.Logs.Log_chat;
import ServidorV2.Logs.Log_errores;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Toolkit;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JProgressBar;
import javax.swing.border.LineBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/*
 * Clase principal del Servidor
 */
public class Ventana_Servidor extends JFrame {

	private static final long serialVersionUID = 930849024921895057L;
	private JPanel contentPane, p_oeste, p_centro, p_este, p_sur, p_norte;
	private String ip; // ip del cliente
	private static Connection conex;
	private static Statement stat;
	private static BD_Remota remota;
	private static BD_Local local;
	private static BD_Padre padre;
	private H_Comunicacion comunicarse;
	private static boolean hayInternet = Analisis_Red.TestInternet();
	private JLabel lbTitulo, lblUsuariosActivos, lbVisualizar, lbOpciones, lbEnlace;
	private JButton btnExpulsar, btnBaseDeDatos, btnRedWifi, btnActuali, btnEliminar, btnEscanear;
	private JScrollPane scrollPane1, scrollPane2;
	private JTable table;
	private DefaultTableModel modelo;
	private JList<Integer> lista;
	public DefaultListModel<Integer> model;
	private JList<String> list_activos;
	private DefaultListModel<String> model_activos;
	public static ArrayList<Integer> puertos;
	public static ArrayList<String[]> usuarios;
	private MiBoton btnDesconectar, btnConectar;


	public static JProgressBar progressBar;

	public Ventana_Servidor() {
		Ini();
		Add();
		Comp();
		Actions();
		RefrescarConexiones(null);
	}

	private void Ini() {
		btnRedWifi = new JButton("Red wifi");
		contentPane = new JPanel();
		p_norte = new JPanel();
		lbTitulo = new JLabel("Panel de control del Servidor");
		p_oeste = new JPanel();
		scrollPane1 = new JScrollPane();
		lblUsuariosActivos = new JLabel("Usuarios activos");
		btnExpulsar = new JButton("Expulsar");
		p_centro = new JPanel();
		lbVisualizar = new JLabel("En el panel aparecer\u00E1 lo que pulse");
		scrollPane2 = new JScrollPane();
		p_este = new JPanel();
		lbOpciones = new JLabel("Opciones");
		btnBaseDeDatos = new JButton("Base de datos");
		btnActuali = new JButton("Actualizar");
		p_sur = new JPanel();
		btnDesconectar = new MiBoton("Desconectado");
		btnConectar = new MiBoton("Conectar");
		list_activos = new JList<String>();
		model_activos = new DefaultListModel<String>();
		progressBar = new JProgressBar();
	}

	private void Add() {
		setTitle("Flash Messenger");
		setAutoRequestFocus(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		setContentPane(contentPane);
		setLocationRelativeTo(null);

		p_norte.setLayout(new BorderLayout(0,0));
		p_oeste.setLayout(new BorderLayout(0, 0));
		p_centro.setLayout(new BorderLayout(0, 0));
		p_este.setLayout(new GridLayout(0, 1, 0, 0));
		p_sur.setLayout(new BorderLayout(0, 0));

		p_norte.add(lbTitulo, BorderLayout.CENTER);
		p_norte.add(btnDesconectar, BorderLayout.EAST);
		p_norte.add(btnConectar, BorderLayout.WEST);
		p_este.add(lbOpciones);
		p_este.add(btnBaseDeDatos);
		p_este.add(btnRedWifi);
		p_este.add(btnActuali);
		p_sur.add(progressBar);

		p_oeste.add(scrollPane1, BorderLayout.CENTER);
		p_oeste.add(btnExpulsar, BorderLayout.SOUTH);
		p_centro.add(lbVisualizar, BorderLayout.NORTH);
		p_centro.add(scrollPane2, BorderLayout.CENTER);

		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(p_sur, BorderLayout.SOUTH);
		contentPane.add(p_oeste, BorderLayout.WEST);
		contentPane.add(p_centro, BorderLayout.CENTER);
		contentPane.add(p_este, BorderLayout.EAST);
		contentPane.add(p_norte, BorderLayout.NORTH);

	}

	private void Comp() {
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		lbTitulo.setFont(new Font("Tahoma", Font.BOLD, 15));
		lbTitulo.setHorizontalAlignment(SwingConstants.CENTER);
		lbTitulo.setPreferredSize(new Dimension(10, 40));
		scrollPane1.setViewportView(list_activos);
		scrollPane1.setPreferredSize(new Dimension(150, 0));
		lblUsuariosActivos.setFont(new Font("Tahoma", Font.BOLD, 12));
		scrollPane1.setColumnHeaderView(lblUsuariosActivos);
		lblUsuariosActivos.setHorizontalAlignment(SwingConstants.CENTER);
		btnExpulsar.setVerticalAlignment(SwingConstants.BOTTOM);
		lbVisualizar.setHorizontalAlignment(SwingConstants.CENTER);
		lbVisualizar.setFont(new Font("Tahoma", Font.BOLD, 12));
		lbOpciones.setFont(new Font("Tahoma", Font.BOLD, 12));
		lbOpciones.setHorizontalAlignment(SwingConstants.CENTER);
		progressBar.setBackground(Color.ORANGE);
		progressBar.setStringPainted(true);
		btnDesconectar.setBackground(Color.yellow);
	}

	private void Actions() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(hayInternet == true){
					try {
						remota.servidorDelete(stat, Inet4Address.getLocalHost().getHostAddress());
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		btnExpulsar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				eliminarNombre();
			}
		});
		btnBaseDeDatos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cargarTabla();
			}
		});

		btnRedWifi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cargarListaPuertos();
			}
		});
		
		btnConectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnConectar.setBackground(Color.yellow);
				btnConectar.setText("Conectado");
				btnDesconectar.setBackground(getBackground());
				btnDesconectar.setText("Desconectar");

				comunicarse = new H_Comunicacion();
				if(server.isAlive() == false){
					comunicarse.start();
					server.start();
				}
			}
		});
		btnDesconectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnConectar.setBackground(getBackground());
				btnConectar.setText("Conectar");
				btnDesconectar.setBackground(Color.yellow);
				btnDesconectar.setText("Desconectado");
							
				comunicarse.interrupt();
				server.interrupt();
				
				if(hayInternet == true){
					try {
						remota.servidorDelete(stat, Inet4Address.getLocalHost().getHostAddress());
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					}
				}
				
			}
		});
		btnActuali.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int c = 6;
				lbVisualizar.setText("B�squeda de actualizaciones");
				lbEnlace = new JLabel();

				if (remota.hayNuevaVersion(stat) == false) {

					String enlace = "https://github.com/aitorugarte/Flash-Messenger";
					lbEnlace.setText("<html><a href=" + enlace + "> �Nueva versi�n encontrada!</a></html>");
					
					lbEnlace.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent arg0) {
							try {
								if (Desktop.isDesktopSupported()) {
									Desktop desktop = Desktop.getDesktop();
									if (desktop.isSupported(Desktop.Action.BROWSE)) {
										desktop.browse(new URI(enlace));
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				} else {
					lbEnlace.setText("Ya tienes la �ltima versi�n.");
				}
				lbEnlace.setFont(new Font("Tahoma", Font.BOLD, 12));
				lbEnlace.setHorizontalAlignment(SwingConstants.CENTER);
				scrollPane2.setViewportView(lbEnlace);

				if (p_centro.isAncestorOf(btnEscanear)) {
					p_centro.remove(btnEscanear);
					c = 5;
				}
				if (p_centro.isAncestorOf(btnEliminar)) {
					p_centro.remove(btnEliminar);
					c = 5;
				}
				contentPane.setBorder(new EmptyBorder(5, 5, 5, c));

			}
		});
	}
	Thread server = new Thread(){
		@Override
		public void run(){
			runServer();
		}
	};
	
	public void cargarTabla() {
		int c = 6;
		modelo = new DefaultTableModel();

		modelo.addColumn("Nombre");
		modelo.addColumn("Contrase�a");
		modelo.addColumn("Correo electr�nico");
		padre.obtenerContenido();
		
		usuarios = BD_Padre.contenido;
		for (int i = 0; i < usuarios.size(); i++) {
			modelo.addRow(usuarios.get(i));
		}
		
		table = new JTable();
		table.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (table.getSelectedRow() != -1) {
					System.out.println("Fila modificada: " + table.getSelectedRow());
					System.out.println("Cambios guardados.");
				}
			}
		});
		scrollPane2.setViewportView(table);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setModel(modelo);

		table.getColumnModel().getColumn(0).setPreferredWidth(0);
		table.getColumnModel().getColumn(1).setPreferredWidth(10);
		table.getColumnModel().getColumn(2).setPreferredWidth(70);

		if (p_centro.isAncestorOf(btnEscanear)) {
			p_centro.remove(btnEscanear);
			c = 5;
		}
		btnEliminar = new JButton("Eliminar");
		btnEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Fila n�mero: " + table.getSelectedRow());
			}
		});
		
		p_centro.add(btnEliminar, BorderLayout.SOUTH);
		lbVisualizar.setText("Contenido de la Base de Datos");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, c)); // Para que se actualice la ventana y aparezca el bot�n
	}

	public void cargarListaPuertos() {
		int c = 6;
		if (model != null) {
			scrollPane2.setViewportView(lista);
			lista.setModel(model);
		}

		btnEscanear = new JButton("Escanear red");
		btnEscanear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lista = new JList<Integer>();
				model = new DefaultListModel<>();
				progressBar.setMaximum(65535);
				iniciar.start();
			}
		});
		lbVisualizar.setText("Puertos abiertos en la red wifi");
		if (p_centro.isAncestorOf(btnEliminar)) {
			p_centro.remove(btnEliminar);
			c = 5;
		}
		if (scrollPane2.isAncestorOf(table) || scrollPane2.isAncestorOf(lbEnlace)){
			scrollPane2.setViewportView(null);;
		}
		
		p_centro.add(btnEscanear, BorderLayout.SOUTH);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, c)); // Para que se actualice la ventana y aparezca el bot�n

	}
	private Timer conect;
	
	private void RefrescarConexiones(ActionEvent evt) {
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				agregarNombre();
			}
		};

		conect = new Timer(3000, taskPerformer);
		conect.start();
	}
	public void agregarNombre(){
		//TODO eliminar nombres que no est�n en la lista
		
		int tamanio = H_Servidor.clientesActivos.size();
		H_Servidor user = null;
		String datos = "";
	
			for (int i = 0; i < tamanio; i++) {
				
				user = H_Servidor.clientesActivos.get(i);
				datos = user.getNombre() + "  " + user.getIp(); 
	
				if(model_activos.size() == 0){
					model_activos.addElement(datos);
				}
				for (int j = 0; j < model_activos.size(); j++) {
					if(!datos.equals(model_activos.get(j))){
						model_activos.addElement(datos);
					}
				}	
			
			}
				list_activos.setModel(model_activos);

	}
	public void eliminarNombre(){
		
		H_Servidor user = null;
		Integer indice = list_activos.getSelectedIndex();
		
		try{
		model_activos.removeElementAt(indice); //Borramos al usuario de la lista
		user = H_Servidor.clientesActivos.get(indice);
		user.desconectar(); //Echamos al usuario del servidor :)	
		}catch(IndexOutOfBoundsException e){
			Log_errores.log( Level.SEVERE, "Error al eliminar al usuario. ", e );
		}
		
	}
	Thread iniciar = new Thread() {
		@Override
		public void run() {
			
			ExecutorService executor = Executors.newCachedThreadPool();
			int inicio = 1;
			int fin = 100;
			long s = System.currentTimeMillis();
			while (fin <= 65500) {
				executor.execute(new ejecutarTareas(inicio, fin));
				inicio += 100;
				fin += 100;
			}
			executor.execute(new ejecutarTareas(65501, 65535));

			executor.shutdown();

			try {
				executor.awaitTermination(4, TimeUnit.MINUTES);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}

			System.out.println("Ha tardado: " + (System.currentTimeMillis() - s) / 1000 + " segundos");
			progressBar.setValue(65535);

			puertos = Analisis_Red.almacen;
			Collections.sort(puertos);
			for (int i = 0; i < puertos.size(); i++) {
				model.addElement(puertos.get(i));
			}
			cargarListaPuertos();
		}
	};
	//M�todo que busca el usuario y la contrase�a en la BD
		public static boolean buscarUsuario(String usuario){
			boolean hay = padre.existeUsuario(usuario, stat, conex);
			return hay;
		}
		
		/*
		 * M�todo que divide la cadena de texto para ser insertada
		 */
		public static void dividir(String algo) {

			String nombre = "";
			String contrase�a = "";
			String correo = "";

			// Encontar la posici�n de los espacios
			int espacio = 0;
			int espacio2 = 0;

			for (int i = 0; i < algo.length(); i++) {

				if (algo.charAt(i) == ' ') {
					if (espacio == 0) {
						espacio = i;
					} else {
						espacio2 = i;
					}
				}
			}
			// Formamos las palabras
			int x = 0;
			while (x < espacio) {

				nombre = nombre + algo.charAt(x);
				x++;
			}
			espacio = espacio + 1;

			while (espacio < espacio2) {

				contrase�a = contrase�a + algo.charAt(espacio);
				espacio++;
			}
			espacio2 = espacio2 + 1;
			while (espacio2 < algo.length()) {

				correo = correo + algo.charAt(espacio2);
				espacio2++;
			}

			padre.clienteInsert(stat, nombre, contrase�a, correo);

		}
		
		/*
		 * M�todo que corre el servidor
		 */
		public void runServer() {

			ServerSocket servidor1 = null;
			
			try {

				servidor1 = new ServerSocket(8000);
				
				
				while (true) {
		
					Socket socket = null;
				
					try {
						
						socket = servidor1.accept();

					} catch (IOException e) {
						Log_errores.log( Level.SEVERE, "Error al unirse al servidor: " + e.getMessage(), e );
					    JOptionPane.showInputDialog("Error al unirse al servidor : " + e.getMessage());
						continue;
					}
					
					ip = (((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
					
					//Activamos el usuario (nombre, puerto1, puerto2, la ventana, su ip);
					H_Servidor user = new H_Servidor(BD_Padre.nomb, socket, this, ip);
					user.start();
				}

			} catch (IOException e) {
				Log_errores.log( Level.SEVERE, "Error " + e.getMessage(), e );
				JOptionPane.showInputDialog("Error: " + e.getMessage());
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
			// If Nimbus is not available, you can set the GUI to another look
			// and feel.
		}
		Image icon = Toolkit.getDefaultToolkit().getImage("images/logo.jpg");

		if(hayInternet == true){
			remota = BD_Remota.getBD();
			stat = remota.getStat();
			conex = remota.getConexion();
			padre = new BD_Padre(conex, stat);
			remota.servidorInsert(stat, Inet4Address.getLocalHost().getHostAddress());

		}else{
			local = BD_Local.getBD();
			local.crearTablasBD();
			stat = local.getStat();
			conex = local.getConexion();
			padre = new BD_Padre(conex, stat);
			
		H_EnviarIp enviar = new H_EnviarIp();
		enviar.start();
		}
		
		Calendar calendario = GregorianCalendar.getInstance();
		try {
			Log_chat.empezarLog(calendario);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Ventana_Servidor servidor = new Ventana_Servidor();
		servidor.setIconImage(icon);
		servidor.setVisible(true);

	}

	@SuppressWarnings("serial")
	class MiBoton extends JButton {

		public MiBoton(String texto) {
			super(texto);
			setContentAreaFilled(false);
		}
		
		protected void paintComponent(Graphics g) {
		
			g.setColor(getBackground());
			g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);

			super.paintComponent(g);
		}

		protected void paintBorder(Graphics g) {
			g.setColor(getForeground());
			g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
		}

		Shape shape;
		public boolean contains(int x, int y) {
			if (shape == null || !shape.getBounds().equals(getBounds())) {
				shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
			}
			return shape.contains(x, y);
		}

	}
}
