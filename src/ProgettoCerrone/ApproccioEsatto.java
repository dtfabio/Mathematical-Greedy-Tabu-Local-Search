package ProgettoCerrone;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.util.concurrent.TimeUnit;

public class ApproccioEsatto {

    FileManager fm = new FileManager();

    public void solveModel(int numEventi, int numStanze, String[][] eventi, String[][] stanze) {

        try {

            //Istanziamo il modello 
            IloCplex model = new IloCplex();

            //Definiamo le variabili(binarie)
            IloNumVar[][] x = new IloNumVar[numEventi][numStanze];
            for (int i = 0; i < numEventi; i++) {
                for (int j = 0; j < numStanze; j++) {
                    if (Integer.parseInt(eventi[i][3]) <= Integer.parseInt(stanze[j][1])) {
                        x[i][j] = model.boolVar(); //una matrice di variabili binarie
                    }
                }
            }

            //Definiamo la funzione obiettivo
            IloLinearNumExpr funzioneObj = model.linearNumExpr();
            for (int i = 0; i < numEventi; i++) {
                for (int j = 0; j < numStanze; j++) {

                    if (x[i][j] != null) {

                        double t = TimeUnit.SECONDS.toMinutes(Integer.parseInt(eventi[i][2]) - Integer.parseInt(eventi[i][1]));

                        int th = (int) t / 60;
                        int tm = (int) t % 60;
                        StringBuffer sb = new StringBuffer();
                        sb.append(th);
                        sb.append(".");
                        sb.append(tm);

                       // double time = Double.parseDouble(sb.toString());
                        double time =Integer.parseInt(eventi[i][2]) - Integer.parseInt(eventi[i][1]);
                        //System.out.println(time);
                        double app = Integer.parseInt(eventi[i][3]) * time;
                        funzioneObj.addTerm(app, x[i][j]);
                    }
                }
            }
            model.addMaximize(funzioneObj);

            IloLinearNumExpr[] vincolo1 = new IloLinearNumExpr[numEventi]; //un evento può avvenire solo in 1 stanza
            for (int i = 0; i < numEventi; i++) {
                vincolo1[i] = model.linearNumExpr();
                for (int j = 0; j < numStanze; j++) {

                    if (x[i][j] != null) {
                        vincolo1[i].addTerm(1.0, x[i][j]);
                    }
                }
            }

            for (int i = 0; i < numEventi; i++) {
                model.addLe(vincolo1[i], 1);
            }

            int[][] eventiIncompatbili = new int[numEventi][numEventi];
            int[][] eventiPerfetIncomp = new int[numEventi][numEventi];
            for (int i = 0; i < numEventi; i++) {
                for (int j = 0; j < numEventi; j++) {
                    int timeEvent1I = Integer.parseInt(eventi[i][1]);
                    int timeEvent1F = Integer.parseInt(eventi[i][2]);
                    int timeEvent2I = Integer.parseInt(eventi[j][1]);
                    int timeEvent2F = Integer.parseInt(eventi[j][2]);

                    if ((timeEvent1I == timeEvent2I) && (timeEvent1F == timeEvent2F)) {
                        eventiPerfetIncomp[i][j] = 1;
                    } else if ((timeEvent1I >= timeEvent2I) && (timeEvent1I < timeEvent2F)) {
                        eventiIncompatbili[i][j] = 1;
                    } else if ((timeEvent1F > timeEvent2I) && (timeEvent1F <= timeEvent2F)) {
                        eventiIncompatbili[i][j] = 1;
                    } else if ((timeEvent1I <= timeEvent2I) && (timeEvent1F >= timeEvent2F)) {
                        eventiIncompatbili[i][j] = 1;
                    }
                }
            }

            for (int i = 0; i < numEventi; i++) {
                for (int j = 0; j < numEventi; j++) {
                    for (int k = 0; k < numStanze; k++) {

                        if (x[i][k] != null) {
                            if (x[j][k] != null) {
                                if ((eventiIncompatbili[i][j] == 1) && (i < j)) {

                                    model.addLe(model.sum(x[i][k], x[j][k]), 1);
                                }
                            }
                        }
                    }
                }
            }

            // incompatibilità perfetta
            IloLinearNumExpr vincolo3 = model.linearNumExpr();
            boolean write = false;
            for (int k = 0; k < numStanze; k++) {

                for (int i = 0; i < numEventi; i++) {
                    if (x[i][k] != null) {

                        for (int j = 0; j < numEventi; j++) {
                            if (x[j][k] != null) {

                                if ((eventiPerfetIncomp[i][j] == 1) && (i < j)) {
                                    if (!write) {
                                        vincolo3 = model.linearNumExpr();
                                        vincolo3.addTerm(x[i][k], 1.0);
                                        write = true;
                                    }
                                    vincolo3.addTerm(x[j][k], 1.0);

                                }

                            }
                        }

                        if (write) {
                            model.addLe(vincolo3, 1);
                            write = false;
                        }
                    }
                }
            }

            model.exportModel("export.lp");

            boolean isSolved = model.solve();
            if (isSolved) {
/*
                System.out.println("\n");
                //System.out.println("Status = " + model.getStatus());
                for (int i = 0; i < numEventi; i++) {
                    for (int j = 0; j < numStanze; j++) {
                        if (x[i][j] != null) {
                            if (model.getValue(x[i][j]) == 1.0) {
                                System.out.println(eventi[i][0] + " -> " + stanze[j][0]);
                            }
                        }
                    }
                }
                System.out.println("\n");
*/
                System.out.println("Obj_ESATTO = " + model.getObjValue());

                int[][] soluzione = new int[numEventi][numStanze];
                for (int i = 0;
                        i < numEventi;
                        i++) {
                    for (int j = 0; j < numStanze; j++) {
                        if (x[i][j] != null) {
                            soluzione[i][j] = (int) model.getValue(x[i][j]);
                        }
                    }
                }
                fm.saveData(soluzione, "esatto", model.getObjValue());

            } else {
                System.out.println("Model not solved :(");
            }

            //Libera la memoria
            model.end();

        } catch (IloException ex) {
            ex.printStackTrace();

        }
    }
}
