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

	File ficheroCSV;
	File ficheroConf;
	private String csvFile = "";
	private String lineCSV = "";
	private String filaBatch = "";
	
	private int id;

	private String username;
	private String passwd;
	private int uid;
	private int gid;
	private String nombre;
	private String apellido1;
	private String apellido2;
	private String curso;
	private String grupo;

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
			if (tipoUsuario.compareTo("alumnos") == 0) {

				in.readObject();
				in.readObject();
				this.uid = Integer.parseInt((String) in.readObject());
				this.gid = Integer.parseInt((String) in.readObject());

			} else if (tipoUsuario.compareTo("profesores") == 0) {
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

					// Comporbamos si ya esta dado de alta el usuario en la BBDD
					if (!existeUsuario(con, tabla, datos[0])) {
						
						primeraLetraSegundoApellido = (datos[3].compareTo("")==0)?"":String.valueOf(datos[3].replaceAll("\"", "").charAt(0));
						username = limpiarCaracteres(
								(datos[1].replaceAll("\"", "").charAt(0) + datos[2].replaceAll("\"", "") + primeraLetraSegundoApellido ).toLowerCase().replace(" ", ""));
						
						// Comprobamos que no haya otro username igual
						
						if(existeLogin(con,tabla,username)){
							j=1;
		
							while(existeLogin(con,tabla,username + j)){
								j++;
							}
							username=username+j;							
						}
						
						passwd = datos[0].replaceAll("\"", "");
						
						nombre = datos[1].replaceAll("\"", "");
						apellido1 = datos[2].replaceAll("\"", "");
						apellido2 = datos[3].replaceAll("\"", "");
						curso = datos[11].replaceAll("\"", "");
						grupo = datos[12].replaceAll("\"", "");
						id = Integer.parseInt(datos[0].replaceAll("\"", ""));						
						homedir = "/home/" + username;
						
						fecha = new java.util.Date();						
						expira = new java.sql.Date(fecha.getTime());						
						expira = sumarAnyos(expira,2);
						
						insertar(con, tabla);
						resultado = resultado + " Insertado el usuario " + nombre + " " + apellido1 + " " + apellido2 + " con login " + username + "\n";  
						i++;
						uid++;
					}

				}
				resultado = resultado + "Se han insertado " + i + " usuarios nuevos";
				jfr.añadirTexto(resultado);

			} catch (IOException e) {
				e.printStackTrace();
			}

		} finally {
			if (con != null)
				con.close();
		}

	}

	public String generarPassword(String dni) {
		String pass = dni.substring(dni.length() - 5, dni.length() - 1);
		pass = pass + dni.charAt(dni.length() - 1) + dni.charAt(dni.length() - 1);
		pass = pass + (int) (Math.random() * 1000) + 1;
		return pass;
	}
	
	public java.sql.Date sumarAnyos(java.sql.Date fecha, int anyos){			
		      Calendar calendar = Calendar.getInstance();			
		      calendar.setTime(fecha); 		
		      calendar.add(Calendar.YEAR, anyos);  	
		      return new java.sql.Date(calendar.getTimeInMillis()); 	
		 }

	public void insertar(Connection c, String table) throws SQLException {

		String query = "";

		switch (table) {
		case "alumnos":
			query = "insert into alumnos (NIA, nombre, apellido1, apellido2, email,username,expira,curso,grupo,ldap,gam,uid,password)"
					+ " values (?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?,?)";
			break;
		case "profesores":
			query = "insert into profesores (DNI, nombre, apellido1, apellido2, email,username,expira,especialidad,ldap,gam,uid)"
					+ " values (?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?)";
			break;
		}

		PreparedStatement preparedStmt = c.prepareStatement(query);
		
		switch (table) {
		case "alumnos":
			preparedStmt.setInt(1, id);
			preparedStmt.setString(2, nombre);
			preparedStmt.setString(3, apellido1);
			preparedStmt.setString(4, apellido2);
			preparedStmt.setString(5, username+"@ieslavereda.es");
			preparedStmt.setString(6, username);
			preparedStmt.setDate(7, expira);
			preparedStmt.setString(8, curso);
			preparedStmt.setString(9, grupo);
			preparedStmt.setString(10, "NO");
			preparedStmt.setString(11, "NO");
			preparedStmt.setInt(12, uid);
			preparedStmt.setInt(13, id);
			break;
		case "profesores":
			preparedStmt.setInt(1, id);
			preparedStmt.setString(2, nombre);
			preparedStmt.setString(3, apellido1);
			preparedStmt.setString(4, apellido2);
			preparedStmt.setString(5, username+"@ieslavereda.es");
			preparedStmt.setString(6, username);
			preparedStmt.setString(7, expiraAlumnos);
			preparedStmt.setString(8, curso);
			preparedStmt.setString(9, grupo);
			preparedStmt.setString(10, "NO");
			preparedStmt.setString(11, "NO");
			preparedStmt.setInt(12, uid);
			break;
		}

		

		
		preparedStmt.execute();

	}
	
	public boolean existeLogin(Connection c,String tabla,String username) throws SQLException{
		
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
		case "alumnos":
			query = "select count(*) " + "from " + table + " where NIA=" + id;
			break;
		case "profesores":
			query = "select count(*) " + "from " + table + " where DNI=" + id;
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
