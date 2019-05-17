package org.dhbw.mosbach.ai.tickets.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class InitEJB {
    private static final Logger logger = LoggerFactory.getLogger(InitEJB.class);

    @Resource
    private TimerService timerService;

    @PostConstruct
    private void init() {
        logger.info("Requesting delayed start.");
        final TimerConfig timerConfig = new TimerConfig("App Startup Timer", false);
        timerService.createSingleActionTimer(120_000, timerConfig);
    }

    @Timeout
    private void startApp(Timer timer) throws Exception {
        logger.info("Starting Business Application [{}].", timer.getInfo());
        logger.info("...timer [{}]", timer);
    }

    @PreDestroy
    private void stopApp() {
        logger.info("Stopping Business Application.");
    }
}
