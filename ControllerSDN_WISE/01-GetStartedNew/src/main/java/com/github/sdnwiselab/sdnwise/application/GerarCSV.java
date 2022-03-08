package com.github.sdnwiselab.sdnwise.application;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;



public class GerarCSV {

    public void gerarCsv(String dadosColetados){
        
        String[] dados = new String[]{dadosColetados};
        String path = "/home/bruna/infoMotes.csv";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
                        
            for (String line : dados) {
                bw.write(line);
                bw.newLine();
            }
            
        } catch (IOException e) {
            System.out.println("Erro ao inserir dados no csv");
        } 
    }

}