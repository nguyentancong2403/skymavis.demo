package utils;


import javax.mail.*;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MailUtils {
    public Message[] getEmails(String host, String port, String enableTls, String storeType, String username, String password) {
        try{
            Properties props = new Properties();
            props.put("mail.pop3.host", host);
            props.put("mail.pop3.port", port);
            props.put("mail.pop3.starttls.enable", enableTls);
            Session session = Session.getDefaultInstance(props);
            Store store = session.getStore(storeType);
            store.connect(host, username, password);
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);
            return emailFolder.getMessages();
        }
        catch (MessagingException e){
            e.printStackTrace();
            return null;
        }
    }

    public List<Message> filterEmailsBySenderAndSubject(Message[] messages,String sender, String subject)  {
        List<Message> listFilterMessages = new ArrayList<>();
        List<Message> listFilterMessageFromSubject = new ArrayList<>();
        try {
            for (Message message:messages) {
                if(message.getSubject().equals(subject)){
                    listFilterMessageFromSubject.add(message);
                }
            }
            for (Message message: listFilterMessageFromSubject) {
                for (Address address: message.getFrom()) {
                    if (address.toString().equals(sender)){
                        listFilterMessages.add(message);
                    }
                }
            }
        }
        catch (MessagingException e){
            e.printStackTrace();
        }
        return listFilterMessages;
    }
}
