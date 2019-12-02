package net.jfabricationgames.json_rpc.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.jfabricationgames.json_rpc.UnsupportedParameterException;

public abstract class JsonRpcParserUtil {
	
	public static <T> T parseToType(Object obj, Class<T> clazz) throws UnsupportedParameterException {
		T parsed;
		
		try {
			//serialize parameters object to string
			ObjectMapper mapper = new ObjectMapper();
			String parametersJson = mapper.writeValueAsString(obj);
			
			//deserialize as clazz object
			parsed = mapper.readValue(parametersJson, clazz);
		}
		catch (Exception e) {
			throw new UnsupportedParameterException("Parsing a NoteSelector from the parameter object failed", e);
		}
		
		return parsed;
	}
}