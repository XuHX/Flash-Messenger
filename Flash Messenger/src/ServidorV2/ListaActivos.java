package ServidorV2;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import ServidorV2.Hilos.H_Servidor;
import ServidorV2.Logs.Log_errores;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.awt.event.ActionEvent;

public class ListaActivos extends JFrame {

	
	private static final long serialVersionUID = 8993508281880165735L;
	private JPanel contentPane;
	private JLabel lblUsuariosActivos;
	private JList<String> listaNombres;
	private DefaultListModel<String> modelo;
	private JScrollPane scrollLista;
	private JButton btnEcharDelServidor;

	
	public ListaActivos() {
		setResizable(false);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 233, 300);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		lblUsuariosActivos = new JLabel("Usuarios activos");
		lblUsuariosActivos.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblUsuariosActivos.setHorizontalAlignment(SwingConstants.CENTER);
		lblUsuariosActivos.setBounds(5, 5, 215, 37);
		contentPane.add(lblUsuariosActivos);
		
		//instanciamos la lista
		listaNombres = new JList<String>();
		listaNombres.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
		   
		//instanciamos el modelo
		modelo = new DefaultListModel<String>();
		      
		//instanciamos el Scroll que tendra la lista
		scrollLista = new JScrollPane();
		scrollLista.setBounds(20, 53,181, 147);
		scrollLista.setViewportView(listaNombres);
		contentPane.add(scrollLista);
		
		btnEcharDelServidor = new JButton("Echar del servidor");
		btnEcharDelServidor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eliminarNombre();	
					}
		});
		btnEcharDelServidor.setBounds(20, 220, 181, 23);
		contentPane.add(btnEcharDelServidor);
		
	}
	
	public void agregarNombre(){
		
		int tamanio = H_Servidor.clientesActivos.size();
		H_Servidor user = null;
		String datos;
	
			for (int i = 0; i < tamanio; i++) {
				
				user = H_Servidor.clientesActivos.get(i);
				datos = user.getNombre() + "  " + user.getIp(); //Mal!!! Obtiene la ip del servidor, no del cliente. TODO
				user.interrupt();
				
				modelo.addElement(datos);
				listaNombres.setModel(modelo);
			}
	}
	public void eliminarNombre(){
		
		H_Servidor user = null;
		Integer indice = listaNombres.getSelectedIndex();
		
		try{
		modelo.removeElementAt(indice); //Borramos al usuario de la lista
		user = H_Servidor.clientesActivos.get(indice);
		user.desconectar(); //Echamos al usuario del servidor :)	
		}catch(IndexOutOfBoundsException e){
			Log_errores.log( Level.SEVERE, "Error al eliminar al usuario. ", e );
		}
		
		
	/*Falta que se cierre la ventana del usuario */
	}
}
