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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.thiagolvlsantos.rest.storage.error.ApiFailure;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping(GenericRestController.API_URL_VERSION)
@Slf4j
public class GenericRestController {

	public static final String API_URL_VERSION = "/v1";

	public static final String PATH_PROPERTIES = "properties";
	public static final String PATH_RESOURCES = "resources";
	public static final String PATH_HISTORY = "history";

	public static final String TAG = "All";
	public static final String TAG_DESCRIPTION = "<p> <b>IMPORTANT</b>: The name parts depends on your implementation, i.e. for file-rest-storage implementation names have pattern: 'string(;string)*'.</p>";

	public static final String TAG_BASIC = "Basic";
	public static final String TAG_BASIC_DESCRIPTION = "Basic operations. " + TAG_DESCRIPTION;

	public static final String TAG_PROPERTIES = "Properties";
	public static final String TAG_PROPERTIES_DESCRIPTION = "Properties operations." + TAG_DESCRIPTION;

	public static final String TAG_RESOURCES = "Resources";
	public static final String TAG_RESOURCES_DESCRIPTION = "Resources operations." + TAG_DESCRIPTION;

	public static final String TAG_HISTORY = "History";
	public static final String TAG_HISTORY_DESCRIPTION = "History operations." + TAG_DESCRIPTION;

	private static final String PATH_SEPARATOR = "/";

	private static final String PATH_ENTITY_TYPE_NAME = "entity";
	private static final String PATH_ENTITY_TYPE_DESCRIPTION = "Entity type.";
	private static final String PATH_ENTITY_NAME_NAME = "name";
	private static final String PATH_ENTITY_NAME_DESCRIPTION = "Entity name.";

	private static final String PARAMETER_FILTER_NAME = "filter";
	private static final String PARAMETER_FILTER_DESCRIPTION = "Filter predicate as String. i.e. JSON pattern described at https://github.com/thiagolvlsantos/json-predicate for file-storage backend services.";
	private static final String PARAMETER_FILTER_EXAMPLE = "{\"name\": {\"$contains\": \"k8s\"}}";

	private static final String PARAMETER_PAGING_NAME = "paging";
	private static final String PARAMETER_PAGINATION_DESCRIPTION = "Pagination information, with optional skip and max values.";
	private static final String PARAMETER_PAGINATION_EXAMPLE = "{ \"skip\":0, \"max\":10 }";

	private static final String PARAMETER_SORTING_NAME = "sorting";
	private static final String PARAMETER_SORTING_DESCRIPTION = "Sorting information, with fields and orders. i.e. In the JSON format accepted by file-storage.";
	private static final String PARAMETER_SORTING_EXAMPLE = "{ \"property\":\"parent.name\", \"sort\":\"asc\", \"nullsFirst\":true }";

	private static final String PARAMETER_COMMIT_NAME = "commit";
	private static final String PARAMETER_COMMIT_DESCRIPTION = "Commit id.";

	private static final String PARAMETER_AT_NAME = "at";
	private static final String PARAMETER_AT_DESCRIPTION = "Date of reading. ISO DATE_TIME format.";

	private @Autowired ApplicationEventPublisher publisher;

	@PostConstruct
	public void init() {
		if (log.isInfoEnabled()) {
			log.info(getClass().getSimpleName() + " init:" + API_URL_VERSION);
		}
	}

	// +------------- ENTITY METHODS ------------------+

	// EXAMPLE:
	// https://www.dariawan.com/tutorials/spring/documenting-spring-boot-rest-api-springdoc-openapi-3/

