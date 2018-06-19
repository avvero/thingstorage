//
// Built on Wed Oct 07 09:44:43 UTC 2015 by logback-translator
// For more information on configuration files in Groovy
// please see http://logback.qos.ch/manual/groovy.html

// For assistance related to this tool or configuration files
// in general, please contact the logback user mailing list at
//    http://qos.ch/mailman/listinfo/logback-user

// For professional support please see
//   http://www.qos.ch/shop/products/professionalSupport
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import com.avvero.flow.support.logback.MarkerSocketAppender

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.ERROR
import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.classic.Level.TRACE
import static ch.qos.logback.classic.Level.WARN

appender("file", RollingFileAppender) {
    file = "/var/log/service/thingstorage/service.log"
    encoder(PatternLayoutEncoder) {
        charset = java.nio.charset.StandardCharsets.UTF_8
        pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %5p %t %c{0}:%M:%L - %m%n"
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "/var/log/service/thingstorage/service.log.%d{yyyy-MM-dd}"
    }
}

logger("org", ERROR, ["file"])
logger("org.springframework", ERROR, ["file"])
logger("org.springframework.web", INFO, ["file"])
logger("org.springframework.web.filter", DEBUG, ["file"])
logger("org.hibernate", INFO, ["file"])
logger("org.hibernate.SQL", DEBUG, ["file"])
logger("com.avvero", TRACE, ["file"])
root(ERROR, ["file"])
