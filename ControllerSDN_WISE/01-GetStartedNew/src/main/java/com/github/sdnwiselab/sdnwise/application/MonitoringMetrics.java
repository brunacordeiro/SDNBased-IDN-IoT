package com.github.sdnwiselab.sdnwise.application;

import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import com.github.sdnwiselab.sdnwise.packet.ReportPacket;

public class MonitoringMetrics {

    //private final HashMap<Integer, Integer> metrics;
    private int idMote = 0;
    private int mote = 0;
    private int ttl = 0;
    private int battery = 0;
    private int flag = 0;
    private int[] startMetrics;
    private int[] stopMetrics;
    private int[] resultMetrics;

    public MonitoringMetrics() {
        //this.metrics = new HashMap<>();
        startMetrics = new int[11];
        stopMetrics = new int[11];
        resultMetrics = new int[11];
    }

    public void receiveMetrics(NetworkPacket netPacket) {

        ReportPacket rp = new ReportPacket(netPacket);

        String mote = netPacket.getSrc().toString();
        String m = mote.replaceAll("0.", "");
        idMote = Integer.parseInt(m);
        setMote(idMote);
        String ttl = String.valueOf(netPacket.getTtl());
        setTtl(Integer.parseInt(ttl));
        String bateria = String.valueOf(rp.getBatt());
        setBattery(Integer.parseInt(bateria));
        String vizinhos = rp.getNeighborsHashMap().toString();
        // setNeighbors(Integer.parseInt(vizinhos));

        //String dadosColetados = getMote() + "," + getTtl() + "," + getBattery();
        //System.out.println("Dados coletados: " + dadosColetados);
        collectMetrics(getMote(), getBattery());
    }

    public void start() {
        for (int i = 0; i < 11; i++) {
            startMetrics[i] = 0;
            stopMetrics[i] = 0;
            resultMetrics[i] = 0;
        }
        print(startMetrics, "Start - Start");
        print(stopMetrics, "Start - Stop");
        print(resultMetrics, "Start - Result");
        flag = 1;
    }

    public void stop() {
        flag = 2;
        for (int i = 0; i < 11; i++) {
            resultMetrics[i] = startMetrics[i] - stopMetrics[i];
        }
        print(startMetrics, "Stop - Start");
        print(stopMetrics, "Stop - Stop");
        print(resultMetrics, "Stop - Result");
    }

    public void collectMetrics(int mote, int batt) {

        if (flag == 1) {
            if (startMetrics[mote - 1] == 0) {
                startMetrics[mote - 1] = batt;
                //System.out.println("Metrics start - mote: " + mote + " Batt: " + batt);
            }
            stopMetrics[mote - 1] = batt;
            // System.out.println("Metrics stop - mote: " + mote + " Batt: " + batt);
        } else if (flag == 2) {
            System.out.println("Metrics stop!");
            flag = 0;
        }
    }

    public void print(int[] res, String x) {
        System.out.print(x + ": [");
        for (int i = 0; i < res.length; i++) {
            System.out.print(" " + res[i]);
        }

        System.out.println(" ]");

    }

    /**
     * @return the mote
     */
    public int getMote() {
        return mote;
    }

    /**
     * @param mote the mote to set
     */
    public void setMote(int mote) {
        this.mote = mote;
    }

    /**
     * @return the ttl
     */
    public int getTtl() {
        return ttl;
    }

    /**
     * @param ttl the ttl to set
     */
    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    /**
     * @return the battery
     */
    public int getBattery() {
        return battery;
    }

    /**
     * @param battery the battery to set
     */
    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int[] getResultMetrics() {
        return resultMetrics;
    }

} // fim do MonitoringMetrics


/* Comentarios

        
        System.out.println("\n*****************************************************************");
        System.out.println("Informacoes do Mote: " + mote);
        System.out.println("TTL: " + ttl);
        System.out.println("Bateria: " + bateria);
        System.out.println("Vizinhos (Hash) : " + vizinhos);
        System.out.println("*****************************************************************\n");



 */
