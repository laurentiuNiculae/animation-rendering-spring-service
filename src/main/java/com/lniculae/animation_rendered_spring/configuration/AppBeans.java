package com.lniculae.animation_rendered_spring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.lniculae.AnimationParser.AnimationScriptParser;

@Configuration
public class AppBeans {
    
    @Bean
    public AnimationScriptParser getScriptParser() {
        return new AnimationScriptParser();
    }

    @Bean
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(300);
        executor.initialize();

        return executor;
    }

}
