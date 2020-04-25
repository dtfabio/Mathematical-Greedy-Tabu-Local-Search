package ProgettoCerrone;

public class Main {

    public static void main(String[] args) {
        FileManager fm = new FileManager();
        ApproccioEsatto ae = new ApproccioEsatto();
        ApproccioGreedy ag = new ApproccioGreedy();
        RicercaLocale rl = new RicercaLocale();
        RicercaTabu rt = new RicercaTabu();
       
        //System.out.println("Nome file: " + fm.getNOMEFILE());
        //ag.solveModel(fm.getNumEventi(), fm.getNumStanze(), fm.getEventiOrdinati(), fm.getStanze());
        //rl.solveModel(fm.getNumEventi(), fm.getNumStanze(), fm.getEventiOrdinati(), fm.getStanze());
        rt.solveModel(fm.getNumEventi(), fm.getNumStanze(), fm.getEventiOrdinati(), fm.getStanze());

    }
}
