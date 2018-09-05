package tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import frame.JFResultados;

public class CargarCSV {

	private static final String TABLA_ALUMNOS = "alumnos";
	private static final String TABLA_PROFESORES = "profesores";
	File ficheroCSV;
	File ficheroConf;
	private String csvFile = "";
	private String lineCSV = "";
	private String filaBatch = "";

	private int NIA;
	private String DNI;

	private String username;
	private String passwd;
	private int uid;
	private int gid;
	private String nombre;
	private String apellido1;
	private String apellido2;
	private String curso;
	private String grupo;
	private int registroActualizados=0;
	
	private String id;

	private String homedir;
	private String shell = "/bin/bash";
	private String cvsSplitBy;
	private Date expira;
	private String expiraProfesor;
	private String expiraAlumnos;

	public CargarCSV(File fconf, String tipoUsuario) throws IOException {

		this.ficheroConf = fconf;

		cargarConfiguracion(tipoUsuario);

		JFileChooser fcsv = new JFileChooser();

		int opcion = fcsv.showOpenDialog(null);

		// Si hacemos click
		if (opcion == JFileChooser.APPROVE_OPTION) {
			try {
				// Obtenenemos el nombre del fichero seleccionado
				actualizarBBDD(fcsv.getSelectedFile(), fconf, tipoUsuario);
				JOptionPane.showMessageDialog(null, "BBDD actualizada ", "Informacion",
						JOptionPane.INFORMATION_MESSAGE);

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}

	public void cargarConfiguracion(String tipoUsuario) throws FileNotFoundException, IOException {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(ficheroConf)));

			this.cvsSplitBy = (String) in.readObject();
			this.shell = (String) in.readObject();
			if (tipoUsuario.compareTo(TABLA_ALUMNOS) == 0) {

				in.readObject();
				in.readObject();
				this.uid = Integer.parseInt((String) in.readObject());
				this.gid = Integer.parseInt((String) in.readObject());

			} else if (tipoUsuario.compareTo(TABLA_PROFESORES) == 0) {
				this.uid = Integer.parseInt((String) in.readObject());
				this.gid = Integer.parseInt((String) in.readObject());
				in.readObject();
				in.readObject();
			}
			this.expiraProfesor = (String) in.readObject();
			this.expiraAlumnos = (String) in.readObject();

		} catch (EOFException e) {
			System.out.println(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			in.close();
		}
	}

