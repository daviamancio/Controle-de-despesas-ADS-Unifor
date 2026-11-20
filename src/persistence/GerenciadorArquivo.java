package persistence;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import model.Planejamento;

public class GerenciadorArquivo implements PersistenciaPlanejamento {

    @Override
    public void salvar(String caminhoArquivo, Planejamento planejamento) throws IOException {
        if (caminhoArquivo == null || caminhoArquivo.trim().isEmpty()) {
            throw new IOException("Caminho de arquivo inválido.");
        }
        if (planejamento == null) {
            throw new IOException("Planejamento nulo não pode ser salvo.");
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminhoArquivo))) {
            oos.writeObject(planejamento);
            oos.flush();
        }
    }

    @Override
    public Planejamento carregar(String caminhoArquivo) throws IOException, ClassNotFoundException {
        if (caminhoArquivo == null || caminhoArquivo.trim().isEmpty()) {
            throw new IOException("Caminho de arquivo inválido.");
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminhoArquivo))) {
            Object obj = ois.readObject();
            if (!(obj instanceof Planejamento)) {
                throw new IOException("O arquivo não contém um objeto Planejamento válido.");
            }
            return (Planejamento) obj;
        }
    }
}