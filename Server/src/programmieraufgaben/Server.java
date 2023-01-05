package programmieraufgaben;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class Server {
    private int port;
    private LinkedList<String> history; //speichert alle Anfragen des Clients
    private ServerSocket serverSocket;
    private PrintWriter out;
    private BufferedReader in;

    /* Diese Methode beinhaltet die gesamte Ausführung (Verbindungsaufbau und Beantwortung
       der Client-Anfragen) des Servers.
     */
    public void execute() {
        try {
            serverSocket = new ServerSocket(port);  //Socket mit dem eingegebenen Port erzeugen
            Socket connectionSocket = serverSocket.accept();     //wartet auf Client und baut Verbindung auf
            out = new PrintWriter(new OutputStreamWriter(connectionSocket.getOutputStream(), StandardCharsets.UTF_8), true);
            in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(), StandardCharsets.UTF_8));
            history = new LinkedList<String>();

            while (connectionSocket.isConnected()) {

                String request = in.readLine();  //Anfrage des Clients
                if( request != null){
                    //************ GET Time sendet die aktuelle Zeit ********
                    if (request.equals("GET Time")) {
                        history.add(request); //Anfrage speichern
                        SimpleDateFormat serverTime = new SimpleDateFormat("HH:mm:ss");
                        out.println("TIME " + serverTime.format(new Date()));
                    }

                    //************ GET Date sendet das heutige Datum *******
                    else if (request.equals("GET Date")) {
                        history.add(request); //Anfrage speichern
                        SimpleDateFormat serverDate = new SimpleDateFormat("dd.MM.yyyy");
                        out.println("DATE " + serverDate.format(new Date()));
                    }

                    //************* ADDITION ********************
                    else if (request.startsWith("ADD")) { //prefix
                        history.add(request); //Anfrage speichern
                        {
                            if (request.length() >= 6) { // ADD a b
                                request = request.substring(4); //Zahlen extrahieren
                                String[] n = request.split(" ");
                                try {
                                    int a = Integer.parseInt(n[0]);
                                    int b = Integer.parseInt(n[1]);
                                    out.println("SUM " + (a + b));
                                } catch (Exception e) {
                                    out.println("ERROR Falsches Format!");
                                }
                            } else {
                                out.println("ERROR Falsches Format!");
                            }
                        }
                    }

                    //*************** Subtraktion ***************************
                    else if (request.startsWith("SUB")) { //prefix
                        history.add(request); //Anfrage speichern
                        if (request.length() >= 6) {
                            request = request.substring(4);
                            String[] n = request.split(" ");
                            try {
                                int a = Integer.parseInt(n[0]);
                                int b = Integer.parseInt(n[1]);
                                out.println("DIFFERENCE " + (a - b));
                            } catch (Exception e) {
                                out.println("ERROR Falsches Format!");
                            }
                        } else {
                            out.println("ERROR Falsches Format!");
                        }
                    }

                    //************* Multiplikation **************************
                    else if (request.startsWith("MUL")) { //prefix
                        history.add(request); //Anfrage speichern
                        if (request.length() >= 6) {
                            request = request.substring(4);
                            String[] n = request.split(" ");
                            try {
                                int a = Integer.parseInt(n[0]);
                                int b = Integer.parseInt(n[1]);
                                out.println("PRODUCT " + (a * b));
                            } catch (Exception e) {
                                out.println("ERROR Falsches Format!");
                            }
                        } else {
                            out.println("ERROR Falsches Format!");
                        }
                    }

                    //************** Division **************************
                    else if (request.startsWith("DIV")) { //prefix
                        history.add(request); //Anfrage speichern
                        if (request.length() >= 6) {
                            request = request.substring(4);
                            String[] n = request.split(" ");
                            try {
                                int a = Integer.parseInt(n[0]);
                                int b = Integer.parseInt(n[1]);
                                if (b == 0) {
                                    out.println("QUOTIENT undefined");
                                } else {
                                    out.println("QUOTIENT " + (float) a / b);
                                }
                            } catch (Exception e) {
                                out.println("ERROR Falsches Format!");
                            }
                        } else {
                            out.println("ERROR Falsches Format!");
                        }
                    }

                    //************** ECHO ************************************
                    else if (request.startsWith("ECHO")) {
                        history.add(request.replaceAll(",", "%")); //Anfrage speichern
                        if (request.length() > 4) {
                            out.println(request);
                        } else {
                            out.println("ERROR Falsches Format!");
                        }
                    }

                    //*************** DISCARD *********************************
                    else if (request.startsWith("DISCARD")) {
                        out.println("");
                    }

                    //*************** PING-PONG ********************************
                    else if (request.equals("PING")) {
                        history.add(request); //Anfrage speichern
                        out.println("PONG");
                    }

                    //*************** HISTORIE *********************************
                    else if (request.startsWith("HISTORY")) {
                        if (history.size() == 0) { //Wenn es noch keine Anfragen gab
                            out.println("ERROR Keine Historie vorhanden!");
                        } else {
                            String toSend = history.toString(); // [ , , ] Format
                            toSend = toSend.substring(1, toSend.length() - 1); //eckige Klammern wegmachen
                            if (request.length() == 7) { //alle bisher vom Client gestellten Anfragen
                                out.println("HISTORY," + toSend); // , , , Format
                            } else { //letzte <Integer> vom Client gestellte Anfragen
                                try {
                                    int i = Integer.parseInt(request.substring(8)); //<Integer> extrahieren
                                    if (i >= history.size() - 1) { //falls es weniger als <Integer> Anfragen geschickt wurde
                                        out.println(toSend);
                                    } else { //falls es mehr Anfragen gab als <Integer>
                                        LinkedList<String> tmp = new LinkedList<String>(); //Teilliste von history mit letzten Anfragen
                                        tmp.addFirst("HISTORY"); //prefix
                                        while (i > 0) {
                                            tmp.add(history.get(history.size()-i));
                                            i--;
                                        }
                                        String toSend1 = tmp.toString();
                                        toSend1 = toSend1.substring(1, toSend1.length() - 1);
                                        out.println(toSend1);
                                    }
                                } catch (Exception e) {
                                    out.println("ERROR Falsches Format!");
                                }
                            }
                        }
                        history.add(request); //Anfrage speichern
                    }
                    //Falsche Angaben
                    else {
                        history.add(request); //Anfrage speichern
                        out.println("ERROR Unbekannte Anfrage!");
                    }
                } else{
                    System.out.println("Die Verbindung wurde unterbrochen!");
                    disconnect();
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("");
        }
    }

    //Hier wird die Verbindung und alle Streams geschlossen
    public void disconnect() {
        try {
            history = null;
            port = 0;
            in = null;
            out = null;
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Ein Fehler ist aufgetreten!");
        }
    }

    //Überprüfung der Port-Nummer und Speicherung dieser in die Klassen-Variable "port"
    public boolean checkPort(String port) {
        try {
            this.port = Integer.parseInt(port);
            if (this.port == 8080) {
                return true;
            }
            System.out.println("\nKein korrekter Port! Aktuell ist nur Port 2020 m\u00F6glich.\n");
        } catch (Exception e) {
            System.out.println("\nKein korrekter Port! Aktuell ist nur Port 2020 m\u00F6glich.\n");
        }
        return false;
    }
    //Gibt die akzeptierte und gespeicherte Port-Nummer zurück
    public int getPort() {
        return port;
    }
}
