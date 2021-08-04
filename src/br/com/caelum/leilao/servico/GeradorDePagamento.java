package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.repositorio.RepositorioDeLeiloes;
import br.com.caelum.leilao.repositorio.RepositorioDePagamentos;

import java.util.Calendar;
import java.util.List;

public class GeradorDePagamento {

    private RepositorioDeLeiloes leiloes;
    private Avaliador avaliador;
    private RepositorioDePagamentos pagamentos;

    public GeradorDePagamento(
            RepositorioDeLeiloes leiloes,
            RepositorioDePagamentos pagamentos,
            Avaliador avaliador
    ) {
        this.leiloes = leiloes;
        this.pagamentos = pagamentos;
        this.avaliador = avaliador;
    }

    public void gera() {
        List<Leilao> leiloesEncerrados = this.leiloes.encerrados();

        for(Leilao leilao : leiloesEncerrados) {
            this.avaliador.avalia(leilao);

            Pagamento novoPagamento = new Pagamento(avaliador.getMaiorLance(), Calendar.getInstance());
            this.pagamentos.salva(novoPagamento);
        }
    }
}
