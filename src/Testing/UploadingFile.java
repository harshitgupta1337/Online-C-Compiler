package Testing;

import java.io.File;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class UploadingFile 
{

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		
	

	Spark.get(new Route("/"){
	           	@Override
	           	public Object handle(final Request request, final Response response)
	           	{
	           		final Configuration configuration = new Configuration();
	        		configuration.setClassForTemplateLoading(Wait_For_VM_To_Boot_Online.class, "/");
	        		final StringWriter writer = new StringWriter();
	        		try 
	        		{
	        			configuration.setDirectoryForTemplateLoading(new File("/home/harshit/workspace/AppOnCloud/Resources"));
					
	        			
	        			Template template = null;
						template = configuration.getTemplate("FileUploadForm.ftl");
					
						Map<String, Object> helloMap = new HashMap<String, Object>();
						template.process(helloMap, writer);
	        		}
	        		 catch (Exception e) {
						// TODO Auto-generated catch block
	        			 response.redirect("/error");
	        			 e.printStackTrace();
					}
	           		return writer;
	           	}
	           });
	
	Spark.post(new Route("/fileSubmitted"){
       	@Override
       	public Object handle(final Request request, final Response response)
       	{
       		final Configuration configuration = new Configuration();
    		configuration.setClassForTemplateLoading(Wait_For_VM_To_Boot_Online.class, "/");
    		final StringWriter writer = new StringWriter();
    		try 
    		{
    			configuration.setDirectoryForTemplateLoading(new File("/home/harshit/workspace/AppOnCloud/Resources"));
			
    			
    			Template template = null;
				template = configuration.getTemplate("FileSubmitted.php");
			
				Map<String, Object> helloMap = new HashMap<String, Object>();
				template.process(helloMap, writer);
    		}
    		 catch (Exception e) {
				// TODO Auto-generated catch block
    			 response.redirect("/error");
    			 e.printStackTrace();
			}
    		return writer;
       		//String str = request.params("file");
       		//return "Hello !!! "+str;
       	}
       });
	}

}
