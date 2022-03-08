package com.github.sdn_idn_iot.idn;

import com.github.sdnwiselab.sdnwise.application.MonitoringMetrics;
import java.util.HashMap;

public class Mediador {

    private HashMap<String, Behavior[]> intentMap;
    private Communication interaction;
    private MonitoringMetrics monitoringMetrics;
    private ResultMetrics[] result;

    public Mediador(HashMap<String, Behavior[]> intentMap, Communication interaction) {
        this.intentMap = intentMap;
        this.interaction = interaction;
        this.result = null;  //quantidade de comportamentos
    }

    public void processIntents(Intent intent) {
        Behavior behavior[] = (Behavior[]) intentMap.get(intent.getNameIntent());
        result = new ResultMetrics[behavior.length];
        interaction.getMonitoringMetrics().start();
        behavior[0].execute(interaction, intent);

        behavior[0].execute(interaction, intent);
        behavior[0].execute(interaction, intent);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            System.err.println("Error");
        }

        interaction.getMonitoringMetrics().stop();
        result[0] = new ResultMetrics();
        result[0].setResults(interaction.getMonitoringMetrics().getResultMetrics().clone());
        print(result[0]);
    }

    public void print(ResultMetrics res) {
        System.out.print("Resultados: [");
        for (int i = 0; i < res.getResults().length; i++) {
            System.out.print(res.getResults()[i]);
        }

        System.out.println("] ");

    }

}// fim do Mediador()

/* COMENTÁRIOS

//        String mote = netPacket.getSrc().toString();
//        int qntMotes = intent.getIdMotes().length;
//        
//        for(int i = 0; i < qntMotes; i++){
//            System.out.println("ReportPacket do mote: " + mote);
//        }
//        
//        String ttl = String.valueOf(netPacket.getTtl());
//        String bateria = String.valueOf(rp.getBatt());
//        String vizinhos = rp.getNeighborsHashMap().toString();
//        String vzn = vizinhos.replaceAll("=-1", "");
//
//        String dadosColetados = mote + "," + ttl + "," + bateria;


 */
