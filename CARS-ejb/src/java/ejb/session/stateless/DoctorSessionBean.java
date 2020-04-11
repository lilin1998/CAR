package ejb.session.stateless;

import ejb.session.stateful.AppointmentEntitySessionBeanLocal;
import entity.AppointmentEntity;
import entity.DoctorEntity;
import java.sql.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AppointmentNotFoundException;
import util.exception.CreateDoctorException;
import util.exception.DeleteDoctorException;
import util.exception.DoctorNotFoundException;
import util.exception.LeaveApplicationException;
import util.exception.UpdateDoctorException;

@Stateless
@Local(DoctorSessionBeanLocal.class)
@Remote(DoctorSessionBeanRemote.class)
public class DoctorSessionBean implements DoctorSessionBeanRemote, DoctorSessionBeanLocal 
{
    @PersistenceContext(unitName = "CARS-ejbPU")
    private EntityManager em;
    
    @EJB
    private AppointmentEntitySessionBeanLocal appointmentEntitySessionBeanLocal; 
    
    public DoctorSessionBean() 
    {
    }
    
    
    
    @Override
    public Long createNewDoctor(DoctorEntity newDoctorEntity) 
    {
        em.persist(newDoctorEntity);
        em.flush();
        DoctorEntity d = null;
        try 
        {
            d = retrieveDoctorByDoctorId(newDoctorEntity.getDoctorId());
        } 
        catch (DoctorNotFoundException e) 
        {
            Logger.getLogger(DoctorSessionBean.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return d.getDoctorId();
    }
    
    
    
    @Override
    public List<DoctorEntity> retrieveAllDoctors() 
    {
        Query query = em.createQuery("SELECT d FROM DoctorEntity d ORDER BY d.doctorId ASC");
        
        return query.getResultList();
    }
    
    
    
    @Override
    public void updateDoctor(DoctorEntity doctorEntity) throws UpdateDoctorException, DoctorNotFoundException
    {
        if(doctorEntity.getDoctorId()!= null)
        {
            DoctorEntity doctorEntityToUpdate = retrieveDoctorByDoctorId(doctorEntity.getDoctorId());
            
            if(doctorEntityToUpdate.getDoctorId().equals(doctorEntity.getDoctorId()))
            {
                doctorEntityToUpdate.setFirstName(doctorEntity.getFirstName());
                doctorEntityToUpdate.setLastName(doctorEntity.getLastName());
                doctorEntityToUpdate.setQualifications(doctorEntity.getQualifications());
                doctorEntityToUpdate.setRegistration(doctorEntity.getRegistration());
            }
            else
            {
                throw new UpdateDoctorException("ID of doctor record to be updated does not match the existing record");
            }
        }
        else
        {
            throw new DoctorNotFoundException("Doctor ID not provided for product to be updated");
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
}
