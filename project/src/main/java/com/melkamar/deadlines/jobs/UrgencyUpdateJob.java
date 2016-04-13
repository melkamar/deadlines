package com.melkamar.deadlines.jobs;

import com.melkamar.deadlines.services.api.InternalApi;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 11.04.2016 11:36
 */
@Component
public class UrgencyUpdateJob {
    private final Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    private InternalApi internalApi;

//    @Value("${update.urgency.interval}")
//    private final Long interval = 15 * 60 * 1000L;
//    private static final String interval = 5 * 1000L;
//    @Value()
//    private final String interval;
    @Value("${update.urgency.interval}")
    private String interval;

    @Scheduled(fixedDelayString = "${update.urgency.interval}")
    public void logCurrentTime() {
        logger.info("Updating urgency. Interval: "+interval);
        internalApi.updateAllUrgencies(true);
    }
}