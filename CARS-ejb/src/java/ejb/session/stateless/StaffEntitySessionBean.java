package ejb.session.stateless;

import entity.StaffEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InvalidLoginCredentialException;
import util.exception.StaffNotFoundException;

@Stateless
@Local(StaffEntitySessionBeanLocal.class)
@Remote(StaffEntitySessionBeanRemote.class)
public class StaffEntitySessionBean implements StaffEntitySessionBeanRemote, StaffEntitySessionBeanLocal 
{
    @PersistenceContext(unitName = "CARS-ejbPU")
    private EntityManager em;

    
    
    public StaffEntitySessionBean() 
    {
    }
    
    
    
    @Override
    public Long createStaffEntity(StaffEntity newStaffEntity) 
    {
        em.persist(newStaffEntity);
        em.flush();
        StaffEntity s = null;
        try 
        {
            s = retrieveStaffEntityByStaffId(newStaffEntity.getStaffId());
        } 
        catch (StaffNotFoundException e) 
        {
            Logger.getLogger(StaffEntitySessionBean.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return s.getStaffId();
    }
    
    
    
    @Override
    public List<StaffEntity> retrieveAllStaffs()
    {
        Query query = em.createQuery("SELECT s FROM StaffEntity s");
        
        return query.getResultList();
    }
    
    
    
    @Override
    public StaffEntity retrieveStaffEntityByStaffId(Long staffId) throws StaffNotFoundException
    {
        StaffEntity staffEntity = em.find(StaffEntity.class, staffId);
        
        if(staffEntity != null) 
        {
            return staffEntity;
        }
        else 
        {
            throw new StaffNotFoundException("Staff ID " + staffId + " does not exist!");
        }
    } 
    
    
    
    
    @Override
    public StaffEntity retrieveStaffByUsername(String username) throws StaffNotFoundException
    {
        Query query = em.createQuery("SELECT s FROM StaffEntity s WHERE s.username = :inUsername");
        query.setParameter("inUsername", username);
        
        try
        {
            return (StaffEntity)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new StaffNotFoundException("Staff Username " + username + " does not exist!");
        }
    }
    
    
    
    @Override
    public StaffEntity staffLogin(String username, String password) throws InvalidLoginCredentialException
    {
        try
        {
            StaffEntity staffEntity = retrieveStaffByUsername(username);
            
            if(staffEntity.getPassword().equals(password))
            {
//                staffEntity.getSaleTransactionEntities().size();                
                return staffEntity;
            }
            else
            {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        }
        catch(StaffNotFoundException ex)
        {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }
    
    
    
    @Override
    public void updateStaffEntity(StaffEntity staffEntity)
    {
        em.merge(staffEntity);
        try 
        {
            StaffEntity s = retrieveStaffEntityByStaffId(staffEntity.getStaffId());
            s.setFirstName(staffEntity.getFirstName());
            s.setLastName(staffEntity.getLastName());
            s.setPassword(staffEntity.getPassword());
            s.setUsername(staffEntity.getUsername());
        } catch (Exception e) {
            Logger.getLogger(StaffEntitySessionBean.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    
    
    @Override
    public void deleteStaffEntity(Long staffId)
    {
        StaffEntity staffEntity = new StaffEntity();
        try {
            staffEntity = retrieveStaffEntityByStaffId(staffId);
        } catch (StaffNotFoundException ex) {
            Logger.getLogger(StaffEntitySessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        em.remove(staffEntity);
    }
}
