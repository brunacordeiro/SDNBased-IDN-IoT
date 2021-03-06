/*
 * Copyright (C) 2021 Bruna Michelly
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
package com.github.sdn_idn_iot.idn;

/**
 *
 * @author Bruna Michelly
 */

/* Modelo de Intenção

* <verb, object, modifiers, subject>
* Um verbo é uma operação que descreve a intenção com base em uma ontologia
* Objeto identifica um serviço, processo ou item que é o objetivo do verbo
* Modificadores são usados ​​para especializar ou parametrizar isso; 
* cada modificador pode ser marcado como 'essencial' ou 'desejável', indicando a preferência de priorização

@JSON
 
{
    "intent":{
        "id": 01,
        "name": "Qualidade do Ar no ambiente",
        "verb":"manter",       
        "object":"resolução",
    }    
    "Modifiers":{    
        "sensor":["sensor_1", "sensor_2", "sensor_3"],
        "values":["max_Sensor_1; min_Sensor_1"],
        "periodicidade": "intervalo_tempo",
        "local":"Lab01"
    }
}

*/

public class Intent {
    
    private int idIntent;
    private String nameIntent;    // periodic, alert or polling = modelo de interação
    private String verbIntent;
    private String objectIntent;
    private String sensor;     // temp. umid. co2
    private String values;
    private int periodicity;  // intervalo de tempo
    private int idMotes[];  //sensor - moteId - 
    
    
    public int getIdIntent(){
        return idIntent;
    }
    
    public void setIdIntent(int idIntent){
        this.idIntent = idIntent;
    }
    
    public String getNameIntent(){
        return nameIntent;
    }
    
    public void setNameIntent(String nameIntent){
        this.nameIntent = nameIntent;
    }
    
    public String getVerbIntent(){
        return verbIntent;
    }
    
    public void setVerbIntent(String verbIntent){
        this.verbIntent = verbIntent;
    }
    
    public String getObjectIntent(){
        return objectIntent;
    }
    
    public void setObjectIntent(String objectIntent){
        this.objectIntent = objectIntent;
    }
    
    public String getSensor(){
        return sensor;
    }
    
    public void setSensor(String sensor){
        this.sensor = sensor;
    }
    
    public String getValues(){
        return values;
    }
    
    public void setValues(String values){
        this.values = values;
    }
    
    public int getPeriodicity(){
        return periodicity;
    }
    
    public void setPeriodicity(int periodicity){
        this.periodicity = periodicity;
    }
    
    public int[] getIdMotes(){
        return idMotes;
    }
    
    public void setIdMotes(int[] idMotes){
        this.idMotes = idMotes;
    }
}