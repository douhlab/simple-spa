package com.dev.simplespa.module.mail.support;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import com.dev.simplespa.module.mail.MailConfig;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public final class MailServiceHelper {
	private static final String MAIL_ENCODING = "UTF-8";
	private static final VelocityEngine VELOCITY_ENGINE = initVelocityEngine();

	private MailServiceHelper() {
	}

	public static String buildMailTemplate(String template, Map<String, ?> templateParams) {
		VelocityContext velocityContext = new VelocityContext();
		templateParams.forEach(velocityContext::put);

		StringWriter stringWriter = new StringWriter();
		VELOCITY_ENGINE.mergeTemplate(template, MAIL_ENCODING, velocityContext, stringWriter);

		return stringWriter.toString();
	}

	public static MailConfig configWithSubjectPrefixes(MailConfig mailConfig, List<String> prefixes) {
		String newSubject = "";

		if (!prefixes.isEmpty()) {
			newSubject += "[" + String.join(", ", prefixes) + "] ";
		}

		newSubject += mailConfig.subject();

		return new MailConfig(mailConfig.to(),
			mailConfig.from(),
			newSubject,
			mailConfig.htmlTemplate(),
			mailConfig.tags());
	}

	public static VelocityEngine initVelocityEngine() {
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty("runtime.log.logsystem.log4j.logger", "org.apache.velocity");
		ve.setProperty("input.encoding", MAIL_ENCODING);
		ve.setProperty("output.encoding", MAIL_ENCODING);
		ve.setProperty("resource.loader", "file, class");
		ve.setProperty("file.resource.loader.description", "Velocity File Resource Loader");
		ve.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		ve.setProperty("file.resource.loader.path", ".");
		ve.setProperty("file.resource.loader.cache", "false");
		ve.setProperty("file.resource.loader.modificationCheckInterval", "0");
		ve.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
		ve.setProperty("class.resource.loader.class",
			"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		ve.init();
		return ve;
	}
}
