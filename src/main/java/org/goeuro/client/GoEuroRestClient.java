package org.goeuro.client;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class GoEuroRestClient {

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws ClientProtocolException, IOException, UniformInterfaceException, JSONException {

		if(args.length == 0){
			System.out.println("Oops!! Did you miss something??");
			System.out.println("Proper usage: java -jar GoEuroRestClient <cityName>");
			System.exit(0);
		}
		
		String fileName = System.getProperty("java.io.tmpdir")+"\\GoEuro.csv";
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(UriBuilder.fromUri("http://api.goeuro.com/api/v2/position/suggest/en/" + args[0]).build());
		String serviceresponse = service.accept(MediaType.APPLICATION_JSON).get(String.class);
		if(serviceresponse.equals("[]")){
			System.out.println("Houston!! We have a problem!! No data returned!!");
			System.exit(0);
		}
		JSONArray jsonArray = new JSONArray(service.accept(MediaType.APPLICATION_JSON).get(String.class));
		FileWriter writer = new FileWriter(fileName);

		for (int j = 0; j < jsonArray.length(); j++) {

			JSONObject jsonObject = jsonArray.getJSONObject(j);
			Iterator iterator = jsonObject.keys();
			while (iterator.hasNext()) {
				//for (Iterator iterator = jsonObject.keys(); iterator.hasNext();) {
				String key = (String) iterator.next();
				switch (key) {
					case "_id":
						writer.append(jsonObject.get(key).toString());
						writer.append(',');
						break;
					case "name":
						writer.append(jsonObject.get(key).toString());
						writer.append(',');
						break;
					case "type":
						writer.append(jsonObject.get(key).toString());
						writer.append(',');
						break;
					case "geo_position":
						JSONObject jsonPosObject = (JSONObject) jsonObject.get(key);
						for (Iterator posIterator = jsonPosObject.keys(); posIterator.hasNext();) {
							String posKey = (String) posIterator.next();
							writer.append(jsonPosObject.get(posKey).toString());
							writer.append(',');
						}
						break;
					default:
						break;
				}
			}
			writer.append('\n');
		}
		writer.flush();
		writer.close();
		System.out.println("Yippee!! File saved successfully in " + fileName );
	}
}
