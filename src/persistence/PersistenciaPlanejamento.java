package persistence;

import java.io.IOException;
import model.Planejamento;

public interface PersistenciaPlanejamento {
    void salvar(String caminhoArquivo, Planejamento planejamento) throws IOException;

    Planejamento carregar(String caminhoArquivo) throws IOException, ClassNotFoundException;
}