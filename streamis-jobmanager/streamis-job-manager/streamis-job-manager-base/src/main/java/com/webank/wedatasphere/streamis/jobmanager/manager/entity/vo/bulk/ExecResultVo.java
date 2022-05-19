package com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.bulk;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExecResultVo extends BulkOperationResult<ExecResultVo.Result>{


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Result{
        /**
         * id
         */
        private Long id;
        /**
         * Message
         */
        private String message;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
