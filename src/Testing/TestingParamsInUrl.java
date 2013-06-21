package Testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class TestingParamsInUrl 
{
	public static void main(String[] args) throws IOException, TemplateException 
	{
		
        Spark.get(new Route("/") 
        {	
			@Override
			public Object handle(Request request, Response response) 
			{
				final Configuration configuration = new Configuration();
				configuration.setClassForTemplateLoading(TestingParamsInUrl.class, "/");

				try 
				{
					configuration.setDirectoryForTemplateLoading(new File("/home/ubuntu/Resources"));
				
					final StringWriter writer = new StringWriter();
					Template template = null;
					template = configuration.getTemplate("Running.ftl");
					
					Map<String, Object> helloMap = new HashMap<String, Object>();
					template.process(helloMap, writer);		
					return writer;
				} 
				catch (Exception e) 
				{	
					//return e.getMessage();//response.redirect("/error");
					return e.getMessage();
					//e.printStackTrace();
					//return null;
				}
				
			}
		});	
        
        Spark.post(new Route("/compile")
        {
        	@Override
			public Object handle(Request request, Response response) 
			{
        			String time = Long.toString(Calendar.getInstance().getTimeInMillis());
        			try 
        			{
						FileUtils.writeStringToFile(new File("program"+time+".c"), request.queryParams("textarea"));
					}
        			catch (IOException e) 
        			{
						e.printStackTrace();
						//return e.getMessage();//response.redirect("/error");
						return e.getMessage();
					}
        			response.redirect("/compilationResult/"+time);
        			return null;
           	}
        });
        
        Spark.get(new Route("/compilationResult/:time")
        {
        	@Override
			public Object handle(Request request, Response response) 
			{
        		try
        		{
        			String result = new String();
        			String command = "gcc program"+request.params(":time")+".c -o output-"+request.params(":time");
        			InputStream error = Runtime.getRuntime().exec(command).getErrorStream();
        			//Runtime.getRuntime().exec("mkdir TestDir");
        			        			
        			String line;
        			StringBuilder sb = new StringBuilder();

        			BufferedReader br = new BufferedReader(new InputStreamReader(error));
        			while ((line = br.readLine()) != null)
        			{
        				sb.append(line);
        				sb.append("\n");
        			}	
        			
        			result = sb.toString();

        			if(result.isEmpty())
        				response.redirect("/compiledCorrectly/"+request.params(":time"));
        			Configuration configuration = new Configuration();
            		configuration.setClassForTemplateLoading(TestingParamsInUrl.class, "/");
            		StringWriter writer = new StringWriter();
            		configuration.setDirectoryForTemplateLoading(new File("/home/ubuntu/Resources"));
    				
            		result = result.replaceAll("program[0-9]+.c", "program.c");
            		
            		Template template = null;
            		template = configuration.getTemplate("CompliationResult.ftl");
    				
            		Map<String, Object> helloMap = new HashMap<String, Object>();
            		helloMap.put("result", result);
            		template.process(helloMap, writer);
            		
            		return writer;
        		}
        		catch(Exception e)
        		{
        			e.printStackTrace();
        			return e.getMessage();//response.redirect("/error");
        			//return null;
        		}
        			
        		} 	
           	});
        Spark.get(new Route("/compiledCorrectly/:time")
        {
        	@Override
			public Object handle(Request request, Response response) 
			{
        		final Configuration configuration = new Configuration();
        		configuration.setClassForTemplateLoading(TestingParamsInUrl.class, "/");
        		final StringWriter writer = new StringWriter();
        		try 
        		{
        			configuration.setDirectoryForTemplateLoading(new File("/home/ubuntu/Resources"));
        			Template template = null;
					template = configuration.getTemplate("CompiledCorrectly.ftl");
				
					Map<String, Object> helloMap = new HashMap<String, Object>();
					helloMap.put("instanceID", request.params(":time"));
					
					template.process(helloMap, writer);
        		}
        		 catch (Exception e) {
        			 return e.getMessage();//response.redirect("/error");
					//e.printStackTrace();
				}
           		return writer;
           	}

        });
        
        Spark.get(new Route("/output/:time")
        {
        	@Override
			public Object handle(Request request, Response response) 
			{
        		final Configuration configuration = new Configuration();
        		configuration.setClassForTemplateLoading(TestingParamsInUrl.class, "/");
        		final StringWriter writer = new StringWriter();
        		String result = new String();
        		try 
        		{
            		Runtime.getRuntime().exec("rm program"+request.params(":time")+".c");

    				InputStream in = Runtime.getRuntime().exec("./output-"+request.params(":time")).getInputStream();
        			String line;
        			StringBuilder sb = new StringBuilder();

        			BufferedReader br = new BufferedReader(new InputStreamReader(in));
        			while ((line = br.readLine()) != null)
        			{
        				sb.append(line);
        				sb.append("\n");
        			}
        			
        			
        			result = sb.toString();
        			        			     			
        		    configuration.setDirectoryForTemplateLoading(new File("/home/ubuntu/Resources"));
        			Template template = null;
					template = configuration.getTemplate("Output.ftl");
				
					Map<String, Object> helloMap = new HashMap<String, Object>();
					helloMap.put("result", result);
					template.process(helloMap, writer);
        		}
        		 catch (Exception e) {
        			 return e.getMessage();//response.redirect("/error");
					//e.printStackTrace();
				}
           		return writer;
           	}
        });
        
        Spark.get(new Route("/error")
        {
        	@Override
			public Object handle(Request request, Response response) 
			{
        		return "Some internal error has occured. Please try again.";
			}
        });
        
	}
}