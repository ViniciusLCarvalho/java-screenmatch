import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.alura.screenmatch.excecao.ErroChaveDeApiException;
import br.com.alura.screenmatch.excecao.ErroDeConversaoDeAnoException;
import br.com.alura.screenmatch.modelos.Titulo;
import br.com.alura.screenmatch.modelos.TituloOmdb;

public class PrincipalComBusca {
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner input = new Scanner(System.in);
        String chave = buscaApiKey();

        System.out.println("Digite um filme para buscar: ");
        var busca = URLEncoder.encode(input.nextLine(), StandardCharsets.UTF_8);
        String endereco = "https://www.omdbapi.com/?t=" + busca + "&apikey=" + chave;

        input.close();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(endereco)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            input.close();

            String json = response.body();
            System.out.println(json);

            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .create();
            TituloOmdb meuTituloOmdb = gson.fromJson(json, TituloOmdb.class);
            System.out.println(meuTituloOmdb);

            Titulo meuTitulo = new Titulo(meuTituloOmdb);
            System.out.println("Titulo já convertido");
            System.out.println(meuTitulo);

            FileWriter escrita = new FileWriter("Filme.txt");
            escrita.write(meuTitulo.toString());
            escrita.close();
        } catch (NumberFormatException e) {
            System.out.println("Aconteceu um erro: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("busca falhou, verifique o endereço");
        } catch (ErroDeConversaoDeAnoException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("O programa finalizou corretamente!");
    }

    public static String buscaApiKey() throws IOException {
        String chave = null;
        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream("apiKey.txt"),
                        StandardCharsets.UTF_8));
        String linha;
        boolean chaveEncontrada = false;

        while ((linha = br.readLine()) != null) {
            if (linha.startsWith("APIKEYOMDB")) {
                String[] partes = linha.split("=", 2);
                if (partes.length == 2) {
                    chave = partes[1].trim();
                    chaveEncontrada = true;
                }
                break;
            }
        }
        br.close();

        if (!chaveEncontrada) {
            throw new ErroChaveDeApiException("Chave de API não encontrada!");
        }

        return chave;
    }
}