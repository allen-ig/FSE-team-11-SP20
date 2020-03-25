package com.neu.prattle.model;

import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.MessageServiceImpl;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.time.Instant;

/***
 * A Basic POJO for Message.
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 */

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Timestamp timestamp;

    /***
     * The name of the user who sent this message.
     */
    private String fromUser;
    /***
     * The name of the user to whom the message is sent.
     */
    private String to;
    /***
     * It represents the contents of the message.
     */
    private String content;

    @Override
    public String toString() {
        return new StringBuilder()
                .append("From: ").append(fromUser)
                .append("To: ").append(to)
                .append("Content: ").append(content)
                .toString();
    }

    public String getFrom() {
        return fromUser;
    }

    public void setFrom(String from) {
        this.fromUser = from;
    }

    public String getTo() {
        return to;
    }

    public int getId(){return this.id;}

    public void setTo(String to) {
        this.to = to;
    }

    public Timestamp getTimestamp(){
        return this.timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static MessageBuilder messageBuilder()   {
        return new MessageBuilder();
    }

    /***
     * A Builder helper class to create instances of {@link Message}
     */
    public static class MessageBuilder    {
        /***
         * Invoking the build method will return this message object.
         */
        private MessageService messageService = MessageServiceImpl.getInstance();
        Message message;

         MessageBuilder()    {
            message = new Message();
            message.setFrom("Not set");
            message.timestamp = Timestamp.from(Instant.now());
        }

        public MessageBuilder setFrom(String from)    {
            message.setFrom(from);
            return this;
        }

        public MessageBuilder setTo(String to)    {
            message.setTo(to);
            return this;
        }

        public MessageBuilder setMessageContent(String content)   {
            message.setContent(content);
            return this;
        }

        public Message build()  {
             Message savedMessage = messageService.createMessage(message);
            return savedMessage;
        }
    }
}
