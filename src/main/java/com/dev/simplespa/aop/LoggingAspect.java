package com.dev.simplespa.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

	// Matches the execution of any method in PersonalLeaveRecordService matching any parameters
	@Before("execution(* com.dev.simplespa.module.personalleaverecord.PersonalLeaveRecordService.*(..))")
	public void logBeforeMethod(JoinPoint joinPoint) {
		String methodName = joinPoint.getSignature().getName();
		log.info("[AOP BEFORE] Entering method: [{}]", methodName);
	}

	// Tracks execution time of all methods inside the service package
	@Around("within(com.dev.simplespa.module.RecaptchaService)")
	public Object profileExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		long start = System.currentTimeMillis();

		// Let the actual business method execute
		Object output = proceedingJoinPoint.proceed();

		long executionTime = System.currentTimeMillis() - start;
		log.info("[AOP AROUND] Execution completed in {} ms", executionTime);

		return output;
	}
}
