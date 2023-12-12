package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    /*private static void sendFile(PrintWriter out, String path){
        try{
            File myObj = new File("."+path);
            Scanner myReader = new Scanner(myObj);
            
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Length: " + myObj.length());
            out.println("Server: Java HTTP Server from Taiti: 1.0");
            out.println("Date: " + new Date());
            out.println("Content-Type: text/html; charset=utf-8");

            out.println();

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                out.println(data);
                System.out.println(data);
            }
            myReader.close();

        }catch (FileNotFoundException e) {
            out.println("HTTP/1.1 404 NOT FOUND");      
        }
    }*/

    private static void sendBinaryFile(Socket socket, String path) {
        
        try{
            File file = new File("." + path);
            InputStream in = new FileInputStream(file);
            
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeBytes("HTTP/1.1 200 OK\n" );
            out.writeBytes("Content-Length: " + file.length()+ "\n");
            out.writeBytes("Server: Java HTTP Server from Taiti: 1.0\n");
            out.writeBytes("Date: " + new Date()+ "\n");
            out.writeBytes("Content-Type: " + getContentType(path) );
            out.writeBytes("\n");

            byte[] buf = new byte[8192];
            int n;
            while((n = in.read(buf)) != -1){
                out.write(buf, 0, n);
                System.out.println(buf);             
            } 
            out.close();
            in.close();
        }
        catch(Exception e){
            try{
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeBytes("HTTP/1.1 404 Not found\n");
            }catch(Exception ex){

            }
        }
           
    }

    private static String getContentType(String path){
        String type = path.split("\\.")[1];
        switch (type) {
            case "html":
                type = "text/" + type+ "; charset=utf-8\n";
                break;
            case "jpg":
            case "png":
            case "jpeg": 
                type = "image/" + type;
                break;
            case "css":
                type = "style/" + type;
                break;
            case "js":    
                type = "application/" + type;
            default:
                type = "text"+ "; charset=utf-8\n";
                break;
        }
        return type;
    }

    public static void main( String[] args )
    {
        
        try {
            ServerSocket server = new ServerSocket(8080);
            while(true){
                Socket client = server.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream());

                String richiesta = "";

                richiesta = in.readLine();
                String[] riga = richiesta.split(" ");
                String path = riga[1];
                path = "/root"+ path;
                System.out.println("--" + path + "--");

                do{
                    richiesta = in.readLine();
                    System.out.println(richiesta);
                    if(richiesta.isEmpty() || richiesta.equals(null)) break;
                }while(true);


                try{
                    sendBinaryFile(client, path);
                }
                catch(Exception e){
                    out.println("HTTP/1.1 404 not found");
                }
                out.flush();
                client.close();
            }          

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
