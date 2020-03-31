package util.exception;

public class DoctorNotFoundException extends Exception
{

    public DoctorNotFoundException() 
    {
    }
    
    public DoctorNotFoundException(String msg)
    {
        super(msg);
    }
}
