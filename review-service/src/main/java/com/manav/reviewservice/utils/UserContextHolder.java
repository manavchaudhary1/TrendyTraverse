package com.manav.reviewservice.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class UserContextHolder {
    private static final ThreadLocal<UserContext> userContext = ThreadLocal.withInitial(UserContext::new);

    private UserContextHolder() {}
    public static UserContext getContext() {
        return userContext.get();
    }

    public static void setContext(UserContext context) {
        Assert.notNull(context, "Only non-null UserContext instances are permitted");
        userContext.set(context);
    }

    public static void setCorrelationId(String correlationId) {
        getContext().setCorrelationId(correlationId);
    }

    public static void clear() {
        userContext.remove();
    }
}

