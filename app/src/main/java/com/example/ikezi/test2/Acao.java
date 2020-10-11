package com.example.ikezi.test2;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Acao {
    public static final BigDecimal CEM = new BigDecimal(100);
    private String nome;
	private String data;
	private Integer quantidade;
	private BigDecimal valor;
	private BigDecimal custo;
	private BigDecimal valorAtual;
    private BigDecimal valorAbertura;
	private BigDecimal percentualDia;
	private BigDecimal percentualTotal;

	public Acao(String nome, String data, Integer quantidade, BigDecimal valor, BigDecimal custo) {
		super();
		this.nome = nome;
		if ("CURRENT".equals(data)) {
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			this.data = fmt.format(new Date());
		} else {
			this.data = data;
		}
		this.quantidade = quantidade;
		this.valor = valor.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.custo = custo.setScale(2, BigDecimal.ROUND_HALF_EVEN);

	}

    public Acao(String nome, Integer quantidade, BigDecimal valor, BigDecimal valorAtual, BigDecimal valorAbertura) {
        super();
        this.nome = nome;
        this.quantidade = quantidade;
        this.valor = valor.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        this.valorAtual = valorAtual.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        this.valorAbertura = valorAbertura.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        try {
            this.percentualDia = this.valorAtual.multiply(CEM).divide(this.valorAbertura, 2, BigDecimal.ROUND_HALF_EVEN).subtract(CEM).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        }catch (  Exception d){
            d.printStackTrace();
            this.percentualDia = BigDecimal.ZERO;
        }
        try {
            this.percentualTotal = this.valorAtual.multiply(CEM).divide(this.valor, 2, BigDecimal.ROUND_HALF_EVEN).subtract(CEM).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        }catch (  Exception d){
            this.percentualTotal = BigDecimal.ZERO;
        }
    }
    public Acao(String nome, Integer quantidade, BigDecimal valor) {
        super();
        this.nome = nome;
        this.quantidade = quantidade;
        this.valor = valor.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

	public Acao() {
		super();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public BigDecimal getCusto() {
		return custo;
	}

	public void setCusto(BigDecimal custo) {
		this.custo = custo;
	}


	public BigDecimal getValorAtual() {
		return valorAtual;
	}

	public void setValorAtual(BigDecimal valorAtual) {
		this.valorAtual = valorAtual;
	}

	public BigDecimal getValorAbertura() {
		return valorAbertura;
	}

	public void setValorAbertura(BigDecimal valorAbertura) {
		this.valorAbertura = valorAbertura;
	}

	public void setPercentualDia(BigDecimal percentualDia) {
		this.percentualDia = percentualDia;
	}

	public void setPercentualTotal(BigDecimal percentualTotal) {
		this.percentualTotal = percentualTotal;
	}

	public BigDecimal getPercentualDia() {
		return percentualDia;
	}

	public BigDecimal getPercentualTotal() {
		return percentualTotal;
	}

	@Override
	public String toString() {
		return "Acao{" +
				"nome='" + nome + '\'' +
				", data='" + data + '\'' +
				", quantidade=" + quantidade +
				", valor=" + valor +
				", custo=" + custo +
				", valorAtual=" + valorAtual +
				", valorAbertura=" + valorAbertura +
				", percentualDia=" + percentualDia +
				", percentualTotal=" + percentualTotal +
				'}';
	}
}