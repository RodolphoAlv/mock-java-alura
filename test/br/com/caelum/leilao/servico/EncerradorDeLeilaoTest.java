package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.repositorio.RepositorioDeLeiloes;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EncerradorDeLeilaoTest {

    private RepositorioDeLeiloes dao;
    private EnviadorDeEmail enviador;
    private Calendar antiga;
    private Calendar ontem;

    @Before
    public void init() {
        this.dao = mock(RepositorioDeLeiloes.class);
        this.enviador = mock(EnviadorDeEmail.class);

        this.antiga = Calendar.getInstance();
        this.antiga.set(1999, Calendar.JANUARY, 20);

        this.ontem = Calendar.getInstance();
        this.ontem.add(Calendar.DAY_OF_MONTH, -1);
    }

    @Test
    public void deveEncerrarLeiloesQueComecaramUmaSemanaAntes() {

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

        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(dao, enviador);
        encerrador.encerra();

        assertEquals(2, encerrador.getTotalEncerrados());
        assertTrue(leilao1.isEncerrado());
        assertTrue(leilao2.isEncerrado());

        InOrder inOrder = inOrder(dao, enviador);
        inOrder.verify(dao).atualiza(leilao1);
        inOrder.verify(enviador).envia(leilao1);

        inOrder.verify(dao).atualiza(leilao2);
        inOrder.verify(enviador).envia(leilao2);
    }

    @Test
    public void naoEncerraLeiloesComecadosOntem() {

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

        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(dao, enviador);
        encerrador.encerra();

        assertEquals(encerrador.getTotalEncerrados(), 0);
        assertFalse(leilao1.isEncerrado());
        assertFalse(leilao2.isEncerrado());

        verify(dao, never()).atualiza(leilao1);
        verify(dao, never()).atualiza(leilao2);
    }

    @Test
    public void semLeiloes() {

        when(dao.correntes()).thenReturn(new ArrayList<>());

        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(dao, enviador);
        encerrador.encerra();

        assertEquals(0, encerrador.getTotalEncerrados());
    }

    @Test
    public void deveAtualizarLeiloesEncerrados() {

        Leilao leilao1 = new CriadorDeLeilao()
                .para("tv")
                .naData(antiga)
                .constroi();

        when(dao.correntes()).thenReturn(Arrays.asList(leilao1));

        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(dao, enviador);
        encerrador.encerra();

        verify(dao, times(1)).atualiza(leilao1);
    }

    @Test
    public void deveContinuarAExecucaoMesmoQuandoODaoFalha() {

        Leilao leilao1 = new CriadorDeLeilao()
                .para("tv")
                .naData(antiga)
                .constroi();

        Leilao leilao2 = new CriadorDeLeilao()
                .para("geladeira")
                .naData(antiga)
                .constroi();

        when(dao.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

        // Criando uma exception do além
        doThrow(new RuntimeException()).when(dao).atualiza(leilao1);

        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(dao, enviador);
        encerrador.encerra();

        verify(dao).atualiza(leilao2);
        verify(enviador).envia(leilao2);

        verify(enviador, times(0)).envia(leilao1);

    }

    @Test
    public void nuncaInvocandoOMetodoEnviarEmail() {
        Leilao leilao1 = new CriadorDeLeilao()
                .para("tv")
                .naData(antiga)
                .constroi();

        Leilao leilao2 = new CriadorDeLeilao()
                .para("geladeira")
                .naData(antiga)
                .constroi();

        when(dao.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

        doThrow(new RuntimeException()).when(dao).atualiza(any(Leilao.class));

        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(dao, enviador);

        encerrador.encerra();

        verify(enviador, never()).envia(any(Leilao.class));
    }
}
