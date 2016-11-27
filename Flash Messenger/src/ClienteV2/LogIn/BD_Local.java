package ClienteV2.LogIn;

import java.sql.*;
import java.util.*; 

import ServidorV2.HiloServidor;

/*
 * Clase de la gesti�n de la BD local 
 */
public class BD_Local {

	private static Exception lastError = null;  // Informaci�n de �ltimo error SQL ocurrido
	
	/** Inicializa una BD SQLITE y devuelve una conexi�n con ella
	 * @param nombreBD	Nombre de fichero de la base de datos
	 * @return	Conexi�n con la base de datos indicada. Si hay alg�n error, se devuelve null
	 */
	public Connection initBD() {
		try {
		    Class.forName("org.sqlite.JDBC");
		    Connection con = DriverManager.getConnection("jdbc:sqlite:ProyectoFlash.db");
		    return con;
		} catch (ClassNotFoundException | SQLException e) {
			lastError = e;
			e.printStackTrace();
			return null;
		}
	}
	
	/** Devuelve statement para usar la base de datos
	 * @param con	Conexi�n ya creada y abierta a la base de datos
	 * @return	sentencia de trabajo si se crea correctamente, null si hay cualquier error
	 */
	public static Statement usarBD(Connection con) {
		try {
			Statement statement = con.createStatement();
			statement.setQueryTimeout(30);  // poner timeout 30 msg
			return statement;
		} catch (SQLException e) {
			lastError = e;
			e.printStackTrace();
			return null;
		}
	}
	
	/** Crea las tablas de la base de datos. Si ya existen, las deja tal cual
	 * @param con	Conexi�n ya creada y abierta a la base de datos
	 * @return	sentencia de trabajo si se crea correctamente, null si hay cualquier error
	 */
	public Statement usarCrearTablasBD(Connection con) {
		try {
			Statement statement = con.createStatement();
			statement.setQueryTimeout(30);  // poner timeout 30 msg
			try {
				statement.executeUpdate("create table cliente " +
					"(id_cliente integer primary key autoincrement,"
					+ "usuario text not null,"
					+ " contrase�a text not null, correo text not null)");
			} catch (SQLException e) {} // Tabla ya existe. Nada que hacer		
			System.out.println("Conectado con base de datos local.");
			return statement;
		} catch (SQLException e) {
			lastError = e;
			e.printStackTrace();
			return null;
		}
	}
	
	/** Reinicia en blanco las tablas de la base de datos. 
	 * UTILIZAR ESTE M�TODO CON PRECAUCI�N. Borra todos los datos que hubiera ya en las tablas
	 * @param con	Conexi�n ya creada y abierta a la base de datos
	 * @return	sentencia de trabajo si se borra correctamente, null si hay cualquier error
	 */
	public Statement reiniciarBD(Connection con) {
		try {
			Statement statement = con.createStatement();
			statement.setQueryTimeout(30);  // poner timeout 30 msg
			statement.executeUpdate("drop table if exists cliente");
			return usarCrearTablasBD( con );
		} catch (SQLException e) {
			lastError = e;
			e.printStackTrace();
			return null;
		}
	}
	
	/** Cierra la base de datos abierta
	 * @param con	Conexi�n abierta de la BD
	 * @param st	Sentencia abierta de la BD
	 */
	public static void cerrarBD(Connection con, Statement st) {
		try {
			if (st!=null) st.close();
			if (con!=null) con.close();
		} catch (SQLException e) {
			lastError = e;
			e.printStackTrace();
		}
	}
	
	/** Devuelve la informaci�n de excepci�n del �ltimo error producido por cualquiera 
	 * de los m�todos de gesti�n de base de datos
	 */
	public static Exception getLastError() {
		return lastError;
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
			lastError = e;
			e.printStackTrace();
			return false;
		}
	}
	public void mostrarContenido(Statement st, Connection con){
	
		try {
			ResultSet rs = st.executeQuery("select * from cliente");
			while (rs.next()) {
				  String nombre = rs.getString("usuario");
				  String contrase�a = rs.getString("contrase�a");
				  String correo = rs.getString("correo");
				  
				  System.out.println(nombre + " " + contrase�a + " " + correo);
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
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