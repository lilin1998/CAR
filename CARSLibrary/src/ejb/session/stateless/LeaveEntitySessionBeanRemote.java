package ejb.session.stateless;

import entity.LeaveEntity;
import java.sql.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DoctorNotFoundException;
import util.exception.LeaveApplicationException;

public interface LeaveEntitySessionBeanRemote {
    
    public LeaveEntity createNewLeave(LeaveEntity leaveEntity); 
    
    public List<LeaveEntity> retrieveLeaveByDoctorId(Long doctorId) throws DoctorNotFoundException;
    
    public List<LeaveEntity> retrieveLeaveByDateNDoctorId(Long doctorId, Date date);

    public void checkIfDoctorAppliedInSameWeek(Long doctorId, Date date) throws DoctorNotFoundException, LeaveApplicationException;
}
