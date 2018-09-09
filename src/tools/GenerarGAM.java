package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import frame.JFResultados;

public class GenerarGAM {

	private static final String TABLA_PROFESORES = "profesores";
	private static final String TABLA_ALUMNOS = "alumnos";
	private static final String FICHERO_LOG_GAM = "/home/joaalsai/logGAM";
	private static final String FICHERO_LOG_GAM_MOVIDOS = "/home/joaalsai/logGAM-Movidos";

	private String email;
	private String nombre;
	private String apellido1;
	private String apellido2;
	private String password;
	private JFResultados jpr;
	private int usuariosGenerados = 0;
	private int usuariosMovidos = 0;

	private Connection con = null;

	public GenerarGAM(File f, String table) throws SQLException {

		con = new ConexionMySQL(f).Conectar();

		String query = "";
		Statement stmt = null;
		ResultSet rs = null;
		jpr = new JFResultados();
		jpr.setVisible(true);

		// gam create user <email address> firstname <First Name>
		// lastname <Last Name> password <Password>
		// [suspended on|off] [changepassword on|off]
		// [gal on|off] [sha] [md5] [crypt] [nohash]
		// [org <Org Name>]
		//
		// gam create user droth
		// firstname "David Lee" lastname Roth
		// password 'MightAsWellJump!'

		switch (table) {
		case TABLA_ALUMNOS:
			query = "Select email,nombre,apellido1,apellido2,NIA from " + TABLA_ALUMNOS
					+ " where gam=\"NO\" order by nombre";
			break;
		case TABLA_PROFESORES:
			query = "Select email,nombre,apellido1,apellido2,DNI from " + TABLA_PROFESORES
					+ " where gam=\"NO\" order by nombre";
			break;
		}

		try {

			stmt = con.createStatement();
			rs = stmt.executeQuery(query);

			while (rs.next()) {

				email = rs.getString("email");
				nombre = rs.getString("nombre");
				apellido1 = rs.getString("apellido1");
				apellido2 = rs.getString("apellido2");

				switch (table) {
				case TABLA_ALUMNOS:
					password = String.valueOf(rs.getInt("NIA"));
					break;
				case TABLA_PROFESORES:
					password = rs.getString("DNI");
					break;
				}

				if (existeCuentaGAM(email)) {

					if (table.compareTo(TABLA_ALUMNOS) == 0) {
						moverUsuarioAlumnos(email);
						usuariosMovidos++;
						System.out.println("Moviendo email " + email);
						jpr.añadirTexto("Moviendo email " + email);
					}

				} else {
					añadirUsuarioGAM("pruebas");
					usuariosGenerados++;
				}
				System.out.println("Usuario: " + (usuariosGenerados+usuariosMovidos) + "  Movidos: " + usuariosMovidos + "  Generados: "+usuariosGenerados);
			}
			
			jpr.añadirTexto("Se han creado " + usuariosGenerados + "\n" );
			jpr.añadirTexto("Se han movido " + usuariosMovidos + "\n" );

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stmt != null)
				stmt.close();
			if (rs != null)
				rs.close();
			if (con != null)
				con.close();
		}
	}

	public boolean existeCuentaGAM(String email) throws IOException {

		String[] command = { "/bin/bash", "-c", "/home/joaalsai/bin/gam/gam info user " + email };

		Process pb = Runtime.getRuntime().exec(command);

		String line, salida = null;
		BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()));
		while ((line = input.readLine()) != null) {
			// System.out.println(line);
			salida = salida + line + "\n";
		}
		input.close();

		System.out.println((salida != null) ? "Cuenta encontrada para " + email : "La cuenta " + email + " no existe.");
		jpr.añadirTexto((salida != null) ? "Cuenta encontrada para " + email : "La cuenta " + email + " no existe.");
		return (salida != null) ? true : false;

	}

	public void moverUsuarioAlumnos(String email) throws IOException {

		String[] command = { "/bin/bash", "-c",
				"/home/joaalsai/bin/gam/gam update org pruebas add users " + email + " &>> " + FICHERO_LOG_GAM };

		// System.out.println(command.toString());

		Process pb = Runtime.getRuntime().exec(command);

		String line, salida = null;
		BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()));
		while ((line = input.readLine()) != null) {
			System.out.println(line);
			jpr.añadirTexto(line);
		}

		input.close();
	}

	public void añadirUsuarioGAM(String organizacion) throws IOException {

		String[] command = { "/bin/bash", "-c",
				"/home/joaalsai/bin/gam/gam create user " + email + " firstname \"" + nombre + "\" lastname \""
						+ apellido1 + " " + apellido2 + "\"" + " password '" + password + "'"
						+ " changepassword on org " + organizacion + " &>> " + FICHERO_LOG_GAM_MOVIDOS };

		System.out.println("Añadiendo la cuenta: " + email + " a la organizacion " + organizacion);
		jpr.añadirTexto("Añadiendo la cuenta: " + email + " a la organizacion " + organizacion);

		Process pb = Runtime.getRuntime().exec(command);

		String line, salida = null;
		BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()));
		while ((line = input.readLine()) != null) {
			System.out.println(line);
			salida = salida + line + "\n";
		}
		if (salida != null) {
			System.out.println("Error!");
			jpr.añadirTexto("Error!");
		} else {
			jpr.añadirTexto("Creada!");
			System.out.println("Creada!");
		}
		input.close();
	}

}
