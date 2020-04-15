package xmlhelper;

import java.sql.Time;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class TimeDataAdapter extends XmlAdapter<String, Time> 
{
    @Override
    public Time unmarshal(String v) throws Exception {
        return Time.valueOf(v);
    }

    
    
    @Override
    public String marshal(Time v) throws Exception {
        return v.toString();
    }
}
