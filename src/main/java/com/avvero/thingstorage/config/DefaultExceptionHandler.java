package com.avvero.thingstorage.config;

import com.avvero.thingstorage.exception.ThingStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author Avvero
 */
@Slf4j
@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(value= Exception.class)
    public Object defaultErrorHandler(HttpServletRequest req,
                                      Exception e,
                                      HttpServletResponse response) throws Throwable {
        return defaultHandle(HttpServletResponse.SC_BAD_REQUEST, req, e, response);
    }

    @ExceptionHandler(value= ThingStorageException.class)
    public Object fgCommonExceptionHandler(HttpServletRequest req,
                                           ThingStorageException e,
                                           HttpServletResponse response) throws Throwable {
        return defaultHandle(HttpServletResponse.SC_BAD_REQUEST, req, e, response);
    }

    public Object defaultHandle(int status, HttpServletRequest req, Exception e, HttpServletResponse response) throws Exception {
        log.error(e.getMessage(), e);

        response.setStatus(status);
        e.setStackTrace(new StackTraceElement[]{});
        Boolean isAcceptJson = req.getHeader("accept").contains("application/json");
        if (isAcceptJson) {
            return new ResponseEntity<>(e, getHttpStatus(status));
        } else {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("exception", e);
            mav.addObject("message", e.getLocalizedMessage());

            mav.addObject("error", e.getLocalizedMessage());
            mav.addObject("status", status);
            mav.addObject("timestamp", new Date().getTime());
            mav.addObject("url", req.getRequestURL());
            return mav;
        }
    }

    private HttpStatus getHttpStatus(int status) {
        switch (status) {
            case HttpServletResponse.SC_BAD_REQUEST: {
                return HttpStatus.BAD_REQUEST;
            }
            case HttpServletResponse.SC_FORBIDDEN: {
                return HttpStatus.FORBIDDEN;
            }
            case HttpServletResponse.SC_NOT_FOUND: {
                return HttpStatus.NOT_FOUND;
            }
            default: {
                return HttpStatus.ACCEPTED;
            }
        }
    }

}
