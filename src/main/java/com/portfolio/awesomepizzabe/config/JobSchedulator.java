package com.portfolio.awesomepizzabe.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobSchedulator {

    private final GridFsTemplate gridFsTemplate;

    public JobSchedulator(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    /**
     * The removeTemp method represents a scheduled job. Every 15 minutes starting from 00, all images that are not associated to any product are deleted.
     * The method searches for all the file with field 'temp' set to true and deletes them.
     */
    @Scheduled(cron = "0 */15 * * * ?")
    public void removeTemp() {
        log.info("Temporary file cleanup...");
        gridFsTemplate.delete(Query.query(Criteria.where("metadata.temp").is(true)));
    }
}
