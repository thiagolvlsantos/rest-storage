package io.github.thiagolvlsantos.rest.storage.audit;

import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.provider.IGitAudit;

@Component
public class FixedAudit implements IGitAudit {

	@Override
	public UserInfo author() {
		return new UserInfo("Thiago Santos", "thiagolvlsantos@gmail.com");
	}

	@Override
	public UserInfo committer() {
		return new UserInfo("Thiago", "thiagosaint@gmail.com");
	}
}
