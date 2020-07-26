package com.genius.framework.multitenancy.transactional;

import com.genius.framework.multitenancy.event.TenantEvent;
import com.genius.framework.multitenancy.provider.MultiTenantContextProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TenantTransactionalListener {

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(Object event) {
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(Object event) {

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void afterRollback(Object event) {

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION,fallbackExecution = true)
    public void afterCompletion(TenantEvent event) {
        //事务完成后自动清除自定义租户id
        MultiTenantContextProvider.cleanCurrentTenant();
    }
}