	public void actualizarBBDD(File fcsv, File fconf, String tabla) throws SQLException {

		JFResultados jfr = new JFResultados();
		jfr.setVisible(true);

		String[] datos;
		String resultado = "";

		BufferedReader br = null;
		int i = 0;
		int j;
		String usernamePrueba;

		Connection con = null;

		try {

			con = new ConexionMySQL(fconf).Conectar();

			int maxUidBBDD = actualizarUID(con, tabla);

			this.uid = (maxUidBBDD > this.uid) ? maxUidBBDD : this.uid;

			try {

				br = new BufferedReader(new InputStreamReader(new FileInputStream(fcsv), "ISO-8859-1"));
				lineCSV = br.readLine();
				// eliminamos cabecera
				lineCSV.split(cvsSplitBy);
				
				java.util.Date fecha;

				String primeraLetraSegundoApellido;

				while ((lineCSV = br.readLine()) != null) {

					// usamos cvsSplitBy como separador
					datos = lineCSV.split(cvsSplitBy);
					
					// Eliminamos las primeras filas del archivos profesores si es el caso
					if(tabla.compareTo(TABLA_PROFESORES)==0){
						while((datos[0].replaceAll("\"", "")).compareTo("Docente")!=0 && lineCSV!=null){
							lineCSV = br.readLine();
							datos = lineCSV.split(cvsSplitBy);
						}
					}
					

					if (tabla.compareTo(TABLA_ALUMNOS) == 0)
						NIA = Integer.parseInt(datos[0].replaceAll("\"", ""));
					if (tabla.compareTo(TABLA_PROFESORES) == 0)
						DNI = datos[1].replaceAll("\"", "");
					
					id = (tabla.compareTo(TABLA_ALUMNOS) == 0)? String.valueOf(NIA):DNI;

					// Comporbamos si ya esta dado de alta el usuario en la BBDD
					if (!existeUsuario(con, tabla, id)) {

						primeraLetraSegundoApellido = (datos[3].compareTo("") == 0) ? ""
								: String.valueOf(datos[3].replaceAll("\"", "").charAt(0));
						
						
						switch(tabla){
						case TABLA_ALUMNOS:
							username = limpiarCaracteres(
									(datos[1].replaceAll("\"", "").charAt(0) + datos[2].replaceAll("\"", "")
											+ primeraLetraSegundoApellido).toLowerCase().replace(" ", "")).toLowerCase();
							break;
						case TABLA_PROFESORES:
							username = limpiarCaracteres((datos[2].replaceAll("\"", "").charAt(0) + datos[3].replaceAll("\"", ""))).toLowerCase();
							break;
						}
						

						// Comprobamos que no haya otro username igual

						if (existeLogin(con, tabla, username)) {
							j = 1;

							while (existeLogin(con, tabla, username + j)) {
								j++;
							}
							username = username + j;
						}
						
						switch (tabla) {
						case TABLA_ALUMNOS:
							passwd = datos[0].replaceAll("\"", "");
							nombre = datos[1].replaceAll("\"", "");
							apellido1 = datos[2].replaceAll("\"", "");
							apellido2 = datos[3].replaceAll("\"", "");
							curso = datos[11].replaceAll("\"", "");
							grupo = datos[12].replaceAll("\"", "");
							expira = obternerFechaExpiracion(Integer.parseInt(expiraAlumnos));							
							break;
						case TABLA_PROFESORES:							
							nombre = datos[2].replaceAll("\"", "");
							apellido1 = datos[3].replaceAll("\"", "");
							apellido2 = datos[4].replaceAll("\"", "");
							expira = obternerFechaExpiracion(Integer.parseInt(expiraProfesor));
							
							break;
						}
						
						
						insertar(con, tabla);
						resultado = resultado + " Insertado el usuario " + nombre + " " + apellido1 + " " + apellido2
								+ " con login " + username + "\n";
						i++;
						uid++;
					} else {
						if (tabla.compareTo(TABLA_ALUMNOS)==0)actualizarExpiracion(con, tabla, NIA);
						if (tabla.compareTo(TABLA_PROFESORES)==0)actualizarExpiracion(con, tabla, DNI);
					}

				}
				resultado = resultado + "Se han insertado " + i + " usuarios nuevos \n" +
										"Se han actualizado " + registroActualizados +" registros.";
				
				jfr.añadirTexto(resultado);

			} catch (IOException e) {
				e.printStackTrace();
			}

		} finally {
			if (con != null)
				con.close();
		}

	}

	public void actualizarExpiracion(Connection c, String table, int id) throws SQLException {

		String query = "";

		query = "update alumnos set expira = ? where NIA = ?";

		PreparedStatement preparedStmt = c.prepareStatement(query);

		preparedStmt.setDate(1, expira);
		preparedStmt.setInt(2, id);

		preparedStmt.execute();
		registroActualizados++;

	}

	public void actualizarExpiracion(Connection c, String table, String id) throws SQLException {

		String query = "";

		query = "update profesores set expira = ? where DNI = ?";

		PreparedStatement preparedStmt = c.prepareStatement(query);

		java.sql.Date ex = obternerFechaExpiracion(Integer.parseInt(expiraProfesor));

		preparedStmt.setDate(1, ex);
		preparedStmt.setString(2, id);

		preparedStmt.execute();
		registroActualizados++;

	}

	public String generarPassword(String dni) {
		String pass = dni.substring(dni.length() - 5, dni.length() - 1);
		pass = pass + dni.charAt(dni.length() - 1) + dni.charAt(dni.length() - 1);
		pass = pass + (int) (Math.random() * 1000) + 1;
		return pass;
	}

