package com.webank.wedatasphere.streamis.workflow.server.conf;

import com.webank.wedatasphere.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * created by yangzhiyue on 2021/4/20
 * Description:
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public Response toResponse(ValidationException e) {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("failed to validate request bean", e);
        }
        StringBuilder strBuilder = new StringBuilder();
        for (ConstraintViolation<?> cv : ((ConstraintViolationException) e).getConstraintViolations()) {
            strBuilder.append(cv.getMessage()).append(";");
        }
        Message message = Message.error(strBuilder.toString());
        return Message.messageToResponse(message);
    }
}
