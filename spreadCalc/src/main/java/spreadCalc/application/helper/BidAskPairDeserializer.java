package spreadCalc.application.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import spreadCalc.domain.model.BidAskPair;

public class BidAskPairDeserializer extends JsonDeserializer<List<BidAskPair>> {

	@Override
	public List<BidAskPair> deserialize(JsonParser jsonParser, DeserializationContext context)
			throws IOException, JacksonException {
		List<BidAskPair> result = new ArrayList<>();

		if (jsonParser.currentToken() == null) {
			jsonParser.nextToken();
		}

		if (jsonParser.currentToken() != JsonToken.START_ARRAY) {
			context.reportWrongTokenException(this, JsonToken.START_ARRAY, "Expected start of array for BidAskPair list", jsonParser.currentToken());
		}
		
		while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
			if (jsonParser.currentToken() != JsonToken.START_ARRAY) {
				context.reportWrongTokenException(this, JsonToken.START_ARRAY, "Expected start of inner array for BidAskPair", jsonParser.currentToken());
			}

			jsonParser.nextToken();
			double price = Double.parseDouble(jsonParser.getValueAsString());

			jsonParser.nextToken();
			double amount = Double.parseDouble(jsonParser.getValueAsString());

			jsonParser.nextToken(); // END_ARRAY of inner array
			result.add(new BidAskPair(price, amount));
		}

		return result;
	}

}
