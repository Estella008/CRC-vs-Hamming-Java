package com.mycompany.tp1_arquitetura;

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
    // a função decodificarDado foi separada para reaproveitamento do meto de converter bist em numero inteiro
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
    
    private boolean[] decoficarDadoCRC(boolean bits[]){ //socorrooooo aaaaaa

        //se a divisão for igual a 0, não houve errro
        boolean[] polinomio = {true, false, false, false, false, true, true, true, true}; //1101
        boolean[] dados = bits.clone(); //clona os bits recebidos

        //realizo a divisao, mas usando o polinomio
        for(int i = 0; i < bits.length- polinomio.length; i++){
            if(dados[i]){
                for (int j = 0; j < polinomio.length; j++){
                    dados[i + j] ^= polinomio[j];
                }
            }
        }

        //verifica se o resto da divisão ézero
        boolean semErro = true;
        for (int i = bits.length - (polinomio.length - 1); i < bits.length; i++) {
            if (dados[i]) {
                semErro = false;
                break;
            }
        }

        if (semErro) {
            //extrai os bits originais
            boolean[] bitsOriginais = new boolean[bits.length - (polinomio.length - 1)];
            for (int i = 0; i < bitsOriginais.length; i++) {
                bitsOriginais[i] = bits[i];
            }

            decodificarDado(bitsOriginais);
        }

        return semErro ? bits : null; // retorna os bits se ok, senão null
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

        //calculando paridade 1
        paridades[3] = valorParidade(0, bits);
        paridades[2] = valorParidade(1, bits);
        paridades[1] = valorParidade(3, bits);
        paridades[0] = valorParidade(7, bits);
         boolean erro = false;
         for(int i=0; i < paridades.length; i++){
             if (paridades[i]) {
                 erro = true;
             }
         }

         if (erro) {
             int bitErrado= bitsParaInteiro(paridades);
             if (bits[bitErrado]) {
                 bits[bitErrado] = false;
             }else {
                 bits[bitErrado] = true;
             }

         }


        return bits;
        }

        

    
    
    //recebe os dados do transmissor
    public void receberDadoBits(){

        boolean[] dadosRecebidos = this.canal.recebeDado();
        boolean[] dadosCorrigidos = null;
        boolean sucesso = false;

        if (this.tecnica == Estrategia.CRC) {
            dadosCorrigidos = decoficarDadoCRC(dadosRecebidos);
            sucesso = (dadosCorrigidos != null);
        } else {
            //tem que fazer o hammer aqui depois
            sucesso = decodificarDado(dadosRecebidos);
        }

        this.canal.enviaFeedBack(sucesso);

        /*if(this.tecnica == Estrategia.CRC){
            
        }else{
            
        }*/
        
        //aqui você deve trocar o médodo decofificarDado para decoficarDadoCRC (implemente!!)
        //decoficarDado(this.canal.recebeDado());
        
        
        
        //será que sempre teremos sucesso nessa recepção?????
        this.canal.enviaFeedBack(true);
    }
    
    //
    public void gravaMensArquivo(){
        /*
        aqui você deve implementar um mecanismo para gravar a mensagem em arquivo
        */
    }
}
