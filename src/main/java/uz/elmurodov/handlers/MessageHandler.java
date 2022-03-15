package uz.elmurodov.handlers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.elmurodov.FreePdfBot;
import uz.elmurodov.buttons.InlineBoards;
import uz.elmurodov.buttons.MarkupBoard;
import uz.elmurodov.buttons.utilsInlineBoard;
import uz.elmurodov.config.GetAll;
import uz.elmurodov.config.LangConfig;
import uz.elmurodov.config.PConfig;
import uz.elmurodov.emojis.Emojis;
import uz.elmurodov.entity.Book;
import uz.elmurodov.entity.Comment;
import uz.elmurodov.enums.Language;

import uz.elmurodov.enums.state.AllState;
import uz.elmurodov.enums.state.BookState;
import uz.elmurodov.processors.PutProcess;
import uz.elmurodov.processors.SearchProcess;
import uz.elmurodov.repository.authuser.AuthUserRepository;
import uz.elmurodov.repository.bookRepository.BookRepository;

import uz.elmurodov.repository.bookRepository.ReceivedBookRepository;
import uz.elmurodov.service.LogService;
import uz.elmurodov.services.*;
import uz.elmurodov.services.search.LanguageService;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static uz.elmurodov.enums.state.AllState.findByText;
import static uz.elmurodov.handlers.CallbackHandler.count;
import static uz.elmurodov.handlers.CallbackHandler.messages;


