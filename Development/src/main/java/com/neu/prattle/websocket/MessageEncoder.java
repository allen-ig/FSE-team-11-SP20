package com.neu.prattle.websocket;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.neu.prattle.model.Message;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Class MessageEncoder.
 * 
 * @author https://github.com/eugenp/tutorials/java-websocket/src/main/java/com/baeldung/websocket
 * @version dated 2017-03-05
 */
public class MessageEncoder implements Encoder.Text<Message> {

	/** @see org.codehaus.jackson.map.ObjectMapper */
    private static ObjectMapper objectMapper = new ObjectMapper();
    
    /** The logger. */
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Encode.
     *
     * Constucts a JSON structure from a Message object, in effect
     * serializing the Message by converting it into a String.
     *  
     * @param message     What needs to be serialized
     * @return            the resulting JSON (String)
     * @throws EncodeException   see javax.websocket.EncodeException
     */
    @Override
    public String encode(Message message) throws EncodeException {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return "{}";
        }
    }

    /**
     * Custom code if anything special is needed when establishing the session
     * with a particular endpoint (the websocket).  Not used at present. 
     * 
     * @param endpointConfig the endpoint config
     */
    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    /**
     * Destroy.
     * Close the connection.  Nothing implemented in the prototype.
     * But then again, there's no disconnect message.
     */
    @Override
    public void destroy() {
        // Close resources
    }
}
