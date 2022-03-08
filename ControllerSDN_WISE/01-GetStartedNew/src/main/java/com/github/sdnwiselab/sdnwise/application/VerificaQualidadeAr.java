package com.github.sdnwiselab.sdnwise.application;

import static com.github.sdnwiselab.sdnwise.application.ConstantsApplication.*;


public class VerificaQualidadeAr {

    public void verificarFatores(String dado) {
        
        System.out.println("\nAnálise do ambiente monitorado");

        String[] array = dado.split(";");
        float temp = Float.parseFloat(array[1]);
        float umid = Float.parseFloat(array[2]);
        float tvoc = Float.parseFloat(array[3]);
        float co2 = Float.parseFloat(array[4]);

        if (temp < MAX_TEMP && temp > MIN_TEMP) { //temperatura fora da faixa
            if (umid < MAX_UMID && umid > MIN_UMID) {  //umidade fora da faixa
                if (co2 < MAX_CO2 && tvoc < MAX_TVOC) {  //co2 e tvoc fora da faixa
                    System.out.println("\nAmbiente Adequado - Índice da Qualidade do Ar: BOA\n");
                    printResult(temp, umid, tvoc, co2);
                }
            }
        }

        if (temp > MAX_TEMP || temp < MIN_TEMP) { //temperatura fora da faixa
            if (umid > MAX_UMID || umid < MIN_UMID) {  //umidade fora da faixa
                if (co2 > MAX_CO2 && tvoc > MAX_TVOC) {  //co2 e tvoc fora da faixa
                    System.out.println("Ambiente Inadequado - Índica da Qualidade do Ar: RUIM\n");
                    printResult(temp, umid, tvoc, co2);
                } else {
                    System.out.println("Ambiente Inadequado - Conforto térmico: RUIM\n");
                    printResult(temp, umid, tvoc, co2);
                }
            }
        }

        if (temp > MAX_TEMP) { //temperatura ACIMA   
           // System.out.println("\nTemperatura: " + temp + " Graus - ACIMA do permitido...");
           // System.out.println("Verificando TVOC...\n");
            if (tvoc > MAX_TVOC) {
                System.out.println("TVOC: " + tvoc + " ppb ACIMA do permitido!"
                        + "\nNivel do CO2: " + analisaCO2(co2));
                if (analisaCO2(co2).equals(CRITICO)) {
             //       System.out.println("\nVerificar aparelhos condicionador de ar e a ventilação do ambiente!\n");
                    printResult(temp, umid, tvoc, co2);
                } else {
             //       System.out.println("\nVerificar ventilação do ambiente!\n");
                    printResult(temp, umid, tvoc, co2);
                }
            //    System.out.println("Concentração TVOC: " + tvoc + "ppb - " + analisaTVOC(tvoc));
                printResult(temp, umid, tvoc, co2);
            } else if (umid < MAX_UMID) {
                System.out.println("Instabilidade no conforto termico");
                printResult(temp, umid, tvoc, co2);
            }
        }

        if (co2 > MAX_CO2) {
            System.out.println("\nConcentração de CO2: " + co2 + "ppm - ACIMA DO PERMITIDO!"
                    + "\nVerificando TVOC...");
            if (tvoc > MAX_TVOC) {
             //   System.out.println("Verificando Temperatura...");
                if (temp > MAX_TEMP) {
                    printResult(temp, umid, tvoc, co2);
                }
            } else if (tvoc < MAX_TVOC) {
            //    System.out.println("Concentração TVOC: " + tvoc + "ppb - " + analisaTVOC(tvoc));
                printResult(temp, umid, tvoc, co2);
            } else if (temp > MAX_TEMP) {
            //    System.out.println("\nVerificar aparelho condicionar de ar!");
            }
            printResult(temp, umid, tvoc, co2);
        }
        //umidade acima do permitido
        //tvoc acima do permitido
    }

    public String analisaTVOC(float tvoc) {
        if (tvoc > 500) {
            return CRITICO;
        } else if (tvoc <= 500 && tvoc >= 400) {
            return ALARMANTE;
        } else if (tvoc < 400 && tvoc >= 300) {
            return REGULAR;
        } else if (tvoc < 300) {
            return BOM;
        } else {
            return "Valor TVOC nao analisado!";
        }
    }

    public String analisaCO2(float co2) {
        if (co2 > 1500) {
            return CRITICO;
        } else if (co2 <= 1500 && co2 >= 1000) {
            return ALARMANTE;
        } else if (co2 < 1000 && co2 >= 850) {
            return REGULAR;
        } else if (co2 < 850) {
            return BOM;
        } else {
            return "Valor CO2 nao analisado!";
        }
    }

    public String analisaTemp(float temp) {

        if (temp > MAX_TEMP) {
            return TEMP_ELEVADA;
        } else if (temp < MIN_TEMP) {
            return TEMP_ABAIXO;
        }
        return TEMP_ADEQUADA;
    }

    public String analisaUmid(float umid) {
        if (umid > MAX_TEMP) {
            return UMID_ELEVADA;
        } else if (umid < MIN_TEMP) {
            return UMID_ABAIXO;
        }
        return UMID_ADEQUADA;
    }

    public void printResult(float temp, float umid, float tvoc, float co2) {
        System.out.println(TEMPERATURA + ": " + temp + " Graus - " + analisaTemp(temp) + "\n"
                + UMIDADE + ": " + umid + " % - " + analisaUmid(umid) + "\n"
                + TVOC + ": " + tvoc + " ppb - " + analisaTVOC(tvoc) + "\n"
                + CO2 + ": " + co2 + " ppm - " + analisaCO2(co2) + "\n");
    }
}
