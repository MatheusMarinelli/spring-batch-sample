package br.com.spring.batch.component;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * Essa classe é necessária para manter o job sempre de pé
 * com uma espécie de Listener
 *
 */
@Component
public class BatchLauncher {

    @Autowired
    @Qualifier("job")
    private Job job;

    @Autowired
    private JobLauncher jobLauncher;

    // cron = "segundo Minuto Hora Dia Mês Ano"
    @Scheduled(cron = "*/5 * * * * *") // configura para rodar a cada 5 segundos independentemente do horário
    public void performJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(job, new JobParametersBuilder().addString("startTime",String.valueOf(System.currentTimeMillis())).toJobParameters());
    }


}
