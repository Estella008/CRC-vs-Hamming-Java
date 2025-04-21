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
        if((Math.pow(2,r))<(numeroBits+r+1)){
            return r;
        }
        return nParidadeHamming(numeroBits, r++);


    }
    
    private int dadoBitsHamming(boolean[] bits){
        int numeroParidade=nParidadeHamming(bits.length,0);
        boolean vetAux[] = new boolean[bits.length+numeroParidade];
        int potencia = 0;
        int ibits=0;
        for(int i=0; i<vetAux.length; i++){
            if(Math.pow(2,potencia)!=(i+1)){
                vetAux[i]=bits[ibits];
                ibits++;
            }
            potencia++;
        }

        
        /*sua implementação aqui!!!
        modifique o que precisar neste método
        */
        
        return vetAux.length;
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
