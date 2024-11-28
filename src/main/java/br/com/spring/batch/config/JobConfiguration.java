package br.com.spring.batch.config;

import br.com.spring.batch.domain.ImportData;
import br.com.spring.batch.mapper.ImportDataMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;
import java.util.Objects;

@Configuration
public class JobConfiguration {

    @Autowired
    private PlatformTransactionManager transactionManager;

    /**
     *
     * CONFIGURANDO O JOB QUE EXECUTARÁ ALGUMA AÇÃO
     * (NESSE EXEMPLO TEMOS A LEITURA DE UM ARQUIVO .CSV
     * E UMA ESCRITA DE SAÍDA EM UM POSTGRESQL)
     *
     * @param passoInicial -> STEP que fará a leitura do arquivo .csv e a escrita na base de dados
     * @param jobRepository -> repository da base de dados que grava os logs de execução do job
     * @return
     */
    @Bean
    public Job job(Step passoInicial, JobRepository jobRepository) {
        return new JobBuilder("geracao-tickets", jobRepository)
                .start(passoInicial)
                .next(moverArquivosStep(jobRepository))
                .incrementer(new RunIdIncrementer()) // gera IDs para auxiliar na rastreabilidade
                .build();
    }


    /**
     *
     * CONFIGURAÇÃO DO STEP DE LEITURA DO ARQUIVO .CSV
     * E ESCRITA NA BASE DE DADOS
     *
     * @param reader -> objeto responsável pela leitura do arquivo .csv
     * @param writer -> objeto resposável pela escrita das informações na base de dados
     * @param jobRepository
     * @return
     */
    @Bean
    public Step passoInicial(ItemReader<ImportData> reader, ItemWriter<ImportData> writer, JobRepository jobRepository){
        return new StepBuilder("passo-inicial", jobRepository)
                // <qual_objeto_estou_lendo, qual_objeto_gravarei_na_saida>
                .<ImportData, ImportData> chunk(5,transactionManager) // quantidade de itens que serão processados a cada execução
                .reader(reader)
                .processor(processor())
                .writer(writer)
                .build();
    }


    /**
     *
     * BEAN RESPONSÁVEL PELA LEITURA DO ARQUIVO .CSV
     *
     * @return
     */
    @Bean
    public ItemReader<ImportData> reader() {
        return new FlatFileItemReaderBuilder<ImportData>()
                .name("csv-reader")
                .resource(new FileSystemResource("src/main/resources/files/dados.csv"))
                .comments("--") //ignora linhas que possuem comentários
                .delimited() // informa que existe um delimitador separando uma informação da outra
                .delimiter(";") // informa que o delimitador é o ";"
                .names("cpf", "nomeCliente", "dataNascimento", "evento", "dataEvento", "tipoIngresso", "valorIngresso") // nome das colunas/atributos da classe na ordem que estão no arquivo
                //.targetType(ImportData.class) //classe que representa cada linha do arquivo
                .fieldSetMapper(new ImportDataMapper()) // classe que faz o 'De X Para' dos dados de string para o seu tipo específico para gravar corretamente na tabela SQL
                .build();
    }


    /**
     *
     * BEAN RESPONSÁVEL PELA ESCRITA DAS INFORMAÇÕES DO .CSV NO BANCO DE DADOS
     *
     * @param dataSource -> configurações do banco de dados para que o batch possa gravar seus logs nas suas respectivas tabelas
     * @return
     */
    @Bean
    public ItemWriter<ImportData> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<ImportData>()
                .dataSource(dataSource)
                .sql(
                        "INSERT INTO import_data (cpf, nome_cliente, data_nascimento, evento, data_evento, tipo_ingresso, valor_ingresso, hora_importacao, taxa_adm) VALUES " + // usar linguagem sql com "_" representando os campos da tabela
                                "(:cpf, :nomeCliente, :dataNascimento, :evento, :dataEvento, :tipoIngresso, :valorIngresso, :horaImportacao, :taxaAdm)" // usar camelCase representando os atributos da minha entidade
                )
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>()) // informa quem é o provedor dos parâmetros (fonte fornecedora das informações que serão gravadas)
                .build();
    }



    /**
     *
     * Criando o BEAN do processamento das informações lidas do arquivo
     *
     * @return
     */
    @Bean
    public ImportDataProcessor processor() {
        return new ImportDataProcessor();
    }



    /**
     *
     * Tasklet - uma tasklet é definida como sendo uma pequena tarefa a ser executada
     * no exemplo abaixo é a movimentação dos arquivos já lidos da pasta 'files'
     * para a pasta 'imported-files'
     *
     * @return
     */
    @Bean
    public Tasklet moverArquivosTasklet() {
        return (contribution, chunkContext) -> {
            File pastaOrigem = new File("src/main/resources/files");
            File pastaDestino = new File ( "src/main/resources/imported-files");

            if (!pastaDestino.exists()) {
                pastaDestino.mkdirs();
            }

            File[] arquivos = pastaOrigem.listFiles((dir, name) -> name.endsWith(".csv"));

            if (Objects.nonNull(arquivos)) {
                for (File arquivo : arquivos) {
                    File arquivoDestino = new File(pastaDestino, arquivo.getName());
                    if (arquivo.renameTo(arquivoDestino)) {
                        System.out.println("Arquivo movido: " + arquivo.getName());
                    } else {
                        throw new RuntimeException("Não foi possível mover o arquivo: " + arquivo.getName());
                    }
                }
            }
            return RepeatStatus.FINISHED;
        };
    }



    /**
     *
     * Configuração do STEP de mover o arquivo lido para outra pasta
     *
     * @param jobRepository
     * @return
     */
    @Bean
    public Step moverArquivosStep(JobRepository jobRepository) {
        return new StepBuilder("mover-arquivos",jobRepository)
                .tasklet(moverArquivosTasklet(), transactionManager)
                .build();
    }
}
