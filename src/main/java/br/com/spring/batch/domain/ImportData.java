package br.com.spring.batch.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "import_data")
public class ImportData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cpf;
    private String nomeCliente;
    private LocalDate dataNascimento;
    private String evento;
    private LocalDate dataEvento;
    private String tipoIngresso;
    private Double valorIngresso;
    private LocalDateTime horaImportacao;
    private Double taxaAdm;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public LocalDate getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(LocalDate dataEvento) {
        this.dataEvento = dataEvento;
    }

    public String getTipoIngresso() {
        return tipoIngresso;
    }

    public void setTipoIngresso(String tipoIngresso) {
        this.tipoIngresso = tipoIngresso;
    }

    public Double getValorIngresso() {
        return valorIngresso;
    }

    public void setValorIngresso(Double valorIngresso) {
        this.valorIngresso = valorIngresso;
    }

    public LocalDateTime getHoraImportacao() {
        return horaImportacao;
    }

    public void setHoraImportacao(LocalDateTime horaImportacao) {
        this.horaImportacao = horaImportacao;
    }

    public Double getTaxaAdm() {
        return taxaAdm;
    }

    public void setTaxaAdm(Double taxaAdm) {
        this.taxaAdm = taxaAdm;
    }
}
