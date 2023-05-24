package com.rviewer.beertap.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EntityNotFoundException extends RuntimeException {

    private Class entityClass;

    private Object entityKey;

    @Override
    public String getMessage() {
        String message = "Could not find Object of type " + this.entityClass.getSimpleName();
        if (this.entityKey != null) message += " with id " + entityKey;
        return message;
    }
}
