package ServidorV2.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/*
 * Clase que guarda la conversaci�n de chat
 */
public class Log_chat {
	
	private static String ruta = "C:/Flash-Messenger";
	private static String nombre = "/chat.txt";

	/*
	 * M�todo para escribir el chat en el fichero
	 */
	public static void EscribirDatos(String texto) throws IOException {

		// Primero miramos si existe la carpeta
		File carpeta = new File(ruta);
		if (!carpeta.exists()) { // Si no existe, creamos la carpeta
			carpeta.mkdir();
		}
		// Ahora manejamos el txt
		File registro = new File(ruta + nombre);
		BufferedWriter escritor;
		if (registro.exists()) { // Si exite lo modificamos
			escritor = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(registro, true)));
		} else { // Sino, creamos el archivo
			escritor = new BufferedWriter(new FileWriter(registro));
		}
		escritor.write(texto);
		escritor.newLine();
		escritor.close();
	}

	/*
	 * M�todo para leer un fichero (no binario, s�lo txt)
	 */
	public static void LeerDatos() throws FileNotFoundException {

		BufferedReader lector = null;
		File archivo = new File(ruta + nombre);
		FileReader registro = null;
		
		if(archivo.exists()){
	
			try{
			registro = new FileReader(ruta + nombre);
			lector = new BufferedReader(registro);
			
			String linea;
			// Mostramos l�nea a l�nea el contenido del fichero
			while ((linea = lector.readLine()) != null) {
				System.out.println(linea);
			}

		} catch (IOException e2) {
			e2.printStackTrace();
		} finally {
			// En el finally cerramos el fichero, as� nos aseguramos de que se cierra siempre.
			try {
				if (null != lector) {
					lector.close();
				}
			} catch (IOException e3) {
				e3.printStackTrace();
			}
		}
		}else{
			System.out.println("El archivo no existe.");
		}
	}

}
