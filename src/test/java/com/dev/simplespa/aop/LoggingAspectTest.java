package com.dev.simplespa.aop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
class LoggingAspectTest {

	@Mock
	private JoinPoint joinPoint;

	@Mock
	private Signature signature;

	@Mock
	private ProceedingJoinPoint proceedingJoinPoint;

	private LoggingAspect loggingAspect;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		loggingAspect = new LoggingAspect();
	}

	@Test
	void logBeforeMethod_shouldReadMethodName(CapturedOutput output) {
		when(joinPoint.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn("testMethod");

		loggingAspect.logBeforeMethod(joinPoint);

		verify(joinPoint).getSignature();
		verify(signature).getName();
		assertThat(output.getOut())
			.contains("[AOP BEFORE] Entering method: [testMethod]");
	}

	@Test
	void profileExecutionTime_shouldReturnProceedResult() throws Throwable {
		String expected = "success";

		when(proceedingJoinPoint.proceed()).thenReturn(expected);

		Object result = loggingAspect.profileExecutionTime(proceedingJoinPoint);

		assertEquals(expected, result);
		verify(proceedingJoinPoint, times(1)).proceed();
	}

	@Test
	void profileExecutionTime_shouldPropagateException() throws Throwable {
		RuntimeException exception = new RuntimeException("boom");

		when(proceedingJoinPoint.proceed()).thenThrow(exception);

		RuntimeException thrown = assertThrows(
			RuntimeException.class,
			() -> loggingAspect.profileExecutionTime(proceedingJoinPoint)
		);

		assertEquals("boom", thrown.getMessage());
		verify(proceedingJoinPoint).proceed();
	}

}
