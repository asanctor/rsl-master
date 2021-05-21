package rsl.persistence;

import rsl.util.Log;

import javax.persistence.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.*;

public abstract class RslPersistence {

    private final EntityManagerFactory emf;
    private final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<EntityManager>();
    private final ThreadLocal<Integer> threadLocalTransactionsRunning = new ThreadLocal<Integer>();

    public RslPersistence(String connectionString) {
        this(new HashMap<>(), connectionString);
    }

    public RslPersistence(Map<String, String> properties, String connectionString)
    {
        emf = javax.persistence.Persistence.createEntityManagerFactory(connectionString, properties);
    }

    private EntityManager getEntityManager() {
        EntityManager em = threadLocalEntityManager.get();
        if (em == null) {
            em = emf.createEntityManager(); // slow performance
            threadLocalEntityManager.set(em);
            threadLocalTransactionsRunning.set(0);
        }
        return em;
    }

    public boolean startTransaction()
    {
        EntityManager em = this.getEntityManager();
        if(!em.getTransaction().isActive()) {
            threadLocalTransactionsRunning.set(1);
            em.getTransaction().begin();
            return true;
        }else{
            int transactionCount = threadLocalTransactionsRunning.get();
            threadLocalTransactionsRunning.set(++transactionCount);
            return false;
        }
    }

    public boolean endTransaction()
    {
        EntityManager em = this.getEntityManager();

        int transactionCount = threadLocalTransactionsRunning.get();
        threadLocalTransactionsRunning.set(--transactionCount);

        if(transactionCount <= 0)
        {
            threadLocalTransactionsRunning.set(0);
            if(em.getTransaction().isActive()){
                em.getTransaction().commit();
                return true;
            }else{
                Log.warning("No transactions running!");
                return true;
            }
        }
        return false;
    }

    public void refresh(Object o)
    {
        EntityManager em = this.getEntityManager();
        em.refresh(o);
    }

    public void refresh(List o)
    {
        EntityManager em = this.getEntityManager();
        for (int i = 0; i < o.size(); i++) em.refresh(o.get(i));
    }

    public void forceEndTransaction()
    {
        EntityManager em = this.getEntityManager();
        if(em.getTransaction().isActive())
        {
            em.getTransaction().commit();
        }
        threadLocalTransactionsRunning.set(0);
    }

    public void rollbackTransaction()
    {
        this.getEntityManager().getTransaction().rollback();
    }


    public <T> T getByDatabaseID(Class<T> type, long id)
    {
        EntityManager em = getEntityManager();
        return em.find(type, id);
    }

    public <T> T getByID(Class<T> c, long id)
    {
        EntityManager em = getEntityManager();
        TypedQuery<T> query = em.createQuery("SELECT o FROM " + c.getSimpleName() + " o WHERE o.id = :id", c);
        query.setParameter("id", id );
        try {
            return query.getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    public <T> T getByName(Class<T> c, String name)
    {
        EntityManager em = getEntityManager();
        TypedQuery<T> query = em.createQuery("SELECT o FROM " + c.getSimpleName() + " o WHERE o.name = :name", c);
        query.setParameter("name", name );
        try {
            return query.getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    public <T> T getByUsername(Class<T> c, String username)
    {
        EntityManager em = getEntityManager();
        TypedQuery<T> query = em.createQuery("SELECT o FROM " + c.getSimpleName() + " o WHERE o.username = :username", c);
        query.setParameter("username", username );
        try {
            return query.getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    public <T> T getByCode(Class<T> c, String code)
    {
        EntityManager em = getEntityManager();
        TypedQuery<T> query = em.createQuery("SELECT o FROM " + c.getSimpleName() + " o WHERE o.code = :code", c);
        query.setParameter("code", code );
        try {
            return query.getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    public <T> T getByToken(Class<T> c, String token)
    {
        EntityManager em = getEntityManager();
        TypedQuery<T> query = em.createQuery("SELECT o FROM " + c.getSimpleName() + " o WHERE o.token = :token", c);
        query.setParameter("token", token );
        try {
            return query.getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }



    public Object getByID(String className, long id)
    {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("SELECT o FROM " + className + " o WHERE o.id = :id");
        query.setParameter("id", id );
        try {
            return query.getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    public void flush()
    {
        EntityManager em = getEntityManager();
        em.flush();
    }

    public void save(Object entity)
    {
        EntityManager em = this.getEntityManager();
        this.startTransaction();
        em.persist(entity);
        this.endTransaction();
    }

    public void delete(Object entity)
    {
        EntityManager em = this.getEntityManager();
        this.startTransaction();
        em.remove(entity);
        this.endTransaction();
    }

    public int emptyDatabase()
    {
        EntityManager em = this.getEntityManager();
        this.startTransaction();
        int delCount = em.createQuery("DELETE FROM java.lang.Object").executeUpdate();
        this.endTransaction();
        return delCount;
    }

    public long countAll()
    {
        return countByClass(Object.class);
    }

    public long countByClass(Class<?> type)
    {
        EntityManager em = this.getEntityManager();

        if(type != Object.class) {
            Metamodel metamodel = em.getMetamodel();
            EntityType<?> eType = metamodel.entity(type); // introduces the type to ObjectDB in case it doesn't exist
        }

        String className = type.getSimpleName();
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(x) FROM " + className + " x", Long.class);
        return query.getSingleResult();
    }

    public <T> int deleteAllOfClass(Class<T> classObject)
    {
        EntityManager em = this.getEntityManager();
        String className = classObject.getSimpleName();
        this.startTransaction();
        int delCount = em.createQuery("DELETE FROM " + className).executeUpdate();
        this.endTransaction();
        return delCount;
    }

    public <T> List<T> getEntitiesByClass(Class<T> classObject)
    {
        EntityManager em = this.getEntityManager();
        String className = classObject.getSimpleName();
        TypedQuery<T> query = em.createQuery("SELECT x FROM " + className + " x", classObject);
        try {
            return query.getResultList();
        } catch(NoResultException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> getUsersByEmail(Class<T> c, String email)
    {
        EntityManager em = this.getEntityManager();
        Query query = em.createQuery("SELECT x FROM " + c.getSimpleName() + " x WHERE x.email = :email");
        query.setParameter("email", email );
        try {
            return query.getResultList();
        } catch(NoResultException e) {
            return new ArrayList<>();
        }
    }

    public List executeQuery(PersistenceQuery query)
    {
        EntityManager em = this.getEntityManager();
        Query q = em.createQuery(query.getQuery());
        for(Map.Entry<String, Object> entry : query.getParameters().entrySet())
        {
            q.setParameter(entry.getKey(), entry.getValue());
        }
        try {
            return q.getResultList();
        }catch(NoResultException e){
            return new ArrayList();
        }
    }

    public void close()
    {
        EntityManager em = this.getEntityManager();
        if(em != null){em.close(); threadLocalEntityManager.set(null);}
        if(emf != null){emf.close();}
    }

    public abstract void enhanceJARs(String[] paths);


}
