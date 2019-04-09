package com.interview.fluxoftweet.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class H2ConsoleServer {
    private org.h2.tools.Server webServer;

    private org.h2.tools.Server server;

    /**
     * When spring context is reflesh.H2 console will start in memory space.
     * @throws java.sql.SQLException
     */
    @EventListener(org.springframework.context.event.ContextRefreshedEvent.class)
    public void start() throws java.sql.SQLException {
        log.info("*********");
        this.webServer = org.h2.tools.Server.createWebServer("-webPort", "9098", "-webAllowOthers").start();
        this.server = org.h2.tools.Server.createTcpServer("-tcpPort", "9099", "-tcpAllowOthers").start();
    }

    /**
     * When spring context is closed.H2 console will stop.
     */
    @EventListener(org.springframework.context.event.ContextClosedEvent.class)
    public void stop() {
        this.webServer.stop();
        this.server.stop();
    }
}
