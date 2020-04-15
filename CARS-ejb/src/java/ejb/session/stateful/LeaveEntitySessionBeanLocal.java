package ejb.session.stateful;

import entity.LeaveEntity;
import java.sql.Date;
import java.util.List;
import util.exception.DoctorNotFoundException;
import util.exception.LeaveApplicationException;

public interface LeaveEntitySessionBeanLocal 
{
    public LeaveEntity createNewLeave(LeaveEntity leaveEntity);

    public List<LeaveEntity> retrieveLeaveByDoctorId(Long doctorId) throws DoctorNotFoundException;

    public LeaveEntity retrieveLeaveByDateNDoctorId(Long doctorId, Date date);

    public void checkIfDoctorAppliedInSameWeek(Long doctorId, Date date) throws DoctorNotFoundException, LeaveApplicationException;

    public void checkifDocIsOnLeave(Long doctorId, String date) throws LeaveApplicationException;

    public void deleteLeave(Long doctorId) throws DoctorNotFoundException;

}
