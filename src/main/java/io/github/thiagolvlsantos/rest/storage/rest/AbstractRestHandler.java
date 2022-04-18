package io.github.thiagolvlsantos.rest.storage.rest;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationListener;

import io.github.thiagolvlsantos.rest.storage.rest.basic.RestCountEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestDeleteEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestListEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestReadEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestSaveEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestUpdateEvent;
import io.github.thiagolvlsantos.rest.storage.rest.history.HistoryVO;
import io.github.thiagolvlsantos.rest.storage.rest.history.RestHistoryEvent;
import io.github.thiagolvlsantos.rest.storage.rest.history.RestHistoryNameEvent;
import io.github.thiagolvlsantos.rest.storage.rest.history.RestHistoryResourceEvent;
import io.github.thiagolvlsantos.rest.storage.rest.properties.RestGetPropertyEvent;
import io.github.thiagolvlsantos.rest.storage.rest.properties.RestListPropertiesEvent;
import io.github.thiagolvlsantos.rest.storage.rest.properties.RestPropertiesEvent;
import io.github.thiagolvlsantos.rest.storage.rest.properties.RestSetPropertiesEvent;
import io.github.thiagolvlsantos.rest.storage.rest.properties.RestSetPropertyEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.ResourceVO;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestCountResourcesEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestDeleteResourceEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestGetResourceEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestListResourcesEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestSetResourceEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestUpdateResourceEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractRestHandler<P> implements ApplicationListener<AbstractRestEvent<?>> {

	protected String entity;
	protected Class<P> type;

	protected AbstractRestHandler(String entity, Class<P> type) {
		this.entity = entity;
		this.type = type;
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
			} else if (event instanceof RestSetPropertiesEvent) {
				setProperty((RestSetPropertiesEvent<List<P>>) event);
			} else if (event instanceof RestGetPropertyEvent) {
				getProperty((RestGetPropertyEvent<WrapperVO<Object>>) event);
			} else if (event instanceof RestPropertiesEvent) {
				properties((RestPropertiesEvent<Map<String, Object>>) event);
			} else if (event instanceof RestListPropertiesEvent) {
				properties((RestListPropertiesEvent<Map<String, Map<String, Object>>>) event);
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
				historyResources((RestHistoryResourceEvent<List<HistoryVO>>) event);
			}
		}
	}

	public abstract void save(RestSaveEvent<P> event);

	public abstract void read(RestReadEvent<P> event);

	public abstract void update(RestUpdateEvent<P> event);

	public abstract void delete(RestDeleteEvent<P> event);

	public abstract void setProperty(RestSetPropertyEvent<P> event);

	public abstract void setProperty(RestSetPropertiesEvent<List<P>> event);

	public abstract void getProperty(RestGetPropertyEvent<WrapperVO<Object>> event);

	public abstract void properties(RestPropertiesEvent<Map<String, Object>> event);

	public abstract void setResource(RestSetResourceEvent<P> event);

	public abstract void getResource(RestGetResourceEvent<ResourceVO> event);

	public abstract void updateResource(RestUpdateResourceEvent<P> event);

	public abstract void deleteResource(RestDeleteResourceEvent<P> event);

	public abstract void countResources(RestCountResourcesEvent<WrapperVO<Long>> event);

	public abstract void listResources(RestListResourcesEvent<List<ResourceVO>> event);

	public abstract void history(RestHistoryEvent<List<HistoryVO>> event);

	public abstract void historyName(RestHistoryNameEvent<List<HistoryVO>> event);

	public abstract void historyResources(RestHistoryResourceEvent<List<HistoryVO>> event);

	public abstract void count(RestCountEvent<WrapperVO<Long>> event);

	public abstract void list(RestListEvent<List<P>> event);

	public abstract void properties(RestListPropertiesEvent<Map<String, Map<String, Object>>> event);
}