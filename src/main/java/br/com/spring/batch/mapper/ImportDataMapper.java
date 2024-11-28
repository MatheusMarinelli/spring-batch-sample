package br.com.spring.batch.mapper;

import br.com.spring.batch.domain.ImportData;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImportDataMapper implements FieldSetMapper<ImportData> {

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     *
     * Leitura de String do arquivo .csv e convertendo para os tipos específicos do Java
     *
     * @param fieldSet the {@link FieldSet} to map
     * @return
     * @throws BindException
     */
    @Override
    public ImportData mapFieldSet(FieldSet fieldSet) throws BindException {
        ImportData importData = new ImportData();

        // a string passada do metodo readString são dos parâmetros da linha 90 do bean ItemReader
        importData.setCpf(fieldSet.readString("cpf"));
        importData.setNomeCliente(fieldSet.readString("nomeCliente"));
        importData.setDataNascimento(LocalDate.parse(fieldSet.readString("dataNascimento"),dateFormatter));
        importData.setEvento(fieldSet.readString("evento"));
        importData.setDataEvento(LocalDate.parse(fieldSet.readString("dataEvento"),dateFormatter));
        importData.setTipoIngresso(fieldSet.readString("tipoIngresso"));
        importData.setValorIngresso(fieldSet.readDouble("valorIngresso"));
        importData.setHoraImportacao(LocalDateTime.now());

        return importData;
    }

}
