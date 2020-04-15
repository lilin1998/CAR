package ejb.session.stateless;

import entity.QueueEntity;
import java.sql.Date;
import java.util.List;
import util.exception.QueueNotFoundException;

public interface QueueSessionBeanRemote 
{
    public Long createQueue(QueueEntity newQueueEntity);

    public QueueEntity retrieveQueueByQueueId(Long queueId) throws QueueNotFoundException;
    
    public List<QueueEntity> retrieveAllQueues();
    
    public List<QueueEntity> retrieveQueueByDate(Date todayDate) throws QueueNotFoundException;
    
    public void deleteQueue(Long queueId) throws QueueNotFoundException;
    
    public int updateQueueNumber(Date todayDate) throws QueueNotFoundException;
    
    public int resetQueueNumber();
}
