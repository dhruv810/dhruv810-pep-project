package Service;

import java.util.List;

import DAO.MessageDAO;
import Model.Message;

public class MessageService {
    private MessageDAO messageDAO;

    public MessageService() {
        messageDAO = new MessageDAO();
    }

    public Message postMessage(Message message) {
        
        if (message == null || message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
            return null;
        }
        
        return messageDAO.postMessage(message);
    }

    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    public Message getMessageById(int message_id) {
        return messageDAO.getMessageById(message_id);
    }

    public int deleteMessageById(int message_id) {
        return messageDAO.deleteMessageById(message_id);
    }

    public boolean updateMessage(Message message, int message_id) {
        if (message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
            return false;
        }
        return messageDAO.updateMessage(message, message_id);

    }

    public List<Message> getMessageByAccountId(int account_id) {
        return messageDAO.getMessageByAccountId(account_id);
    }



}
