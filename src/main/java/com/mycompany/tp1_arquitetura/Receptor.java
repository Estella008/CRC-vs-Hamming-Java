package com.mycompany.tp1_arquitetura;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLOutput;

public class Receptor {
    
    //mensagem recebida pelo transmissor
    private String mensagem;
    private final Estrategia tecnica;
    private final Canal canal;

    public Receptor(Canal canal, Estrategia tecnica) {
        //mensagem vazia no inicio da execução
        this.mensagem = "";
        this.tecnica = tecnica;
        this.canal = canal;
    }
    
    public String getMensagem() {
        return mensagem;
    }
    // a função decodificarDado foi separada para reaproveitamento do metodo de converter bits em número inteiro
    private int bitsParaInteiro(boolean[] bits) {
        int resultado = 0;
        int expoente = bits.length - 1;

        for (int i = 0; i < bits.length; i++) {
            if (bits[i]) {
                resultado += Math.pow(2, expoente);
            }
            expoente--;
        }

        return resultado;
    }


    private boolean decodificarDado(boolean bits[]){
        int codigoAscii = bitsParaInteiro(bits);
        System.out.print("Bits recebidos: ");
        for (boolean b : bits) System.out.print(b ? 1 : 0);
        System.out.println(" (tamanho: " + bits.length + ")");
//        int expoente = bits.length-1;
//
//        //converntendo os "bits" para valor inteiro para então encontrar o valor tabela ASCII
//        for(int i = 0; i < bits.length;i++){
//            if(bits[i]){
//                codigoAscii += Math.pow(2, expoente);
//            }
//            expoente--;
//        }

        //concatenando cada simbolo na mensagem original
        this.mensagem += (char)codigoAscii;

        //esse retorno precisa ser pensado... será que o dado sempre chega sem ruído???
        return true;

    }

    private boolean[] decoficarDadoCRC(boolean bits[]){
        boolean[] polinomio = {true, true, false, true}; // 1101
        boolean[] dados = bits.clone(); // clona os bits recebidos

        // Aplica novamente a divisão para obter o resto
        for (int i = 0; i < bits.length - polinomio.length + 1; i++) {
            if (dados[i]) {
                for (int j = 0; j < polinomio.length; j++) {
                    dados[i + j] ^= polinomio[j];
                }
            }
        }

        return dados;
    }
    //função para verificar se o resto do CRC é zero
    private boolean verificarRestoCRC(boolean[] resto){

        for (boolean bit : resto) {
            if (bit) {
                return false;
            }
        }
        return true;
    }
    private boolean[] extrairBitsCRC(boolean[] bits) {
        //extrai os bits originais
        boolean[] bitsOriginais = new boolean[bits.length - 3];
        for (int i = 0; i < bitsOriginais.length; i++) {
            bitsOriginais[i] = bits[i];
        }
        return bitsOriginais;
    }

        //implemente a decodificação Hemming aqui e encontre os 
        //erros e faça as devidas correções para ter a imagem correta
    private boolean valorParidade(int indiceParidade, boolean[] bits) {
        boolean resultado = false;
        for (int i = indiceParidade; i < bits.length; i += 2 * (indiceParidade + 1)) {
            // Dentro de cada bloco, percorre os bits que esse bit de paridade verifica
            for (int k = i; k < i + (indiceParidade + 1) && k < bits.length; k++) {

                    resultado ^= bits[k];

            }
        }
        return resultado;
    }

    
    private boolean[] decoficarDadoHammig(boolean bits[]){
        boolean[] paridades = new boolean[4];

        // Calculando as paridades
        paridades[3] = valorParidade(0, bits);
        paridades[2] = valorParidade(1, bits);
        paridades[1] = valorParidade(3, bits);
        paridades[0] = valorParidade(7, bits);

        // Verificando se há erro
        boolean erro = false;
        for (int i = 0; i < paridades.length; i++) {
            if (paridades[i]) {
                erro = true;
            }
        }

        // Se houver erro, corrigimos o bit
        if (erro) {
            // Calculando o número binário que representa a posição do bit com erro
            int bitErrado = bitsParaInteiro(paridades);

            // Corrige o bit na posição indicada
            if (bits[bitErrado - 1]) {
                bits[bitErrado - 1] = false;
            } else {
                bits[bitErrado - 1] = true;
            }
        }

        // Extrair os dados corrigidos
        return extrairDadosHamming(bits);



        }
    boolean[] extrairDadosHamming(boolean[] bitsComParidade) {
        boolean[] dadosOriginais = new boolean[8]; // Para 8 bits de dados
        int indiceDado = 0;

        // Posições dos bits de paridade (índices base 0)
        int[] posicoesParidade = {0, 1, 3, 7}; // Para 12 bits (8 dados + 4 paridades)

        // Extração dos dados (ignora os bits de paridade)
        for (int i = 0; i < bitsComParidade.length; i++) {
            boolean ehParidade = false;

            // Verifica se o índice i é um bit de paridade
            for (int pos : posicoesParidade) {
                if (i == pos) {
                    ehParidade = true;
                    break;
                }
            }

            if (!ehParidade && indiceDado < dadosOriginais.length) {
                dadosOriginais[indiceDado] = bitsComParidade[i];
                indiceDado++;
            }
        }

        return dadosOriginais;
    }

        

    
    
    //recebe os dados do transmissor
    public void receberDadoBits(){
        boolean[] bitsRecebidos = this.canal.recebeDado();
        boolean[] bitsCorrigidos = null;
        boolean sucesso = false;

        if (this.tecnica == Estrategia.CRC) {
            // Usar os bits recebidos diretamente para a verificação
            boolean[] resto = decoficarDadoCRC(bitsRecebidos);
            sucesso = verificarRestoCRC(resto);

            if (sucesso) {
                // Extrair os bits originais diretamente dos bits recebidos
                boolean[] bitsOriginais = extrairBitsCRC(bitsRecebidos);
                decodificarDado(bitsOriginais);
            }

        } else if(this.tecnica== Estrategia.HAMMING) {
            bitsCorrigidos= decoficarDadoHammig(bitsRecebidos);
            sucesso = decodificarDado(bitsCorrigidos);

        }

        this.canal.enviaFeedBack(sucesso);

    }
    

    public void gravaMensArquivo()  {

        String nomeArquivo= "";
        if(this.tecnica == Estrategia.CRC){
            nomeArquivo= "Livro_CRC.txt";
        }else if(this.tecnica== Estrategia.HAMMING){
            nomeArquivo= "Livro_Hamming.txt";
        }
        try(BufferedWriter wr = new BufferedWriter(new FileWriter(nomeArquivo))){
            wr.write(this.mensagem);

        }catch (IOException e){
            System.out.println("Erro ao gravar arquivo");
        }


        /*
        aqui você deve implementar um mecanismo para gravar a mensagem em arquivo
        */
    }
}
