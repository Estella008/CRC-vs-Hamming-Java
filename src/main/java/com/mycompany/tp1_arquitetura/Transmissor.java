package com.mycompany.tp1_arquitetura;

import java.io.*;

public class Transmissor {
    private String mensagem= "";
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

        //caracteres inválidos para UTF-8
        if(valorSimbolo > 255){
            valorSimbolo = 0; //quebra de linha
        }
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

        boolean[] polinomio = {true, false, false, false, false, true, true, true, true}; //1101 - vou colocar só um exemplo, depois tento mudar aaaaaa
        boolean[] dados = new boolean[bits.length + polinomio.length - 1]; //aqui eu crio o vetor do tamanho do polinomio-1, mas ainda tenho que rever isso"

        for (int i = 0; i < bits.length; i++) { //copiando os bits originais
            dados[i] = bits[i];
        }


        //Fazer um laço pra pegar a mensagem
        for (int i = 0; i < bits.length; i++) {
            if (dados[i]) { //realiza xor se for 1
                for (int j = 0; j < polinomio.length; j++) {
                    dados[i + j] ^= polinomio[j]; //como fazer xor aqui? tem alguma forma direta?
                //aqui faz o xor, custei descobrir
                }
            }
        }

        //geralmente depois disso faço a divisao xor, como fazer isso aqui? só deus... pesquiso depois
        boolean[] resultado = new boolean[bits.length + polinomio.length - 1];

        //bits originais
        for (int i = 0; i < bits.length; i++) {
            resultado[i] = bits[i];
        }

        //bits do crc que são da divisão do xor
        for (int i = 0; i < polinomio.length - 1; i++) {
            resultado[bits.length + i] = dados[bits.length + i];
        }
        
        return resultado;

    }

    private boolean valorParidadeHamming(int indiceParidade, boolean[] bits){
        boolean valorParidade = false;
        // Percorre os blocos que o bit de paridade cobre
        for (int i = indiceParidade; i < bits.length; i += 2 * (indiceParidade + 1)) {
            // Dentro de cada bloco, percorre os bits que esse bit de paridade verifica
            for (int k = i; k < i + (indiceParidade + 1) && k < bits.length; k++) {
                if (k != indiceParidade) {
                    // Aplica XOR apenas nos bits diferentes da posição da própria paridade
                    valorParidade ^= bits[k];
                }
            }
        }
        return valorParidade;
    }

    private boolean[] dadoBitsHamming(boolean[] bits) {
        boolean[] bitsCodificado = new boolean[bits.length+4]; // 8 bits de dados + 4 bits de paridade
        int indiceBits = 0;

        // Passo 1: Preencher os bits de dados (nos índices que não são de paridade)
        int potencia=0;
        for (int i = 0; i < bitsCodificado.length; i++) {
            if (i!=(int) Math.pow(2,potencia)) {
                bitsCodificado[i] = bits[indiceBits];
                indiceBits++;
            }else{
                potencia++;
            }
        }

        // Passo 2: Calcular os bits de paridade (após os dados estarem no lugar)
        bitsCodificado[0] = valorParidadeHamming(0, bitsCodificado); // paridade 1
        bitsCodificado[1] = valorParidadeHamming(1, bitsCodificado); // paridade 2
        bitsCodificado[3] = valorParidadeHamming(3, bitsCodificado); // paridade 4
        bitsCodificado[7] = valorParidadeHamming(7, bitsCodificado); // paridade 8

        return bitsCodificado;
    }


    
    public void enviaDado(){
        for(int i = 0; i < this.mensagem.length();i++){
            do{
                boolean bits[] = streamCaracter(this.mensagem.charAt(i));
                boolean[] bitsCodificado= null;
                
                if(this.tecnica == Estrategia.CRC){
                    bitsCodificado = dadoBitsCRC(bits);
                } else if (this.tecnica==Estrategia.HAMMING) {
                    bitsCodificado = dadoBitsHamming(bits);

                }


                //enviando a mensagem "pela rede" para o receptor (uma forma de testarmos esse método)
                this.canal.enviarDado(bitsCodificado);
            }while(this.canal.recebeFeedback() == false);
            
            
            
            //o que faremos com o indicador quando houver algum erro? qual ação vamos tomar com o retorno do receptor
        }
    }
}
