package io.github.thiagolvlsantos.rest.storage.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.thiagolvlsantos.file.storage.annotations.UtilAnnotations;
import io.github.thiagolvlsantos.file.storage.util.repository.ResourceVO;
import io.github.thiagolvlsantos.rest.storage.error.ApiFailure;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestDeleteEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestReadEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestSaveEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestUpdateEvent;
import io.github.thiagolvlsantos.rest.storage.rest.collection.RestCountEvent;
import io.github.thiagolvlsantos.rest.storage.rest.collection.RestListEvent;
import io.github.thiagolvlsantos.rest.storage.rest.collection.RestListPropertiesEvent;
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
import io.github.thiagolvlsantos.rest.storage.service.HistoryVO;
import io.github.thiagolvlsantos.rest.storage.service.WrapperVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(GenericRestController.API_URL)
@Tag(name = GenericRestController.TAG, description = "The all in one API. <p> <b>IMPORTANT</b>: All names have pattern: 'string(;string)*'.</p>")
@Slf4j
public class GenericRestController {

	public static final String PATH_PROPERTIES = "properties";
	public static final String PATH_RESOURCES = "resources";
	public static final String PATH_HISTORY = "history";

	public static final String TAG = "All";
	public static final String API_URL = "/";

	private @Autowired ApplicationEventPublisher publisher;

	@PostConstruct
	public void init() {
		if (log.isInfoEnabled()) {
			log.info(getClass().getSimpleName() + " init:" + API_URL);
		}
	}

	// +------------- ENTITY METHODS ------------------+

	// EXAMPLE:
	// https://www.dariawan.com/tutorials/spring/documenting-spring-boot-rest-api-springdoc-openapi-3/

