@echo off
call java -cp config;exreco-0.2.3-SNAPSHOT.jar  org.exreco.log.server.JmsLoggingService config/log4j2-jmsLoggingService.xml
