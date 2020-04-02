package entity;

import java.io.Serializable;
import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class LeaveEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveId;
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private DoctorEntity doctorEntity; //to get doctorId
    @Column(nullable = false)
    private Date date;

    public LeaveEntity() {
    }

    public LeaveEntity(DoctorEntity doctorEntity, Date date) 
    {
        this.doctorEntity = doctorEntity;
        this.date = date;
    }

    public LeaveEntity(Long id, DoctorEntity doctorEntity, Date date) 
    {
        this.leaveId = id;
        this.doctorEntity = doctorEntity;
        this.date = date;
    }

    public Long getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(Long leaveId) {
        this.leaveId = leaveId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (leaveId != null ? leaveId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the leaveId fields are not set
        if (!(object instanceof LeaveEntity)) {
            return false;
        }
        LeaveEntity other = (LeaveEntity) object;
        if ((this.leaveId == null && other.leaveId != null) || (this.leaveId != null && !this.leaveId.equals(other.leaveId))) {
            return false;
        }
        return true;
    }

    public DoctorEntity getDoctorEntity() {
        return doctorEntity;
    }

    public void setDoctorEntity(DoctorEntity doctorEntity) {
        this.doctorEntity = doctorEntity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "entity.LeaveEntity[ id=" + leaveId + " ]";
    }
    
}
