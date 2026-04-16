package com.dev.simplespa.module.mail.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;

import java.util.List;
import java.util.Map;

import com.dev.simplespa.module.mail.MailConfig;
import com.dev.simplespa.module.mail.support.MailServiceHelper;
import org.apache.velocity.app.VelocityEngine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MailServiceHelperTest {
	@Test
	void testBuildMailTemplate() {
		// given
		String simpleMailTemplate = "templates/mail/simpleMailTemplate.vm";
		int x = 12;
		int y = 34;
		Map<String, Object> templateParams = Map.of("x", x, "y", y);

		// when
		String cookedHtml = MailServiceHelper.buildMailTemplate(simpleMailTemplate, templateParams);

		// then
		assertThat(cookedHtml).contains(Integer.toString(x))
			.contains(Integer.toString(y))
			.doesNotContain("$x")
			.doesNotContain("$y");
	}

	@Test
	void testBuildMailTemplateWithNoParams() {
		// given
		String simpleMailTemplate = "templates/mail/simpleMailTemplate.vm";
		Map<String, Object> emptyTemplateParams = Map.of();

		// when
		String cookedHtml =
			MailServiceHelper.buildMailTemplate(simpleMailTemplate, emptyTemplateParams);

		// then
		assertThat(cookedHtml).contains("$x").contains("$y");
	}

	@Test
	void testConfigWithSubjectPrefixesWithNoPrefix() {
		// given
		MailConfig baseConfig = new MailConfig(
			List.of("integration@a.b"),
			"noreply@a.b",
			"This is a test email",
			"templates/mail/simpleMailTemplate.vm",
			List.of("DEV-simple-spa"));
		List<String> emptyPrefixes = List.of();

		// when
		MailConfig configWithNoPrefix =
			MailServiceHelper.configWithSubjectPrefixes(baseConfig, emptyPrefixes);

		// then
		assertThat(configWithNoPrefix).usingRecursiveComparison().isEqualTo(baseConfig);
	}

	@Test
	void testConfigWithSubjectPrefixesWithOnePrefix() {
		// given
		MailConfig baseConfig = new MailConfig(
			List.of("integration@a.b"),
			"noreply@a.b",
			"This is a test email",
			"templates/mail/simpleMailTemplate.vm",
			List.of("DEV-simple-spa"));

		List<String> prefixes = List.of("ABC");
		String newSubject = "[ABC] This is a test email";

		// when
		MailConfig configWithNoPrefix =
			MailServiceHelper.configWithSubjectPrefixes(baseConfig, prefixes);

		// then
		assertThat(configWithNoPrefix).isNotNull();
		assertThat(configWithNoPrefix.from()).isEqualTo(baseConfig.from());
		assertThat(configWithNoPrefix.to()).hasSameElementsAs(baseConfig.to());
		assertThat(configWithNoPrefix.htmlTemplate()).isEqualTo(baseConfig.htmlTemplate());

		assertThat(configWithNoPrefix.subject()).isEqualTo(newSubject);
	}

	@Test
	void testConfigWithSubjectPrefixesWithMultiplePrefixes() {
		// given
		MailConfig baseConfig = new MailConfig(
			List.of("integration@a.b"),
			"noreply@a.b",
			"This is a test email",
			"templates/mail/simpleMailTemplate.vm",
			List.of("DEV-simple-spa"));

		List<String> prefixes = List.of("ABC", "DEF", "GHI");
		String newSubject = "[ABC, DEF, GHI] This is a test email";

		// when
		MailConfig configWithNoPrefix =
			MailServiceHelper.configWithSubjectPrefixes(baseConfig, prefixes);

		// then
		assertThat(configWithNoPrefix).isNotNull();
		assertThat(configWithNoPrefix.from()).isEqualTo(baseConfig.from());
		assertThat(configWithNoPrefix.to()).hasSameElementsAs(baseConfig.to());
		assertThat(configWithNoPrefix.htmlTemplate()).isEqualTo(baseConfig.htmlTemplate());

		assertThat(configWithNoPrefix.subject()).isEqualTo(newSubject);
	}

	@Test
	void testInitVelocityEngine() {
		// when
		VelocityEngine velocityEngine = MailServiceHelper.initVelocityEngine();

		// then
		assertThat(velocityEngine).isNotNull();
		assertThat(velocityEngine.getProperty("runtime.log.logsystem.log4j.logger")).isEqualTo("org.apache.velocity");
		assertThat(velocityEngine.getProperty("input.encoding")).isEqualTo("UTF-8");
		assertThat(velocityEngine.getProperty("output.encoding")).isEqualTo("UTF-8");
		assertThat(velocityEngine.getProperty("resource.loader")).asInstanceOf(LIST).contains("file", "class");
		assertThat(velocityEngine.getProperty("file.resource.loader.description")).isEqualTo(
			"Velocity File Resource Loader");
		assertThat(velocityEngine.getProperty("file.resource.loader.class")).isEqualTo(
			"org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		assertThat(velocityEngine.getProperty("file.resource.loader.path")).isEqualTo(".");
		assertThat(velocityEngine.getProperty("file.resource.loader.cache")).isEqualTo("false");
		assertThat(velocityEngine.getProperty("file.resource.loader.modificationCheckInterval")).isEqualTo("0");
		assertThat(velocityEngine.getProperty("class.resource.loader.description")).isEqualTo(
			"Velocity Classpath Resource Loader");
		assertThat(velocityEngine.getProperty("class.resource.loader.class")).isEqualTo(
			"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
	}
}
