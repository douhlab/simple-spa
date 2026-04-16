package com.dev.simplespa.module.mail;

import java.time.LocalDate;
import java.util.List;

public interface IMailService {
	boolean sendGenericMail(LocalDate currentDate, List<String> messages);
}
