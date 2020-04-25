package ProgettoCerrone;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    private int numeroEventi = 0;
    private int numeroStanze = 0;
    private String[][] matriceEventi;
    private String[][] matriceStanze;
    private String[][] matriceEventiOrdinati;
    private String[][] matriceStanzeOrdinate;
    private String[][] matriceEventiTemp;
    private String[][] matriceStanzeTemp;

    private int[][] matriceSoluzione;
    private double objective = 0;

/*PER PERCORSO DEI FILE */
    private String NUMEROFILE = "1";
    private String NOMEFILE = "1000_10.in";
    private String MATRICE = NUMEROFILE + "_matrice_"+ NOMEFILE;
    private String PATH = "/Users/fabiodt/Desktop/Archivio-I-O-eventi/data_1000_10/";
    
/*CARICA LA MATRICE INIZIALE E ORDINA LA MATRICE INIZIALE*/
    public FileManager() {
/*GESTISCE L'ASSEGNAZIONE DELL'INPUT EVENTI INIZIALI ALLE VARIABILI*/
        int colonneEventi = 4;
        try {
            String input = PATH + NUMEROFILE + "_data_" + NOMEFILE;
            BufferedReader br = new BufferedReader(new FileReader(input)); //legge file input
            String line = null;
            
            /*GESTISCE L'ASSEGNAZIONE DELLA PRIMA RIGA DEL FILE ALLE VARIABILI*/
            if ((line = br.readLine()) != null) { //legge la prima riga 
                String[] strTokens = line.split(" "); //prende ogni parola divisa dallo split " " e la assegna temporaneamente a ogni elemento dell'array di stringhe strTokens
                if (!strTokens[0].isEmpty()) {
                    numeroEventi = Integer.parseInt(strTokens[0]); //assegna numero eventi
                } else {
                    System.out.println("Impossibile caricare il numero di eventi");
                }
                if (!strTokens[1].isEmpty()) {
                    numeroStanze = Integer.parseInt(strTokens[1]);  //assegna numero stanze
                } else {
                    System.out.println("Impossibile caricare il numero di stanze");
                }
            } else {
                System.out.println("Errore nel file");
            }
            

            /*GESTISCE L'ASSEGNAZIONE DELLE RIGHE EVENTI DEL FILE ALLE VARIABILI*/
            int rowCount = 0;
            int colCount = 0;
            matriceEventi = new String[numeroEventi][colonneEventi]; //creo la matrice utilizzando la dimensione di numeroEventi 

            while ((rowCount < numeroEventi) && ((line = br.readLine()) != null)) { //legge tutte le righe fin quando rowCount non raggiunge numeroEventi (oppure la riga è vuota). P.S: Ogni volta che esegue il metodo readline passa alla riga successiva.
                String[] strTokens = line.split(" "); //prende ogni parola divisa dallo split " " e la assegna temporaneamente a ogni elemento dell'array di stringhe strTokens
                for (String token : strTokens) { //prende uno ad uno gli elementi di strTokens e assegna ciascuno di essi alla stringa token ed esegue per ciascun elemento il blocco di codice che segue
                    if (!token.isEmpty()) {
                        matriceEventi[rowCount][colCount] = token; //assegna ogni elemento della riga del tile in ogni colonna di una riga della matrice
                        colCount++;
                    } else {
                        colCount++;
                        System.out.println("Impossibile caricare gli eventi");
                    }
                }
                colCount = 0;
                rowCount++; 
            }

            /*GESTISCE L'ASSEGNAZIONE DELLE RIGHE STANZE DEL FILE ALLE VARIABILI*/
            rowCount = 0;
            colCount = 0;
            int colonneStanze = 2;
            matriceStanze = new String[numeroStanze][colonneStanze];

            while ((rowCount < numeroStanze) && ((line = br.readLine()) != null)) {  
                String[] strTokens = line.split(" ");
                for (String token : strTokens) {
                    if (!token.isEmpty()) {
                        matriceStanze[rowCount][colCount] = token;
                        colCount++;
                    } else {
                        System.out.println("Impossibile caricare le stanze");
                    }
                }
                colCount = 0;
                rowCount++;
            }

            br.close(); //chiude il file di input

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
/*GESTISCE LA CREAZIONE DI UNA MATRICE ORDINATA (matriceEventiOrdinati) IN BASE AI PARTECIPANTI DELLA MATRICE INIZIALE (matriceEventi) */
        matriceEventiTemp = new String[numeroEventi][colonneEventi]; //matrice temporanea per ordinare
        matriceEventiOrdinati = new String[numeroEventi][colonneEventi]; //matrice finale ordinata

        for (int i = 0; i < numeroEventi; i++) {    //assegniamo i dati caricati dal file di input e messi in matriceEventi alla matriceEventiTemp
            for (int j=0; j<colonneEventi;j++)
                matriceEventiTemp[i][j] = matriceEventi[i][j];
        }
        for (int i = 0; i < numeroEventi; i++) {
            int max = 0;
            int indiceMax = 0;
            for (int j = 0; j < numeroEventi; j++) {
                if (Integer.parseInt(matriceEventiTemp[j][3]) > max) {  //troviamo evento con partecipanti massimi
                    max = Integer.parseInt(matriceEventiTemp[j][3]); 
                    indiceMax = j;
                }
            }
            for(int j=0; j<colonneEventi;j++)
                matriceEventiOrdinati[i][j] = matriceEventiTemp[indiceMax][j]; //l'evento trovato lo inseriamo nella matrice finale ordinata
                matriceEventiTemp[indiceMax][3] = "0"; //lo azzeriamo dalla matrice temporanea per evitare di ripescarlo
        }
    }

/*CARICA MATRICE SOLUZIONE*/
    public void loadSolution() {
        try {
/*GESTISCE L'ASSEGNAZIONE DELL'INPUT MATRICE SOLUZIONE ALLE VARIABILI*/
            String input = PATH + "soluzione/"+ MATRICE;
            String line = null;
            matriceSoluzione = new int[numeroEventi][numeroStanze];
            BufferedReader br = new BufferedReader(new FileReader(input)); //legge file input

            /*GESTISCE L'ASSEGNAZIONE DELLA PRIMA RIGA DELLA MATRICE, CHE È LA FUNZIONE OBIETTIVO OTTENUTA, ALLA VARIABILE*/
            if ((line = br.readLine()) != null) {  //legge la prima riga 
                String[] strTokens = line.split(" ");

                if (!strTokens[0].isEmpty()) { 
                    objective = Double.parseDouble(strTokens[0]); //assegnamo ad objective il valore ottenuto dalla soluzione del greedy, che sta nella prima riga 
                } else {
                    System.out.println("Impossibile caricare l'obiettivo");
                }

            } else {
                System.out.println("Errore nel file");
            }
            
            /*GESTISCE L'ASSEGNAZIONE DEGLI ELEMENTI DELLA MATRICE SOLUZIONE (0,1), PER POTER FARE LA RICERCA LOCALE */
            int rowCount = 0;
            int colCount = 0;
            while ((rowCount < numeroEventi) && ((line = br.readLine()) != null)) {
                String[] strTokens = line.split(" ");
                for (String token : strTokens) {
                    if (!token.isEmpty()) {
                        matriceSoluzione[rowCount][colCount] = Integer.parseInt(token);
                        colCount++;

                    } else {
                        colCount++;
                        System.out.println("Impossibile caricare la matrice soluzione");
                    } 
                }
                colCount = 0;
                rowCount++;
            }

            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

/*RESTITUISCE NUMERO EVENTI*/
    public int getNumEventi() {
        return numeroEventi;
    }

/*RESTITUISCE NUMERO STANZE*/
    public int getNumStanze() {
        return numeroStanze;
    }
    
/*RESTITUISCE EVENTI*/
    public String[][] getEventi() {
        return matriceEventi;
    }
   
/*RESTITUISCE STANZE*/
    public String[][] getStanze() {
        return matriceStanze;
    }
 
/*RESTITUISCE EVENTI ORDINATI*/
    public String[][] getEventiOrdinati() {
        return matriceEventiOrdinati;
    }

/*RESTITUISCE STANZE ORDINATE*/
    public String[][] getStanzeOrdinate() {
        return matriceStanzeOrdinate;
    }
   
/*RESTITUISCE MATRICE SOLUZIONE */
    public int[][] getSolution() {
        return matriceSoluzione;
    }

/*RESTITUISCE FUNZIONE OBIETTIVO OTTENUTA*/
    public double getObjective() {
        return objective;
    }

/*RESTITUISCE NOME DEL FILE*/
    public String getNOMEFILE() {
        return NOMEFILE;
    }

/*SALVA MATRICE TROVATA E MATRICE SOLUZIONE*/     
    public void saveData(int soluzione[][], String tipo, double obj) {
        try {
/*GESTISCE IL SALVATAGGIO DELLA MATRICE EVENTI*/
            String output = PATH + "soluzione/" + NUMEROFILE + "_" + tipo + "_" + NOMEFILE;
            BufferedWriter writer = new BufferedWriter(new FileWriter(output)); //Scrivo sul file output
            for (int i = 0; i < numeroStanze; i++) {
                writer.write(matriceStanze[i][0] + " : ");
                writer.newLine();
                for (int j = 0; j < numeroEventi; j++) {
                    if (soluzione[j][i] == 1) { //Stampa gli eventi in
                        writer.write("            " + matriceEventi[j][0] + " ");
                        writer.newLine();
                    }
                }
                writer.newLine();
                writer.newLine();
            }
            writer.write("Funzione obj -> " + obj);
            writer.close();
            
/*GESTISCE IL SALVATAGGIO DELLA MATRICE SOLUZIONE*/
            String output2 = PATH + "soluzione/" + NUMEROFILE + "_matrice_" + NOMEFILE;
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(output2));

            writer2.write(obj + " ");
            writer2.newLine();
            for (int i = 0; i < numeroEventi; i++) {
                for (int j = 0; j < numeroStanze; j++) {
                    writer2.write(soluzione[i][j] + " ");
                }
                writer2.newLine();
            }
            writer2.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
