package rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


//klasa koja daje precizniji path - mogao sam i u web.xml
//<servlet-mapping>
//<servlet-name>javax.ws.rs.core.Application</servlet-name>
//<url-pattern>/*</url-pattern>
//</servlet-mapping>


@ApplicationPath("/rest")
public class App extends Application 
{

}
