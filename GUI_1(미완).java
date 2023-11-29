package Testing_package;

import java.awt.*;
import javax.swing.*;

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GUI_1 {

	private JFrame frame;
	StringBuilder sb = new StringBuilder();
	RSA rsa;
	
	
	//Huffman encoder and decoder
    //HuffmanEncoder encoder = new HuffmanEncoder();
    //HuffmanDecoder decoder = new HuffmanDecoder();
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI_1 window = new GUI_1();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		/*
		 * GUI_1 main = new GUI_1(); main.rsa = new RSA(); main.rsa.setMain(main);
		 */
	}

	public void showFrameTest() {
		rsa.dispose();
	}
	/**
	 * Create the application.
	 */
	public GUI_1() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 711, 605);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		
		JButton btnNewButton = new JButton("INPUT");
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 20));
		
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				
				chooser.showOpenDialog(frame);
				File f= chooser.getSelectedFile();
				
				try(
						BufferedReader in = new BufferedReader(new FileReader(f))){
					String line = in.readLine();
					while(line != null) {
						sb.append(line + "");
						line = in.readLine();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		
		
		btnNewButton.setBounds(263, 175, 186, 69);
		frame.getContentPane().add(btnNewButton);
		
		
		JButton CompBtn = new JButton("Compression");
		CompBtn.setFont(new Font("Tahoma", Font.BOLD, 20));
		CompBtn.setBounds(110, 444, 186, 56);
		frame.getContentPane().add(CompBtn);
		
		JButton DecompBtn = new JButton("Decompression");
		
		DecompBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GUI_1 main = new GUI_1();
				main.rsa = new RSA();
				main.rsa.setMain(main);
				main.setVisible(true);
			}
		});
		
		
		DecompBtn.setFont(new Font("Tahoma", Font.BOLD, 20));
		DecompBtn.setBounds(442, 444, 217, 56);
		frame.getContentPane().add(DecompBtn);
		
		TextField FilePathText = new TextField();
		FilePathText.setBounds(197, 311, 345, 36);
		frame.getContentPane().add(FilePathText);
		
		JLabel lblNewLabel = new JLabel("Huffman & RSA");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 30));
		lblNewLabel.setBounds(263, 28, 296, 36);
		frame.getContentPane().add(lblNewLabel);
		
		
		
	}
	
	protected void setVisible(boolean b) {
		// TODO Auto-generated method stub
		
	}

	
}
