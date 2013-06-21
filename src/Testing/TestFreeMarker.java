package Testing;

import java.util.Arrays;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class TestFreeMarker 
{
	public static void main(String[] args) 
	{
		Spark.post(new Route("/:name")
		{
			@Override
			public Object handle(Request arg0, Response arg1) 
			{
				return "Name : "+arg0.params(":name");
			}
		});
	}

}