	public java.sql.Date sumarAnyos(java.sql.Date fecha, int anyos) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fecha);
		calendar.add(Calendar.YEAR, anyos);
		return new java.sql.Date(calendar.getTimeInMillis());
	}

	public java.sql.Date obternerFechaExpiracion(int anyos) {
		java.util.Date f = new java.util.Date();
		java.sql.Date ex = new java.sql.Date(f.getTime());
		ex = sumarAnyos(ex, anyos);
		return ex;
	}

	public void insertar(Connection c, String table) throws SQLException {

		String query = "";

		switch (table) {
		case TABLA_ALUMNOS:
			query = "insert into alumnos (NIA, nombre, apellido1, apellido2, email,username,expira,curso,grupo,ldap,gam,uid,password)"
					+ " values (?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?,?)";
			break;
		case TABLA_PROFESORES:
			query = "insert into profesores (DNI, nombre, apellido1, apellido2, email,username,expira,especialidad,ldap,gam,uid)"
					+ " values (?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?)";
			break;
		}

		PreparedStatement preparedStmt = c.prepareStatement(query);

		switch (table) {
		case TABLA_ALUMNOS:
			preparedStmt.setInt(1, NIA);
			preparedStmt.setString(2, nombre);
			preparedStmt.setString(3, apellido1);
			preparedStmt.setString(4, apellido2);
			preparedStmt.setString(5, username + "@ieslavereda.es");
			preparedStmt.setString(6, username);
			preparedStmt.setDate(7, expira);
			preparedStmt.setString(8, curso);
			preparedStmt.setString(9, grupo);
			preparedStmt.setString(10, "NO");
			preparedStmt.setString(11, "NO");
			preparedStmt.setInt(12, uid);
			preparedStmt.setInt(13, NIA);
			break;
		case TABLA_PROFESORES:
			preparedStmt.setString(1, DNI);
			preparedStmt.setString(2, nombre);
			preparedStmt.setString(3, apellido1);
			preparedStmt.setString(4, apellido2);
			preparedStmt.setString(5, username + "@ieslavereda.es");
			preparedStmt.setString(6, username);
			preparedStmt.setDate(7, expira);
			preparedStmt.setString(8, "");
			preparedStmt.setString(9, "NO");
			preparedStmt.setString(10, "NO");
			preparedStmt.setInt(11, uid);
			break;
		}

		preparedStmt.execute();

	}

	public boolean existeLogin(Connection c, String tabla, String username) throws SQLException {

		PreparedStatement ps = null;
		String query = "";
		ResultSet rs = null;
		int rowCount;

		query = "select count(*) from " + tabla + " where username= ?";

		try {
			ps = c.prepareStatement(query);

			ps.setString(1, username);

			rs = ps.executeQuery();

			rs.next();
			rowCount = rs.getInt(1);

		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
		return (rowCount == 1) ? true : false;

	}

	public String limpiarCaracteres(String s) {
		String texto = s;
		texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
		texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		texto = texto.replaceAll("ñ", "ny");
		return texto;
	}

	public boolean existeUsuario(Connection con, String table, String id) throws SQLException {

		Statement stmt = null;
		String query = "";
		ResultSet rs = null;
		int rowCount;

		switch (table) {
		case TABLA_ALUMNOS:
			query = "select count(*) " + "from " + table + " where NIA=" + id;
			break;
		case TABLA_PROFESORES:
			query = "select count(*) " + "from " + table + " where DNI=\"" + id + "\"";
			break;
		}

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);

			rs.next();
			rowCount = rs.getInt(1);

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
		return (rowCount != 0) ? true : false;
	}
	
	
	
	

	public int actualizarUID(Connection con, String table) throws SQLException {

		Statement stmt = null;
		String query = "select max(uid) " + "from " + table;
		ResultSet rs = null;
		int maxUID;

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);

			rs.next();
			maxUID = rs.getInt(1);

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
		return maxUID + 1;
	}

}
