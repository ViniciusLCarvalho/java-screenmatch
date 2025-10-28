package br.com.alura.screenmatch.excecao;

public class ErroChaveDeApiException extends RuntimeException {
    public ErroChaveDeApiException(String mensagem){
        super(mensagem);
    }
}
