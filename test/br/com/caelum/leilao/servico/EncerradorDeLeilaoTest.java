package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.repositorio.RepositorioDeLeiloes;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EncerradorDeLeilaoTest {

    private RepositorioDeLeiloes dao;

    @Before
    public void init() {
        this.dao = mock(RepositorioDeLeiloes.class);
    }

    @Test
    public void deveEncerrarLeiloesQueComecaramUmaSemanaAntes() {
        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, Calendar.JANUARY, 20);

        Leilao leilao1 = new CriadorDeLeilao()
                .para("tv")
                .naData(antiga)
                .constroi();

        Leilao leilao2 = new CriadorDeLeilao()
                .para("geladeira")
                .naData(antiga)
                .constroi();

        dao.salva(leilao1);
        dao.salva(leilao2);

        List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);

        when(dao.correntes()).thenReturn(leiloesAntigos);

        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(dao);
        encerrador.encerra();

        assertEquals(2, encerrador.getTotalEncerrados());
        assertTrue(leilao1.isEncerrado());
        assertTrue(leilao2.isEncerrado());
    }

    @Test
    public void naoEncerraLeiloesComecadosOntem() {
        Calendar ontem = Calendar.getInstance();
        ontem.add(Calendar.DAY_OF_MONTH, -1);

        Leilao leilao1 = new CriadorDeLeilao()
                .para("tv")
                .naData(ontem)
                .constroi();

        Leilao leilao2 = new CriadorDeLeilao()
                .para("geladeira")
                .naData(ontem)
                .constroi();

        dao.salva(leilao1);
        dao.salva(leilao2);

        List<Leilao> leiloesDeOntem = Arrays.asList(leilao1, leilao2);

        when(dao.correntes()).thenReturn(leiloesDeOntem);

        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(dao);
        encerrador.encerra();

        assertEquals(encerrador.getTotalEncerrados(), 0);
        assertFalse(leilao1.isEncerrado());
        assertFalse(leilao2.isEncerrado());
    }

    @Test
    public void semLeiloes() {

        when(dao.correntes()).thenReturn(new ArrayList<>());

        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(dao);
        encerrador.encerra();

        assertEquals(0, encerrador.getTotalEncerrados());
    }

    @Test
    public void deveAtualizarLeiloesEncerrados() {

        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, Calendar.JANUARY, 3);

        Leilao leilao1 = new CriadorDeLeilao()
                .para("tv")
                .naData(antiga)
                .constroi();

        when(dao.correntes()).thenReturn(Arrays.asList(leilao1));

        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(dao);
        encerrador.encerra();

        verify(dao, times(1)).atualiza(leilao1);
    }
}