	@Operation(summary = "Creates an entity with the given information.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "201", description = "On creation success.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = Object.class)) }), //
			@ApiResponse(responseCode = "4XX", description = "On creation errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@PostMapping(value = "/{entity}", //
			consumes = { MediaType.APPLICATION_JSON_VALUE }, //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<Object> save( //
			@Parameter(description = "Entity type.", required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @PathVariable("entity") String entity, //
			@Parameter(description = "Entity information to add.", required = true, //
					schema = @Schema(implementation = Object.class)) //
			@Valid @RequestBody(required = true) String content //
	) throws URISyntaxException {
		RestSaveEvent<Object> re = new RestSaveEvent<>(this);
		re.setEntity(entity);
		re.setContent(content);
		publisher.publishEvent(re);
		Object response = re.getResult();
		if (response == null) {
			return ResponseEntity.badRequest().build();
		}
		URI uri = new URI(API_URL + "/" + UtilAnnotations.getKeysChain(response.getClass(), response));
		return ResponseEntity.created(uri).body(response);
	}

	@Operation(summary = "Reads the entity data by the given name.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "On success request.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = Object.class)) }), //
			@ApiResponse(responseCode = "4XX", description = "On read errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@GetMapping(value = "/{entity}/{name}", //
			produces = { MediaType.APPLICATION_JSON_VALUE }//
	)
	public ResponseEntity<Object> read( //
			@Parameter(description = "Entity type.", required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @PathVariable("entity") String entity, //

			@Parameter(description = "Entity name.", required = true) //
			@Valid @NotBlank @PathVariable(required = true) String name, //

			@Parameter(description = "Commit id.") //
			@Nullable @RequestParam(name = "commit", required = false) String commit, //

			@Parameter(description = "Date of reading. ISO DATE_TIME format.") //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = "at", required = false) LocalDateTime at //
	) {
		RestReadEvent<Object> re = new RestReadEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		re.setCommit(commit);
		re.setAt(timestamp(at));
		publisher.publishEvent(re);
		return handle(re);
	}

	private Long timestamp(LocalDateTime at) {
		return at != null ? at.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : null;
	}

	private <T> ResponseEntity<T> handle(AbstractRestEvent<T> re) {
		T response = re.getResult();
		if (response == null) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Updates the entity with the given information.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = Object.class)) }), //
			@ApiResponse(responseCode = "4XX", description = "On update errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@PatchMapping(value = "/{entity}/{name}", //
			consumes = { MediaType.APPLICATION_JSON_VALUE }, //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<Object> update(//
			@Parameter(description = "Entity type.", required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @PathVariable("entity") String entity, //

			@Parameter(description = "Entity name.", required = true) //
			@Valid @PathVariable(required = true) String name, //

			@Parameter(description = "Object with new data.", required = true, //
					schema = @Schema(implementation = Object.class)) //
			@Valid @RequestBody(required = true) String content //
	) {
		RestUpdateEvent<Object> re = new RestUpdateEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		re.setContent(content);
		publisher.publishEvent(re);
		return handle(re);
	}

	@Operation(summary = "Deletes an entity by the given name.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "On success request.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = Object.class)) }), //
			@ApiResponse(responseCode = "4XX", description = "On read errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@DeleteMapping(value = "/{entity}/{name}", //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<Object> delete(//
			@Parameter(description = "Entity type.", required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @PathVariable("entity") String entity, //

			@Parameter(description = "Entity name.", required = true) //
			@Valid @PathVariable(required = true) String name //
	) {
		RestDeleteEvent<Object> re = new RestDeleteEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		publisher.publishEvent(re);
		return handle(re);
	}

	@Operation(summary = "Count objects.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "On success request.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = WrapperVO.class)) }), //
			@ApiResponse(responseCode = "4XX", description = "On read errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@GetMapping(value = "/{entity}/count", //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<WrapperVO<Long>> count(//
			@Parameter(description = "Entity type.", required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @PathVariable("entity") String entity, //

			@Parameter(description = "Filter predicate with pattern described at https://github.com/thiagolvlsantos/json-predicate.", //
					example = "{\"name\": {\"$contains\": \"k8s\"}}") //
			@Nullable @RequestParam(name = "filter", required = false) String filter, //

			@Parameter(description = "Pagination information.", example = "{ \"skip\":0, \"max\":10 }") //
			@Nullable @RequestParam(name = "paging", required = false) String paging, //

			@Parameter(description = "Commit id.") //
			@Nullable @RequestParam(name = "commit", required = false) String commit, //

			@Parameter(description = "Date of reading. ISO DATE_TIME format.") //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = "at", required = false) LocalDateTime at //
	) {
		RestCountEvent<WrapperVO<Long>> re = new RestCountEvent<>(this);
		re.setEntity(entity);
		re.setFilter(filter);
		re.setPaging(paging);
		re.setCommit(commit);
		re.setAt(timestamp(at));
		publisher.publishEvent(re);
		return handle(re);
	}

	@Operation(summary = "List objects.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							array = @ArraySchema(schema = @Schema(implementation = Object.class))) }), //
			@ApiResponse(responseCode = "4XX", description = "On read errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@GetMapping(value = "/{entity}/list", //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<List<Object>> list(//
			@Parameter(description = "Entity type.", required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @PathVariable("entity") String entity, //

			@Parameter(description = "Filter predicate with pattern described at https://github.com/thiagolvlsantos/json-predicate.", //
					example = "{\"name\": {\"$contains\": \"k8s\"}}") //
			@Nullable @RequestParam(name = "filter", required = false) String filter, //

			@Parameter(description = "Pagination information.", example = "{ \"skip\":0, \"max\":10 }") //
			@Nullable @RequestParam(name = "paging", required = false) String paging, //

			@Parameter(description = "Sorting information.", example = "{ \"property\":\"parent.name\", \"sort\":\"asc\", \"nullsFirst\":true }") //
			@Nullable @RequestParam(name = "sorting", required = false) String sorting, //

			@Parameter(description = "Commit id.") //
			@Nullable @RequestParam(name = "commit", required = false) String commit, //

			@Parameter(description = "Date of reading. ISO DATE_TIME format.") //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = "at", required = false) LocalDateTime at //
	) {
		RestListEvent<List<Object>> re = new RestListEvent<>(this);
		re.setEntity(entity);
		re.setFilter(filter);
		re.setPaging(paging);
		re.setSorting(sorting);
		re.setCommit(commit);
		re.setAt(timestamp(at));
		publisher.publishEvent(re);
		return handle(re);
	}

	// +------------- PROPERTY METHODS ------------------+

	@Operation(summary = "Updates an object property with the given information.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = Object.class)) }), //
			@ApiResponse(responseCode = "4XX", description = "On update errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@PatchMapping(value = "/{entity}/{name}/" + PATH_PROPERTIES + "/{property}", //
			consumes = { MediaType.APPLICATION_JSON_VALUE }, //
			produces = { MediaType.APPLICATION_JSON_VALUE }//
	)
	public ResponseEntity<Object> setProperty(//
			@Parameter(description = "Object type.", required = true) //
			@Valid @PathVariable(required = true) String entity, //

			@Parameter(description = "Object name.", required = true) //
			@Valid @PathVariable(required = true) String name, //

			@Parameter(description = "Object property.", required = true) //
			@Valid @PathVariable(required = true) String property, //

			@Parameter(description = "Property data as String.", required = true) //
			@Valid @RequestBody(required = true) String dataAsString //
	) {
		RestSetPropertyEvent<Object> re = new RestSetPropertyEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		re.setProperty(property);
		re.setDataAsString(dataAsString);
		publisher.publishEvent(re);
		return handle(re);
	}

	@Operation(summary = "Reads an object property by the given name.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "On success request.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = WrapperVO.class)) }), //
			@ApiResponse(responseCode = "4XX", description = "On read errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@GetMapping(value = "/{entity}/{name}/" + PATH_PROPERTIES + "/{property}", //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<WrapperVO<Object>> getProperty( //
			@Parameter(description = "Object type.", required = true) //
			@Valid @PathVariable(required = true) String entity, //

			@Parameter(description = "Object name.", required = true) //
			@Valid @PathVariable(required = true) String name, //

			@Parameter(description = "Object property.", required = true) //
			@Valid @PathVariable(required = true) String property, //

			@Parameter(description = "Commit id.") //
			@Nullable @RequestParam(name = "commit", required = false) String commit, //

			@Parameter(description = "Date of reading. ISO DATE_TIME format.") //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = "at", required = false) LocalDateTime at //
	) {
		RestGetPropertyEvent<WrapperVO<Object>> re = new RestGetPropertyEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		re.setProperty(property);
		re.setCommit(commit);
		re.setAt(timestamp(at));
		publisher.publishEvent(re);
		return handle(re);
	}

	@Operation(summary = "Properties mapping.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "On success request.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = Object.class)) }), //
			@ApiResponse(responseCode = "4XX", description = "On read errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@GetMapping(value = "/{entity}/{name}/" + PATH_PROPERTIES, //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<Map<String, Object>> properties(//
			@Parameter(description = "Object type.", required = true) //
			@Valid @PathVariable(required = true) String entity, //

			@Parameter(description = "Object name.", required = true) //
			@Valid @PathVariable(required = true) String name, //

			@Parameter(description = "Property names. Separated with ','.") //
			@Nullable @RequestParam(name = "properties", required = false) String properties, //

			@Parameter(description = "Commit id.") //
			@Nullable @RequestParam(name = "commit", required = false) String commit, //

			@Parameter(description = "Date of reading. ISO DATE_TIME format.") //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = "at", required = false) LocalDateTime at //
	) {
		RestPropertiesEvent<Map<String, Object>> re = new RestPropertiesEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		re.setProperties(properties);
		re.setCommit(commit);
		re.setAt(timestamp(at));
		publisher.publishEvent(re);
		return handle(re);
	}

	// +------------- RESOURCE METHODS ------------------+

	@Operation(summary = "Creates an object resource with the given information.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "201", description = "", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = Object.class)) }), //
			@ApiResponse(responseCode = "4XX", description = "On update errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) } //
	)
	@PostMapping(value = "/{entity}/{name}/" + PATH_RESOURCES, //
			consumes = { MediaType.APPLICATION_JSON_VALUE }, //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<Object> setResource(//
			@Parameter(description = "Object type.", required = true) //
			@Valid @PathVariable(required = true) String entity, //

			@Parameter(description = "Object name.", required = true) //
			@Valid @PathVariable(required = true) String name, //

			@Parameter(description = "Resource content.", required = true) //
			@Valid @RequestBody(required = true) ResourceVO resource//
	) throws URISyntaxException {
		RestSetResourceEvent<Object> re = new RestSetResourceEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		re.setResource(resource);
		publisher.publishEvent(re);
		Object response = re.getResult();
		if (response == null) {
			return ResponseEntity.badRequest().build();
		}
		URI uri = new URI(API_URL + "/" + UtilAnnotations.getKeysChain(response.getClass(), response)
				+ "/resources?path=" + resource.getMetadata().getPath());
		return ResponseEntity.created(uri).body(response);
	}

	@Operation(summary = "Reads an object resource by the given path.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "On success request.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ResourceVO.class)) }), //
			@ApiResponse(responseCode = "4XX", description = "On read errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@GetMapping(value = "/{entity}/{name}/" + PATH_RESOURCES, //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<ResourceVO> getResource(//
			@Parameter(description = "Object type.", required = true) //
			@Valid @PathVariable(required = true) String entity, //

			@Parameter(description = "Object name.", required = true) //
			@Valid @PathVariable(required = true) String name, //

			@Parameter(description = "Object resource path.", required = true) //
			@Valid @RequestParam(name = "path", required = true) String path, //

			@Parameter(description = "Commit id.") //
			@Nullable @RequestParam(name = "commit", required = false) String commit, //

			@Parameter(description = "Date of reading. ISO DATE_TIME format.") //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = "at", required = false) LocalDateTime at //
	) {
		RestGetResourceEvent<ResourceVO> re = new RestGetResourceEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		re.setPath(path);
		re.setCommit(commit);
		re.setAt(timestamp(at));
		publisher.publishEvent(re);
		return handle(re);
	}

	@Operation(summary = "Updates an object resource with the given information.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = Object.class)) }), //
			@ApiResponse(responseCode = "4XX", description = "On update errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) } //
	)
	@PatchMapping(value = "/{entity}/{name}/" + PATH_RESOURCES, //
			consumes = { MediaType.APPLICATION_JSON_VALUE }, //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<Object> updateResource(//
			@Parameter(description = "Object type.", required = true) //
			@Valid @PathVariable(required = true) String entity, //

			@Parameter(description = "Object name.", required = true) //
			@Valid @PathVariable(required = true) String name, //

			@Parameter(description = "Resource content.", required = true) //
			@Valid @RequestBody(required = true) ResourceVO resource//
	) {
		RestUpdateResourceEvent<Object> re = new RestUpdateResourceEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		re.setResource(resource);
		publisher.publishEvent(re);
		return handle(re);
	}

	@Operation(summary = "Deletes an object resource by the given path.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "On success request.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = Object.class)) }), //
			@ApiResponse(responseCode = "4XX", description = "On read errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@DeleteMapping(value = "/{entity}/{name}/" + PATH_RESOURCES, //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<Object> deleteResource( //
			@Parameter(description = "Object type.", required = true) //
			@Valid @PathVariable(required = true) String entity, //

			@Parameter(description = "Object name.", required = true) //
			@Valid @PathVariable(required = true) String name, //

			@Parameter(description = "Object resource path.", required = true) //
			@Valid @RequestParam(name = "path", required = true) String path//
	) {
		RestDeleteResourceEvent<Object> re = new RestDeleteResourceEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		re.setPath(path);
		publisher.publishEvent(re);
		return handle(re);
	}

	@Operation(summary = "Count object resources.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "On success request.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = WrapperVO.class)) }), //
			@ApiResponse(responseCode = "4XX", description = "On read errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@GetMapping(value = "/{entity}/{name}/" + PATH_RESOURCES + "/count", //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<WrapperVO<Long>> countResources( //
			@Parameter(description = "Object type.", required = true) //
			@Valid @PathVariable(required = true) String entity, //

			@Parameter(description = "Object name.", required = true) //
			@Valid @PathVariable(required = true) String name, //

			@Parameter(description = "Filter predicate with pattern described at https://github.com/thiagolvlsantos/json-predicate.", //
					example = "{\"content.data\": {\"$contains\": \"html\"}}") //
			@Nullable @RequestParam(name = "filter", required = false) String filter, //

			@Parameter(description = "Pagination information.", example = "{ \"skip\":0, \"max\":10 }") //
			@Nullable @RequestParam(name = "paging", required = false) String paging, //

			@Parameter(description = "Commit id.") //
			@Nullable @RequestParam(name = "commit", required = false) String commit, //

			@Parameter(description = "Date of reading. ISO DATE_TIME format.") //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = "at", required = false) LocalDateTime at //
	) {
		RestCountResourcesEvent<WrapperVO<Long>> re = new RestCountResourcesEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		re.setFilter(filter);
		re.setPaging(paging);
		re.setCommit(commit);
		re.setAt(timestamp(at));
		publisher.publishEvent(re);
		return handle(re);
	}

	@Operation(summary = "List object resources.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "On success request.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							array = @ArraySchema(schema = @Schema(implementation = ResourceVO.class))) }),
			@ApiResponse(responseCode = "4XX", description = "On read errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@GetMapping(value = "/{entity}/{name}/" + PATH_RESOURCES + "/list", //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<List<ResourceVO>> listResources( //
			@Parameter(description = "Object type.", required = true) //
			@Valid @PathVariable(required = true) String entity, //

			@Parameter(description = "Object name.", required = true) //
			@Valid @PathVariable(required = true) String name, //

			@Parameter(description = "Filter predicate with pattern described at https://github.com/thiagolvlsantos/json-predicate.", //
					example = "{\"content.data\": {\"$contains\": \"html\"}}") //
			@Nullable @RequestParam(name = "filter", required = false) String filter, //

			@Parameter(description = "Pagination information.", example = "{ \"skip\":0, \"max\":10 }") //
			@Nullable @RequestParam(name = "paging", required = false) String paging, //

			@Parameter(description = "Sorting information.", example = "{ \"property\":\"metadata.path\", \"sort\":\"desc\" }") //
			@Nullable @RequestParam(name = "sorting", required = false) String sorting, //

			@Parameter(description = "Commit id.") //
			@Nullable @RequestParam(name = "commit", required = false) String commit, //

			@Parameter(description = "Date of reading. ISO DATE_TIME format.") //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = "at", required = false) LocalDateTime at //
	) {
		RestListResourcesEvent<List<ResourceVO>> re = new RestListResourcesEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		re.setFilter(filter);
		re.setPaging(paging);
		re.setSorting(sorting);
		re.setCommit(commit);
		re.setAt(timestamp(at));
		publisher.publishEvent(re);
		return handle(re);
	}

	// +------------- HISTORY METHODS ------------------+

	@Operation(summary = "Get the full history of a given entity.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "On success request.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							array = @ArraySchema(schema = @Schema(implementation = HistoryVO.class))) }), //
			@ApiResponse(responseCode = "4XX", description = "On read errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@GetMapping(value = "/{entity}/" + PATH_HISTORY, //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<List<HistoryVO>> history(//
			@Parameter(description = "Object type.", required = true) //
			@Valid @PathVariable(required = true) String entity, //

			@Parameter(description = "Pagination information.", example = "{ \"skip\":0, \"max\":10 }") //
			@Nullable @RequestParam(name = "paging", required = false) String paging //
	) {
		RestHistoryEvent<List<HistoryVO>> re = new RestHistoryEvent<>(this);
		re.setEntity(entity);
		re.setPaging(paging);
		publisher.publishEvent(re);
		return handle(re);
	}

	@Operation(summary = "Get an specific entity`s history.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "On success request.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							array = @ArraySchema(schema = @Schema(implementation = HistoryVO.class))) }), //
			@ApiResponse(responseCode = "4XX", description = "On read errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@GetMapping(value = "/{entity}/{name}/" + PATH_HISTORY, //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<List<HistoryVO>> historyName( //
			@Parameter(description = "Object type.", required = true) //
			@Valid @PathVariable(required = true) String entity, //

			@Parameter(description = "Object name.", required = true) //
			@Valid @PathVariable(required = true) String name, //

			@Parameter(description = "Pagination information.", example = "{ \"skip\":0, \"max\":10 }") //
			@Nullable @RequestParam(name = "paging", required = false) String paging //
	) {
		RestHistoryNameEvent<List<HistoryVO>> re = new RestHistoryNameEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		re.setPaging(paging);
		publisher.publishEvent(re);
		return handle(re);
	}

	@Operation(summary = "Get an entity`s resources history.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "On success request.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							array = @ArraySchema(schema = @Schema(implementation = HistoryVO.class))) }), //
			@ApiResponse(responseCode = "4XX", description = "On read errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@GetMapping(value = "/{entity}/{name}/" + PATH_RESOURCES + "/" + PATH_HISTORY, //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<List<HistoryVO>> historyResource( //
			@Parameter(description = "Object type.", required = true) //
			@Valid @PathVariable(required = true) String entity, //

			@Parameter(description = "Object name.", required = true) //
			@Valid @PathVariable(required = true) String name, //

			@Parameter(description = "Object resource path.") //
			@Nullable @RequestParam(name = "path", required = false) String path, //

			@Parameter(description = "Pagination information.", example = "{ \"skip\":0, \"max\":10 }") //
			@Nullable @RequestParam(name = "paging", required = false) String paging //
	) {
		RestHistoryResourceEvent<List<HistoryVO>> re = new RestHistoryResourceEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		re.setPath(path);
		re.setPaging(paging);
		publisher.publishEvent(re);
		return handle(re);
	}

	// +------------- COLLECTION METHODS ------------------+
	@Operation(summary = "Properties for entities mapping.", tags = { TAG })
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "On success request.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = Object.class)) }), //
			@ApiResponse(responseCode = "4XX", description = "On read errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@GetMapping(value = "/{entity}/" + PATH_PROPERTIES, //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<Map<String, Map<String, Object>>> properties(//
			@Parameter(description = "Object type.", required = true) //
			@Valid @PathVariable(required = true) String entity, //

			@Parameter(description = "Property names. Separated with ','.") //
			@Nullable @RequestParam(name = "properties", required = false) String properties, //

			@Parameter(description = "Filter predicate with pattern described at https://github.com/thiagolvlsantos/json-predicate.", //
					example = "{\"content.data\": {\"$contains\": \"html\"}}") //
			@Nullable @RequestParam(name = "filter", required = false) String filter, //

			@Parameter(description = "Pagination information.", example = "{ \"skip\":0, \"max\":10 }") //
			@Nullable @RequestParam(name = "paging", required = false) String paging, //

			@Parameter(description = "Sorting information.", example = "{ \"property\":\"metadata.path\", \"sort\":\"desc\" }") //
			@Nullable @RequestParam(name = "sorting", required = false) String sorting, //

			@Parameter(description = "Commit id.") //
			@Nullable @RequestParam(name = "commit", required = false) String commit, //

			@Parameter(description = "Date of reading. ISO DATE_TIME format.") //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = "at", required = false) LocalDateTime at //
	) {
		RestListPropertiesEvent<Map<String, Map<String, Object>>> re = new RestListPropertiesEvent<>(this);
		re.setEntity(entity);
		re.setProperties(properties);
		re.setFilter(filter);
		re.setPaging(paging);
		re.setSorting(sorting);
		re.setCommit(commit);
		re.setAt(timestamp(at));
		publisher.publishEvent(re);
		return handle(re);
	}
}