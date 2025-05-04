package com.mycompany.tp1_arquitetura;

import java.io.File;

public class TP1_Arquitetura {

    public static void main(String[] args) {
        File livro = new File("teste.txt");
        double probabilidadeErro []= {0.1,0.2,0.3,0.4,0.5};
        long resultadoCrc[] = new long[probabilidadeErro.length];
        long resultadoHamming[]= new long[probabilidadeErro.length];
        long tempoI;
        long tempoF;
        Canal canal = new Canal(0.0);

        Transmissor transmCRC = new Transmissor(livro, canal, Estrategia.CRC);
        //é necessário modificar a estratégia e avaliar o desempenho
        Receptor receberCRC = new Receptor(canal, Estrategia.CRC);

        canal.conectaTransmissor(transmCRC);
        canal.conectaReceptor(receberCRC);

        //mensurando o tempo de execução
        tempoI = System.currentTimeMillis();
        transmCRC.enviaDado();
        tempoF = System.currentTimeMillis();
        System.out.println(receberCRC.getMensagem());
        

    }
}
