package com.servicecops.project.utils;

import lombok.*;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class OperationReturnObject extends OperationReturn{
    Object returnObject;

    public void setReturnCodeAndReturnObject(Integer returnCode, Object returnObject){
        this.returnObject = returnObject;
        this.returnCode = returnCode;
    }
    public void setCodeAndMessageAndReturnObject(Integer returnCode, String message, Object returnObject){
        this.returnObject = returnObject;
        this.returnCode = returnCode;
        this.returnMessage = message;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OperationReturnObject that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getReturnObject(), that.getReturnObject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getReturnObject());
    }
}
