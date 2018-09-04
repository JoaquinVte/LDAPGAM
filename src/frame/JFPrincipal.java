package frame;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.FlowLayout;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;

import tools.*;
import tools.CargarCSV;

public class JFPrincipal extends JFrame {

	private JPanel contentPane;
	private JFrame frame;
	
	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	private File ficheroConfiguracion;
	private FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivo de csv", "csv");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFPrincipal frame = new JFPrincipal();
					frame.setVisible(true);
					frame.setFrame(frame);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public JFPrincipal() {
		setTitle("Ventana principal");
		setResizable(false);

		this.ficheroConfiguracion = new File("./configuracion");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 341, 643);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JButton btnGenerarGamAlumnos = new JButton("Generar GAM Alumnos");
		btnGenerarGamAlumnos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fch = new JFileChooser();

				fch.setFileFilter(filter);

				int opcion = fch.showOpenDialog(null);

				// Si hacemos click
				if (opcion == JFileChooser.APPROVE_OPTION) {
					try {
						// Obtenenemos el nombre del fichero seleccionado
						
						CSVtoLDAPAlumnos ctl = new CSVtoLDAPAlumnos(fch.getSelectedFile(),ficheroConfiguracion,"alumno");

					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		JButton btnGenerarGamProfesores = new JButton("Generar GAM Profesores");
		btnGenerarGamProfesores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		JButton btnGenerarLDAPSambaAlumnos = new JButton("Generar LDAP/SAMBA Alumnos");

		JButton btnGenerarLDAPSambaProfesores = new JButton("Generar LDAP/SAMBA Profesores");
		btnGenerarLDAPSambaProfesores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fch = new JFileChooser();

				fch.setFileFilter(filter);

				int opcion = fch.showOpenDialog(null);

				// Si hacemos click
				if (opcion == JFileChooser.APPROVE_OPTION) {
					try {
						// Obtenenemos el nombre del fichero seleccionado
						
						CSVtoLDAPProfesores ctl = new CSVtoLDAPProfesores(fch.getSelectedFile(),ficheroConfiguracion,"profesor");

					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		JButton btnConfiguracion = new JButton("Configuracion");
		btnConfiguracion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFConfiguracion jfc = new JFConfiguracion(ficheroConfiguracion);
				jfc.setVisible(true);
			}
		});
		
		JButton btnCargarcsvAlumnos = new JButton("Cargar .csv Alumnos en BBDD");
		btnCargarcsvAlumnos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
					try{	
						CargarCSV cCSV = new CargarCSV(ficheroConfiguracion,"alumnos");
					}catch (Exception ex){
						ex.printStackTrace();
					}
				
				
			}
		});
		
		JButton btnCargarcsvPorfesores = new JButton("Cargar .csv  Porfesores en BBDD");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnCargarcsvPorfesores, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnCargarcsvAlumnos, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnGenerarGamProfesores, GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnGenerarGamAlumnos, GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnGenerarLDAPSambaProfesores, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnGenerarLDAPSambaAlumnos, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnConfiguracion, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addComponent(btnCargarcsvAlumnos, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
					.addGap(12)
					.addComponent(btnCargarcsvPorfesores, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnGenerarGamProfesores, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnGenerarGamAlumnos, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnGenerarLDAPSambaProfesores, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnGenerarLDAPSambaAlumnos, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
					.addGap(29)
					.addComponent(btnConfiguracion, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(67, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}

}