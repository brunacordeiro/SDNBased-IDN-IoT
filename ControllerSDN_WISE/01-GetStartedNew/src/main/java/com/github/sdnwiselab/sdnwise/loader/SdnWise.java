/* 
 * Copyright (C) 2015 SDN-WISE
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.sdnwiselab.sdnwise.loader;

import com.github.sdn_idn_iot.idn.Behavior;
import com.github.sdn_idn_iot.idn.BehaviorLogic;
import com.github.sdn_idn_iot.idn.Intent;
import com.github.sdn_idn_iot.idn.Communication;
//import com.github.sdnwiselab.sdnwise.application.MessageHandler;
import com.github.sdn_idn_iot.idn.Mediador;
import com.github.sdnwiselab.sdnwise.application.*;
import com.github.sdnwiselab.sdnwise.configuration.Configurator;
import com.github.sdnwiselab.sdnwise.controller.Controller;
import com.github.sdnwiselab.sdnwise.controller.ControllerFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SdnWise class of the SDN-WISE project. It loads the configuration file and
 * starts the the Controller.
 *
 * @author Sebastiano Milardo
 * @version 0.1
 */
public class SdnWise {

    public Controller controller;

    /**
     * Starts the components of the SDN-WISE Controller.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        new SdnWise().startExample();
    }

    /**
     * Starts the Controller layer of the SDN-WISE network. The path to the
     * configurations are specified in the configFilePath String. The options to
     * be specified in this file are: a "lower" Adapter, in order to communicate
     * with the flowVisor (See the Adapter javadoc for more info), an
     * "algorithm" for calculating the shortest path in the network. The only
     * supported at the moment is "DIJKSTRA". A "map" which contains
     * informations regarding the "TIMEOUT" in order to remove a non responding
     * node from the topology, a "RSSI_RESOLUTION" value that triggers an event
     * when a link rssi value changes more than the set threshold.
     *
     * @param configFilePath a String that specifies the path to the
     * configuration file.
     * @return the Controller layer of the current SDN-WISE network.
     */
    public Controller startController(String configFilePath) {
        InputStream configFileURI = null;
        if (configFilePath == null || configFilePath.isEmpty()) {
            configFileURI = this.getClass().getResourceAsStream("/config.ini");
        } else {
            try {
                configFileURI = new FileInputStream(configFilePath);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SdnWise.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Configurator conf = Configurator.load(configFileURI);
        controller = new ControllerFactory().getController(conf.getController());
        new Thread(controller).start();
        return controller;
    }

    public void startExample() {
        controller = startController("");
        System.out.println("\nInicializando o Mediador....\n");

        // We wait for the network to start 
        try {
            Thread.sleep(60000); // 1 minuto

            // parte do usuário do intent (Aplicação) - Recebe intent
            Intent intent = new Intent();
            intent.setNameIntent("polling");
            int idMotes[] = new int[11];
            idMotes[0] = 1;
            idMotes[1] = 2;
            idMotes[2] = 3;
            idMotes[3] = 4;
            idMotes[4] = 5;
            idMotes[5] = 6;
            idMotes[6] = 7;
            idMotes[7] = 8;
            idMotes[8] = 9;
            idMotes[9] = 10;
            idMotes[10] = 11;

            intent.setIdMotes(idMotes);//lista de sensores
            // fim da parte do intent (Aplicação)

            // Criação do mapa que liga intent com comportamentos (papel do Intent Developer)
            HashMap<String, Behavior[]> intentMap = new HashMap<>();
            Behavior behavior[] = new Behavior[1];

            behavior[0] = new Behavior(new BehaviorLogic() {
                @Override
                public void reify(Communication interaction, Intent intent) {  //so vai ser executado quando o mediador.processIntents for chamado - classe anomymous 

                    for (int mote = 1; mote < intent.getIdMotes().length; mote++) {
                        interaction.polling("Qualidade do Ar", intent.getIdMotes()[mote], new MessageHandler() {
                            @Override
                            public void message(String dado) {
                                //dado =  1;15.0;12.0;200.0;1819.0;10:34:25
                                System.out.println("Resposta da rede: " + dado);
                            }
                        }); // fim interaction.polling

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(SdnWise.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } // fim do for
                } // fim reify
            }); // fim do behavior[0]

            intentMap.put("polling", behavior);
            // fim da criação do mapa(papel do Intent Developer)

            Communication interaction = new Communication(controller);

            System.out.println("Inicializando Tabela de Fluxo. ");
/*            for (int mote = 1; mote < intent.getIdMotes().length; mote++) {
                interaction.polling("Qualidade do Ar", intent.getIdMotes()[mote], new MessageHandler() {
                    @Override
                    public void message(String dado) {
                        //dado =  1;15.0;12.0;200.0;1819.0;10:34:25
                        System.out.println("Recebido da rede: " + dado);
                    }
                }); // fim interaction.polling
                System.out.println("Antes sleep");
                Thread.sleep(2000);
                System.out.println("Acordei");
            } // fim do for
*/
            System.out.println("Instanciando o Mediador...");
            Mediador mediador = new Mediador(intentMap, interaction);

            while (true) {
                mediador.processIntents(intent);
                //Thread.sleep(10000);
            } // fim do while
        } catch (InterruptedException ex) {
            Logger.getLogger(SdnWise.class.getName()).log(Level.SEVERE, null, ex);
        }

    }// fim do startExample

} // fim da classe

/*




        while (true){    
                for (mote = 1; mote <= 5; mote++){
                    mediador.polling("Qualidade do Ar", mote, new MessageHandler() {
                        @Override
                        public void message(String dado) {
                            //dado =  1;15.0;12.0;200.0;1819.0;10:34:25
                            verQldAr.verificarFatores(dado, mote);

                        }
                    });
                    Thread.sleep(2000);
                }
     }

        intent.setIdIntent(001);
        intent.setNameIntent("Garantir a Qualidade do Ar");
        intent.setVerbIntent("manter");
        intent.setObjectIntent("resolução ANVISA"); 
        intent.setSensor("Temperatura, Umidade, CO2");
        intent.setValues("28;20, 65;35, 1000;400");
        intent.setPeriodicity(10);
        //intent.setEnvironment("Lab-01");                //lista de sensores
        
     
        for (mote = 2; mote <= 6; mote++){
                    int id = mediador.geraIdMessage();
                    NodeAddress src = new NodeAddress(1);
                    NodeAddress dst = new NodeAddress(mote);
                    
                    //System.out.println("Conectando ao mote: " + mote);
                    String solicitacao = "Qualidade do Ar";
                    DataPacket dp = new DataPacket(1, src, dst);
                    dp.setNxhop(src);
                    dp.setPayload(solicitacao.getBytes(Charset.forName("UTF-8")));
                    controller.sendNetworkPacket(dp);
                    Thread.sleep(2000);
                }
        
    public void startExample() {

        controller = startController("");
        Mediador mediador = new Mediador(controller);
        System.out.println("\nControlador ADA executando....\n");
        NodeAddress src = new NodeAddress(1);
  
        try {
            Thread.sleep(20000);  //20 segundos

            while (true) {
                
                String[] command = intent.getCommand().split(", ");
                
                for (mote = 1; mote <= 5; mote++){
                    mediador.polling("Qualidade do Ar", mote, new MessageHandler() {
                        @Override
                        public void message(String dado) {
                            //dado =  1;15.0;12.0;200.0;1819.0;10:34:25
                            //verQldAr.verificarFatores(dado, mote);

                        }
                    });

                }//fim do for
            } //fim while

        } catch (InterruptedException ex) {
            Logger.getLogger(SdnWise.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 */
//}//fim classe SdnWise
/*
  

switch (command[0]) {
                        
                        case "consultar":  //consultar = polling
                            mediador.polling("Qualidade do Ar", mote, new MessageHandler() {
                                @Override
                                public void message(String dado) {
                                    //dado =  1;15.0;12.0;200.0;1819.0;10:34:25
                                    //verQldAr.verificarFatores(dado, mote);
                                   
                                }
                            });
                            break;

                        case "periodo": //evento periodico
                            System.out.println("Periodico");
                            mediador.eventoPeriodico(intent.getSensor(), mote, intent.getPeriodicity(), new MessageHandler() {
                                @Override
                                public void message(String dado) {
                                    //verPerd.verDadosPeriodico(dado, mote);
                                }
                            });
                            break;

                        case "alert":
                            System.out.println("Alerta");
                            mediador.alerta(intent.getSensor(), mote, intent.getValues(), new MessageHandler() {
                                @Override
                                public void message(String dado) {
                                    System.out.println(dado + " - Mote: " + mote);
                                }
                            });
                            break;

                        case "delete":
                            System.out.println("Deletar Evento");
                            mediador.delete("deleteEvent", mote, new MessageHandler() {
                                @Override
                                public void message(String dado) {
                                    System.out.println("Situação: " + dado + " Mote: " + mote);
                                }
                            });
                            break;

                        case "consult":
                            System.out.println("Consultar Evento");
                            mediador.delete("consultEvent", mote, new MessageHandler() {
                                @Override
                                public void message(String dado) {
                                    //dado = [6=3;4;0;30.0;20.0:7=2;4;0;30.0;20.0]
                                    consltEvent.consultar(dado, mote);
                                }
                            });
                            break;
                        default:
                            System.out.println("Comando Invalido!");
                            break;
                    }



--- JoptionPane - versão incial

 controller = startController("");
        Mediador mediador = new Mediador(controller);

        System.out.println("\nControlador ADA executando....\n");
        String cmdOption, cmdFactor, cmdInterval, cmdLimiteMAX, cmdLimiteMIN;
        NodeAddress src = new NodeAddress(1);
        ConnectionPg con = null;

        try {
            Thread.sleep(20000);  //20 segundos

            while (true) {

                cmdOption = JOptionPane.showInputDialog("Comando cmd: ", "polling");

                if (cmdOption == null) {
                    JOptionPane.showMessageDialog(null, "Cancelado!", "Atenção", JOptionPane.WARNING_MESSAGE);
                    break;
                    //System.exit(0);
                } else {
                    switch (cmdOption) {
                        case "polling":
                            cmdMote = JOptionPane.showInputDialog("Mote: ", 3);
                            System.out.println("Polling no Mote: " + cmdMote);
                            mediador.polling("Qualidade do Ar", Integer.parseInt(cmdMote), new MessageHandler() {
                                @Override
                                public void message(String dado) {
                                    //dado =  1;15.0;12.0;200.0;1819.0;10:34:25
                                    verQldAr.verificarFatores(dado, Integer.parseInt(cmdMote));
                                    String bd = dado + ";" + Integer.parseInt(cmdMote);
                                    try {
                                        OperationPg.insertPolling(con, bd);
                                    } catch (ClassNotFoundException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });
                            break;

                        case "event":
                            System.out.println("Evento Periodico");
                            cmdFactor = JOptionPane.showInputDialog("Fator: "
                                    + "\nTemperatura"
                                    + "\nUmidade"
                                    + "\nTVOC"
                                    + "\nCO2\n", "Temperatura");
                            cmdMote = JOptionPane.showInputDialog("Mote: ", 4);
                            cmdInterval = JOptionPane.showInputDialog("Intervalo de Envio (s): ", 5);
                            mediador.eventoPeriodico(cmdFactor, Integer.parseInt(cmdMote), Integer.parseInt(cmdInterval), new MessageHandler() {
                                @Override
                                public void message(String dado) {
                                    verPerd.verDadosPeriodico(dado, Integer.parseInt(cmdMote));
                                }
                            });
                            break;

                        case "alert":
                            System.out.println("Alerta");
                            cmdFactor = JOptionPane.showInputDialog("Fator: "
                                    + "\nTemperatura"
                                    + "\nUmidade"
                                    + "\nTVOC"
                                    + "\nCO2\n", "Temperatura");
                            cmdLimiteMAX = JOptionPane.showInputDialog("Valor Limite MAXIMO : ", 30);
                            cmdLimiteMIN = JOptionPane.showInputDialog("Valor Limite MINIMO : ", 20);
                            cmdMote = JOptionPane.showInputDialog("Mote: ", 5);
                            mediador.alerta(cmdFactor, Integer.parseInt(cmdMote), Integer.parseInt(cmdLimiteMAX),
                                    Integer.parseInt(cmdLimiteMIN), new MessageHandler() {
                                @Override
                                public void message(String dado) {
                                    System.out.println(dado + " - Mote: " + cmdMote);
                                }
                            });
                            break;

                        case "delete":
                            System.out.println("Deletar Evento");
                            cmdMote = JOptionPane.showInputDialog("Mote: ", 4);
                            mediador.delete("deleteEvent", Integer.parseInt(cmdMote), new MessageHandler() {
                                @Override
                                public void message(String dado) {
                                    System.out.println("Situação: " + dado + " Mote: " + cmdMote);
                                }
                            });
                            break;

                        case "consult":
                            System.out.println("Consultar Evento");
                            cmdMote = JOptionPane.showInputDialog("Mote: ", 4);
                            mediador.delete("consultEvent", Integer.parseInt(cmdMote), new MessageHandler() {
                                @Override
                                public void message(String dado) {
                                    //dado = [6=3;4;0;30.0;20.0:7=2;4;0;30.0;20.0]
                                    consltEvent.consultar(dado, Integer.parseInt(cmdMote));
                                }
                            });
                            break;
                        default:
                            System.out.println("Comando Invalido!");
                            break;
                    }//fim do switch
                }// fim do else
            } //fim while

        } catch (InterruptedException ex) {
            Logger.getLogger(SdnWise.class.getName()).log(Level.SEVERE, null, ex);
        }
    }




IDN app = new IDN()  

int idEvento = app.cadastrarAlerta("TEMPERATURA", "limite=35", MOTE);

app.recebeAlerta(idEvento, new Handler(String msg) {

    System.out.println(msg);

});



main () {

        application.send("Qualidade do ar", BROAD_CAST);
        Response response = application.recv();
        AirPureData data = parse.parseAirPure(response);

        if (data.temperature > 28) {
            application.send("TVOC", 4);
            Response resp2 = application.recv();
            AirPureData data2 = parse.parseAirpure(resp2);
            data2.tvoc
        }

return 0;
}

mediator.defineIntention("intention.xml");
application.send("Qualidade do ar", "REGION_A"");

              /*
                for (int i = 2; i < 12; i++){   //start na rede...

                    dst = new NodeAddress(i);
                    src = new NodeAddress(1);
                    dp  = new DataPacket(netId,src,dst);
                    
                    String intent = mediador.intent("Qualidade do Ar", "broadcast", 5000);
                    
                    dp.setNxhop(src);
                    dp.setPayload(intent.getBytes(Charset.forName("UTF-8")));
                    controller.sendNetworkPacket(dp);
                    //System.out.println("Solicitando: " + message);
                    Thread.sleep(2000); 
                }

                
               if(cmd.equals("teste")){
                    System.out.println("if teste");
                    dp  = new DataPacket(netId,new NodeAddress(1),new NodeAddress(4));
                    String enviar = "Qualidade do Ar" + ", " + "polling" + ", " + 5000;
                    dp.setNxhop(new NodeAddress(1));
                    dp.setPayload(enviar.getBytes(Charset.forName("UTF-8")));
                    controller.sendNetworkPacket(dp);
               
               }else if(cmd.equals("evento")){
                    System.out.println("if evento");
                    dp  = new DataPacket(netId,new NodeAddress(1),new NodeAddress(4));
                    String enviar = "Temperatura" + ", " + "evento" + ", " + 5000;
                    dp.setNxhop(new NodeAddress(1));
                    dp.setPayload(enviar.getBytes(Charset.forName("UTF-8")));
                    controller.sendNetworkPacket(dp);
               }else {
                   System.out.println("Invalido: " + cmd);
               } 


Intenção: 
Nome: QUALIDADE_DO_AR_PERIODICO_1
Valores: TEMP, TVOC, CO2,  UMID 
Local: Sala 204
Interação: EVENTO PERIODICO 1 minuto

noId 4 
temperatura (F, C)umidade (%)
sala 204 INF

noID 5 
potencia: X
commando: TOGGLE (ON/OFF) (OFF/ON)
status: ON/OFF

noID 6 
TVOC (PPB)CO2 (PPM)
sala 204 INF


mediador.coletar(QUALIDADE_DO_AR_PERIODICO, new MessageHandler() {
    public void message(String data) {
        //parseData
        //plotGrafico
    });

 */
