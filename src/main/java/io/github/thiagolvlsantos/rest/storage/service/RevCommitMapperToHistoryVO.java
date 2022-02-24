package io.github.thiagolvlsantos.rest.storage.service;

import javax.annotation.PostConstruct;

import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.MappingDirection;

@Component
public class RevCommitMapperToHistoryVO extends CustomMapper<RevCommit, HistoryVO> {

	private @Autowired MapperFactory factory;

	@PostConstruct
	protected void configure() {
		factory.classMap(RevCommit.class, HistoryVO.class)//
				.field("id.name", "id")//
				.customize(this)//
				.byDefault(MappingDirection.A_TO_B)//
				.register();
	}
}