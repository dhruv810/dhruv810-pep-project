package Controller;

import static org.mockito.ArgumentMatchers.contains;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    private AccountService accountService;
    private MessageService messageService;

    public SocialMediaController() {
        accountService = new AccountService();
        messageService = new MessageService();
    }
    
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("register", this::registerAccount);
        app.post("login", this::loginUser);
        app.post("messages", this::postMessage);
        app.get("messages", this::getAllMessages);
        app.get("messages/{message_id}", this::getMessageById);
        app.delete("messages/{message_id}", this::deleteMessageById);
        app.patch("messages/{message_id}", this::updateeMessageById);
        app.get("accounts/{account_id}/messages", this::getMessageByAccountId);

        return app;
    }

    /**
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    private void registerAccount(Context context) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);

        Account addedAccount = accountService.createAccount(account);
        if (addedAccount == null)
            context.status(400);
        else {
            context.json(mapper.writeValueAsString(addedAccount));
        }
    }

    /**
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    private void loginUser(Context context) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);
        
        Account validLogin = accountService.checkCredentials(account);
        
        if (validLogin == null) {
            context.status(401);
        }
        else {
            context.json(mapper.writeValueAsString(validLogin));
        }

    }

    /**
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    private void postMessage(Context context) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);

        boolean isValidUser = accountService.checkAccountByAccountID(message.getPosted_by());

        if ( ! isValidUser) {
            context.status(400);
        }
        else {
            Message postedMessage = messageService.postMessage(message);
            if (postedMessage == null){
                context.status(400);
            }
            else {
                context.json(mapper.writeValueAsString(postedMessage));
            }
        }
    }

    /**
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void getAllMessages(Context context) { 
        List<Message> messages = messageService.getAllMessages();
        context.json(messages);
    }

    /**
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws JsonProcessingException
     */
    private void getMessageById(Context context) throws JsonProcessingException {
        int message_id = Integer.parseInt(context.pathParam("message_id"));
        ObjectMapper mapper = new ObjectMapper();
        Message message = messageService.getMessageById(message_id);
        if (message == null) {
            context.json("");
        }
        else {
            context.json(mapper.writeValueAsString(message));
        }
    }
    
    /**
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws JsonProcessingException
     */
    private void deleteMessageById(Context context) throws JsonProcessingException  {
        int message_id = Integer.parseInt(context.pathParam("message_id"));
        ObjectMapper mapper = new ObjectMapper();
        
        Message message = messageService.getMessageById(message_id);
        if (message == null) {
            context.result("");
        }
        else {
            int rowsEffected = messageService.deleteMessageById(message_id);
            if (rowsEffected != 0) {
                context.json(mapper.writeValueAsString(message));
            }
            else {
                context.result("");
            }
        }
    }

     /**
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws JsonProcessingException
     */
    private void updateeMessageById(Context context) throws JsonProcessingException {
        int message_id = Integer.parseInt(context.pathParam("message_id"));

        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);
        
        Message oldMessage = messageService.getMessageById(message_id);
        
        if (oldMessage == null) {
            context.status(400);
        }
        else {
            if (messageService.updateMessage(message, message_id)) {
                Message updatedMessage = messageService.getMessageById(message_id);
                context.json(mapper.writeValueAsString(updatedMessage));
            }
            else {
                context.status(400);
            }
        }
    }

    /**
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    public void getMessageByAccountId(Context context) {
        int account_id = Integer.parseInt(context.pathParam("account_id"));

        List<Message> messages = messageService.getMessageByAccountId(account_id);
        context.json(messages); 
        
    }

}