/**
 * @author Narzullayev Husan, Fri 6:44 PM. 12/17/2021
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageHandler {

    private static final MessageHandler instance = new MessageHandler();
    private static final LogService service = LogService.getInstance();
    private static final AuthUserRepository authUserRepository = AuthUserRepository.getInstance();
    private static final FreePdfBot BOT = FreePdfBot.getInstance();
    private static final OtherService otherService = OtherService.getInstance();
    private static final ContactService contactService = ContactService.getInstance();
    private static final StartAndOffService service1 = StartAndOffService.getInstance();
    private static final LanguageService languageService = LanguageService.getInstance();
    private static final SearchService searchService = SearchService.getInstance();
    private static final SearchProcess searchProcess = SearchProcess.getInstance();
    private static final PutProcess putProcess = new PutProcess();
    private static final LinkService linkService = LinkService.getInstance();
    private static final SettingService settingService = SettingService.getInstance();
    public static Language language;
    public static Book book;
    public static Comment comment;
    public static GetAll instanceGetAll=GetAll.getInstance();
    public static final Map<String, BookState> bookStatus = new HashMap<>();
    public static final Map<String, List<Book>>bookMap=new HashMap<>();
    public static final ReceivedBookRepository instanceReceivedBook= ReceivedBookRepository.getInstance();


    public void handle(Message message) {
        service.create(message.getText());
        String chatId = message.getChatId().toString();
        Map<String ,String> map=instanceGetAll.getAllState();
        String text = map.get(chatId);
        AllState state=findByText(text);
        Map<String, String > map1=instanceGetAll.getBookState();
        String string=map1.get(chatId);
        BookState bookState=BookState.findBookStateByText(string);
        if(message.hasContact()){
            putProcess.Put(message,state);
        }
        if(message.hasDocument() && AllState.AUTHORIZED.equals(state) ){
            putProcess.sendMeBook(chatId,message.getDocument());
        }
        else if(BookState.DESCRIPTION.equals(bookState)){
            putProcess.setAuthor(chatId,message.getText());
        }else if(BookState.AUTHOR.equals(bookState)){
            book.setAuthor(message.getText());
            putProcess.setDescription(chatId);
        }
         if(AllState.AUTHORIZED.equals(state) && ("/start".equals(message.getText()) ||
                LangConfig.get(chatId,"start").equals(message.getText()))){
            SendMessage sendMessage=new SendMessage(chatId,LangConfig.get(chatId,"welcome.to.our.library"));
            sendMessage.setReplyMarkup(MarkupBoard.mainMenu(chatId));
            BOT.executeMessage(sendMessage);
        }
        else if (("/start".equals(message.getText()) ||
                LangConfig.get(chatId, "start").equals(message.getText()) ) && !AllState.AUTHORIZED.equals(state)) {
            service1.start(chatId);
            putProcess.languageChoice(chatId);
            SendMessage sendMessage = new SendMessage(chatId, "Welcome to our library");
            BOT.executeMessage(sendMessage);
        }
        else if ("/off".equals(message.getText())) {
            service1.off(chatId);
            SendMessage sendMessage = new SendMessage(chatId, LangConfig.get(chatId, "subscription") +
                    "\n\n" +
                    LangConfig.get(chatId, "again"));
            sendMessage.setReplyMarkup(MarkupBoard.start(chatId));
            BOT.executeMessage(sendMessage);
        }

        else if ("/on".equals(message.getText())) {
            service1.on(chatId);
            String message1 = LangConfig.get(chatId, "on") +
                    "\n" +
                    LangConfig.get(chatId, "off") + "\n";
            SendMessage sendMessage = new SendMessage(chatId, message1);
            sendMessage.setReplyMarkup(MarkupBoard.mainMenu(chatId));
            BOT.executeMessage(sendMessage);
        }

        else if ((Emojis.SEARCH+LangConfig.get(chatId, "search")).equals(message.getText()) || "/search".equals(message.getText())) {
            searchProcess.search(chatId,AllState.SEARCH);
            searchService.search(chatId, message.getText());
        }
        else if ((authUserRepository.isHaveUser(chatId) && (Emojis.ADD+LangConfig.get(chatId, "put")).equals(message.getText())) ||
                (authUserRepository.isHaveUser(chatId) && "/put".equals(message.getText()))) {
           putProcess.putBooks(message.getChatId().toString());
        }
        else if(!authUserRepository.isHaveUser(chatId) && (Emojis.ADD+LangConfig.get(chatId,"put")).equals(message.getText()) ||
                (!authUserRepository.isHaveUser(chatId) && "/put".equals(message.getText()))){
            putProcess.Put(message,state);
        }
        else if ("/contact_us".equals(message.getText()) ||
                (Emojis.CONTACT_US+LangConfig.get(chatId, "contact.us")).equals(message.getText())) {
            contactService.contactUs(chatId);
        }

        else if ("/language".equals(message.getText()) ||
                (Emojis.LANGUAGE+LangConfig.get(chatId, "language")).equals(message.getText())) {
            languageService.lang(chatId);
        }

        else if ("/links".equals(message.getText()) ||
                (Emojis.LINKS+LangConfig.get(chatId, "links")).equals(message.getText())) {
            linkService.link(chatId);
        }
        else if ("/comments".equals(message.getText()) ||
                (Emojis.FEEDBACK+LangConfig.get(chatId, "subscribe.feedback")).equals(message.getText())) {
            ///coment lar ro'yhati

        }
        else if ((Emojis.COMMENT+LangConfig.get(chatId, "comment")).equals(message.getText())) {
            otherService.report(chatId,Emojis.REPORT_COMMENT+LangConfig.get(chatId, "report.comment"));
        }
        else if ((Emojis.OK+LangConfig.get(chatId, "ok")).equals(message.getText()) && message.hasText()) {
            otherService.sendMessage(message, chatId, LangConfig.get(chatId, "thanks"));
            otherService.menuExecte(chatId);
        }

        else if ((Emojis.OK+LangConfig.get(chatId, "cancel")).equals(message.getText()) ) {
            otherService.sendMessage(message, chatId, LangConfig.get(chatId, "cancel1"));
            otherService.menuExecte(chatId);
        }


        else if ("/users".equals(message.getText())) {
            otherService.sendMessage(message, chatId, LangConfig.get(chatId, "users"));
            // userslar soni chiqadi
        }

        else if ((Emojis.ABOUT_US+LangConfig.get(chatId, "about.us")).equals(message.getText())) {
            otherService.aboutUs(chatId);
        }

        else if ("/report".equals(message.getText())) {
            otherService.report(chatId,LangConfig.get(chatId, "report"));
        }

        else if ("/help".equals(message.getText()) || (Emojis.HELP+LangConfig.get(chatId, "help")).equals(message.getText())) {
            otherService.help(chatId);
        }

        else if ("/settings".equals(message.getText()) ||
                (Emojis.SETTINGS+LangConfig.get(chatId, "settings")).equals(message.getText())) {
            // language iwlayapti
            // age bn fullname logikasi qoldi u faqat put qiladiganlarga kurinadigan qilamiz
            settingService.setting(chatId);
            
        }

        else if ((Emojis.DONATE+LangConfig.get(chatId, "donate")).equals(message.getText())) {
            otherService.donate(chatId);
        }

        else if ((Emojis.CHANNEL+LangConfig.get(chatId, "channel")+Emojis.CHANNEL).equals(message.getText())) {
            otherService.sendMessage(message, chatId, "https://t.me/PDFBOOKSYOUNEED");
        }

        else if ((Emojis.GROUP+LangConfig.get(chatId, "group")+Emojis.GROUP).equals(message.getText())) {
            otherService.sendMessage(message, chatId, "https://t.me/+ertcIJalI4g5OWI6");
        }

        else if ((Emojis.GO_BACK+LangConfig.get(chatId, "go.back")).equals(message.getText())) {
           otherService.menuExecte(chatId);
        }

        else if ("Husan".equals(message.getText())) {
            String name="husan";
            sendPhoto(chatId,name);
            otherService.sendMessage(message, chatId, "<a href=\"https://t.me/Narzullayev_Husan\"> Husan</a>");
        }

        else if ("Axrullo".equals(message.getText())) {
            String name="axrullo";
            sendPhoto(chatId,name);
            otherService.sendMessage(message, chatId, "<a href=\"https://t.me/akhrullo\">Axrullo</a>");
        }

        else if ("Aziza".equals(message.getText())) {
            String name="aziza";
            sendPhoto(chatId,name);
          otherService. sendMessage(message, chatId, "<a href=\"https://t.me/AzizaTojiboeva\">Aziza</a>");
        }

        else if ("Uchqun".equals(message.getText())) {
            String name="uchqun";
            sendPhoto(chatId,name);
           otherService.sendMessage(message, chatId, "<a href=\"https://t.me/Uchqun99bek26\">Uchqun</a>");
        }

        else if ((Emojis.MY_BOOKS +LangConfig.get(chatId,"my.books")).equals(message.getText())) {
            count.put(chatId,0);
            List<Book>books= BookRepository.getMY(chatId,0);
            messages.put(chatId,chatId);
            utilsInlineBoard.next(books, chatId);

        }
        else if (LangConfig.get(chatId,"science").equals(message.getText())){


        }
        else if (LangConfig.get(chatId,"adventure").equals(message.getText())){


        }
        else if (LangConfig.get(chatId,"history").equals(message.getText())){


        }

        else if (LangConfig.get(chatId,"romance").equals(message.getText())){


        }

        else if (LangConfig.get(chatId,"fantasy").equals(message.getText())){


        }
        else if (LangConfig.get(chatId,"detective").equals(message.getText())){

        }

        else {
            putProcess.Put(message,state);
        }
    }

    private void sendPhoto(String chatId,String  name) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(new File(PConfig.get(name))));
        BOT.executeMessage(sendPhoto);
    }

    private boolean isUserAuthorized(AllState state) {
        return AllState.AUTHORIZED.equals(state);
    }

    public static void delete(Message message, String chatId) {
        DeleteMessage deleteMessage=new DeleteMessage(chatId, message.getMessageId());
        BOT.executeMessage(deleteMessage);
    }

    public void removeKeyboard(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove());
        BOT.executeMessage(sendMessage);
    }




    public static MessageHandler getInstance() {
        return instance;
    }


}