	@Operation(summary = "Creates an entity with the given information.", tags = { TAG_BASIC })
	@Tag(name = GenericRestController.TAG_BASIC, description = TAG_BASIC_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = "Entity information to add.", required = true, //
					schema = @Schema(implementation = Object.class)) //
			@Valid @RequestBody(required = true) String content //
	) throws URISyntaxException {
		RestSaveEvent<IObjectReference> re = new RestSaveEvent<>(this);
		re.setEntity(entity);
		re.setContent(content);
		publisher.publishEvent(re);
		IObjectReference response = re.getResult();
		if (response == null) {
			return ResponseEntity.badRequest().build();
		}
		URI uri = new URI(API_URL_VERSION + PATH_SEPARATOR + entity + PATH_SEPARATOR + response.getReference());
		return ResponseEntity.created(uri).body(response);
	}

	@Operation(summary = "Reads the entity data by the given name.", tags = { TAG_BASIC })
	@Tag(name = GenericRestController.TAG_BASIC, description = TAG_BASIC_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = PATH_ENTITY_NAME_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_NAME_NAME, required = true) String name, //

			@Parameter(description = PARAMETER_COMMIT_DESCRIPTION) //
			@Nullable @RequestParam(name = PARAMETER_COMMIT_NAME, required = false) String commit, //

			@Parameter(description = PARAMETER_AT_DESCRIPTION) //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = PARAMETER_AT_NAME, required = false) LocalDateTime at //
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

	@Operation(summary = "Updates the entity with the given information.", tags = { TAG_BASIC })
	@Tag(name = GenericRestController.TAG_BASIC, description = TAG_BASIC_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = PATH_ENTITY_NAME_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_NAME_NAME, required = true) String name, //

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

	@Operation(summary = "Deletes an entity by the given name.", tags = { TAG_BASIC })
	@Tag(name = GenericRestController.TAG_BASIC, description = TAG_BASIC_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = PATH_ENTITY_NAME_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_NAME_NAME, required = true) String name //
	) {
		RestDeleteEvent<Object> re = new RestDeleteEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		publisher.publishEvent(re);
		return handle(re);
	}

	@Operation(summary = "Count objects.", tags = { TAG_BASIC })
	@Tag(name = GenericRestController.TAG_BASIC, description = TAG_BASIC_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = PARAMETER_FILTER_DESCRIPTION, //
					example = PARAMETER_FILTER_EXAMPLE) //
			@Nullable @RequestParam(name = PARAMETER_FILTER_NAME, required = false) String filter, //

			@Parameter(description = PARAMETER_PAGINATION_DESCRIPTION, example = PARAMETER_PAGINATION_EXAMPLE) //
			@Nullable @RequestParam(name = PARAMETER_PAGING_NAME, required = false) String paging, //

			@Parameter(description = PARAMETER_COMMIT_DESCRIPTION) //
			@Nullable @RequestParam(name = PARAMETER_COMMIT_NAME, required = false) String commit, //

			@Parameter(description = PARAMETER_AT_DESCRIPTION) //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = PARAMETER_AT_NAME, required = false) LocalDateTime at //
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

	@Operation(summary = "List objects.", tags = { TAG_BASIC })
	@Tag(name = GenericRestController.TAG_BASIC, description = TAG_BASIC_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = PARAMETER_FILTER_DESCRIPTION, //
					example = PARAMETER_FILTER_EXAMPLE) //
			@Nullable @RequestParam(name = PARAMETER_FILTER_NAME, required = false) String filter, //

			@Parameter(description = PARAMETER_PAGINATION_DESCRIPTION, example = PARAMETER_PAGINATION_EXAMPLE) //
			@Nullable @RequestParam(name = PARAMETER_PAGING_NAME, required = false) String paging, //

			@Parameter(description = PARAMETER_SORTING_DESCRIPTION, example = PARAMETER_SORTING_EXAMPLE) //
			@Nullable @RequestParam(name = PARAMETER_SORTING_NAME, required = false) String sorting, //

			@Parameter(description = PARAMETER_COMMIT_DESCRIPTION) //
			@Nullable @RequestParam(name = PARAMETER_COMMIT_NAME, required = false) String commit, //

			@Parameter(description = PARAMETER_AT_DESCRIPTION) //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = PARAMETER_AT_NAME, required = false) LocalDateTime at //
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

	@Operation(summary = "Updates an object property with the given information.", tags = { TAG_PROPERTIES })
	@Tag(name = GenericRestController.TAG_PROPERTIES, description = TAG_PROPERTIES_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = PATH_ENTITY_NAME_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_NAME_NAME, required = true) String name, //

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

	@Operation(summary = "Updates a property of all objects with the given information.", tags = { TAG_PROPERTIES })
	@Tag(name = GenericRestController.TAG_PROPERTIES, description = TAG_PROPERTIES_DESCRIPTION)
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							array = @ArraySchema(schema = @Schema(implementation = Object.class))) }), //
			@ApiResponse(responseCode = "4XX", description = "On update errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@PatchMapping(value = "/{entity}/" + PATH_PROPERTIES + "/{property}", //
			consumes = { MediaType.APPLICATION_JSON_VALUE }, //
			produces = { MediaType.APPLICATION_JSON_VALUE }//
	)
	public ResponseEntity<List<Object>> setProperty(//
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = "Object property.", required = true) //
			@Valid @PathVariable(required = true) String property, //

			@Parameter(description = "Property data as String.", required = true) //
			@Valid @RequestBody(required = true) String dataAsString, //

			@Parameter(description = PARAMETER_FILTER_DESCRIPTION, //
					example = PARAMETER_FILTER_EXAMPLE) //
			@Nullable @RequestParam(name = PARAMETER_FILTER_NAME, required = false) String filter, //

			@Parameter(description = PARAMETER_PAGINATION_DESCRIPTION, example = PARAMETER_PAGINATION_EXAMPLE) //
			@Nullable @RequestParam(name = PARAMETER_PAGING_NAME, required = false) String paging, //

			@Parameter(description = PARAMETER_SORTING_DESCRIPTION, example = PARAMETER_SORTING_EXAMPLE) //
			@Nullable @RequestParam(name = PARAMETER_SORTING_NAME, required = false) String sorting //

	) {
		RestSetPropertiesEvent<List<Object>> re = new RestSetPropertiesEvent<>(this);
		re.setEntity(entity);
		re.setProperty(property);
		re.setDataAsString(dataAsString);
		re.setFilter(filter);
		re.setPaging(paging);
		re.setSorting(sorting);
		publisher.publishEvent(re);
		return handle(re);
	}

	@Operation(summary = "Reads an object property by the given name.", tags = { TAG_PROPERTIES })
	@Tag(name = GenericRestController.TAG_PROPERTIES, description = TAG_PROPERTIES_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = PATH_ENTITY_NAME_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_NAME_NAME, required = true) String name, //

			@Parameter(description = "Object property.", required = true) //
			@Valid @PathVariable(required = true) String property, //

			@Parameter(description = PARAMETER_COMMIT_DESCRIPTION) //
			@Nullable @RequestParam(name = PARAMETER_COMMIT_NAME, required = false) String commit, //

			@Parameter(description = PARAMETER_AT_DESCRIPTION) //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = PARAMETER_AT_NAME, required = false) LocalDateTime at //
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

	@Operation(summary = "Properties mapping.", tags = { TAG_PROPERTIES })
	@Tag(name = GenericRestController.TAG_PROPERTIES, description = TAG_PROPERTIES_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = PATH_ENTITY_NAME_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_NAME_NAME, required = true) String name, //

			@Parameter(description = "Property names. Separated by ';'.") //
			@Nullable @RequestParam(name = "properties", required = false) String properties, //

			@Parameter(description = PARAMETER_COMMIT_DESCRIPTION) //
			@Nullable @RequestParam(name = PARAMETER_COMMIT_NAME, required = false) String commit, //

			@Parameter(description = PARAMETER_AT_DESCRIPTION) //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = PARAMETER_AT_NAME, required = false) LocalDateTime at //
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

	@Operation(summary = "Properties for entities mapping.", tags = { TAG_PROPERTIES })
	@Tag(name = GenericRestController.TAG_PROPERTIES, description = TAG_PROPERTIES_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = "Property names. Separated by ';'.") //
			@Nullable @RequestParam(name = "properties", required = false) String properties, //

			@Parameter(description = PARAMETER_FILTER_DESCRIPTION, //
					example = PARAMETER_FILTER_EXAMPLE) //
			@Nullable @RequestParam(name = PARAMETER_FILTER_NAME, required = false) String filter, //

			@Parameter(description = PARAMETER_PAGINATION_DESCRIPTION, example = PARAMETER_PAGINATION_EXAMPLE) //
			@Nullable @RequestParam(name = PARAMETER_PAGING_NAME, required = false) String paging, //

			@Parameter(description = PARAMETER_SORTING_DESCRIPTION, example = PARAMETER_SORTING_EXAMPLE) //
			@Nullable @RequestParam(name = PARAMETER_SORTING_NAME, required = false) String sorting, //

			@Parameter(description = PARAMETER_COMMIT_DESCRIPTION) //
			@Nullable @RequestParam(name = PARAMETER_COMMIT_NAME, required = false) String commit, //

			@Parameter(description = PARAMETER_AT_DESCRIPTION) //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = PARAMETER_AT_NAME, required = false) LocalDateTime at //
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

	// +------------- RESOURCE METHODS ------------------+

	@Operation(summary = "Creates an object resource with the given information.", tags = { TAG_RESOURCES })
	@Tag(name = GenericRestController.TAG_RESOURCES, description = TAG_RESOURCES_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = PATH_ENTITY_NAME_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_NAME_NAME, required = true) String name, //

			@Parameter(description = "Resource content.", required = true) //
			@Valid @RequestBody(required = true) ResourceVO resource//
	) throws URISyntaxException {
		RestSetResourceEvent<IObjectReference> re = new RestSetResourceEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		re.setResource(resource);
		publisher.publishEvent(re);
		IObjectReference response = re.getResult();
		if (response == null) {
			return ResponseEntity.badRequest().build();
		}
		URI uri = new URI(API_URL_VERSION + PATH_SEPARATOR + entity + PATH_SEPARATOR + response.getReference()
				+ "/resources?path=" + resource.getMetadata().getPath());
		return ResponseEntity.created(uri).body(response);
	}

	@Operation(summary = "Reads an object resource by the given path.", tags = { TAG_RESOURCES })
	@Tag(name = GenericRestController.TAG_RESOURCES, description = TAG_RESOURCES_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = PATH_ENTITY_NAME_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_NAME_NAME, required = true) String name, //

			@Parameter(description = "Object resource path.", required = true) //
			@Valid @RequestParam(name = "path", required = true) String path, //

			@Parameter(description = PARAMETER_COMMIT_DESCRIPTION) //
			@Nullable @RequestParam(name = PARAMETER_COMMIT_NAME, required = false) String commit, //

			@Parameter(description = PARAMETER_AT_DESCRIPTION) //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = PARAMETER_AT_NAME, required = false) LocalDateTime at //
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

	@Operation(summary = "Updates an object resource with the given information.", tags = { TAG_RESOURCES })
	@Tag(name = GenericRestController.TAG_RESOURCES, description = TAG_RESOURCES_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = PATH_ENTITY_NAME_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_NAME_NAME, required = true) String name, //

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

	@Operation(summary = "Deletes an object resource by the given path.", tags = { TAG_RESOURCES })
	@Tag(name = GenericRestController.TAG_RESOURCES, description = TAG_RESOURCES_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = PATH_ENTITY_NAME_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_NAME_NAME, required = true) String name, //

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

	@Operation(summary = "Count object resources.", tags = { TAG_RESOURCES })
	@Tag(name = GenericRestController.TAG_RESOURCES, description = TAG_RESOURCES_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = PATH_ENTITY_NAME_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_NAME_NAME, required = true) String name, //

			@Parameter(description = PARAMETER_FILTER_DESCRIPTION, //
					example = "{\"content.data\": {\"$contains\": \"html\"}}") //
			@Nullable @RequestParam(name = PARAMETER_FILTER_NAME, required = false) String filter, //

			@Parameter(description = PARAMETER_PAGINATION_DESCRIPTION, example = PARAMETER_PAGINATION_EXAMPLE) //
			@Nullable @RequestParam(name = PARAMETER_PAGING_NAME, required = false) String paging, //

			@Parameter(description = PARAMETER_COMMIT_DESCRIPTION) //
			@Nullable @RequestParam(name = PARAMETER_COMMIT_NAME, required = false) String commit, //

			@Parameter(description = PARAMETER_AT_DESCRIPTION) //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = PARAMETER_AT_NAME, required = false) LocalDateTime at //
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

	@Operation(summary = "List object resources.", tags = { TAG_RESOURCES })
	@Tag(name = GenericRestController.TAG_RESOURCES, description = TAG_RESOURCES_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = PATH_ENTITY_NAME_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_NAME_NAME, required = true) String name, //

			@Parameter(description = PARAMETER_FILTER_DESCRIPTION, //
					example = "{\"content.data\": {\"$contains\": \"html\"}}") //
			@Nullable @RequestParam(name = PARAMETER_FILTER_NAME, required = false) String filter, //

			@Parameter(description = PARAMETER_PAGINATION_DESCRIPTION, example = PARAMETER_PAGINATION_EXAMPLE) //
			@Nullable @RequestParam(name = PARAMETER_PAGING_NAME, required = false) String paging, //

			@Parameter(description = PARAMETER_SORTING_DESCRIPTION, example = "{ \"property\":\"metadata.path\", \"sort\":\"desc\" }") //
			@Nullable @RequestParam(name = PARAMETER_SORTING_NAME, required = false) String sorting, //

			@Parameter(description = PARAMETER_COMMIT_DESCRIPTION) //
			@Nullable @RequestParam(name = PARAMETER_COMMIT_NAME, required = false) String commit, //

			@Parameter(description = PARAMETER_AT_DESCRIPTION) //
			@Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //
			@RequestParam(name = PARAMETER_AT_NAME, required = false) LocalDateTime at //
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

	@Operation(summary = "Get the full history of a given entity.", tags = { TAG_HISTORY })
	@Tag(name = GenericRestController.TAG_HISTORY, description = TAG_HISTORY_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = PARAMETER_PAGINATION_DESCRIPTION, example = PARAMETER_PAGINATION_EXAMPLE) //
			@Nullable @RequestParam(name = PARAMETER_PAGING_NAME, required = false) String paging //
	) {
		RestHistoryEvent<List<HistoryVO>> re = new RestHistoryEvent<>(this);
		re.setEntity(entity);
		re.setPaging(paging);
		publisher.publishEvent(re);
		return handle(re);
	}

	@Operation(summary = "Get an specific entity`s history.", tags = { TAG_HISTORY })
	@Tag(name = GenericRestController.TAG_HISTORY, description = TAG_HISTORY_DESCRIPTION)
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
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = PATH_ENTITY_NAME_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_NAME_NAME, required = true) String name, //

			@Parameter(description = PARAMETER_PAGINATION_DESCRIPTION, example = PARAMETER_PAGINATION_EXAMPLE) //
			@Nullable @RequestParam(name = PARAMETER_PAGING_NAME, required = false) String paging //
	) {
		RestHistoryNameEvent<List<HistoryVO>> re = new RestHistoryNameEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		re.setPaging(paging);
		publisher.publishEvent(re);
		return handle(re);
	}

	@Operation(summary = "Get an entity`s resources history.", tags = { TAG_HISTORY })
	@Tag(name = GenericRestController.TAG_HISTORY, description = TAG_HISTORY_DESCRIPTION)
	@ApiResponses(value = { //
			@ApiResponse(responseCode = "200", description = "On success request.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							array = @ArraySchema(schema = @Schema(implementation = HistoryVO.class))) }), //
			@ApiResponse(responseCode = "4XX", description = "On read errors.", //
					content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, //
							schema = @Schema(implementation = ApiFailure.class)) }) //
	})
	@GetMapping(value = "/{entity}/{name}/" + PATH_RESOURCES + PATH_SEPARATOR + PATH_HISTORY, //
			produces = { MediaType.APPLICATION_JSON_VALUE } //
	)
	public ResponseEntity<List<HistoryVO>> historyResource( //
			@Parameter(description = PATH_ENTITY_TYPE_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_TYPE_NAME, required = true) String entity, //

			@Parameter(description = PATH_ENTITY_NAME_DESCRIPTION, required = true, //
					schema = @Schema(implementation = String.class)) //
			@Valid @NotBlank @PathVariable(name = PATH_ENTITY_NAME_NAME, required = true) String name, //

			@Parameter(description = "Object resource path.") //
			@Nullable @RequestParam(name = "path", required = false) String path, //

			@Parameter(description = PARAMETER_PAGINATION_DESCRIPTION, example = PARAMETER_PAGINATION_EXAMPLE) //
			@Nullable @RequestParam(name = PARAMETER_PAGING_NAME, required = false) String paging //
	) {
		RestHistoryResourceEvent<List<HistoryVO>> re = new RestHistoryResourceEvent<>(this);
		re.setEntity(entity);
		re.setName(name);
		re.setPath(path);
		re.setPaging(paging);
		publisher.publishEvent(re);
		return handle(re);
	}

}