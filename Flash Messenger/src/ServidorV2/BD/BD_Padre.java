package ServidorV2.BD;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ServidorV2.H_Servidor;

public class BD_Padre {

	protected Connection conexion;
	protected Statement stat;
	public static String nomb;
	public ArrayList<String> contenido = new ArrayList<String>();

	
	public BD_Padre(Connection conexion, Statement stat) {
		this.conexion = conexion;
		this.stat = stat;
	}

	public Connection getConexion() {
		return conexion;
	}

	public void setConexion(Connection conexion) {
		this.conexion = conexion;
	}

	public Statement getStat() {
		return stat;
	}

	public void setStat(Statement stat) {
		this.stat = stat;
	}
	/** A�ade un cliente a la tabla abierta de BD, usando la sentencia INSERT de SQL
	 * @param st Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente a la habitaci�n)
	 * @param h	cliente
	 * @param contrase�a	contase�a a a�adir en la base de datos
	 * @param correo correo a a�adir en la base de datos
	 * @return	true si la inserci�n es correcta, false en caso contrario
	 */
	public boolean clienteInsert(Statement st, String nombre, String contrase�a, String correo) {
		String sentSQL = "";
		try {
			sentSQL = "insert into cliente (usuario, contrase�a, correo) values(" +
					"'" + secu(nombre) + "'," +
					"'" + secu(contrase�a) + "'," +
					"'" + secu(correo) +  "')";
			int val = st.executeUpdate( sentSQL );
			if (val!=1) {  // Se tiene que a�adir 1 - error si no
				return false;  
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * M�todo que muestra el contenido de la base de datos
	 * TODO que devuelva un arraylist
	 */

	public void mostrarContenido() {

		try {
			ResultSet rs = stat.executeQuery("select * from cliente");
			while (rs.next()) {
				String nombre = rs.getString("usuario");
				String contrase�a = rs.getString("contrase�a");
				String correo = rs.getString("correo");
				String cadena = nombre + " " + contrase�a + " " + correo;

				contenido.add(cadena);
				System.out.println(nombre + " " + contrase�a + " " + correo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	


	public boolean existeUsuario(String usuario, Statement st, Connection con){
		
		try {
			ResultSet rs = st.executeQuery("select usuario, contrase�a from cliente");
			while (rs.next()) {
				  String nombre = rs.getString("usuario");
				  String contrase�a = rs.getString("contrase�a");
				  String juntado = nombre + " " + contrase�a;
				  
				  if(usuario.equals(juntado)){
					  for (int i = 0; i < H_Servidor.clientesActivos.size(); i++) {
						 if(nombre.equals(H_Servidor.clientesActivos.get(i).getNombre())){
							return false;
						 }
					}
					  nomb = nombre;
					//  H_Servidor.setNombUser(nombre); //TODO Seteamos el nombre del usuario
					  return true;
				  }
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		return false;
		
	}
	/**
	 *M�todo que indica si hay disponible una conexi�n a internet
	 * @return true si hay internet
	 * @return false si no hay internet
	 */
	public boolean TestInternet() {

		String web = "www.google.es";
		int puerto = 80;
		boolean hayinternet = false;
		Socket test = null;
		try {
			test = new Socket(web, puerto);
			if (test.isConnected()) {
				// Si hay internet comprobamos el puerto 3306
				if (TestPuerto() == true) {
					hayinternet = true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		//Cerramos el puerto
		try {
			test.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hayinternet;
	}

	/*
	 * M�todo que comprueba si el puerto necesario para conectarse a la bd
	 * est� abierto o cerrado
	 * @return true si est� abierto
	 * @return false si est� cerrado
	 */
	private boolean TestPuerto() {
		boolean abierto = true;
		try {
			Socket s = null;
			try{
			//Comprobamos con nuestro host
			s = new Socket("www.phpmyadmin.co", 3306);
			}catch(ConnectException e){
				abierto = false;
			}
			//Cerramos el puerto
			s.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return abierto;
	}
	
	// Devuelve el string "securizado" para volcarlo en SQL
	// (Implementaci�n 1) Sustituye ' por '' y quita saltos de l�nea
	// (Implementaci�n 2) Mantiene solo los caracteres seguros en espa�ol
	private static String secu( String string ) {
		// Implementaci�n (1)
		// return string.replaceAll( "'",  "''" ).replaceAll( "\\n", "" );
		// Implementaci�n (2)
		StringBuffer ret = new StringBuffer();
		for (char c : string.toCharArray()) {
			if ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ��������������.,:;-_(){}[]-+*=<>'\"�?�!&%$@#/\\0123456789".indexOf(c)>=0) ret.append(c);
		}
		return ret.toString();
	}	
}
