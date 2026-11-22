
package ru.aston.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.aston.homework.dto.UserCreateRequest;
import ru.aston.homework.dto.UserResponse;
import ru.aston.homework.dto.UserUpdateRequest;
import ru.aston.homework.exception.RequestIdMismatchException;
import ru.aston.homework.exception.UserNotFoundException;
import ru.aston.homework.service.UserService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(
        name = "Управление пользователями",
        description = "API для создания, получения, обновления и удаления пользователей"
)
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список всех зарегистрированных пользователей"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список пользователей успешно получен"
    )
    @GetMapping
    public CollectionModel<EntityModel<UserResponse>> findAll() {

        List<EntityModel<UserResponse>> users = userService.findAll()
                .stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(
                users,
                linkTo(methodOn(UserController.class).findAll()).withSelfRel(),
                linkTo(methodOn(UserController.class).create(null)).withRel("create")
        );
    }

    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает информацию о пользователе по его уникальному идентификатору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно найден"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь с указанным ID не найден",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public EntityModel<UserResponse> findById(
            @Parameter(
                    description = "Уникальный идентификатор пользователя",
                    example = "1"
            )
            @PathVariable Long id
    ) {
        UserResponse user = userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return toModel(user);
    }

    @Operation(
            summary = "Создать пользователя",
            description = "Создаёт нового пользователя на основе переданных данных"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Пользователь успешно создан"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные пользователя",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Пользователь с таким email уже существует",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserResponse>> create(
            @Valid @RequestBody UserCreateRequest request
    ) {
        UserResponse response = userService.create(request);

        EntityModel<UserResponse> model = toModel(response);

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class)
                        .findById(response.id())).toUri())
                .body(model);
    }

    @Operation(
            summary = "Обновить пользователя",
            description = "Обновляет данные существующего пользователя по его уникальному идентификатору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно обновлён"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные или несовпадение ID",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь с указанным ID не найден",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Указанный email уже занят",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public EntityModel<UserResponse> update(
            @Parameter(
                    description = "ID пользователя, которого необходимо обновить. Должен совпадать с ID в теле запроса",
                    example = "3"
            )
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        if (!id.equals(request.id())) {
            throw new RequestIdMismatchException(id, request.id());
        }

        UserResponse response = userService.update(request);

        return toModel(response);
    }
    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя из системы по его уникальному идентификатору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Пользователь успешно удалён",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь с указанным ID не найден",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(
                    description = "Уникальный идентификатор пользователя, которого необходимо удалить",
                    example = "10"
            )
            @PathVariable Long id
    ) {
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }

    private EntityModel<UserResponse> toModel(UserResponse user) {

        return EntityModel.of(
                user,

                linkTo(methodOn(UserController.class)
                        .findById(user.id()))
                        .withSelfRel(),

                linkTo(methodOn(UserController.class)
                        .findAll())
                        .withRel("users"),

                linkTo(methodOn(UserController.class)
                        .update(user.id(), null))
                        .withRel("update"),

                linkTo(methodOn(UserController.class)
                        .delete(user.id()))
                        .withRel("delete")
        );
    }
}
