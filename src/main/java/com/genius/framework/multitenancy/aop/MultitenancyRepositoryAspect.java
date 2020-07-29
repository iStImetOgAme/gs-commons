package com.genius.framework.multitenancy.aop;

import com.genius.framework.multitenancy.event.TenantEvent;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 多租户的切面事件处理类
 */
@Aspect
@Component
public class MultitenancyRepositoryAspect {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Pointcut("@annotation(com.genius.framework.multitenancy.annotation.MultiTenancyRepository)")
    public void multitenancyRepository() {
    }

    /**
     * 环绕增强
     */
    @Around("multitenancyRepository()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object res = null;
        try {
            res = joinPoint.proceed();
            return res;
        } finally {
            try {
                addPublisher(joinPoint, res);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 在切点发送事件的消息，在事务提交的监听器中监听TenantEvent事件，清理租户id
     * @param joinPoint
     * @param res
     */
    private void addPublisher(JoinPoint joinPoint, Object res) {
        applicationEventPublisher.publishEvent(new TenantEvent((joinPoint.getTarget())));
    }
}
