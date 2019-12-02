package net.jfabricationgames.notebook.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.jfabricationgames.json_rpc.JsonRpcRequest;
import net.jfabricationgames.json_rpc.JsonRpcResponse;
import net.jfabricationgames.json_rpc.UnsupportedParameterException;
import net.jfabricationgames.json_rpc.util.JsonRpcErrorUtil;
import net.jfabricationgames.notebook.db.DatabaseConnection;

@Path("/notebook")
public class NoteBookService {
	
	public static final String JSON_RPC = "2.0";
	
	private static final Logger LOGGER = LogManager.getLogger(NoteBookService.class);
	
	/**
	 * A simple hello world to test whether the service is reachable
	 */
	@GET
	@Path("/hello")
	@Produces(MediaType.APPLICATION_JSON)
	public Response processHelloRequestGet() {
		LOGGER.info("Received 'hello' request (HTTP GET)");
		String answer = "Hello there!";
		JsonRpcResponse rpcResponse = new JsonRpcResponse();
		rpcResponse.setId("42");
		rpcResponse.setJsonRpc(JSON_RPC);
		rpcResponse.setResult(answer);
		return Response.status(Status.OK).entity(rpcResponse).build();
	}
	
	/**
	 * Test whether the database is working (by creating a DatabaseConnection object that creates the database and tables)
	 */
	@GET
	@Path("/test_db")
	@Produces(MediaType.APPLICATION_JSON)
	public Response testDatabase() {
		LOGGER.info("Received 'testDatabase' request (HTTP GET)");
		
		String answer;
		try {
			DatabaseConnection.getInstance();
			answer = "Database up and running";
		}
		catch (Exception e) {
			answer = "Database error: " + e.getClass().getSimpleName() + ": " + e.getLocalizedMessage() + "\n"
					+ JsonRpcErrorUtil.getStackTraceAsString(e);
		}
		
		JsonRpcResponse rpcResponse = new JsonRpcResponse();
		rpcResponse.setId("42");
		rpcResponse.setJsonRpc(JSON_RPC);
		rpcResponse.setResult(answer);
		return Response.status(Status.OK).entity(rpcResponse).build();
	}
	
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response callJsonRpc(JsonRpcRequest request) {
		LOGGER.info("Received RPC method call (parameter: " + request + ")");
		return processRequest(request);
	}
	
	/**
	 * Process any request by delegating it to the method that is to be called.
	 * 
	 * @return Returns a {@link Response} to the processed request.
	 */
	private Response processRequest(JsonRpcRequest request) {
		//execute the requested method via reflection
		try {
			LOGGER.info("Invoking RPC method via reflection (method: " + request.getMethod() + "; parameters: " + request.getParams() + ")");
			NoteBookServiceProvider provider = new NoteBookServiceProvider();
			Class<?> clazz = provider.getClass();
			Method method = clazz.getMethod(request.getMethod(), Object.class);
			Object obj = method.invoke(provider, request.getParams());
			
			LOGGER.info("Building rpc response (id: " + request.getId() + "; jsonRpc: " + JSON_RPC + "; result: " + obj + ")");
			JsonRpcResponse rpcResponse = new JsonRpcResponse();
			rpcResponse.setId(request.getId());
			rpcResponse.setJsonRpc(JSON_RPC);
			rpcResponse.setResult(obj);
			
			//build the response and send it back to the client
			Response response = Response.status(Status.OK).entity(rpcResponse).build();
			return response;
		}
		catch (NoSuchMethodException | SecurityException e) {
			LOGGER.error("Error: ", e);
			return JsonRpcErrorUtil.createMethodNotFoundErrorResponse(request.getId(), request.getMethod());
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("Error: ", e);
			if (e.getCause() instanceof UnsupportedParameterException) {
				return JsonRpcErrorUtil.createIllegalParameterErrorResponse(request.getId(), request.getParams());
			}
			else {
				return JsonRpcErrorUtil.createMethodCouldNotBeInvocedErrorResponse(request.getId(), request.getMethod());
			}
		}
	}
}