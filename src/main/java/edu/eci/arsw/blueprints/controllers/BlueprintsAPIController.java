package edu.eci.arsw.blueprints.controllers;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.eci.arsw.blueprints.dto.BlueprintDTO;
import edu.eci.arsw.blueprints.dto.PointDTO;
import edu.eci.arsw.blueprints.dto.BlueprintMapper;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/blueprints") //Punto 1 del parcial
public class BlueprintsAPIController {

    private final BlueprintsServices services;

    public BlueprintsAPIController(BlueprintsServices services) { this.services = services; }

    // GET /blueprints
    @Operation(summary = "Obtener todos los blueprints")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de blueprints obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "No se encontraron blueprints",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"code\":404,\"message\":\"No se encontraron blueprints\",\"data\":null}"
                )
            )
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Set<BlueprintDTO>>> getAll() {
        Set<Blueprint> data = services.getAllBlueprints();
        Set<BlueprintDTO> dtoSet = data.stream().map(BlueprintMapper::toDTO).collect(java.util.stream.Collectors.toSet());
        return ResponseEntity.ok(new ApiResponse<>(200, "Success", dtoSet));
    }

    // GET /blueprints/{author}
    @Operation(summary = "Obtener todos los blueprints de un autor")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de blueprints del autor obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "No se encontraron blueprints para el autor",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"code\":404,\"message\":\"No se encontraron blueprints para el autor\",\"data\":null}"
                )
            )
        )
    })
    @GetMapping("/{author}")
    public ResponseEntity<ApiResponse<Set<BlueprintDTO>>> byAuthor(@PathVariable String author) {
        try {
            Set<Blueprint> data = services.getBlueprintsByAuthor(author);
            Set<BlueprintDTO> dtoSet = data.stream().map(BlueprintMapper::toDTO).collect(java.util.stream.Collectors.toSet());
            return ResponseEntity.ok(new ApiResponse<>(200, "Success", dtoSet)); // 200 OK
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(404, e.getMessage(), null)); // 404 Not Found
        }
    }


    // GET /blueprints/{author}/{bpname}
    @Operation(summary = "Obtener un blueprint por autor y nombre")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Blueprint obtenido exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Blueprint no encontrado",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"code\":404,\"message\":\"Blueprint no encontrado\",\"data\":null}"
                )
            )
        )
    })
    @GetMapping("/{author}/{bpname}")
    public ResponseEntity<ApiResponse<BlueprintDTO>> byAuthorAndName(@PathVariable String author, @PathVariable String bpname) {
        try {
            Blueprint data = services.getBlueprint(author, bpname);
            BlueprintDTO dto = BlueprintMapper.toDTO(data);
            return ResponseEntity.ok(new ApiResponse<>(200, "Success", dto)); // 200 OK
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(404, e.getMessage(), null)); // 404 Not Found
        }
    }


    // POST /blueprints
    @Operation(summary = "Agregar un nuevo blueprint")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Blueprint creado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Solicitud inválida o datos incorrectos",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"code\":400,\"message\":\"Solicitud inválida o datos incorrectos\",\"data\":null}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "El blueprint ya existe",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"code\":409,\"message\":\"El blueprint ya existe\",\"data\":null}"
                )
            )
        )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> add(@Valid @RequestBody NewBlueprintRequest req) {
        try {
            List<Point> points = req.points().stream()
                    .map(p -> new Point(p.x(), p.y()))
                    .collect(Collectors.toList());
            Blueprint bp = new Blueprint(req.author(), req.name(), points);
            services.addNewBlueprint(bp);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Created", null)); // 201 Created
        } catch (BlueprintPersistenceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(400, e.getMessage(), null)); // 400 Bad Request
        }
    }

    // PUT /blueprints/{author}/{bpname}/points
    @Operation(summary = "Agregar un punto a un blueprint existente")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Punto agregado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Blueprint no encontrado",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"code\":404,\"message\":\"Blueprint no encontrado\",\"data\":null}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Solicitud inválida o datos incorrectos",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"code\":400,\"message\":\"Solicitud inválida o datos incorrectos\",\"data\":null}"
                )
            )
        )
    })
    @PutMapping("/{author}/{bpname}/points")
    public ResponseEntity<ApiResponse<Void>> addPoint(@PathVariable String author, @PathVariable String bpname,
                                      @RequestBody PointDTO p) {
        try {
            services.addPoint(author, bpname, p.getX(), p.getY());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Created", null)); // 201 Created
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(404, e.getMessage(), null)); // 404 Not Found
        }
    }

    public record NewBlueprintRequest(
            @NotBlank String author,
            @NotBlank String name,
            @Valid java.util.List<Point> points
    ) { }
}
