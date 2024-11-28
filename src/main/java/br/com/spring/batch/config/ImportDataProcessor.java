package br.com.spring.batch.config;

import br.com.spring.batch.domain.ImportData;
import org.springframework.batch.item.ItemProcessor;

/**
 *
 * Simulando um processamento após a leitura dos dados do arquivo .csv
 *
 */
public class ImportDataProcessor implements ItemProcessor<ImportData, ImportData> {

    @Override
    public ImportData process(ImportData item) throws Exception {

        if (item.getTipoIngresso().equalsIgnoreCase("vip")) {
            item.setTaxaAdm(130.0);
        } else if (item.getTipoIngresso().equalsIgnoreCase("camarote")) {
            item.setTaxaAdm(80.0);
        } else {
            item.setTaxaAdm(50.0);
        }

        return item;
    }

}
