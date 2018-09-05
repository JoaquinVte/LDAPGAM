package tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author PC-ORACLE
 */

public class ConexionMySQL {

	private Connection conexion;
	private String host;
	private String puerto;
	private String usuario;
	private String password;
	private File f;

	public Connection getConexion() {
		return conexion;
	}

	public ConexionMySQL(File f) {
		this.f = f;
		try {
			this.cargar();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPuerto() {
		return puerto;
	}

	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ConexionMySQL(String host, String puerto, String usuario, String password, File f) {
		super();
		this.host = host;
		this.puerto = puerto;
		this.usuario = usuario;
		this.password = password;
		this.f = f;
	}

	public void setConexion(Connection conexion) {
		this.conexion = conexion;
	}

	public Connection Conectar() {
		try {
			// Class.forName("oracle.jdbc.driver.OracleDriver");
			// String BaseDeDatos = "jdbc:mysql:" + host + ":" + puerto ;

			conexion = DriverManager.getConnection(
					"jdbc:mysql://" + host + "/mydb?" + "user=" + usuario + "&password=" + password + "&useSSL=false");
			if (conexion == null) {
				JOptionPane.showMessageDialog(null, "Error al conectar. Revise Configuracion", "Error",
						JOptionPane.ERROR_MESSAGE);
				System.out.println("Conexión fallida");
			}
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

		return conexion;
	}

	public void setFile(File f) {
		this.f = f;
	}

	public void cargar() throws FileNotFoundException, IOException {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));

			for (int i = 1; i < 9; i++) {
				in.readObject();
			}

			this.host = (String) in.readObject();
			this.puerto = (String) in.readObject();
			this.usuario = (String) in.readObject();
			this.password = (String) in.readObject();

		} catch (EOFException e) {
			System.out.println(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			in.close();
		}
	}

}
