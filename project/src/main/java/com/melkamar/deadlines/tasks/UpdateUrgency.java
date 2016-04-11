package com.melkamar.deadlines.tasks;

import com.melkamar.deadlines.services.api.InternalAPI;
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
public class UpdateUrgency {
    private final Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    private InternalAPI internalAPI;

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
        internalAPI.updateAllUrgencies();
    }
}
