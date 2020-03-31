package entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class AppointmentEntity implements Serializable 
{

    private static long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;
    @ManyToOne(optional = false)
    private DoctorEntity doctorEntity; //to get doctorId
    @Column(nullable = false)
    private Date date;
    @Column(nullable = false)
    private Time time;
    @ManyToOne(optional = false)
    private PatientEntity patientEntity; //to get identityNumber

    public AppointmentEntity() 
    {
    }

    public AppointmentEntity(DoctorEntity doctorEntity, Date date, Time time, PatientEntity patientEntity) 
    {
        this.doctorEntity = doctorEntity;
        this.date = date;
        this.time = time;
        this.patientEntity = patientEntity;
    }

    public AppointmentEntity(Long appointmentId, DoctorEntity doctorEntity, Date date, Time time, PatientEntity patientEntity) 
    {
        this.appointmentId = appointmentId;
        this.doctorEntity = doctorEntity;
        this.date = date;
        this.time = time;
        this.patientEntity = patientEntity;
    }

    public Long getAppointmentId() 
    {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId)
    {
        this.appointmentId = appointmentId;
    }

    @Override
    public int hashCode() 
    {
        int hash = 0;
        hash += (appointmentId != null ? appointmentId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) 
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AppointmentEntity)) {
            return false;
        }
        AppointmentEntity other = (AppointmentEntity) object;
        if ((this.appointmentId == null && other.appointmentId != null) || (this.appointmentId != null && !this.appointmentId.equals(other.appointmentId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() 
    {
        return "entity.AppointmentEntity[ id=" + appointmentId + " ]";
    }

    public static long getSerialVersionUID() 
    {
        return serialVersionUID;
    }

    public static void setSerialVersionUID(long aSerialVersionUID) 
    {
        serialVersionUID = aSerialVersionUID;
    }

    public DoctorEntity getDoctorEntity() 
    {
        return doctorEntity;
    }

    public void setDoctorEntity(DoctorEntity doctorEntity) 
    {
        this.doctorEntity = doctorEntity;
    }

    public Date getDate() 
    {
        return date;
    }

    public void setDate(Date date) 
    {
        this.date = date;
    }

    public Time getTime() 
    {
        return time;
    }

    public void setTime(Time time) 
    {
        this.time = time;
    }

    public PatientEntity getPatientEntity() 
    {
        return patientEntity;
    }

    public void setPatientEntity(PatientEntity patientEntity) 
    {
        this.patientEntity = patientEntity;
    }
    
}
