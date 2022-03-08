/*
 * Copyright (C) 2021 miche
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.sdnwiselab.sdnwise.application;

import static com.github.sdnwiselab.sdnwise.application.ConstantsApplication.*;

/**
 *
 * @author Bruna Michelly
 */
public class VerificaPeriodico {

    VerificaQualidadeAr verQld = new VerificaQualidadeAr();
 
    public void verDadosPeriodico(String dado, int mote) {

        switch (dado) {
            case MESSAGE_NOTIFY_REGISTER_EVENT:
            case MESSAGE_ERROR_REGISTER_EVENT:
            case MESSAGE_ERROR_INTERACTION:
            case MESSAGE_ERROR_RECEIVE:
                System.out.println(dado);
                break;
            default:
                String[] array = dado.split(";");
                int type = Integer.parseInt(array[0]);
                float valueData = Float.parseFloat(array[1]);
                String hora = array[2];
                analisarDados(type, valueData, hora, mote);
        }
    }

    public void analisarDados(int type, float value, String hora, int mote) {
        String typeData = null, typeValue = null, analise = null;

        switch (type) {
            case 2:
                typeData = TEMPERATURA;
                typeValue = value + " Graus";
                analise = verQld.analisaTemp(value);
                break;
            case 3:
                typeData = UMIDADE;
                typeValue = value + " %";
                analise = verQld.analisaUmid(value);
                break;
            case 4:
                typeData = TVOC;
                typeValue = value + " ppb";
                analise = verQld.analisaTVOC(value);
                break;
            case 5:
                typeData = CO2;
                typeValue = value + " ppm";
                analise = verQld.analisaCO2(value);
                break;
            default:
                System.out.println("Erro ao identificar o tipo do dado monitorado!");
        }

        System.out.println("\nDado Analisado: " + typeData + " - " + typeValue
                + "\nAnalise: " + analise
                + "\nHora da Leitura: " + hora
                + "\nMote: " + mote);
    }

}
