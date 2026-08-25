package com.servicecops.project.utils;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OperationReturn {
    Integer returnCode;
    String returnMessage;

    public void setReturnCodeAndReturnMessage(Integer returnCode, String returnMessage) {
        this.returnCode = returnCode;
         this.returnMessage = returnMessage;
    }
}

