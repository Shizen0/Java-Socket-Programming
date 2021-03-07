import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Client extends JFrame {
	
	private static JTextArea operation;
	private static JTextArea information;
	private static JScrollPane scroll;
	
	static DataInputStream dis;
	static DataOutputStream dos;
	static Socket s;
	
	Client() {
		operation = new JTextArea();
		information = new JTextArea();
		
		information.setBounds(50, 50, 480, 300);
		operation.setBounds(50, 400, 480, 100);
		
		operation.setBorder(BorderFactory.createLineBorder(new Color(50,50,50), 7));
		operation.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		
		information.setEditable(false);
		information.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		
		scroll = new JScrollPane(information);
		scroll.setBounds(50, 50, 480, 300);
		scroll.setBorder(BorderFactory.createLineBorder(new Color(50,50,50), 7));
		
		operation.addKeyListener(new connectToServer());
		
		try {           
            InetAddress ip = InetAddress.getByName("localhost"); 
            
            s = new Socket(ip, 7373); 
      
            dis = new DataInputStream(s.getInputStream()); 
            dos = new DataOutputStream(s.getOutputStream());
            information.setText(information.getText() + dis.readUTF());
            
        } catch(Exception e) { 
            e.printStackTrace(); 
        }
		
		this.setLayout(null);
		
		this.add(scroll);
		this.add(operation);
		
		this.setVisible(true);
		this.setSize(600, 600);
		
		this.getContentPane().setBackground(new Color(120,120,120));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	class connectToServer implements KeyListener {		
		public void keyPressed(KeyEvent key) {
			int keyCode = key.getKeyCode();
			if(keyCode == KeyEvent.VK_ENTER ) {	
				try {
					doOperation();
				} catch (IOException e) {
					e.printStackTrace();
				}
				operation.setText("");
			}	
		}

		public void keyReleased(KeyEvent k) {
		}
		public void keyTyped(KeyEvent k) {
		}
	}
	
	public static void main(String[] args) {	
		new Client();
    }
	
	public static void doOperation() throws IOException {
    	String tosend = operation.getText();
    		
        dos.writeUTF(tosend); 
        String received = dis.readUTF();
                  
        if(tosend.equals("Exit")) { 
        	information.setText(information.getText() + "Closing this connection: \n" + s); 
        	s.close(); 
        	information.setText(information.getText() + "Connection closed \n");
        	dis.close(); 
            dos.close();
        } 
        information.setText(information.getText() + received);
        information.setText(information.getText() + dis.readUTF());
	}	
}