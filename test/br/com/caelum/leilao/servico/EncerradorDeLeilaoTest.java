package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class EncerradorDeLeilaoTest {

    private LeilaoDao dao;

    @Before
    public void init() {
        this.dao = new LeilaoDao();
    }

    @After
    public void kamikaze() {
        dao.kamikaze();
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

        EncerradorDeLeilao encerrador = new EncerradorDeLeilao();
        encerrador.encerra();

        List<Leilao> encerrados = dao.encerrados();

        assertTrue(encerrados.get(0).isEncerrado());
        assertTrue(encerrados.get(1).isEncerrado());


    }
}
