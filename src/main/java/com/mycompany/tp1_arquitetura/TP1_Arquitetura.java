package com.mycompany.tp1_arquitetura;

import java.io.File;

public class TP1_Arquitetura {

    public static void main(String[] args) {
        File livro = new File("Moby Dick.txt");
        double probabilidadeErro []= {0.1,0.2,0.3,0.4,0.5};
        
        //é necessário modificar as probabilidades e avaliar o desempenho
        Canal canal = new Canal(0.1);
        
        Transmissor transm = new Transmissor("Teste:?*/", canal, Estrategia.CRC);
        //é necessário modificar a estratégia e avaliar o desempenho
        Receptor receber = new Receptor(canal, Estrategia.CRC); 
        
        canal.conectaTransmissor(transm);
        canal.conectaReceptor(receber);
        
        //mensurando o tempo de execução
        long tempoI = System.currentTimeMillis();
        transm.enviaDado();
        long tempoF = System.currentTimeMillis();
        
        System.out.println("Tempo total: " + (tempoF - tempoI));
        
        System.out.println(receber.getMensagem());
    }
}
