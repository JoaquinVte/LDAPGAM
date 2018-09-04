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
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import frame.JFResultados;

public class CSVtoLDAPAlumnos {

	private String csvFile = "";
	private String lineCSV = "";
	private String filaBatch = "";

	private String username;
	private String passwd;
	private int uid;
	private int gid;
	private String realname;
	private String homedir;
	private String shell = "/bin/bash";
	private String min = "";
	private String max = "";
	private String warn = "";
	private String inactive = "";
	private String expire = "";
	private String[] cabeceraCSV;
	private String[] datos;
	private String cvsSplitBy;
	private File ficheroCSV;
	private File ficheroConf;
	private String expiraProfesor;
	private String expiraAlumnos;

	public CSVtoLDAPAlumnos(File fcsv, File fconf, String tipoUsuario) throws IOException {

		this.ficheroCSV = fcsv;
		this.ficheroConf = fconf;
		this.csvFile = fcsv.getAbsolutePath();

		cargarConfiguracion(tipoUsuario);

		System.out.println(toString());

		JFileChooser fch = new JFileChooser();

		int opcion = fch.showOpenDialog(null);

		// Si hacemos click
		if (opcion == JFileChooser.APPROVE_OPTION) {
			try {
				// Obtenenemos el nombre del fichero seleccionado

				generarBachFile(fch.getSelectedFile());
				JOptionPane.showMessageDialog(null, "Archivo generado en: " + fch.getSelectedFile().getAbsolutePath(),
						"Informacion", JOptionPane.INFORMATION_MESSAGE);

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}

	public void generarBachFile(File ficheroSalida) throws IOException {

		JFResultados jfr = new JFResultados();
		jfr.setVisible(true);
		// create:prueba:1003:/home/prueba:Alonso:jprueba:1112:/bin/bash:1234:1234
		String resultado = "";
		PrintWriter os = null;
		BufferedReader br = null;
		int i=1;

		try {
			os = new PrintWriter(new FileWriter(ficheroSalida));

			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(csvFile)), "ISO-8859-1"));
				lineCSV = br.readLine();
				cabeceraCSV = lineCSV.split(cvsSplitBy);

				while ((lineCSV = br.readLine()) != null) {

					// usamos cvsSplitBy como separador
					datos = lineCSV.split(cvsSplitBy);

					username = (datos[2].replaceAll("\"", "").charAt(0) + datos[3].replaceAll("\"", "")).toLowerCase().replace(" ", "");
					passwd = generarPassword(datos[1].replaceAll("\"", ""));
					realname = datos[2].replaceAll("\"", "") + " " + datos[3].replaceAll("\"", "") + " "
							+ datos[4].replaceAll("\"", "");
					homedir = "/home/" + username;

					// create:username:passwd:uid:gid:realname:homedir:shell:min:max:warn:inactive:expire
					if (datos[0].replaceAll("\"", "").toLowerCase().compareTo("docente") == 0) {
						// gid = gidProfesor;
						filaBatch = "create:" + username + ":" + passwd + ":" + uid + ":" + gid + ":" + realname + ":"
								+ homedir + ":" + shell + ":" + min + ":" + max + ":" + warn + ":" + inactive + ":"
								+ expire;
						os.println(filaBatch.replaceAll("ñ", "ny"));
						resultado = resultado + filaBatch.replaceAll("ñ", "ny") + "\n";
						i++;
					}
					uid++;
				}
				resultado= resultado + "Se ha generado un archivo con " + i + " usuarios nuevos";
				jfr.añadirTexto(resultado);

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				br.close();
			}

		} finally {
			os.close();
		}
	}

	public void cargarConfiguracion(String tipoUsuario) throws FileNotFoundException, IOException {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(ficheroConf)));

			this.cvsSplitBy = (String) in.readObject();
			this.shell = (String) in.readObject();
			if (tipoUsuario.compareTo("alumno") == 0) {

				in.readObject();
				in.readObject();
				this.uid = Integer.parseInt((String) in.readObject());
				this.gid = Integer.parseInt((String) in.readObject());

			} else if (tipoUsuario.compareTo("profesor") == 0) {
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

	public String generarPassword(String dni) {
		String pass = dni.substring(dni.length() - 5, dni.length() - 1);
		pass = pass + dni.charAt(dni.length() - 1) + dni.charAt(dni.length() - 1);
		pass = pass + (int) (Math.random() * 1000) + 1;
		return pass;
	}

	@Override
	public String toString() {
		return "CSVtoLDAP [csvFile=" + csvFile + ", lineCSV=" + lineCSV + ", filaBatch=" + filaBatch + ", username="
				+ username + ", passwd=" + passwd + ", uid=" + uid + ", gid=" + gid + ", realname=" + realname
				+ ", homedir=" + homedir + ", shell=" + shell + ", min=" + min + ", max=" + max + ", warn=" + warn
				+ ", inactive=" + inactive + ", expire=" + expire + ", cabeceraCSV=" + Arrays.toString(cabeceraCSV)
				+ ", datos=" + Arrays.toString(datos) + ", cvsSplitBy=" + cvsSplitBy + ", ficheroCSV=" + ficheroCSV
				+ ", ficheroConf=" + ficheroConf + ", expiraProfesor=" + expiraProfesor + ", expiraAlumnos="
				+ expiraAlumnos + ", gidProfesor=" + "]";
	}

}
