package ejb.session.stateless;

import entity.QueueEntity;
import java.sql.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.QueueNotFoundException;

@Stateless
@Local(QueueSessionBeanLocal.class)
@Remote(QueueSessionBeanRemote.class)
public class QueueSessionBean implements QueueSessionBeanRemote, QueueSessionBeanLocal 
{
    @PersistenceContext(unitName = "CARS-ejbPU")
    private EntityManager em;

    public QueueSessionBean() 
    {
    }
    
    
    
    @Override
    public Long createQueue(QueueEntity newQueueEntity)
    {
        em.persist(newQueueEntity);
        em.flush();
        QueueEntity q = null;
        
        try 
        {
            q = retrieveQueueByQueueId(newQueueEntity.getQueueId());
        }
        catch(QueueNotFoundException ex)
        {
            Logger.getLogger(PatientSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return q.getQueueId();
    }
    
    
    
    @Override
    public QueueEntity retrieveQueueByQueueId(Long queueId) throws QueueNotFoundException
    {
        QueueEntity queueEntity = em.find(QueueEntity.class, queueId);
        
        if (queueEntity != null)
        {
            return queueEntity;
        }
        else 
        {
            throw new QueueNotFoundException("Queue ID " + queueId + " does not exist!");
        }
    }
    
    
    
    @Override
    public List<QueueEntity> retrieveAllQueues()
    {
        Query query = em.createQuery("SELECT q FROM QueueEntity q");
        
        return query.getResultList();
    }
    
    
    
    @Override
    public List<QueueEntity> retrieveQueueByDate(Date todayDate) throws QueueNotFoundException
    {
        Query query = em.createQuery("SELECT q from QueueEntity q WHERE q.queueDate = :intodayDate ORDER BY q.queueId DESC");
        query.setParameter("intodayDate", todayDate);
        
        return query.getResultList();
    }
    
    
    
    @Override
    public void deleteQueue(Long queueId) throws QueueNotFoundException
    {
        QueueEntity queueEntityToRemove = retrieveQueueByQueueId(queueId);
        em.remove(queueEntityToRemove);
    }
    
    
    
    @Override
    public int updateQueueNumber(Date todayDate) throws QueueNotFoundException
    {
        List<QueueEntity> queueList = retrieveQueueByDate(todayDate);
        QueueEntity latestQueue = queueList.get(0);
        
        return latestQueue.getQueueNumber() + 1;
    }
    
    
    
    @Override
    public int resetQueueNumber()
    {
        return 1;
    }
}
