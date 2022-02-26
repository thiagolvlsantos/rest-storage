package io.github.thiagolvlsantos.rest.storage.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.thiagolvlsantos.file.storage.FilePaging;
import io.github.thiagolvlsantos.file.storage.FileParams;
import io.github.thiagolvlsantos.file.storage.util.repository.AbstractFileRepository;
import io.github.thiagolvlsantos.file.storage.util.repository.IObjectMapper;
import io.github.thiagolvlsantos.file.storage.util.repository.ResourceVO;
import io.github.thiagolvlsantos.git.transactions.GitRepo;
import io.github.thiagolvlsantos.git.transactions.GitServices;
import io.github.thiagolvlsantos.git.transactions.exceptions.GitTransactionsException;
import io.github.thiagolvlsantos.git.transactions.read.GitCommit;
import io.github.thiagolvlsantos.git.transactions.read.GitRead;
import io.github.thiagolvlsantos.git.transactions.write.GitWrite;

public abstract class AbstractService<T> {

	private @Autowired GitServices gits;
	private @Autowired IObjectMapper mapper;

	public abstract AbstractFileRepository<T> repository();

	// +---
	public String group() {
		GitRepo repo = AnnotationUtils.findAnnotation(getClass(), GitRepo.class);
		if (repo == null) {
			throw new GitTransactionsException("Repository location not found.", null);
		}
		return repo.value();
	}

	private File readDirectory() {
		return gits.readDirectory(group());
	}

	private File writeDirectory() {
		return gits.writeDirectory(group());
	}

	// +------------- ENTITY METHODS ------------------+

	@GitWrite
	public T save(T obj) {
		if (repository().exists(writeDirectory(), obj)) {
			throw new IllegalArgumentException(repository().getType().getSimpleName() + " already exists.");
		}
		return repository().write(writeDirectory(), obj);
	}

	@GitRead
	public T read(FileParams keys, @GitCommit String commit, @GitCommit Long at) {
		return repository().read(readDirectory(), keys);
	}

	@GitWrite
	public T update(T obj) {
		if (!repository().exists(writeDirectory(), obj)) {
			throw new IllegalArgumentException(repository().getType().getSimpleName() + " not found.");
		}
		return repository().write(writeDirectory(), obj);
	}

	@GitWrite
	public T delete(FileParams keys) {
		return repository().delete(writeDirectory(), keys);
	}

	@GitRead
	public WrapperVO<Long> count(String filter, String paging, @GitCommit String commit, @GitCommit Long at) {
		return new WrapperVO<>(repository().count(readDirectory(), filter, paging));
	}

	@GitRead
	public List<T> list(String filter, String paging, String sorting, @GitCommit String commit, @GitCommit Long at) {
		return repository().list(readDirectory(), filter, paging, sorting);
	}

	// +------------- PROPERTY METHODS ------------------+

	@GitWrite
	public T setProperty(FileParams keys, String property, String data) {
		return repository().setProperty(writeDirectory(), keys, property, data);
	}

	@GitRead
	public WrapperVO<Object> getProperty(FileParams keys, String property, @GitCommit String commit,
			@GitCommit Long at) {
		return new WrapperVO<>(repository().getProperty(readDirectory(), keys, property));
	}

	@GitRead
	public Map<String, Object> properties(FileParams keys, FileParams names, @GitCommit String commit,
			@GitCommit Long at) {
		return repository().properties(readDirectory(), keys, names);
	}

	// +------------- RESOURCE METHODS ------------------+

	@GitWrite
	public T setResource(FileParams keys, ResourceVO resource) {
		if (repository().existsResources(writeDirectory(), keys, resource.getMetadata().getPath())) {
			throw new IllegalArgumentException("Resource already exists.");
		}
		return repository().setResource(writeDirectory(), keys, resource);
	}

	@GitRead
	public ResourceVO getResource(FileParams keys, String path, @GitCommit String commit, @GitCommit Long at) {
		return repository().getResource(readDirectory(), keys, path);
	}

	@GitWrite
	public T updateResource(FileParams keys, ResourceVO resource) {
		if (!repository().existsResources(writeDirectory(), keys, resource.getMetadata().getPath())) {
			throw new IllegalArgumentException("Resource not found.");
		}
		return repository().setResource(writeDirectory(), keys, resource);
	}

	@GitWrite
	public T deleteResource(FileParams keys, String path) {
		return repository().deleteResource(writeDirectory(), keys, path);
	}

	@GitRead
	public WrapperVO<Long> countResources(FileParams keys, String filter, String paging, @GitCommit String commit,
			@GitCommit Long at) {
		return new WrapperVO<>(repository().countResources(readDirectory(), keys, filter, paging));
	}

	@GitRead
	public List<ResourceVO> listResources(FileParams keys, String filter, String paging, String sorting,
			@GitCommit String commit, @GitCommit Long at) {
		return repository().listResources(readDirectory(), keys, filter, paging, sorting);
	}

	// +------------- HISTORY METHODS ------------------+

	@GitRead
	public List<HistoryVO> history(FileParams keys, String paging) {
		FilePaging page = repository().paging(paging);
		Integer skip = page != null ? page.getSkip() : null;
		Integer max = page != null ? page.getMax() : null;
		return mapper.mapList(//
				gits.history(group(), repository().location(readDirectory(), keys), skip, max), //
				HistoryVO.class);
	}

	@GitRead
	public List<HistoryVO> history(FileParams keys, String path, String paging) {
		FilePaging page = repository().paging(paging);
		Integer skip = page != null ? page.getSkip() : null;
		Integer max = page != null ? page.getMax() : null;
		return mapper.mapList(//
				gits.history(group(), repository().locationResources(readDirectory(), keys, path), skip, max), //
				HistoryVO.class);
	}
}
