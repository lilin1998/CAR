package ejb.session.stateless;

import entity.StaffEntity;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CreateStaffException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.StaffNotFoundException;
import util.exception.StaffUsernameExistException;
import util.exception.UnknownPersistenceException;
import util.security.CryptographicHelper;

@Stateless
@Local(StaffEntitySessionBeanLocal.class)
@Remote(StaffEntitySessionBeanRemote.class)
public class StaffEntitySessionBean implements StaffEntitySessionBeanRemote, StaffEntitySessionBeanLocal 
{
    @PersistenceContext(unitName = "CARS-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public StaffEntitySessionBean() 
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    
    
    @Override
    public Long createStaffEntity(StaffEntity newStaffEntity) throws StaffUsernameExistException, UnknownPersistenceException, InputDataValidationException
    {
        try
        {
            Set<ConstraintViolation<StaffEntity>> constraintViolation = validator.validate(newStaffEntity);
            
            if(constraintViolation.isEmpty())
            {    
                em.persist(newStaffEntity);
                em.flush();
                
                return newStaffEntity.getStaffId();
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolation));
            }
        }
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new StaffUsernameExistException("Staff is already registered!");
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
            else
            {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
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
            
            //to be deleted
            if (password.equals(staffEntity.getPassword()))
            {
                return staffEntity;
            }
            
            
            String securePassword = getSecurePassword(password);
        
            if (securePassword.equals(staffEntity.getPassword()))
            {
                return staffEntity;
            }
            else
            {
                throw new InvalidLoginCredentialException("Password is invalid!");
            }
        }
        catch(StaffNotFoundException ex)
        {
            throw new InvalidLoginCredentialException("Username does not exist!");
        }
    }
    
    
    
    @Override
    public void updateStaffEntity(StaffEntity staffEntity) throws StaffNotFoundException, InputDataValidationException
    {        
        if(staffEntity != null && staffEntity.getStaffId() != null)
        {
            Set<ConstraintViolation<StaffEntity>>constraintViolations = validator.validate(staffEntity);
        
            if(constraintViolations.isEmpty())
            {
                StaffEntity s = retrieveStaffEntityByStaffId(staffEntity.getStaffId());
                s.setFirstName(staffEntity.getFirstName());
                s.setLastName(staffEntity.getLastName());
                s.setPassword(staffEntity.getPassword());
                s.setUsername(staffEntity.getUsername());
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        else
        {
            throw new StaffNotFoundException("Staff ID not provided for staff to be updated");
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
    
    
    
    @Override
    public String getSecurePassword(String passwordToHash) 
    {
       String hashedPassword = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doSHA1Hashing(passwordToHash));
        
       return hashedPassword;
    }
    
    
    
    @Override
    public void inputIsIncorrect(StaffEntity staffEntity) throws CreateStaffException
    {
        Boolean incorrect = false;
        
        if ("".equals(staffEntity.getFirstName()) || " ".equals(staffEntity.getFirstName()))
        {
            incorrect = true;
        }
        else if ("".equals(staffEntity.getLastName()) || " ".equals(staffEntity.getLastName()))
        {
            incorrect = true;
        }
        else if ("".equals(staffEntity.getUsername()) || " ".equals(staffEntity.getUsername()))
        {
            incorrect = true;
        }
        else if ("".equals(staffEntity.getPassword()) || " ".equals(staffEntity.getPassword()))
        {
            incorrect = true;
        }
        
        if (incorrect == true)
        {
            throw new CreateStaffException("Incorrect input field!");
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<StaffEntity>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
