package ProgettoCerrone;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ApproccioGreedy {

    FileManager fm = new FileManager();

    public void solveModel(int numEventi, int numStanze, String[][] eventi, String[][] stanze) {
        double obj = 0;
        int[][] soluzione = new int[numEventi][numStanze];
/*GESTISCE IL CALCOLO DEI COEFFICIENTI*/
        double[] coefficienti = new double[numEventi];  //Assegnazione dei coefficienti
        for (int i = 0; i < numEventi; i++) {
            double t = TimeUnit.SECONDS.toMinutes(Integer.parseInt(eventi[i][2]) - Integer.parseInt(eventi[i][1])); // durata tempo evento in minuti
            int th = (int) t / 60; 
            int tm = (int) t % 60; 
            StringBuffer sb = new StringBuffer();
            sb.append(th);
            sb.append(".");
            sb.append(tm); //sb è l'orario in ore:minuti 
            double time =Integer.parseInt(eventi[i][2]) - Integer.parseInt(eventi[i][1]); //durata evento in secondi
            double val = Integer.parseInt(eventi[i][3]) * time; //calcolo coefficiente "partecipanti * tempo"
            coefficienti[i] = val;
        }
        
/*GESTISCE LA SCELTA GREEDY*/
        for (int i = 0; i < numEventi; i++) {    //ciclo su tutti gli eventi
            double max = 0;
            int indiceMax = 0;
            for (int j = 0; j < numEventi; j++) {   //ciclo su tutti gli eventi
                if (coefficienti[j] > max) {    
                    max = coefficienti[j];  //scelta greedy: per ogni coefficiente prendo il massimo
                    indiceMax = j;
                }
            }

            //Calcolo gli eventi incompatibili con evento[indiceMax]
            ArrayList<Integer> eventiIncompatbili = new ArrayList<>();
            int timeEvent1I = Integer.parseInt(eventi[indiceMax][1]);
            int timeEvent1F = Integer.parseInt(eventi[indiceMax][2]);
            for (int j = 0; j < numEventi; j++) {
                int timeEvent2I = Integer.parseInt(eventi[j][1]);
                int timeEvent2F = Integer.parseInt(eventi[j][2]);

                if ((timeEvent1I >= timeEvent2I) && (timeEvent1I < timeEvent2F)) {
                    eventiIncompatbili.add(j);
                } else if ((timeEvent1F > timeEvent2I) && (timeEvent1F <= timeEvent2F)) {
                    eventiIncompatbili.add(j);
                } else if ((timeEvent1I <= timeEvent2I) && (timeEvent1F >= timeEvent2F)) {
                    eventiIncompatbili.add(j);
                }
            }
          
            //Provo a mettere l'evento in tutte le stanze
            boolean aggiunto = false;
            for (int k = 0; k < numStanze; k++) {
                if (!aggiunto) {    // se non e gia stato aggiunto
                    boolean compatibile = true;
                    if (Integer.parseInt(eventi[indiceMax][3]) > Integer.parseInt(stanze[k][1])) {// se la capienza della stanza è minore dei partecipanti non è compatibile
                        compatibile = false;
                    }
                    if (compatibile) {
                        // per tutti gli eventi incompatibili con l'evento[indiceMax]
                        for (Integer element : eventiIncompatbili) { //prende uno ad uno gli elementi di eventiIncompatibili e assegna ciascuno di essi alla variabile element ed esegue per ciascuno il blocco di codice che segue
                            if (soluzione[element][k] == 1) { //se un evento incompatibile è nella stessa stanza non è compatibile
                                compatibile = false;
                            }
                        }
                    }
                    //se compatibile e non è stato aggiunto lo aggiungo alla soluzione e azzero il coefficiente
                    if (compatibile) { 
                        soluzione[indiceMax][k] = 1;
                        obj = obj + coefficienti[indiceMax];
                        coefficienti[indiceMax] = 0;
                        aggiunto = true;
                    }
                }
            }
            coefficienti[indiceMax] = 0; //
        }

        System.out.print("\nObj greedy -> " + obj + "\n");

        fm.saveData(soluzione, "greedy", obj);
    }
}
