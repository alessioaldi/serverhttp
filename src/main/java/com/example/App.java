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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import main.java.com.example.Classe;

/**
 * Hello world!
 *
 */
public class App 
{

    private static void creaClasse(){
        ObjectMapper objectmapper= new ObjectMapper();
        Alunno a1= new Alunno("alessio","aldinucci","5dia");
        Alunno a2= new Alunno("alessandro","Scrobobebons","5dia");

        Classe classe=new classe();
        classe.registro.add(a1);
        classe.registro.add(a2);

        objectmapper.writeValue(new File("/root/classe.json"), classe);
    }

    private static void sendBinaryFile(Socket socket, String path){

        if(path.endsWith("/")){
            path = path + "index.html";
        } if(path.equals("classe.json")){
            creaClasse();
        } 


        try{

            File file = new File("./root" + path);
            InputStream in = new FileInputStream(file);
            
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeBytes("HTTP/1.1 200 OK\n" );
            out.writeBytes("Content-Length: " + file.length()+ "\n");
            out.writeBytes("Server: Java HTTP Server from Taiti: 1.0\n");
            out.writeBytes("Date: " + new Date()+ "\n");
            out.writeBytes("Content-Type: " + getContentType(path) + "\n");
            out.writeBytes("\n");

            byte[] buf = new byte[8192];
            int n;
            while((n = in.read(buf)) != -1){
                out.write(buf, 0, n);
                System.out.println(buf);             
            } 
            out.close();
            in.close();
        }catch(FileNotFoundException ntF){
            try{
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                if(getContentType(path).equals("text/plain charset=utf-8\n")){
                    out.writeBytes("HTTP/1.1 301 Move Permanently\n" );
                    out.writeBytes("location: " + path+"/");
                }
                else
                    out.writeBytes("HTTP/1.1 404 not found");

            }catch(IOException e){
                System.out.println("IOexception");
            }
        }catch(IOException e){
            System.out.println("IOexception");
        }
           
    }

    private static String getContentType(String path){
        String type = "text/plain charset=utf-8\n";
        try{
            type = path.split("\\.")[1];
            System.out.println("----------------------- "+ type);
            switch (type) {
                case "html":
                case "css":
                    type = "text/" + type+ "; charset=utf-8\n";
                    break;
                case "jpg":
                case "png":
                case "jpeg": 
                    type = "image/" + type;
                    break;
                case "js":    
                    type = "application/" + type;
                    break;
                case "json":
                    type = "application/" + type;
            }
        }catch(IndexOutOfBoundsException inxU){
            System.out.println(type);
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
                System.out.println(richiesta);
                String[] riga = richiesta.split(" ");
                String path = riga[1];
                
                
                
                System.out.println("--" + path + "--");

                do{
                    richiesta = in.readLine();
                    System.out.println(richiesta);
                    if(richiesta.isEmpty() || richiesta.equals(null)) break;
                }while(true);

                sendBinaryFile(client, path);
                
                out.flush();
                client.close();
            }          

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
