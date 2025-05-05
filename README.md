# Comparação de Algoritmos de Detecção e Correção de Erros: CRC vs Hamming

Este projeto tem como objetivo simular e comparar dois algoritmos clássicos de detecção e correção de erros: **CRC (Cyclic Redundancy Check)** e **Código de Hamming**, utilizando a linguagem Java. A comparação é feita com base no **tempo de execução** de ambos os métodos em cenários com **erro em um único bit**.

## 🧪 Objetivo

Avaliar a performance dos algoritmos **CRC** e **Hamming** em relação ao tempo de execução quando submetidos a diferentes probabilidades de erro, com foco em mensagens com erro em apenas um bit.

## 🛠️ Tecnologias Utilizadas

- Java (versão 8 ou superior)
- Ambiente de execução via Prompt de Comando ou Console

## 🚀 Como Executar

1. **Clone o repositório**:
   ```bash
   https://github.com/Estella008/CRC-vs-Hamming-Java.git

2. **Compile os arquivos em java**:
   javac com/mycompany/tp1_arquitetura/*.java
   
3. **Execute o programa**
   java com.mycompany.tp1_arquitetura.TP1_Arquitetura

## Funcionalidades
  -Simulação de transmissão de mensagens com diferentes probabilidades de erro.

  -Implementação dos algoritmos CRC e Hamming.

 -Medição e comparação do tempo de execução dos dois métodos.

  -Geração de dados para análise estatística

## Resultados esperados
  O sistema gera uma média de tempo de execução dos dois métodos em diferentes probabilidades de erro, permitindo identificar:

  -Estabilidade do Hamming em cenários com erro de 1 bit.

  -Aumento progressivo no tempo de execução do CRC conforme cresce a taxa de erro.

  -Limitações e vantagens de cada algoritmo.
  
