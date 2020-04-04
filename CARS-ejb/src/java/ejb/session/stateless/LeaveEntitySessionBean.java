package ejb.session.stateless;

import entity.LeaveEntity;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.DoctorNotFoundException;
import util.exception.LeaveApplicationException;

@Stateless
@Local(LeaveEntitySessionBeanLocal.class)
@Remote(LeaveEntitySessionBeanRemote.class)
public class LeaveEntitySessionBean implements LeaveEntitySessionBeanRemote, LeaveEntitySessionBeanLocal 
{

    @PersistenceContext(unitName = "CARS-ejbPU")
    private EntityManager em;

    public LeaveEntitySessionBean() 
    {
    }
    
    
    
    @Override
    public LeaveEntity createNewLeave(LeaveEntity leaveEntity) 
    {
        em.persist(leaveEntity);
        em.flush();
        
        return leaveEntity;
    }
    
  
    
    @Override
    public List<LeaveEntity> retrieveLeaveByDoctorId(Long doctorId) throws DoctorNotFoundException
    {
        Query query = em.createQuery("SELECT DISTINCT a FROM LeaveEntity a JOIN a.doctorEntity d WHERE d.doctorId = :id");
        query.setParameter("id", doctorId);
        
        return query.getResultList();
    }
    
    
    
    @Override
    public LeaveEntity retrieveLeaveByDateNDoctorId(Long doctorId, Date date)
    {
        Query query = em.createQuery("SELECT DISTINCT a FROM LeaveEntity a JOIN a.doctorEntity d WHERE d.doctorId = :id AND a.date = :date");
        query.setParameter("id", doctorId);
        query.setParameter("date", date);

        try 
        {
            return (LeaveEntity)query.getSingleResult();
        } 
        catch (NoResultException | NonUniqueResultException ex) 
        {
            return null;
        }
    }
    
    
    
    @Override
    public void checkIfDoctorAppliedInSameWeek(Long doctorId, Date date) throws DoctorNotFoundException, LeaveApplicationException
    {
                
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        
        if (day == 3) //tuesday
        {
            c.add(Calendar.DATE, -1);
        }
        else if (day == 4) //wed
        {
            c.add(Calendar.DATE, -2);
        }
        else if (day == 5) //thurs
        {
            c.add(Calendar.DATE, -3);
        }
        else if (day == 6) //fri
        {
            c.add(Calendar.DATE, -4);
        }
            
        for (int i = 0; i <= 5; i++) 
        {
            c.add(Calendar.DATE, i);  // number of days to add
            Date checkDate = new Date((c.getTime()).getTime());
            LeaveEntity leaveEntity = retrieveLeaveByDateNDoctorId(doctorId, checkDate);
            if (leaveEntity != null) {
                throw new LeaveApplicationException("Leave has already been applied during the same week!");
            }
        }       
    }
}
