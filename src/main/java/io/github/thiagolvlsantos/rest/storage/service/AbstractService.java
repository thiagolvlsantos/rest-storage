package io.github.thiagolvlsantos.rest.storage.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.thiagolvlsantos.file.storage.FilePaging;
import io.github.thiagolvlsantos.file.storage.FileParams;
import io.github.thiagolvlsantos.file.storage.exceptions.FileStorageException;
import io.github.thiagolvlsantos.file.storage.util.repository.AbstractFileRepository;
import io.github.thiagolvlsantos.file.storage.util.repository.IObjectMapper;
import io.github.thiagolvlsantos.file.storage.util.repository.ResourceVO;
import io.github.thiagolvlsantos.git.transactions.GitRepo;
import io.github.thiagolvlsantos.git.transactions.GitServices;
import io.github.thiagolvlsantos.git.transactions.exceptions.GitTransactionsException;
import io.github.thiagolvlsantos.git.transactions.read.GitCommit;
import io.github.thiagolvlsantos.git.transactions.read.GitRead;
import io.github.thiagolvlsantos.git.transactions.write.GitWrite;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	protected File readDirectory() {
		return gits.readDirectory(group());
	}

	protected File writeDirectory() {
		return gits.writeDirectory(group());
	}

	// +------------- ENTITY METHODS ------------------+

	@GitRead
	public boolean exists(T obj) {
		return repository().exists(readDirectory(), obj);
	}

	@GitWrite
	public T save(T obj) {
		try {
			beforeSave(obj);
			T result = repository().write(writeDirectory(), obj);
			afterSaveSuccess(obj, result);
			return result;
		} catch (Throwable e) {
			afterSaveError(obj, e);
			throw e;
		}
	}

	protected void beforeSave(T obj) {
		if (repository().exists(writeDirectory(), obj)) {
			throw new FileStorageException(repository().getType().getSimpleName() + " already exists.", null);
		}
	}

	protected void afterSaveSuccess(T obj, T result) {
	}

	protected void afterSaveError(T obj, Throwable e) {
	}

	@GitRead
	public T read(FileParams keys, @GitCommit String commit, @GitCommit Long at) {
		try {
			beforeRead(keys, commit, at);
			T result = repository().read(readDirectory(), keys);
			afterReadSuccess(keys, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterReadError(keys, commit, at, e);
			throw e;
		}
	}

	protected void beforeRead(FileParams keys, String commit, Long at) {
	}

	protected void afterReadSuccess(FileParams keys, String commit, Long at, T result) {
	}

	protected void afterReadError(FileParams keys, String commit, Long at, Throwable e) {
	}

	@GitWrite
	public T update(T obj) {
		try {
			beforeUpdate(obj);
			T result = repository().write(writeDirectory(), obj);
			afterUpdateSuccess(obj, result);
			return result;
		} catch (Throwable e) {
			afterUpdateError(obj, e);
			throw e;
		}
	}

	protected void beforeUpdate(T obj) {
		if (!repository().exists(writeDirectory(), obj)) {
			throw new FileStorageException(repository().getType().getSimpleName() + " not found.", null);
		}
	}

	protected void afterUpdateSuccess(T obj, T result) {
	}

	protected void afterUpdateError(T obj, Throwable e) {
	}

	@GitWrite
	public T delete(FileParams keys) {
		try {
			beforeDelete(keys);
			T result = repository().delete(writeDirectory(), keys);
			afterDeleteSuccess(keys, result);
			return result;
		} catch (Throwable e) {
			afterDeleteError(keys, e);
			throw e;
		}
	}

	protected void beforeDelete(FileParams keys) {
	}

	protected void afterDeleteSuccess(FileParams keys, T result) {
	}

	protected void afterDeleteError(FileParams keys, Throwable e) {
	}

	@GitRead
	public WrapperVO<Long> count(String filter, String paging, @GitCommit String commit, @GitCommit Long at) {
		try {
			beforeCount(filter, paging, commit, at);
			WrapperVO<Long> result = new WrapperVO<>(repository().count(readDirectory(), filter, paging));
			afterCountSuccess(filter, paging, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterCountError(filter, paging, commit, at, e);
			throw e;
		}
	}

	protected void beforeCount(String filter, String paging, String commit, Long at) {
	}

	protected void afterCountSuccess(String filter, String paging, String commit, Long at, WrapperVO<Long> result) {
	}

	protected void afterCountError(String filter, String paging, String commit, Long at, Throwable e) {
	}

	@GitRead
	public List<T> list(String filter, String paging, String sorting, @GitCommit String commit, @GitCommit Long at) {
		try {
			beforeList(filter, paging, sorting, commit, at);
			List<T> result = repository().list(readDirectory(), filter, paging, sorting);
			afterListSuccess(filter, paging, sorting, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterListError(filter, paging, sorting, commit, at, e);
			throw e;
		}
	}

	protected void beforeList(String filter, String paging, String sorting, String commit, Long at) {
	}

	protected void afterListSuccess(String filter, String paging, String sorting, String commit, Long at,
			List<T> result) {
	}

	protected void afterListError(String filter, String paging, String sorting, String commit, Long at, Throwable e) {
	}

	// +------------- PROPERTY METHODS ------------------+

	@GitWrite
	public T setProperty(FileParams keys, String property, String data) {
		try {
			beforeSetProperty(keys, property, data);
			T result = repository().setProperty(writeDirectory(), keys, property, data);
			afterSetPropertySuccess(keys, property, data, result);
			return result;
		} catch (Throwable e) {
			afterSetPropertyError(keys, property, data, e);
			throw e;
		}
	}

	protected void beforeSetProperty(FileParams keys, String property, String data) {
	}

	protected void afterSetPropertySuccess(FileParams keys, String property, String data, T result) {
	}

	protected void afterSetPropertyError(FileParams keys, String property, String data, Throwable e) {
	}

	@GitWrite
	public List<T> setProperty(String property, String data, String filter, String paging, String sorting) {
		try {
			beforeSetProperty(property, data, filter, paging, sorting);
			List<T> result = repository().setProperty(writeDirectory(), property, data, filter, paging, sorting);
			afterSetPropertySuccess(property, data, filter, paging, sorting, result);
			return result;
		} catch (Throwable e) {
			afterSetPropertyError(property, data, filter, paging, sorting, e);
			throw e;
		}
	}

	protected void beforeSetProperty(String property, String data, String filter, String paging, String sorting) {
	}

	protected void afterSetPropertySuccess(String property, String data, String filter, String paging, String sorting,
			List<T> result) {
	}

	protected void afterSetPropertyError(String property, String data, String filter, String paging, String sorting,
			Throwable e) {
	}

	@GitRead
	public WrapperVO<Object> getProperty(FileParams keys, String property, @GitCommit String commit,
			@GitCommit Long at) {
		try {
			beforeGetProperty(keys, property, commit, at);
			WrapperVO<Object> result = new WrapperVO<>(repository().getProperty(readDirectory(), keys, property));
			afterGetPropertySuccess(keys, property, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterGetPropertyError(keys, property, commit, at, e);
			throw e;
		}
	}

	protected void beforeGetProperty(FileParams keys, String property, String commit, Long at) {
	}

	protected void afterGetPropertySuccess(FileParams keys, String property, String commit, Long at,
			WrapperVO<Object> result) {
	}

	protected void afterGetPropertyError(FileParams keys, String property, String commit, Long at, Throwable e) {
	}

	@GitRead
	public Map<String, Object> properties(FileParams keys, FileParams names, @GitCommit String commit,
			@GitCommit Long at) {
		Map<String, Object> result;
		try {
			beforeProperties(keys, names, commit, at);
			result = repository().properties(readDirectory(), keys, names);
			afterPropertiesSuccess(keys, names, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterPropertiesError(keys, names, commit, at, e);
			throw e;
		}
	}

	protected void beforeProperties(FileParams keys, FileParams names, String commit, Long at) {
	}

	protected void afterPropertiesSuccess(FileParams keys, FileParams names, String commit, Long at,
			Map<String, Object> result) {
	}

	protected void afterPropertiesError(FileParams keys, FileParams names, String commit, Long at, Throwable e) {
	}

	@GitRead
	public Map<String, Map<String, Object>> properties(FileParams names, String filter, String paging, String sorting,
			@GitCommit String commit, @GitCommit Long at) {
		Map<String, Map<String, Object>> result;
		try {
			beforeProperties(names, filter, paging, sorting, commit, at);
			result = repository().properties(readDirectory(), names, filter, paging, sorting);
			afterPropertiesSuccess(names, filter, paging, sorting, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterPropertiesError(names, filter, paging, sorting, commit, at, e);
			throw e;
		}
	}

	protected void beforeProperties(FileParams names, String filter, String paging, String sorting, String commit,
			Long at) {
	}

	protected void afterPropertiesSuccess(FileParams names, String filter, String paging, String sorting, String commit,
			Long at, Map<String, Map<String, Object>> result) {
	}

	protected void afterPropertiesError(FileParams names, String filter, String paging, String sorting, String commit,
			Long at, Throwable e) {
	}

	// +------------- RESOURCE METHODS ------------------+

	@GitWrite
	public T setResource(FileParams keys, ResourceVO resource) {
		try {
			beforeSetResource(keys, resource);
			T result = repository().setResource(writeDirectory(), keys, resource);
			afterSetResourceSuccess(keys, resource, result);
			return result;
		} catch (Throwable e) {
			afterSetResourceError(keys, resource, e);
			throw e;
		}
	}

	protected void beforeSetResource(FileParams keys, ResourceVO resource) {
		if (repository().existsResources(writeDirectory(), keys, resource.getMetadata().getPath())) {
			throw new IllegalArgumentException("Resource already exists.");
		}
	}

	protected void afterSetResourceSuccess(FileParams keys, ResourceVO resource, T result) {
	}

	protected void afterSetResourceError(FileParams keys, ResourceVO resource, Throwable e) {
	}

	@GitRead
	public ResourceVO getResource(FileParams keys, String path, @GitCommit String commit, @GitCommit Long at) {
		try {
			beforeGetResource(keys, path, commit, at);
			ResourceVO result = repository().getResource(readDirectory(), keys, path);
			afterGetResourceSuccess(keys, path, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterGetResourceError(keys, path, commit, at, e);
			throw e;
		}
	}

	protected void beforeGetResource(FileParams keys, String path, String commit, Long at) {
	}

	protected void afterGetResourceSuccess(FileParams keys, String path, String commit, Long at, ResourceVO result) {
	}

	protected void afterGetResourceError(FileParams keys, String path, String commit, Long at, Throwable e) {
	}

	@GitWrite
	public T updateResource(FileParams keys, ResourceVO resource) {
		try {
			beforeUpdateResource(keys, resource);
			T result = repository().setResource(writeDirectory(), keys, resource);
			afterUpdateResourceSuccess(keys, resource, result);
			return result;
		} catch (Throwable e) {
			afterUpdateResourceError(keys, resource, e);
			throw e;
		}
	}

	protected void beforeUpdateResource(FileParams keys, ResourceVO resource) {
		if (!repository().existsResources(writeDirectory(), keys, resource.getMetadata().getPath())) {
			throw new IllegalArgumentException("Resource not found.");
		}
	}

	private void afterUpdateResourceSuccess(FileParams keys, ResourceVO resource, T result) {
	}

	private void afterUpdateResourceError(FileParams keys, ResourceVO resource, Throwable e) {
	}

	@GitWrite
	public T deleteResource(FileParams keys, String path) {
		try {
			beforeDeleteResource(keys, path);
			T result = repository().deleteResource(writeDirectory(), keys, path);
			afterDeleteResourceSuccess(keys, path, result);
			return result;
		} catch (Throwable e) {
			afterDeleteResourceError(keys, path, e);
			throw e;
		}
	}

	protected void beforeDeleteResource(FileParams keys, String path) {
	}

	protected void afterDeleteResourceSuccess(FileParams keys, String path, T result) {
	}

	protected void afterDeleteResourceError(FileParams keys, String path, Throwable e) {
	}

	@GitRead
	public WrapperVO<Long> countResources(FileParams keys, String filter, String paging, @GitCommit String commit,
			@GitCommit Long at) {
		try {
			beforeCountResources(keys, filter, paging, commit, at);
			WrapperVO<Long> result = new WrapperVO<>(
					repository().countResources(readDirectory(), keys, filter, paging));
			afterCountResourcesSuccess(keys, filter, paging, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterCountResourcesError(keys, filter, paging, commit, at, e);
			throw e;
		}
	}

	protected void beforeCountResources(FileParams keys, String filter, String paging, String commit, Long at) {
	}

	protected void afterCountResourcesSuccess(FileParams keys, String filter, String paging, String commit, Long at,
			WrapperVO<Long> result) {
	}

	protected void afterCountResourcesError(FileParams keys, String filter, String paging, String commit, Long at,
			Throwable e) {
	}

	@GitRead
	public List<ResourceVO> listResources(FileParams keys, String filter, String paging, String sorting,
			@GitCommit String commit, @GitCommit Long at) {
		try {
			beforeListResources(keys, filter, paging, sorting, commit, at);
			List<ResourceVO> result = repository().listResources(readDirectory(), keys, filter, paging, sorting);
			afterListResourcesSuccess(keys, filter, paging, sorting, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterListResourcesError(keys, filter, paging, sorting, commit, at, e);
			throw e;
		}
	}

	protected void beforeListResources(FileParams keys, String filter, String paging, String sorting, String commit,
			Long at) {
	}

	protected void afterListResourcesSuccess(FileParams keys, String filter, String paging, String sorting,
			String commit, Long at, List<ResourceVO> result) {
	}

	protected void afterListResourcesError(FileParams keys, String filter, String paging, String sorting, String commit,
			Long at, Throwable e) {
	}

	// +------------- HISTORY METHODS ------------------+

	@GitRead
	public List<HistoryVO> history(FileParams keys, String paging) {
		try {
			FilePaging page = repository().paging(paging);
			Integer skip = page != null ? page.getSkip() : null;
			Integer max = page != null ? page.getMax() : null;
			beforeHistory(keys, paging, skip, max);
			List<HistoryVO> result = mapper.mapList(//
					gits.history(group(), repository().location(readDirectory(), keys), skip, max), //
					HistoryVO.class);
			afterHistorySuccess(keys, paging, skip, max, result);
			return result;
		} catch (Throwable e) {
			afterHistoryError(keys, paging, e);
			throw e;
		}
	}

	protected void beforeHistory(FileParams keys, String paging, Integer skip, Integer max) {
	}

	protected void afterHistorySuccess(FileParams keys, String paging, Integer skip, Integer max,
			List<HistoryVO> result) {
	}

	protected void afterHistoryError(FileParams keys, String paging, Throwable e) {
	}

	@GitRead
	public List<HistoryVO> historyResources(FileParams keys, String path, String paging) {
		try {
			FilePaging page = repository().paging(paging);
			Integer skip = page != null ? page.getSkip() : null;
			Integer max = page != null ? page.getMax() : null;
			beforeHistoryResources(keys, path, paging, skip, max);
			List<HistoryVO> result = mapper.mapList(//
					gits.history(group(), repository().locationResources(readDirectory(), keys, path), skip, max), //
					HistoryVO.class);
			afterHistoryResourcesSuccess(keys, path, paging, skip, max, result);
			return result;
		} catch (Throwable e) {
			afterHistoryResourcesError(keys, path, paging, e);
			throw e;
		}
	}

	protected void beforeHistoryResources(FileParams keys, String path, String paging, Integer skip, Integer max) {
	}

	protected void afterHistoryResourcesSuccess(FileParams keys, String path, String paging, Integer skip, Integer max,
			List<HistoryVO> result) {
	}

	protected void afterHistoryResourcesError(FileParams keys, String path, String paging, Throwable e) {
	}
}
