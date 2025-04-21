package com.mycompany.tp1_arquitetura;

import java.io.*;

public class Transmissor {
    private String mensagem;
    private Canal canal;
    private File arquivo;
    private Estrategia tecnica;

    public Transmissor(String mensagem, Canal canal, Estrategia tecnica) {
        this.mensagem = mensagem;
        this.canal = canal;
        this.tecnica = tecnica;
    }
    
    public Transmissor(File arq, Canal canal, Estrategia tecnica) {
        this.arquivo = arq;
        this.canal = canal;
        this.tecnica = tecnica;
        
        carregarMensagemArquivo();
    }

    // Método responsável por carregar o conteúdo de um arquivo de texto (.txt) para a variável 'mensagem'
    private void carregarMensagemArquivo() {

        // Tenta abrir o arquivo informado usando BufferedReader (leitor de texto com buffer)
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {

            String linha; // Variável temporária para armazenar cada linha lida

            // Lê o arquivo linha por linha, até chegar ao final (quando readLine() retorna null)
            while ((linha = br.readLine()) != null) {
                // Adiciona a linha lida à variável 'mensagem' junto com uma quebra de linha (\n)
                mensagem += linha + "\n";
            }

            // Caso o arquivo não seja encontrado, lança uma exceção
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);

            // Caso ocorra qualquer erro de leitura (IO), lança uma exceção
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    
    //convertendo um símbolo para "vetor" de boolean (bits)
    private boolean[] streamCaracter(char simbolo){
        
        //cada símbolo da tabela ASCII é representado com 8 bits
        boolean bits[] = new boolean[8];
        
        //convertendo um char para int (encontramos o valor do mesmo na tabela ASCII)
        int valorSimbolo = (int) simbolo;
        int indice = 7;
        
        //convertendo cada "bits" do valor da tabela ASCII
        while(valorSimbolo >= 2){
            int resto = valorSimbolo % 2;
            valorSimbolo /= 2;
            bits[indice] = (resto == 1);
            indice--;
        }
        bits[indice] = (valorSimbolo == 1);
        
        return bits;
    } 
    
    private boolean[] dadoBitsCRC(boolean bits[]){
        
        /*sua implementação aqui!!!
        modifique o que precisar neste método
        */
        
        return bits;
    }
    private int nParidadeHamming(int numeroBits, int r){ //r= número de paridades
        if((Math.pow(2,r))>=(numeroBits+r+1)){
            return r;
        }
        return nParidadeHamming(numeroBits, r+1);


    }

    private boolean[] dadoBitsHamming(boolean[] bits) {
        // Calcula quantos bits de paridade são necessários
        int numeroParidade = nParidadeHamming(bits.length, 0);

        // Cria um vetor auxiliar com espaço para os bits de dados + bits de paridade
        boolean vetAux[] = new boolean[bits.length + numeroParidade];

        int potencia = 0; // Usado para identificar as posições que são potências de 2
        int ibits = 0;    // Índice para percorrer o vetor original de dados

        // Preenche vetAux com os bits de dados nas posições que NÃO são potências de 2
        for (int i = 0; i < vetAux.length; i++) {
            if ((int)Math.pow(2, potencia) != (i + 1)) {
                // Posição não é de paridade, insere bit de dados
                vetAux[i] = bits[ibits];
                ibits++;
            } else {
                // Posição de paridade, pula
                potencia++;
            }
        }

        // Agora vamos calcular os valores dos bits de paridade
        int indiceParidade;
        int potencia2 = 0;

        // Para cada bit de paridade necessário
        while (potencia2 < numeroParidade) {
            // A posição do bit de paridade é sempre 2^potencia2 - 1 (índice começa em 0)
            indiceParidade = (int)Math.pow(2, potencia2) - 1;

            boolean vParidade = false; // Acumulador para calcular a paridade (XOR)

            // Percorre os blocos que o bit de paridade cobre
            for (int i = indiceParidade; i < vetAux.length; i += 2 * (indiceParidade + 1)) {
                // Dentro de cada bloco, percorre os bits que esse bit de paridade verifica
                for (int k = i; k < i + (indiceParidade + 1) && k < vetAux.length; k++) {
                    if (k != indiceParidade) {
                        // Aplica XOR apenas nos bits diferentes da posição da própria paridade
                        vParidade ^= vetAux[k];
                    }
                }
            }

            // Define o valor do bit de paridade calculado
            vetAux[indiceParidade] = vParidade;
            potencia2++;
        }

        // Retorna o vetor final com bits de dados e de paridade
        return vetAux;
    }

    
    public void enviaDado(){
        for(int i = 0; i < this.mensagem.length();i++){
            do{
                boolean bits[] = streamCaracter(this.mensagem.charAt(i));
                
                /*-------AQUI você deve adicionar os bits do códico CRC para contornar os problemas de ruidos
                            você pode modificar o método anterior também
                    boolean bitsCRC[] = dadoBitsCRC(bits);
                */

                //enviando a mensagem "pela rede" para o receptor (uma forma de testarmos esse método)
                this.canal.enviarDado(bits);
            }while(this.canal.recebeFeedback() == false);
            
            
            
            //o que faremos com o indicador quando houver algum erro? qual ação vamos tomar com o retorno do receptor
        }
    }
}
