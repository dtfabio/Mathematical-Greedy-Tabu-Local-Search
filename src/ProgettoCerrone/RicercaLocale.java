package ProgettoCerrone;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RicercaLocale {

    FileManager fm = new FileManager();

    private int dimVicinato = 1;

    public void solveModel(int numEventi, int numStanze, String[][] eventi, String[][] stanze) {
        fm.loadSolution();
        double tempObj = 0;
        double optimalObj = fm.getObjective();
        int[][] tempSolution = fm.getSolution(); //assegna la soluzione iniziale del greedy a tempSolution
        int[][] optimalSolution = fm.getSolution(); //assegna la soluzione iniziale del greedy a optimalSolution
        int[] vicinato = new int[dimVicinato];

        //Assegnazione dei coefficienti
        double[] coefficienti = new double[numEventi];
        for (int i = 0; i < numEventi; i++) {
            double t = TimeUnit.SECONDS.toMinutes(Integer.parseInt(eventi[i][2]) - Integer.parseInt(eventi[i][1]));
            int th = (int) t / 60;
            int tm = (int) t % 60;
            StringBuffer sb = new StringBuffer();
            sb.append(th);
            sb.append(".");
            sb.append(tm);
            double time =Integer.parseInt(eventi[i][2]) - Integer.parseInt(eventi[i][1]);                    
            double val = Integer.parseInt(eventi[i][3]) * time;
            coefficienti[i] = val;
        }
/*CICLO DI RISOLUZIONE*/
        //vicinato[0]=-1; //perchè sennà nella riga 47 il primo elemento del vicinato lo settava ad esiste=true
        do {
/*SCELTA DEL VICINATO - SCEGLIE LA STANZA CON COEFFICIENTE MINORE*/
            for (int j = 0; j < dimVicinato; j++) { //esegue tante volte quanto grande scegliamo il vicinato
                double tempMin = 1000000000;
                int indMin = -1;
                for (int k = 0; k < numStanze; k++) { //ciclo su tutte le stanze
                    double tempSum = 0;
                    boolean esiste = false;
                    for (int i = 0; i < dimVicinato; i++) { //se la stanza è già presente nel vicinato, non continua
                        if (k == vicinato[i]) {
                            esiste = true;
                        }
                    }

                    if (!esiste) { //se supera lo step precedente
                        for (int i = 0; i < numEventi; i++) { //somma per una stanza tutti i coefficienti degli elementi attribuiti
                            if (optimalSolution[i][k] == 1) {
                                tempSum = tempSum + coefficienti[i];
                            }
                        }              
                        if (tempSum < tempMin) {
                            tempMin = tempSum;  //trova così la stanza con coefficiente minore
                            indMin = k;
                        } 
                    }
                }
                vicinato[j] = indMin;
            }

/*RIMOZIONE EVENTI DELLE STANZE PRESENTI NEL VICINATO*/
            for (int j = 0; j < dimVicinato; j++) {
                for (int i = 0; i < numEventi; i++) {
                    tempSolution[i][vicinato[j]] = 0;
                }
            }

/*AGGIUNTA EVENTI*/
            for (int v = 0; v < dimVicinato; v++) {
                for (int i = 0; i < numEventi; i++) {
                    boolean compatibile = true;

                    for (int j = 0; j < numStanze; j++) { 
                        if (tempSolution[i][j] == 1) { //se l'evento è in soluzione
                            compatibile = false;
                        }
                    }
                    // se i partecipanti degli evento è amggiore della capienza della stanza 
                    if (Integer.parseInt(eventi[i][3]) > Integer.parseInt(stanze[vicinato[v]][1])) {
                        compatibile = false;
                    }

                    // se un evento incompatibile è nella stessa stanza 
                    ArrayList<Integer> eventiIncompatbili = new ArrayList<>();
                    for (int j = 0; j < numEventi; j++) {
                        int timeEvent1I = Integer.parseInt(eventi[i][1]);
                        int timeEvent1F = Integer.parseInt(eventi[i][2]);
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

                    if (compatibile) {
                        for (Integer element : eventiIncompatbili) {
                            // se un evento incompatibile è nella stessa stanza non è compatibile
                            if (tempSolution[element][vicinato[v]] == 1) {
                                compatibile = false;
                            }
                        }
                    }

                    if (compatibile) { //se è compatibile aggiungo l'evento in soluzione 
                        tempSolution[i][vicinato[v]] = 1;
                    }
                }
            }
            //Calcolo la funzione obiettivo
            tempObj = 0;
            for (int i = 0; i < numEventi; i++) {
                for (int j = 0; j < numStanze; j++) {
                    if (tempSolution[i][j] == 1) {
                        tempObj = tempObj + coefficienti[i];
                    }
                }
            }
            if (tempObj > optimalObj) {
                optimalObj = tempObj;
            }
        } while (tempObj > optimalObj);
        System.out.println("OBJ_local search  =" + tempObj);
    }

}
