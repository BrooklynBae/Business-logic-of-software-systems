package com.blps_lab1.demo.config;

import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jcr.Repository;

@Configuration
public class JackrabbitJcaConfig {

    @Bean
    public Repository jackrabbitRepository() {
        try {
            Repository repository = new Jcr(new Oak()).createRepository();

            System.out.println(">>> [JCA Resource Adapter] КИС Apache Jackrabbit Oak успешно инициализирована через JCR-интерфейс.");
            return repository;
        } catch (Exception e) {
            throw new RuntimeException("Критическая ошибка инициализации JCA-адаптера Jackrabbit Oak", e);
        }
    }
}