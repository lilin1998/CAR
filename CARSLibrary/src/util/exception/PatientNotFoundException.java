package util.exception;

public class PatientNotFoundException extends Exception
{
    public PatientNotFoundException() 
    {
    }

    
    
    public PatientNotFoundException(String msg) 
    {
        super(msg);
    }
}
