package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlTransient;

@Entity
public class DoctorEntity implements Serializable 
{

    private static long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId;
    @Column(length = 32, nullable = false)
    private String firstName; 
    @Column(length = 32, nullable = false)
    private String lastName;
    @Column(length = 6, nullable = false, unique = true)
    private String registration; 
    @Column(length = 32, nullable = false)
    private String qualifications;
    @OneToMany(mappedBy = "doctorEntity")
    private List<AppointmentEntity> doctorAppointments;
    
    public DoctorEntity() 
    {
    }

    public DoctorEntity(String firstName, String lastName, String registration, String qualifications) 
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.registration = registration;
        this.qualifications = qualifications;
        this.doctorAppointments = new ArrayList<>();
    }

    public DoctorEntity(Long doctorId, String firstName, String lastName, String registration, String qualifications) 
    {
        this.doctorId = doctorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.registration = registration;
        this.qualifications = qualifications;
        this.doctorAppointments = new ArrayList<>();
    }

    public Long getDoctorId() 
    {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) 
    {
        this.doctorId = doctorId;
    }

    @Override
    public int hashCode() 
    {
        int hash = 0;
        hash += (doctorId != null ? doctorId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) 
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DoctorEntity)) {
            return false;
        }
        DoctorEntity other = (DoctorEntity) object;
        if ((this.doctorId == null && other.doctorId != null) || (this.doctorId != null && !this.doctorId.equals(other.doctorId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() 
    {
        return "entity.DoctorEntity[ id=" + doctorId + " ]";
    }

    public static long getSerialVersionUID() 
    {
        return serialVersionUID;
    }

    public static void setSerialVersionUID(long aSerialVersionUID) 
    {
        serialVersionUID = aSerialVersionUID;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName) 
    {
        this.firstName = firstName;
    }

    public String getLastName() 
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getRegistration() 
    {
        return registration;
    }

    public void setRegistration(String registration) 
    {
        this.registration = registration;
    }

    public String getQualifications() 
    {
        return qualifications;
    }

    public void setQualifications(String qualifications) 
    {
        this.qualifications = qualifications;
    }

    @XmlTransient
    public List<AppointmentEntity> getdoctorAppointments() {
        return doctorAppointments;
    }

    public void setDoctorAppointments(List<AppointmentEntity> doctorAppointments) {
        this.doctorAppointments = doctorAppointments;
    }
} 
