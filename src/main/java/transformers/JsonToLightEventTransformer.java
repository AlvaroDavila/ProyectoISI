package transformers;

import org.mule.api.MuleMessage;
import org.mule.api.annotations.ContainsTransformerMethods;
import org.mule.api.transformer.TransformerException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.mule.transformer.AbstractMessageTransformer;

/**
 * @author Juan Boubeta-Puig <juan.boubeta@uca.es>
 *
 */
@ContainsTransformerMethods
public class JsonToLightEventTransformer extends AbstractMessageTransformer {
	static DecimalFormat df2 = new DecimalFormat("#,00");

	@Override
	public synchronized Map<String, Object> transformMessage(MuleMessage message, String outputEncoding)
			throws TransformerException {

		// LinkedHashMap will iterate in the order in which the entries were put
		// into the map
		Map<String, Object> eventMap = new LinkedHashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = null;

		try {

			jsonNode = mapper.readTree(message.getPayloadAsString());

			// Event payload is a nested map
			Map<String, Object> eventPayload = new LinkedHashMap<String, Object>();
			eventPayload.put("streetId", jsonNode.get("channel").get("id").asInt());
			eventPayload.put("timestamp", jsonNode.get("feeds").path(0).get("created_at").getTextValue());
			eventPayload.put("lightIntensity", jsonNode.get("feeds").path(0).get("field1").asDouble());
			eventPayload.put("pedestrian", jsonNode.get("feeds").path(0).get("field2").asDouble());
			eventPayload.put("vehicle", jsonNode.get("feeds").path(0).get("field3").asDouble());
			eventPayload.put("speed", jsonNode.get("feeds").path(0).get("field4").asDouble());
			eventPayload.put("latitude", jsonNode.get("channel").get("latitude").asDouble());
			eventPayload.put("longitude", jsonNode.get("channel").get("longitude").asDouble());

			// Get the specific attributes for each Thingspeak feed.
			if (jsonNode.get("channel").get("name").getTextValue().contains("Calle Sagasta")) {
				eventPayload.put("name", "Calle Sagasta");
			}
			} else if (jsonNode.get("channel").get("name").getTextValue().contains("Avenida Universidad de Cádiz")) {
				eventPayload.put("name", "Avenida Universidad de Cádiz");
			

	
			eventMap.put("LightEvent", eventPayload);

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("===LightEvent created: " + eventMap);
		return eventMap;
	}
}

