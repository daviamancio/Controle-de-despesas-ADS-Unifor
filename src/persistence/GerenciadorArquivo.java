package persistence;

import model.Planejamento;

import java.io.*;

/**
 * Classe responsável pela camada de persistência local em ficheiro binário.
 * Utiliza o mecanismo nativo de serialização de objetos do Java.
 */
public class GerenciadorArquivo {

    /**
     * Serializa e grava o objeto Planejamento completo em um arquivo binário local.
     */
    public static void salvar(String caminhoArquivo, Planejamento planejamento) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminhoArquivo))) {
            oos.writeObject(planejamento);
        }
    }

    /**
     * Lê e desserializa o objeto Planejamento a partir de um arquivo binário local,
     * recuperando todo o estado anterior (participantes, despesas e vínculos).
     */
    public static Planejamento carregar(String caminhoArquivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminhoArquivo))) {
            return (Planejamento) ois.readObject();
        }
    }
}