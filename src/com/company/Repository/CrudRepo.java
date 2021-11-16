package com.company.Repository;


import java.util.UUID;

public interface CrudRepo<E> {
    /**
     * @param id id must not be null
     * @return the entity with the specified id or null - if there is no entity with the given id
     *
     */
    E findOne(UUID id);
    /**
     * @return all entities
     */
    Iterable<E> findAll();
    /**
     * @param entity entity must be not null
     * @return null- if the given entity is saved otherwise returns the entity (id already exists)
     *
     */
    E save(E entity);
    /**
     * @param id id must be not null
     * @return the removed entity or null if there is no entity with the given id
     *
     */
    E delete(UUID id);
    /**
     * @param entity entity must not be null
     * @return null - if the entity is updated, otherwise returns the entity - (e.g id does not exist).
     *
     */
    E update(E entity);
}
