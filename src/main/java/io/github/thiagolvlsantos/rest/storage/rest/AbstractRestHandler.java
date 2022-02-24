package io.github.thiagolvlsantos.rest.storage.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.thiagolvlsantos.file.storage.FileParams;
import io.github.thiagolvlsantos.file.storage.annotations.UtilAnnotations;
import io.github.thiagolvlsantos.file.storage.util.repository.ResourceVO;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestCountEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestDeleteEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestListEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestReadEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestSaveEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestUpdateEvent;
import io.github.thiagolvlsantos.rest.storage.rest.history.RestHistoryEvent;
import io.github.thiagolvlsantos.rest.storage.rest.history.RestHistoryNameEvent;
import io.github.thiagolvlsantos.rest.storage.rest.history.RestHistoryResourceEvent;
import io.github.thiagolvlsantos.rest.storage.rest.properties.RestGetPropertyEvent;
import io.github.thiagolvlsantos.rest.storage.rest.properties.RestPropertiesEvent;
import io.github.thiagolvlsantos.rest.storage.rest.properties.RestSetPropertyEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestCountResourcesEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestDeleteResourceEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestGetResourceEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestListResourcesEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestSetResourceEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestUpdateResourceEvent;
import io.github.thiagolvlsantos.rest.storage.service.AbstractService;
import io.github.thiagolvlsantos.rest.storage.service.HistoryVO;
import io.github.thiagolvlsantos.rest.storage.service.WrapperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

@Getter
@Setter
public abstract class AbstractRestHandler<P, Q> implements ApplicationListener<AbstractRestEvent<?>> {

	protected String entity;
	protected Class<P> type;
	protected Class<Q> typeAlias;
	protected @Autowired ObjectMapper mapper;
	protected @Autowired AbstractService<P> service;

	public AbstractRestHandler(String entity, Class<P> type, Class<Q> typeAlias) {
		this.entity = entity;
		this.type = type;
		this.typeAlias = typeAlias;
	}

	@SuppressWarnings("unchecked")
	public void onApplicationEvent(AbstractRestEvent<?> event) {
		if (entity.equalsIgnoreCase(event.getEntity())) {
			if (event instanceof RestSaveEvent) {
				save((RestSaveEvent<P>) event);
			} else if (event instanceof RestReadEvent) {
				read((RestReadEvent<P>) event);
			} else if (event instanceof RestUpdateEvent) {
				update((RestUpdateEvent<P>) event);
			} else if (event instanceof RestDeleteEvent) {
				delete((RestDeleteEvent<P>) event);
			} else if (event instanceof RestCountEvent) {
				count((RestCountEvent<WrapperVO<Long>>) event);
			} else if (event instanceof RestListEvent) {
				list((RestListEvent<List<P>>) event);
			} else if (event instanceof RestSetPropertyEvent) {
				setProperty((RestSetPropertyEvent<P>) event);
			} else if (event instanceof RestGetPropertyEvent) {
				getProperty((RestGetPropertyEvent<WrapperVO<Object>>) event);
			} else if (event instanceof RestPropertiesEvent) {
				properties((RestPropertiesEvent<Map<String, Object>>) event);
			} else if (event instanceof RestSetResourceEvent) {
				setResource((RestSetResourceEvent<P>) event);
			} else if (event instanceof RestGetResourceEvent) {
				getResource((RestGetResourceEvent<ResourceVO>) event);
			} else if (event instanceof RestUpdateResourceEvent) {
				updateResource((RestUpdateResourceEvent<P>) event);
			} else if (event instanceof RestDeleteResourceEvent) {
				deleteResource((RestDeleteResourceEvent<P>) event);
			} else if (event instanceof RestCountResourcesEvent) {
				countResources((RestCountResourcesEvent<WrapperVO<Long>>) event);
			} else if (event instanceof RestListResourcesEvent) {
				listResources((RestListResourcesEvent<List<ResourceVO>>) event);
			} else if (event instanceof RestHistoryEvent) {
				history((RestHistoryEvent<List<HistoryVO>>) event);
			} else if (event instanceof RestHistoryNameEvent) {
				historyName((RestHistoryNameEvent<List<HistoryVO>>) event);
			} else if (event instanceof RestHistoryResourceEvent) {
				historyResource((RestHistoryResourceEvent<List<HistoryVO>>) event);
			}
		}
	}

