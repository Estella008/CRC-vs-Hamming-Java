package com.mycompany.tp1_arquitetura;

import java.io.File;

public class TP1_Arquitetura {

    public static void main(String[] args) {
        File livro = new File("Moby Dick-Cap1.txt");
        double probabilidadeErro []= {0.1,0.2,0.3,0.4,0.5};
        long resultadoCrc[] = new long[probabilidadeErro.length];
        long resultadoHamming[]= new long[probabilidadeErro.length];
        long tempoI;
        long tempoF;

        for (int i = 0; i < probabilidadeErro.length; i++) {

            System.out.println("Executando probablidade de erro: " + probabilidadeErro[i]*100+"%");
            Canal canal = new Canal(probabilidadeErro[i]);
            System.out.println("Executando CRC");
            Transmissor transmCRC = new Transmissor(livro, canal, Estrategia.CRC);
            //é necessário modificar a estratégia e avaliar o desempenho
            Receptor receberCRC = new Receptor(canal, Estrategia.CRC);

            canal.conectaTransmissor(transmCRC);
            canal.conectaReceptor(receberCRC);

            //mensurando o tempo de execução
            tempoI = System.currentTimeMillis();
            transmCRC.enviaDado();
            tempoF = System.currentTimeMillis();

            resultadoCrc[i] = (tempoF - tempoI);
            receberCRC.gravaMensArquivo((int)(probabilidadeErro[i]*100));

            System.out.println("Executando Hamming");
            Transmissor transmHamming = new Transmissor(livro, canal, Estrategia.HAMMING);
            //é necessário modificar a estratégia e avaliar o desempenho
            Receptor receberHamming = new Receptor(canal, Estrategia.HAMMING);

            canal.conectaTransmissor(transmHamming);
            canal.conectaReceptor(receberHamming);

            //mensurando o tempo de execução
            tempoI = System.currentTimeMillis();
            transmHamming.enviaDado();
            tempoF = System.currentTimeMillis();

            resultadoHamming[i] = (tempoF - tempoI);
            receberHamming.gravaMensArquivo((int)(probabilidadeErro[i]*100));


        }
        for (int i = 0; i < probabilidadeErro.length; i++) {
            System.out.println("Probabilidade de erro: " +(probabilidadeErro[i]*100)+"   CRC: "+resultadoCrc[i]+
                    "   Hamming: "+resultadoHamming[i]);
        }

    }
}
