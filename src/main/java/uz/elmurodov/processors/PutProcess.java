package uz.elmurodov.processors;

import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.elmurodov.FreePdfBot;
import uz.elmurodov.buttons.InlineBoards;
import uz.elmurodov.buttons.MarkupBoard;
import uz.elmurodov.config.LangConfig;
import uz.elmurodov.config.PConfig;
import uz.elmurodov.config.State;
import uz.elmurodov.entity.Book;
import uz.elmurodov.enums.state.AllState;
import uz.elmurodov.enums.state.BookState;
import uz.elmurodov.handlers.MessageHandler;
import uz.elmurodov.repository.LogRepository;
import uz.elmurodov.repository.bookRepository.ReceivedBookRepository;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static uz.elmurodov.FreePdfBot.users;
import static uz.elmurodov.config.State.setAllState;
import static uz.elmurodov.config.State.setBookState;
import static uz.elmurodov.handlers.MessageHandler.book;

public class PutProcess {
    private static final PutProcess instance = new PutProcess();
    private static final FreePdfBot BOT = FreePdfBot.getInstance();
    public static final Map<String, BookState> bookStatus = new HashMap<>();
    public static  BookState bookState;
    public static final ReceivedBookRepository instanceReceivedBook= ReceivedBookRepository.getInstance();

    public void Put(Message message, AllState state) {
        String chatID = message.getChatId().toString();
        if (AllState.FULL_NAME.equals(state) || Objects.isNull(state)) {
            SendMessage sendMessage = new SendMessage(chatID, LangConfig.get(chatID, "your.full.name.please"));
            sendMessage.setReplyMarkup(new ForceReplyKeyboard());
            BOT.executeMessage(sendMessage);
            setAllState(chatID, AllState.AGE);
            LogRepository.getInstance().updateUser(chatID,AllState.AGE);
            LogRepository.getInstance().save(users.get(chatID), chatID, AllState.AGE.toString());
        } else if (AllState.AGE.equals(state)) {
            users.get(message.getChatId()+"").setFullName(message.getText());
            SendMessage sendMessage = new SendMessage(chatID, LangConfig.get(chatID, "your.age.please"));
            sendMessage.setReplyMarkup(new ForceReplyKeyboard());
            BOT.executeMessage(sendMessage);
            setAllState(chatID, AllState.GENDER);
            LogRepository.getInstance().updateUserName(chatID,message.getText());
            LogRepository.getInstance().updateUser(chatID,AllState.GENDER);;
        } else if (AllState.GENDER.equals(state)) {
            String text = message.getText();
            if (StringUtils.isNumeric(text)) {
                users.get(message.getChatId()+"").setAge(Integer.parseInt(text));
                SendMessage sendMessage1 = new SendMessage(chatID, LangConfig.get(chatID, "gender"));
                sendMessage1.setReplyMarkup(InlineBoards.gender(chatID));
                BOT.executeMessage(sendMessage1);
                setAllState(chatID, AllState.PHONE_NUMBER);
                LogRepository.getInstance().updateUserAge(chatID,Integer.parseInt(text));
                LogRepository.getInstance().updateUser(chatID,AllState.PHONE_NUMBER);
            } else {
                SendMessage sendMessage1 = new SendMessage(chatID, LangConfig.get(chatID, "invalid.number.format") + LangConfig.get(chatID, "please.send.correct.number"));
                BOT.executeMessage(sendMessage1);
            }
        }else if(AllState.PHONE_NUMBER.equals(state)){
            String chatId= message.getChatId().toString();
            if (message.hasContact()) {
                users.get(
                        message.getContact().getUserId() + "").setPhoneNumber(message.getContact().getPhoneNumber());
                setAllState(chatId, AllState.AUTHORIZED);
                users.get(chatId).setRole("user");
                users.get(chatId).setUserName(message.getChat().getUserName());
                LogRepository.getInstance().updateUserPhoneNumber(chatID,message.getContact().getPhoneNumber());
                LogRepository.getInstance().updateUsername(chatID,message.getChat().getUserName());
                LogRepository.getInstance().updateUser(chatID,AllState.AUTHORIZED);
                SendMessage sendMessage = new SendMessage(chatId, LangConfig.get(chatId, "successfully.authorized"));
                sendMessage.setReplyMarkup(MarkupBoard.mainMenu(chatId));
                BOT.executeMessage(sendMessage);
            }
            else {
                SendMessage sendMessage1 = new SendMessage(chatId, "Invalid Number format\nPlease send correct number");
                BOT.executeMessage(sendMessage1);
            }
        }
    }


