package com.dai.trust.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.fasterxml.jackson.core.JsonParser;

/**
 * Deserializes JSON dates into yyyy-MM-dd format 
 */
public class JsonDateOnlyDeserializer extends StdDeserializer<Date> {
 
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
 
    public JsonDateOnlyDeserializer() {
        this(null);
    }
 
    public JsonDateOnlyDeserializer(Class<?> vc) {
        super(vc);
    }
 
    @Override
    public Date deserialize(JsonParser jsonparser, DeserializationContext context)
      throws IOException, JsonProcessingException {
        String date = jsonparser.getText();
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
