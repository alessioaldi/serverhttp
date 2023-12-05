package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private static void sendFile(PrintWriter out, String path){
        try{
            File myObj = new File("."+path);
            Scanner myReader = new Scanner(myObj);
            
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Length: " + myObj.length());
            out.println("Server: Java HTTP Server from Benve: 1.0");
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
            out.println("HTTP/1.1 404 OK");      
        }
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
                System.out.println("--" + path + "--");

                do{
                    richiesta = in.readLine();
                    System.out.println(richiesta);
                    if(richiesta.isEmpty() || richiesta.equals(null)) break;
                }while(true);


                sendFile(out, path);
                out.flush();
                client.close();
            }

            // server.close();
            

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