	@SneakyThrows
	protected void save(RestSaveEvent<P> event) {
		event.setResult(service.save(toInstance(mapper.readValue(event.getContent(), typeAlias))));
	}

	protected abstract P toInstance(Q alias);

	@SneakyThrows
	protected void read(RestReadEvent<P> event) {
		event.setResult(service.read(FileParams.of(event.getName()), event.getCommit(), event.getAt()));
	}

	@SneakyThrows
	protected void update(RestUpdateEvent<P> event) {
		P candidate = mapper.readValue(event.getContent(), type);
		String keys = UtilAnnotations.getKeysChain(type, candidate);
		String name = event.getName();
		if (!name.equalsIgnoreCase(keys)) {
			throw new IllegalArgumentException(
					"Content name '" + keys + "' does not match the received path '" + name + "'.");
		}
		event.setResult(service.update(candidate));
	}

	@SneakyThrows
	protected void delete(RestDeleteEvent<P> event) {
		event.setResult(service.delete(FileParams.of(event.getName())));
	}

	@SneakyThrows
	protected void count(RestCountEvent<WrapperVO<Long>> event) {
		event.setResult(service.count(event.getFilter(), event.getPaging(), event.getCommit(), event.getAt()));
	}

	@SneakyThrows
	protected void list(RestListEvent<List<P>> event) {
		event.setResult(service.list(event.getFilter(), event.getPaging(), event.getSorting(), event.getCommit(),
				event.getAt()));
	}

	@SneakyThrows
	protected void setProperty(RestSetPropertyEvent<P> event) {
		event.setResult(
				service.setProperty(FileParams.of(event.getName()), event.getProperty(), event.getDataAsString()));
	}

	@SneakyThrows
	protected void getProperty(RestGetPropertyEvent<WrapperVO<Object>> event) {
		event.setResult(service.getProperty(FileParams.of(event.getName()), event.getProperty(), event.getCommit(),
				event.getAt()));
	}

	@SneakyThrows
	protected void properties(RestPropertiesEvent<Map<String, Object>> event) {
		event.setResult(service.properties(FileParams.of(event.getName()), FileParams.of(event.getProperties(), ","),
				event.getCommit(), event.getAt()));
	}

	@SneakyThrows
	protected void setResource(RestSetResourceEvent<P> event) {
		event.setResult(service.setResource(FileParams.of(event.getName()), event.getResource()));
	}

	@SneakyThrows
	protected void getResource(RestGetResourceEvent<ResourceVO> event) {
		event.setResult(
				service.getResource(FileParams.of(event.getName()), event.getPath(), event.getCommit(), event.getAt()));
	}

	@SneakyThrows
	protected void updateResource(RestUpdateResourceEvent<P> event) {
		event.setResult(service.updateResource(FileParams.of(event.getName()), event.getResource()));
	}

	@SneakyThrows
	protected void deleteResource(RestDeleteResourceEvent<P> event) {
		event.setResult(service.deleteResource(FileParams.of(event.getName()), event.getPath()));
	}

	@SneakyThrows
	protected void countResources(RestCountResourcesEvent<WrapperVO<Long>> event) {
		event.setResult(service.countResources(FileParams.of(event.getName()), event.getFilter(), event.getPaging(),
				event.getCommit(), event.getAt()));
	}

	@SneakyThrows
	protected void listResources(RestListResourcesEvent<List<ResourceVO>> event) {
		event.setResult(service.listResources(FileParams.of(event.getName()), event.getFilter(), event.getPaging(),
				event.getSorting(), event.getCommit(), event.getAt()));
	}

	@SneakyThrows
	protected void history(RestHistoryEvent<List<HistoryVO>> event) {
		event.setResult(service.history(FileParams.of(new Object[0]), event.getPaging()));
	}

	@SneakyThrows
	protected void historyName(RestHistoryNameEvent<List<HistoryVO>> event) {
		event.setResult(service.history(FileParams.of(event.getName()), event.getPaging()));
	}

	@SneakyThrows
	protected void historyResource(RestHistoryResourceEvent<List<HistoryVO>> event) {
		event.setResult(service.history(FileParams.of(event.getName()), event.getPath(), event.getPaging()));
	}
}