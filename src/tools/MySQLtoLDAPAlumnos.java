package tools;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import frame.JFResultados;

public class MySQLtoLDAPAlumnos {

	private static final String TABLA_PROFESORES = "profesores";

	private static final String TABLA_ALUMNOS = "alumnos";

	private Connection con;

	private String username;
	private String passwd;
	private int uid;
	private String gid;
	private String gidAlumnos;
	private String gidProfesor;
	private String realname;
	private String homedir;
	private String shell = "/bin/bash";
	private String min = "";
	private String max = "";
	private String warn = "";
	private String inactive = "";
	private String expire = "";

	public MySQLtoLDAPAlumnos(File ficheroConfiguracion, String tabla) throws SQLException {

		String resultado = "";
		int i = 0;

		con = new ConexionMySQL(ficheroConfiguracion).Conectar();
		try {
			cargarDatos(ficheroConfiguracion);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ResultSet rs = null;

		JFResultados jfr = new JFResultados();
		jfr.setVisible(true);
		try {
			rs = (tabla.compareTo(TABLA_ALUMNOS) == 0) ? obtenerUsuariosNOLDAP(con, TABLA_ALUMNOS)
					: obtenerUsuariosNOLDAP(con, TABLA_PROFESORES);

			if (rs == null) {
				JOptionPane.showMessageDialog(null, "Todos los usuarios constan como dados de alta en LDAP.",
						"Informacion", JOptionPane.INFORMATION_MESSAGE);
			} else {
				while (rs.next()) {
					username = rs.getString("username");
					uid = rs.getInt("uid");
					homedir = "/home/" + username;
					realname = rs.getString("nombre") + " " + rs.getString("apellido1") + " "
							+ rs.getString("apellido2");
					passwd = (tabla.compareTo(TABLA_ALUMNOS)==0)?rs.getString("password"):rs.getString("dni");

					gid = (tabla.compareTo(TABLA_ALUMNOS) == 0) ? gidAlumnos : gidProfesor;

					resultado = resultado + "create:" + username + ":" + passwd + ":" + uid + ":" + gid + ":" + realname
							+ ":" + homedir + ":" + shell + ":" + min + ":" + max + ":" + warn + ":" + inactive + ":"
							+ expire + "\n";

					jfr.añadirTexto(
							"create:" + username + ":" + passwd + ":" + uid + ":" + gid + ":" + realname + ":" + homedir
									+ ":" + shell + ":" + min + ":" + max + ":" + warn + ":" + inactive + ":" + expire);
					i++;
				}
			}
			jfr.añadirTexto("\nSe han creado " + i + " entradas nuevas.");
			jfr.añadirTexto(
					"Recuerde añadir el resultado a Webmin para la creacion de las cuentas LDAP, y actualice la BBDD");

			JOptionPane.showMessageDialog(null,
					"Proceso finalizado.\nRecuerde añadir el resultado a Webmin para la creacion de las cuentas LDAP, \ny actualice la BBDD.",
					"Informacion", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (con != null)
				con.close();
		}

	}

	public ResultSet obtenerUsuariosNOLDAP(Connection c, String table) {

		String query = "SELECT * FROM " + table + " WHERE ldap=\"NO\"";
		Statement st;
		ResultSet r = null;
		System.out.println(query);
		try {
			st = c.createStatement();
			r = st.executeQuery(query);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return r;
	}

	public void cargarDatos(File f) throws Exception {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));

			in.readObject();
			this.shell = (String) in.readObject();
			in.readObject();
			this.gidProfesor = (String) in.readObject();
			in.readObject();
			this.gidAlumnos = (String) in.readObject();

		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (in != null)
				in.close();
		}
	}
}
