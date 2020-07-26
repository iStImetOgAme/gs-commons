package com.genius.framework.multitenancy.event;

import org.springframework.context.ApplicationEvent;

public class TenantEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    private String message = "";

    public TenantEvent(Object source) {
        super(source);
    }

    public TenantEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
