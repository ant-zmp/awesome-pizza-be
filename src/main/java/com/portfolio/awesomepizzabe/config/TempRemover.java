package com.portfolio.awesomepizzabe.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TempRemover {

    private final GridFsTemplate gridFsTemplate;

    public TempRemover(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    @Scheduled(cron = "0 */15 * * * ?")
    public void removeTemp() {
        log.info("Temporary file cleanup...");
        gridFsTemplate.delete(Query.query(Criteria.where("metadata.temp").is(true)));
    }
}
