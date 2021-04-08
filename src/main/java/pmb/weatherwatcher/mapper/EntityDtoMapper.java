package pmb.weatherwatcher.mapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Entity to Dto Mapper.
 *
 * @param <E> Entity to retrieve
 * @param <D> DTO to map
 *
 */
public interface EntityDtoMapper<E, D> {

    /**
     * Converts the given Entity to a DTO.
     *
     * @param entity the entity to convert
     * @return the DTO generated
     */
    D toDto(E entity);

    /**
     * Converts an {@link Optional} Entity to a DTO.
     *
     * @param entity the entity to convert
     * @return the {@link Optional} DTO generated
     */
    default Optional<D> toOptionalDto(Optional<E> entity) {
        return entity.map(this::toDto);
    }

    /**
     * Converts a list of Entity to a list of DTO.
     *
     * @param entityList the List to convert
     * @return the list of DTO generated
     */
    default List<D> toDtoList(List<E> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().filter(Objects::nonNull).map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Converts a {@link Page} of Entity to a {@link Page} of DTO.
     *
     * @param entityPage the {@link Page} to convert
     * @return the {@link Page} of DTO generated
     */
    default Page<D> toDtoPage(Page<E> entityPage) {
        if (entityPage == null) {
            return null;
        }
        return new PageImpl<>(toDtoList(entityPage.getContent()), entityPage.getPageable(), entityPage.getTotalElements());
    }

    /**
     * Converts the given DTO to an Entity.
     *
     * @param dto the entity to convert
     * @return the Entity generated
     */
    E toEntity(D dto);

}
