package org.dhbw.mosbach.ai.tickets.database;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * Abstract Data Access Object base class.
 *
 * @author Alexander Auch
 * @param <E> Entity
 * @param <I> Type of Identity value (primary key type)
 */
public abstract class DefaultDAO<E, I> implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceContext
	protected EntityManager em;

	protected final Class<?> entityClass;
	protected boolean enableQueryCaching = true;

	public DefaultDAO() {
		@SuppressWarnings("serial")
		final TypeToken<E> type = new TypeToken<E>(getClass()) {
		};

		entityClass = type.getRawType();
	}

	/**
	 * This method can be overridden in sub classes to alter an entity before a
	 * save operation (persist or merge).
	 *
	 * @return original or modified entity
	 */
	protected E checkConsistencyBeforeSave(E entity) {
		return entity;
	}

	@Transactional
	public void persist(E entity) {
		em.persist(checkConsistencyBeforeSave(entity));
	}

	@SafeVarargs
	@Transactional
	public final void persist(@SuppressWarnings("unchecked") E... entities) {
		for (final E entity : entities) {
			persist(entity);
		}
	}

	/**
	 * Tries to determine whether the entity is new or already in the DB by
	 * looking at its Id key.
	 *
	 * @param entity entity
	 */
	@Transactional
	public void persistOrMerge(E entity) {
		if (isPersisted(entity)) {
			merge(entity);
		} else {
			persist(entity);
		}
	}

	/**
	 * Flush the entity manager.
	 */
	public void flush() {
		em.flush();
	}

	/**
	 * Tries to determine whether the entity is new or already in the DB by
	 * looking at its Id key.
	 *
	 * @param entity entity
	 * @return true if it is persisted in the DB according to its primary key
	 */
	public boolean isPersisted(E entity) {
		final I id = getId(entity);

		return ((id != null) && ((Number) id).longValue() != 0);
	}

	/**
	 * Removes managed entities.
	 *
	 * @param entity entity
	 */
	@Transactional
	public void remove(E entity) {
		em.remove(entity);
	}

	/**
	 * Removes managed and detached entities.
	 *
	 * @param detachedEntity detachedEntity
	 */
	@Transactional
	public void removeDetached(E detachedEntity) {
		em.remove(reattach(detachedEntity));
	}

	/**
	 * Checks whether the given entity is in the db prior to trying to delete it
	 *
	 * @param entity entity
	 */
	public void removeSilently(E entity) {
		if (isPersisted(entity)) {
			remove(entity);
		}
	}

	@Transactional
	public E merge(E entity) {
		return em.merge(checkConsistencyBeforeSave(entity));
	}

	/**
	 * Tries to find the entity for the given id.
	 *
	 * @param id id
	 * @return entity or null if no matching entity found
	 */
	@SuppressWarnings("unchecked")
	public E findById(I id) {
		return (E) em.find(entityClass, id);
	}

	/**
	 * Tries to find an instance in the db by using the given key.
	 *
	 * @param fieldName field to search the key
	 * @param key key to search
	 * @return entity or null if none found
	 */
	public E findByUnique(String fieldName, Object key) {
		final String query = String.format("FROM %s e WHERE e.%s = :key", entityClass.getName(), fieldName);

		@SuppressWarnings("unchecked")
		final List<E> resultList = (List<E>) cacheable(em.createQuery(query, entityClass)).setParameter("key", key)
		.getResultList();

		return resultList.isEmpty() ? null : resultList.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<E> getAll() {
		final String query = String.format("FROM %s e", entityClass.getName());

		return (List<E>) cacheable(em.createQuery(query, entityClass)).getResultList();
	}

	@Transactional
	public List<E> getAllFullyLoaded() {
		final List<E> all = getAll();
		Tools.loadFully(all);

		return all;
	}

	/**
	 * Returns a list sorted by the given function. The list will be sorted by
	 * Java, so this is an expensive operation for big lists.
	 *
	 * @param stringFieldExtractor extractor function, e.g. <code>Entity::getName</code>
	 * @return sorted list.
	 */
	public List<E> getAllSortedBy(Function<E, String> stringFieldExtractor) {
		final List<E> sortedList = Lists.newArrayList(getAll());
		sortedList.sort(Comparator.comparing(stringFieldExtractor, String.CASE_INSENSITIVE_ORDER));

		return sortedList;
	}

	/**
	 * Determines the primary key of the given entity by using reflection.
	 *
	 * @param e entity
	 * @return primary key of the given entity
	 */
	public I getId(E e) {
		return Tools.<I> getEntityKey(e);
	}

	/**
	 * Tries to find a managed entity corresponding to the given (possibly
	 * detached) entity. If the entity is already managed, it will be returned
	 * immediately without Db lookup, so it is safe to call this function.
	 * <strong> Please be aware that any modifications to the given entity will
	 * not be reflected by the returned entity, if it has to be re-loaded from the
	 * persistence context!</strong> If the entity is not persisted nor has a
	 * valid key, null will be returned.
	 *
	 * @param e entity
	 * @return managed entity
	 */
	@Transactional
	public E reattach(E e) {
		return em.contains(e) ? e : (isPersisted(e) ? findById(getId(e)) : null);
	}

	/**
	 * Reloads an entity from the database, and reattaches it if needed.
	 *
	 * @param e entity
	 * @return refreshed entity
	 */
	@Transactional
	public E refresh(E e) {
		if (em.contains(e)) {
			em.refresh(e);

			return e;
		} else {
			return reattach(e);
		}
	}

	/**
	 * Sets property to enable query caching.
	 *
	 * @param query query
	 * @return query
	 */
	protected <T> TypedQuery<T> cacheable(TypedQuery<T> query) {
		return enableQueryCaching ? query.setHint("org.hibernate.cacheable", "true")
				.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.USE) : query;
	}
}
