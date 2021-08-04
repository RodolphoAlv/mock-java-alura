package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.dominio.Usuario;
import br.com.caelum.leilao.repositorio.RepositorioDeLeiloes;
import br.com.caelum.leilao.repositorio.RepositorioDePagamentos;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class GeradorDePagamentosTest {

    private RepositorioDeLeiloes leiloes;
    private RepositorioDePagamentos pagamentos;
    private Avaliador avaliador;

    private Usuario jose;
    private Usuario maria;

    @Before
    public void init() {
        this.leiloes = mock(RepositorioDeLeiloes.class);
        this.pagamentos = mock(RepositorioDePagamentos.class);
        this.avaliador = mock(Avaliador.class);

        this.jose = new Usuario("jose");
        this.maria = new Usuario("maria");
    }

    @Test
    public void deveGerarPagamentoParaLeilaoEncerrado() {
        Leilao leilao1 = new CriadorDeLeilao()
                .para("ps3")
                .lance(jose, 2000.0)
                .lance(maria, 2500.0)
                .constroi();

        when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao1));
        when(avaliador.getMaiorLance()).thenReturn(2500.0);

        GeradorDePagamento gerador = new GeradorDePagamento(leiloes, pagamentos, avaliador);
        gerador.gera();

        ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);

        verify(pagamentos).salva(argumento.capture());

        Pagamento pagamentoGerado = argumento.getValue();

        assertEquals(2500.0, pagamentoGerado.getValor(), 0.00001);
        assertTrue(pagamentoGerado.getValor() >= 2500.0);
    }
}
