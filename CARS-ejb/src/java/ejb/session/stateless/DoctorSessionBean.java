package ejb.session.stateless;

import ejb.session.stateful.AppointmentEntitySessionBeanLocal;
import ejb.session.stateful.LeaveEntitySessionBeanLocal;
import entity.AppointmentEntity;
import entity.DoctorEntity;
import entity.LeaveEntity;
import java.sql.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.AppointmentNotFoundException;
import util.exception.CreateDoctorException;
import util.exception.DeleteDoctorException;
import util.exception.DoctorNotFoundException;
import util.exception.DoctorRegistrationExistException;
import util.exception.InputDataValidationException;
import util.exception.LeaveApplicationException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateDoctorException;

@Stateless
@Local(DoctorSessionBeanLocal.class)
@Remote(DoctorSessionBeanRemote.class)
public class DoctorSessionBean implements DoctorSessionBeanRemote, DoctorSessionBeanLocal 
{
    @PersistenceContext(unitName = "CARS-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    @EJB
    private AppointmentEntitySessionBeanLocal appointmentEntitySessionBeanLocal; 
    @EJB
    private LeaveEntitySessionBeanLocal leaveEntitySessionBeanLocal;
    
    public DoctorSessionBean() 
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    
    
    @Override
    public Long createNewDoctor(DoctorEntity newDoctorEntity) throws DoctorRegistrationExistException, UnknownPersistenceException, InputDataValidationException
    {
        try
        {
            Set<ConstraintViolation<DoctorEntity>> constraintViolation = validator.validate(newDoctorEntity);
            
            if(constraintViolation.isEmpty())
            {    
                em.persist(newDoctorEntity);
                em.flush();
                
                return newDoctorEntity.getDoctorId();
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
                    throw new DoctorRegistrationExistException("Doctor is already registered!");
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
    public List<DoctorEntity> retrieveAllDoctors() 
    {
        Query query = em.createQuery("SELECT d FROM DoctorEntity d ORDER BY d.doctorId ASC");
        
        return query.getResultList();
    }
    
    
    
    @Override
    public void updateDoctor(DoctorEntity doctorEntity) throws UpdateDoctorException, DoctorNotFoundException, InputDataValidationException
    {        
        if(doctorEntity != null && doctorEntity.getDoctorId() != null)
        {
            Set<ConstraintViolation<DoctorEntity>>constraintViolations = validator.validate(doctorEntity);
        
            if(constraintViolations.isEmpty())
            {
                DoctorEntity d = retrieveDoctorByDoctorId(doctorEntity.getDoctorId());

                if (d.getDoctorId().equals(doctorEntity.getDoctorId())) {
                    d.setFirstName(doctorEntity.getFirstName());
                    d.setLastName(doctorEntity.getLastName());
                    d.setQualifications(doctorEntity.getQualifications());
                } else {
                    throw new UpdateDoctorException("ID of doctor record to be updated does not match the existing record");
                }
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        else
        {
            throw new DoctorNotFoundException("Doctor ID not provided for doctor to be updated");
        }
    }
    
    
    
    @Override
    public DoctorEntity retrieveDoctorByDoctorId(Long doctorId) throws DoctorNotFoundException
    {
        DoctorEntity doctorEntity = em.find(DoctorEntity.class, doctorId);
        
        if(doctorEntity != null) 
        {
            return doctorEntity;
        }
        else 
        {
            throw new DoctorNotFoundException("Doctor ID " + doctorId + " does not exist!");
        }
    } 
    
    
    
    @Override
    public void deleteDoctor(DoctorEntity doctorEntity) throws DeleteDoctorException, DoctorNotFoundException, AppointmentNotFoundException
    {
        Long doctorId = doctorEntity.getDoctorId();
        DoctorEntity doctorEntityToRemove = retrieveDoctorByDoctorId(doctorId);
        
        List<AppointmentEntity> appointmentEntity = appointmentEntitySessionBeanLocal.retrieveAppointmentByDoctorId(doctorId);
        List<LeaveEntity> leaveEntity = leaveEntitySessionBeanLocal.retrieveLeaveByDoctorId(doctorId);

        if(!leaveEntity.isEmpty())
        {
            leaveEntitySessionBeanLocal.deleteLeave(doctorId);
        }
        
        if(appointmentEntity.isEmpty())
        {
            em.remove(doctorEntityToRemove);
        }
        else
        {
            throw new DeleteDoctorException("Doctor ID " + doctorId + " is associated with existing appointment(s) and cannot be deleted!");
        }
    }
    
    @Override
    public void checkAppointmentSchedule(Long doctorId, Date date) throws LeaveApplicationException, DoctorNotFoundException
    {
        List<AppointmentEntity> appointmentEntitys = appointmentEntitySessionBeanLocal.retrieveAppointmentByDoctorId(doctorId);
        
        for(AppointmentEntity appointmentEntity : appointmentEntitys)
        {
            if (appointmentEntity.getDate().equals(date)) {
                throw new LeaveApplicationException("There is an appointment scheduled on the leave date!\n");
            }
        }          
    }
    
    @Override
    public void updateDoctorList(Long doctorId) throws DoctorNotFoundException
    {
        DoctorEntity doctorEntity = retrieveDoctorByDoctorId(doctorId);
        em.refresh(doctorEntity);
        
        List<AppointmentEntity> doctorAppointment = doctorEntity.getdoctorAppointments();
        doctorEntity.setDoctorAppointments(doctorAppointment);
    }
    
    
    
    @Override
     public void inputIsIncorrect(DoctorEntity doctorEntity) throws CreateDoctorException
     {
         Boolean incorrect = false;
        
        if ("".equals(doctorEntity.getQualifications()) || " ".equals(doctorEntity.getQualifications()))
        {
            incorrect = true;
        }
        else if ("".equals(doctorEntity.getFirstName()) || " ".equals(doctorEntity.getFirstName()))
        {
            incorrect = true;
        }
        else if ("".equals(doctorEntity.getLastName()) || " ".equals(doctorEntity.getLastName()))
        {
            incorrect = true;
        }
        else if ("".equals(doctorEntity.getRegistration()) || " ".equals(doctorEntity.getRegistration()))
        {
            incorrect = true;
        }
        
        if (incorrect == true)
        {
            throw new CreateDoctorException("Incorrect input field!");
        }
     }
     
     private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<DoctorEntity>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
