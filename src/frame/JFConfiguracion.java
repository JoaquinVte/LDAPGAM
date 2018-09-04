package frame;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

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

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSeparator;
import java.awt.Color;

public class JFConfiguracion extends JFrame {

	private JPanel contentPane;
	private JTextField tFSeparador;
	private JTextField tFShell;
	private JTextField tFUidProfesor;
	private JTextField tFUidAlumnos;
	private JTextField tFExpiraProfesor;
	private JTextField tFExpiraAlumnos;

	private File fichero;

	private String separador;
	private String shell;
	private String uidProfesor;
	private String gidProfesor;
	private String uidAlumnos;
	private String gidAlumnos;
	private String expiraProfesor;
	private String expiraAlumnos;
	private String host;
	private String puerto;
	private String usuario;
	private String password;
	
	private JLabel lblGidprofesores;
	private JTextField tFGidProfesor;
	private JTextField tFGidAlumnos;
	private JLabel lblHost;
	private JTextField tFHost;
	private JLabel lblUsuario;
	private JTextField tFUsuario;
	private JLabel lblPassword;
	private JTextField tFPassword;
	private JSeparator separator;
	private JLabel lblPuerto;
	private JTextField tFPuerto;

	/**
	 * Create the frame.
	 */
	public JFConfiguracion(File f) {
		setResizable(false);
		this.fichero = f;
		setAlwaysOnTop(true);
		setTitle("Configuracion");

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 408, 446);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JLabel lblCampoSeparadorPara = new JLabel("Campo separador para csv");

		tFSeparador = new JTextField();
		tFSeparador.setColumns(10);

		JLabel lblShell = new JLabel("Shell");

		tFShell = new JTextField();
		tFShell.setColumns(10);

		JLabel lblUidInicialPara = new JLabel("uid inicial para profesores");

		tFUidProfesor = new JTextField();
		tFUidProfesor.setColumns(10);

		JLabel lblUidInicialPara_1 = new JLabel("uid inicial para alumnos");

		tFUidAlumnos = new JTextField();
		tFUidAlumnos.setColumns(10);

		JLabel lblExpiracionaos = new JLabel("Expiracion de la cuenta de profesores (años)");

		tFExpiraProfesor = new JTextField();
		tFExpiraProfesor.setColumns(10);

		JLabel lblExpiracionDeLa = new JLabel("Expiracion de la cuenta de alumnos (años)");

		tFExpiraAlumnos = new JTextField();
		tFExpiraAlumnos.setColumns(10);

		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					guardar();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		lblGidprofesores = new JLabel("gidProfesores");

		tFGidProfesor = new JTextField();

		tFGidProfesor.setColumns(10);

		JLabel lblGidalumnos = new JLabel("gidAlumnos");

		tFGidAlumnos = new JTextField();

		tFGidAlumnos.setColumns(10);
		
		lblHost = new JLabel("host");
		
		tFHost = new JTextField();
		tFHost.setColumns(10);
		
		lblUsuario = new JLabel("usuario");
		
		tFUsuario = new JTextField();
		tFUsuario.setColumns(10);
		
		lblPassword = new JLabel("password");
		
		tFPassword = new JTextField();
		tFPassword.setColumns(10);
		
		separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		
		lblPuerto = new JLabel("puerto");
		
		tFPuerto = new JTextField();
		tFPuerto.setColumns(10);

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblShell)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tFShell, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblCampoSeparadorPara)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tFSeparador, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblUidInicialPara)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tFUidProfesor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblGidprofesores, GroupLayout.PREFERRED_SIZE, 186, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tFGidProfesor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblUidInicialPara_1, GroupLayout.PREFERRED_SIZE, 186, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tFUidAlumnos, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblGidalumnos, GroupLayout.PREFERRED_SIZE, 186, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tFGidAlumnos, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
							.addContainerGap(66, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblExpiracionaos)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tFExpiraProfesor, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblExpiracionDeLa, GroupLayout.PREFERRED_SIZE, 317, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tFExpiraAlumnos, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)))
							.addContainerGap(22, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnGuardar)
							.addGap(148))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblPassword)
								.addComponent(lblUsuario))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(tFUsuario, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE)
								.addComponent(tFPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(182))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblHost)
							.addGap(12)
							.addComponent(tFHost, GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblPuerto)
							.addGap(4)
							.addComponent(tFPuerto, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE)
							.addGap(37))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(separator, GroupLayout.PREFERRED_SIZE, 363, GroupLayout.PREFERRED_SIZE)
							.addContainerGap(15, Short.MAX_VALUE))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCampoSeparadorPara)
						.addComponent(tFSeparador, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addComponent(lblShell)
								.addComponent(tFShell, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(18)
							.addComponent(lblUidInicialPara))
						.addComponent(tFUidProfesor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblGidprofesores)
						.addComponent(tFGidProfesor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUidInicialPara_1)
						.addComponent(tFUidAlumnos, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblGidalumnos)
						.addComponent(tFGidAlumnos, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblExpiracionaos)
						.addComponent(tFExpiraProfesor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblExpiracionDeLa)
						.addComponent(tFExpiraAlumnos, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
					.addGap(4)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblHost)
						.addComponent(tFHost, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPuerto)
						.addComponent(tFPuerto, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUsuario)
						.addComponent(tFUsuario, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(8)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(tFPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(2)
							.addComponent(lblPassword)))
					.addPreferredGap(ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
					.addComponent(btnGuardar)
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);

		try {
			cargar();
			tFSeparador.setText(separador);
			tFGidProfesor.setText(gidProfesor);
			tFShell.setText(shell);
			tFUidProfesor.setText(uidProfesor);
			tFUidAlumnos.setText(uidAlumnos);
			tFExpiraProfesor.setText(expiraProfesor);
			tFExpiraAlumnos.setText(expiraAlumnos);
			tFGidProfesor.setText(gidProfesor);
			tFGidAlumnos.setText(gidAlumnos);
			tFHost.setText(host);
			tFUsuario.setText(usuario);
			tFPassword.setText(password);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void guardar() throws FileNotFoundException, IOException {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fichero)));

			out.writeObject(tFSeparador.getText());
			out.writeObject(tFShell.getText());
			out.writeObject(tFUidProfesor.getText());
			out.writeObject(tFGidProfesor.getText());
			out.writeObject(tFUidAlumnos.getText());
			out.writeObject(tFGidAlumnos.getText());
			out.writeObject(tFExpiraProfesor.getText());
			out.writeObject(tFExpiraAlumnos.getText());
			
			out.writeObject(tFHost.getText());
			out.writeObject(tFPuerto.getText());
			out.writeObject(tFUsuario.getText());
			out.writeObject(tFPassword.getText());
			
			JOptionPane.showMessageDialog(null, "Configuracion guardada", "Informacion",JOptionPane.INFORMATION_MESSAGE);
			this.dispose();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}

	public void cargar() throws FileNotFoundException, IOException {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fichero)));

			this.separador = (String) in.readObject();
			this.shell = (String) in.readObject();
			this.uidProfesor = (String) in.readObject();
			this.gidProfesor = (String) in.readObject();
			this.uidAlumnos = (String) in.readObject();
			this.gidAlumnos = (String) in.readObject();
			this.expiraProfesor = (String) in.readObject();
			this.expiraAlumnos = (String) in.readObject();
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
