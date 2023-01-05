package programmieraufgaben;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    //Diese Variable gibt den Socket an an dem die Verbindung aufgabaut werden soll
    private Socket clientSocket;

    //Hier werden die Verbindungsinformationen abgefragt und eine Verbindung eingerichtet.
    public void connect() {
        Scanner input = new Scanner(System.in);
        int port = 0;
        System.out.println("Mit welchem Server wollen Sie sich verbinden?\n");
        System.out.print("IP-Adresse: ");
        String host = input.nextLine();
        if( !host.equals("127.0.0.1") && !host.equals("localhost") ){
            System.out.println("\nFalsche IP-Adresse! Aktuell ist nur die IPv4-Adresse 127.0.0.1 und die Eingabe localhost möglich.");
            return;
        }
        System.out.print("Port: ");
        try{
            port = Integer.parseInt(input.nextLine());
            if( port != 8080 ){
                System.out.println("\nKein korrekter Port! Aktuell ist nur Port 2020 möglich.\n");
                return;
            }
        } catch (Exception e){
            System.out.println("\nKein korrekter Port! Aktuell ist nur Port 2020 möglich.\n");
            return;
        }
        try {
            //clientSocket = new Socket(host, port);
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(host, port));

            System.out.println("\nEine TCP-Verbindung zum Server mit IP-Adresse " + host + " (Port "+ port + ") wurde hergestellt. Sie können nun Ihre Anfragen an den Server stellen.\n");
        } catch (IOException e) {
            System.out.println("\nFehler beim Verbindungsaufbau! Es konnte keine TCP-Verbindung zum Server mit IP-Adresse " + host + " (Port " + port + ") hergestellt werden.\n");
        }
    }
    //Hier wird die Verbindung und alle Streams geschlossen
    public void disconnect() {
        try {
            clientSocket.close();
            clientSocket = null;
            System.out.println("Die Verbindung zum Server wurde beendet.\n");
        } catch (IOException e) {
            System.out.println("Ein Fehler ist aufgetreten!");
        }
    }
    //In dieser Methode werden die Eingaben des Benutzers an den Server gesendet und die Antwort empfangen
    public String request(String userInput) {
        String toPrint = "";
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

            out.println(userInput); //flush passiert automatisch
            toPrint = in.readLine();

        } catch (IOException e) {
            System.out.println("Ein Fehler ist aufgetreten!");
        }
        return toPrint;
    }

    //Die vom Server empfangene Nachricht wird hier für die Konsolenausgabe aufbereitet
    public String extract(String reply) {
        if(reply.startsWith("TIME")){
            return reply.substring(5) + "\n";
        }
        else if(reply.startsWith("DATE") ){
            return reply.substring(5) + "\n";
        }
        else if(reply.startsWith("SUM ")){
            return reply.substring(4) + "\n";
        }
        else if(reply.startsWith("DIFFERENCE ")){
            return  reply.substring(11) + "\n";
        }
        else if(reply.startsWith("PRODUCT")){
            return reply.substring(8) + "\n";
        }
        else if(reply.startsWith("QUOTIENT")){
            return reply.substring(9) + "\n";
        }
        else if(reply.startsWith("ECHO")){
            return reply.substring(5) + "\n";
        }
        else if(reply.startsWith("HISTORY")){
            reply.substring(8);
            String[] lines = reply.split(",");
            for (int i = lines.length-1; i > 1; i-- ){
                lines[i] = lines[i].replaceAll("%", ",");
                System.out.println(lines[i]);
            }
            return lines[1].replaceAll("%", ",") + "\n";
        }
        return reply + "\n";
    }

    //Gibt den Status der Verbindung
    public boolean isConnected() {
        return (clientSocket != null && clientSocket.isConnected());
    }
}
