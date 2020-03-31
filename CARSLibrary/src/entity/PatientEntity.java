package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PatientEntity implements Serializable 
{

    private static long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;
    @Column(length = 9, nullable = false, unique = true)
    private String identityNumber;
    @Column(length = 32, nullable = false)
    private String password;
    @Column(length = 32, nullable = false)
    private String firstName;
    @Column(length = 32, nullable = false)
    private String lastName;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
    @Column(nullable = false)
    private Integer age;
    @Column(length = 8, nullable = false)
    private String phone;
    @Column(nullable = false)
    private String address;

    public PatientEntity() 
    {
    }

    public PatientEntity(String identityNumber, String password, String firstName, String lastName, GenderEnum gender, Integer age, String phone, String address) 
    {
        this();
        this.identityNumber = identityNumber;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
        this.address = address;
    }
    
    public PatientEntity(Long patientId, String identityNumber, String password, String firstName, String lastName, GenderEnum gender, Integer age, String phone, String address) 
    {
        this();
        this.patientId = patientId;
        this.identityNumber = identityNumber;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
        this.address = address;
    }

    public Long getPatientId() 
    {
        return patientId;
    }

    public void setPatientId(Long patientId) 
    {
        this.patientId = patientId;
    }

    @Override
    public int hashCode() 
    {
        int hash = 0;
        hash += (patientId != null ? patientId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) 
    {
        if (!(object instanceof PatientEntity)) {
            return false;
        }
        PatientEntity other = (PatientEntity) object;
        if ((this.patientId == null && other.patientId != null) || (this.patientId != null && !this.patientId.equals(other.patientId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() 
    {
        return "entity.PatientEntity[ id=" + patientId + " ]";
    }

    public static long getSerialVersionUID() 
    {
        return serialVersionUID;
    }

    public static void setSerialVersionUID(long aSerialVersionUID) 
    {
        serialVersionUID = aSerialVersionUID;
    }

    public String getIdentityNumber() 
    {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber)
    {
        this.identityNumber = identityNumber;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password) 
    {
        this.password = password;
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

    public GenderEnum getGender() 
    {
        return gender;
    }

    public void setGender(GenderEnum gender)
    {
        this.gender = gender;
    }

    public Integer getAge() 
    {
        return age;
    }

    public void setAge(Integer age) 
    {
        this.age = age;
    }

    public String getPhone() 
    {
        return phone;
    }

    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address) 
    {
        this.address = address;
    }
}
