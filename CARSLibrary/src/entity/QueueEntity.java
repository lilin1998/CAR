package entity;

import java.io.Serializable;
import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Li Lin
 */
@Entity
public class QueueEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long queueId;
    @Column(nullable = false)
    private int queueNumber;
    private Date queueDate;

    public QueueEntity() {
    }

    public QueueEntity(Date queueDate) {
        this.queueDate = queueDate;
    }

    public QueueEntity(int queueNumber, Date queueDate) {
        this.queueNumber = queueNumber;
        this.queueDate = queueDate;
    }

    public QueueEntity(Long queueId, int queueNumber, Date queueDate) {
        this.queueId = queueId;
        this.queueNumber = queueNumber;
        this.queueDate = queueDate;
    }

    public Long getQueueId() {
        return queueId;
    }

    public void setQueueId(Long queueId) {
        this.queueId = queueId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (queueId != null ? queueId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof QueueEntity)) {
            return false;
        }
        QueueEntity other = (QueueEntity) object;
        if ((this.queueId == null && other.queueId != null) || (this.queueId != null && !this.queueId.equals(other.queueId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.QueueEntity[ id=" + queueId + " ]";
    }

    public int getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(int queueNumber) {
        this.queueNumber = queueNumber;
    }

    public Date getQueueDate() {
        return queueDate;
    }

    public void setQueueDate(Date queueDate) {
        this.queueDate = queueDate;
    }
}
