import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Server {

	public static void main(String[] args) throws IOException {

        ServerSocket ss = new ServerSocket(7373); 
          
        while(true){ 
            Socket s = null; 
              
            try { 
                s = ss.accept(); 
                  
                System.out.println("A new client is connected : " + s); 
                  
                DataInputStream dis = new DataInputStream(s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
                
                System.out.println("Assigning new thread for this client"); 
  
                Thread t = new ClientHandler(s, dis, dos);
                dos.writeUTF("Enter the operation you want to do with your file: \n");
                t.start();    
            } 
            catch (Exception e) { 
                s.close(); 
                e.printStackTrace(); 
            } 
        }
	}
}

class ClientHandler extends Thread { 
    
    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s; 
      
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
    } 
  
    public void run() {
    	
        String received;
        String operation[];
        String toreturn;
        
        File folder = new File("D:\\Doða\\Desktop\\Folder");
        
        while(true) { 
            try { 
            	  
                received = dis.readUTF();
                operation = received.split(" ", 2);
                             
                if(received.equals("Exit")) {  
                    System.out.println("Client " + this.s + " sends exit..."); 
                    System.out.println("Closing this connection."); 
                    this.s.close(); 
                    System.out.println("Connection closed"); 
                    break; 
                } 
                  
                switch(operation[0]) {      
                    case "ls":
                    	for(int i=0; i<folder.list().length; i++) {
                    		dos.writeUTF(folder.list()[i]);
                    	}            	
                        break;  
                    case "cd": 
                        folder = new File(operation[1]);
                        dos.writeUTF("New Directory: " + operation[1] + "\n");
                        break;
                    case "mkdir":
                    	File dir = new File(operation[1]);
                    	dir.mkdir();
                    	dos.writeUTF("New Folder created \n");
                    	break;
                    case "pwd":
                    	dos.writeUTF("Directory is: " + folder + "\n");
                    	break;
                    case "rm":
                    	File currentFile = new File(folder.getPath(),operation[1]);
                        currentFile.delete();
                        dos.writeUTF(operation[1] + " is removed \n");
                    	break;
                    case "write":
                    	String[] files = operation[1].split(" ", 2);
                    	Path sourcePath      = Paths.get(files[0]);
                    	Path destinationPath = Paths.get(files[1]);

                    	try {
                    	    Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    	} catch(FileAlreadyExistsException e) {
                    	} catch (IOException e) {
                    	    e.printStackTrace();
                    	}
                    	dos.writeUTF(files[0] + " is replaced with " + files[1] + "\n");
                    	break;
                    case "read":
                    	String[] files2 = operation[1].split(" ", 2);
                    	Path sourcePath2      = Paths.get(files2[0]);
                    	Path destinationPath2 = Paths.get(files2[1]);
                    	try {
                    	    Files.copy(sourcePath2, destinationPath2, StandardCopyOption.REPLACE_EXISTING);
                    	} catch(FileAlreadyExistsException e) {
                    	} catch (IOException e) {
                    	    e.printStackTrace();
                    	}
                    	dos.writeUTF(files2[0] + " is replaced with " + files2[1] + "\n");
                    	break;
                    default: 
                        dos.writeUTF("Invalid input \n"); 
                        break;    
                }
                dos.writeUTF("Enter the operation you want to do with your file: \n");
            } catch (IOException e) { 
                e.printStackTrace(); 
            }
               
        } 
          
        try { 
            this.dis.close(); 
            this.dos.close(); 
              
        } catch(IOException e) { 
            e.printStackTrace(); 
        } 
    } 
}
