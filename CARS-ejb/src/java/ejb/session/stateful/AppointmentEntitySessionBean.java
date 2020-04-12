package ejb.session.stateful;

import entity.AppointmentEntity;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AppointmentNotFoundException;
import util.exception.CreateAppointmentException;
import util.exception.DoctorNotFoundException;
import util.exception.PatientNotFoundException;

@Stateful
@Local(AppointmentEntitySessionBeanLocal.class)
@Remote(AppointmentEntitySessionBeanRemote.class)
public class AppointmentEntitySessionBean implements AppointmentEntitySessionBeanRemote, AppointmentEntitySessionBeanLocal 
{

    @PersistenceContext(unitName = "CARS-ejbPU")
    private EntityManager em;

    public AppointmentEntitySessionBean() {
    }
    
    @Override
    public AppointmentEntity createNewAppointment(AppointmentEntity newAppointmentEntity) 
    {
        em.persist(newAppointmentEntity);
        em.flush();
        
        return newAppointmentEntity;
    }
    
    
    
    @Override
    public List<AppointmentEntity> retrieveAllAppointments()
    {
        Query query = em.createQuery("SELECT a FROM AppointmentEntity a");
        
        return query.getResultList();
    }
    
    
    
    @Override
    public AppointmentEntity retrieveAppointmentByAppointmentId(Long appointmentId) throws AppointmentNotFoundException
    {
        AppointmentEntity appointmentEntity = em.find(AppointmentEntity.class, appointmentId);
        
        if(appointmentEntity != null)
        {            
            return appointmentEntity;
        }
        else
        {
            throw new AppointmentNotFoundException("Appointment ID " + appointmentId + " does not exist!");
        }                
    }
    
    
    
    @Override
    public List<AppointmentEntity> retrieveAppointmentByPatientIdentityNo(String identityNo) throws PatientNotFoundException
    {
        Query query = em.createQuery("SELECT DISTINCT a FROM AppointmentEntity a JOIN a.patientEntity p WHERE p.identityNumber = ?1");
        query.setParameter(1, identityNo);
        
        return query.getResultList();
    }
    
    
    
    @Override
    public List<AppointmentEntity> retrieveAppointmentByPatientIdentityNoAndDate(String identityNo, Date date)
    {
        Query query = em.createQuery("SELECT DISTINCT a FROM AppointmentEntity a JOIN a.patientEntity p WHERE p.identityNumber = :id AND a.date = :date");
        query.setParameter("id", identityNo);
        query.setParameter("date", date);

        return query.getResultList();
    }
    
    
    @Override
    public List<AppointmentEntity> retrieveAppointmentByDoctorId(Long doctorId) throws DoctorNotFoundException
    {
        Query query = em.createQuery("SELECT DISTINCT a FROM AppointmentEntity a JOIN a.doctorEntity d WHERE d.doctorId = :id");
        query.setParameter("id", doctorId);
        
        return query.getResultList();
    }
    
    
    
    @Override
    public List<AppointmentEntity> retrieveAppointmentByDoctorIdAndDate(Long doctorId, Date date) throws DoctorNotFoundException
    {
        Query query = em.createQuery("SELECT DISTINCT a FROM AppointmentEntity a JOIN a.doctorEntity d WHERE d.doctorId = :id AND a.date = :date ORDER BY a.time ASC");
        query.setParameter("id", doctorId);
        query.setParameter("date", date);
        
        return query.getResultList();
    }
    
    
    @Override
    public AppointmentEntity retrieveAppointmentByDoctorIdAndDateAndTime(Long doctorId, Date date, Time time) throws DoctorNotFoundException
    {
        Query query = em.createQuery("SELECT DISTINCT a FROM AppointmentEntity a JOIN a.doctorEntity d WHERE d.doctorId = :id AND a.date = :date AND a.time = :time");
        query.setParameter("id", doctorId);
        query.setParameter("date", date);
        query.setParameter("time", time);
        
        try 
        {
            return (AppointmentEntity)query.getSingleResult();
        } 
        catch (NoResultException | NonUniqueResultException ex) 
        {
            return null;
        }
    }
    
    
    
    @Override
    public void updateAppointment(AppointmentEntity appointmentEntity)
    {
        em.merge(appointmentEntity);
    }    
    
    
    
    @Override
    public void deleteAppointment(Long appointmentId)
    {
        AppointmentEntity appointmentToRemove;
        
        try {
            appointmentToRemove = retrieveAppointmentByAppointmentId(appointmentId);
            em.remove(appointmentToRemove);
        } catch (AppointmentNotFoundException ex) {
            Logger.getLogger(AppointmentEntitySessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void checkApptNotOnWeekendAnd2DaysLater(String date) throws CreateAppointmentException 
    {
        Date actualDate = Date.valueOf(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(actualDate);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        
        //set two days validation rule
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date twoDaysLater = new Date((c.getTime()).getTime());
        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY)
        {
            throw new CreateAppointmentException("Appointment is not available on weekends!\n");
        } 
        else if (actualDate.compareTo(twoDaysLater) < 0) 
        {
            throw new CreateAppointmentException("Appointment must be booked at least two days in advance!\n");
        }
    }
}