    public void setDescription(String chatId){
        SendMessage sendMessage=new SendMessage(chatId,LangConfig.get(chatId,"book.category"));
        ReplyKeyboard replyKeyboard=InlineBoards.category(chatId);
        sendMessage.setReplyMarkup(replyKeyboard);
        BOT.executeMessage(sendMessage);
        instanceReceivedBook.updateBookStateByChatId(chatId,BookState.CATEGORY);
    }
    public void sendMeBook(String chatId, Document document) {
        book=new Book();
        book.setId(document.getFileId());
        book.setOwnerId(chatId);
        book.setBookName(document.getFileName());
        instanceReceivedBook.save(document.getFileId(),chatId);
        SendMessage sendMessage=new SendMessage(chatId,LangConfig.get(chatId,"book.desc"));
        BOT.executeMessage(sendMessage);
        instanceReceivedBook.updateBookState(document.getFileId(),BookState.DESCRIPTION);
        //instanceReceivedBook.setBookState(document.getFileId(),chatId,BookState.DESCRIPTION);
    }
    public void putBooks(String chatId) {
            SendMessage sendMessage= new SendMessage(chatId,LangConfig.get(chatId,"book.send"));
            sendMessage.setReplyMarkup(new ForceReplyKeyboard());
            BOT.executeMessage(sendMessage);
    }

    public void setAuthor(String chatId, String text) {
        book.setDescription(text);
        SendMessage sendMessage= new SendMessage(chatId,LangConfig.get(chatId,"book.author"));
        sendMessage.setReplyMarkup(new ForceReplyKeyboard());
        BOT.executeMessage(sendMessage);
        instanceReceivedBook.updateBookStateByChatId(chatId,BookState.AUTHOR);
    }

    public void forceReplyKeyboard(String chatID, String message) {
        SendMessage sendMessage = new SendMessage(chatID, message);
        sendMessage.setReplyMarkup(new ForceReplyKeyboard());
        BOT.executeMessage(sendMessage);
    }

    public void languageChoice(String chatID) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatID);
        sendPhoto.setPhoto(new InputFile(new File(PConfig.get("bot.logo"))));
        BOT.executeMessage(sendPhoto);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.setText("Your language please");
        sendMessage.setReplyMarkup(InlineBoards.languageButtons());
        BOT.executeMessage(sendMessage);

    }

    public void initBook(Message message, BookState state) {
//        String chatID = message.getChatId().toString();
//        if (BookState.NAME.equals(state) || Objects.isNull(state)) {
//            SendMessage sendMessage = new SendMessage(chatID, LangConfig.get(chatID, "your.full.name.please"));
//            sendMessage.setReplyMarkup(new ForceReplyKeyboard());
//            BOT.executeMessage(sendMessage);
//            setBookState(chatID, BookState.AUTHOR);
//        } else if (BookState.AUTHOR.equals(state)) {
//            users.get(message.getChatId()+"").setFullName(message.getText());
//            SendMessage sendMessage = new SendMessage(chatID, LangConfig.get(chatID, "your.age.please"));
//            sendMessage.setReplyMarkup(new ForceReplyKeyboard());
//            BOT.executeMessage(sendMessage);
//            setBookState(chatID, BookState.DESCRIPTION);
//        } else if (BookState.DESCRIPTION.equals(state)) {
//            users.get(message.getChatId()+"").setFullName(message.getText());
//            SendMessage sendMessage = new SendMessage(chatID, LangConfig.get(chatID, "your.age.please"));
//            sendMessage.setReplyMarkup(new ForceReplyKeyboard());
//            BOT.executeMessage(sendMessage);
//            setBookState(chatID, BookState.CATEGORY);
//        } else if (BookState.CATEGORY.equals(state)) {
//            String text = message.getText();
//            if (StringUtils.isNumeric(text)) {
//                users.get(message.getChatId()+"").setAge(Integer.parseInt(text));
//                SendMessage sendMessage1 = new SendMessage(chatID, LangConfig.get(chatID, "gender"));
//                sendMessage1.setReplyMarkup(InlineBoards.category(chatID));
//                BOT.executeMessage(sendMessage1);
//                setBookState(chatID, BookState.CATEGORY);
//            } else {
//                SendMessage sendMessage1 = new SendMessage(chatID, LangConfig.get(chatID, "invalid.number.format") + LangConfig.get(chatID, "please.send.correct.number"));
//                BOT.executeMessage(sendMessage1);
//            }
//        }else if(AllState.CATEGORY.equals(state)){
//            String chatId= message.getChatId().toString();
//            if (message.hasContact()) {
//                users.get(
//                        message.getContact().getUserId() + "").setPhoneNumber(message.getContact().getPhoneNumber());
//                SendMessage sendMessage = new SendMessage(chatId, LangConfig.get(chatId, "successfully.authorized"));
//                sendMessage.setReplyMarkup(MarkupBoard.mainMenu(chatId));
//
//                BOT.executeMessage(sendMessage);
//                setAllState(chatId, AllState.AUTHORIZED);
//                sendMessage.setReplyMarkup(new ReplyKeyboardRemove());
//
//                users.get(chatId).setRole("user");
//                users.get(chatId).setUserName(message.getChat().getUserName());
//                setAllState(message.getChatId().toString(),AllState.AUTHORIZED);
//                LogRepository.getInstance().save(users.get(chatID), chatID, AllState.AUTHORIZED.toString());
//            }
//
//        }
    }


    public static PutProcess getInstance() {
        return instance;
    }


}
