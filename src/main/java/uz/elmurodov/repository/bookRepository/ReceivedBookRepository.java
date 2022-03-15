package uz.elmurodov.repository.bookRepository;

import org.sqlite.jdbc4.JDBC4Connection;
import uz.elmurodov.config.PConfig;
import uz.elmurodov.entity.ReceivedBook;
import uz.elmurodov.enums.state.BookState;
import uz.elmurodov.repository.AbstractRepository;

import java.sql.*;

public class ReceivedBookRepository extends AbstractRepository {
    public static final ReceivedBookRepository instance=new ReceivedBookRepository();




    public void save(String fileId, String chatId) {
        Connection connection = getConnection();
        try {
            Statement statement = connection.createStatement();
            statement.execute(PConfig.get("receivedBook.insert.fileId.chatId").formatted(fileId,chatId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public  BookState getSate( String ownerId) {
        String sql="select * from receivedBooks  where  receivedBooks.chatId=? ";
        try (Connection connection= getConnection();
             PreparedStatement preparedStatement=connection.prepareStatement(sql)){

            preparedStatement.setString(1,"%"+ownerId+"%");
            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next()){
                ReceivedBook book=new ReceivedBook();
                book.setId(resultSet.getString("fileId"));
                book.setOwnerId(resultSet.getString("chatId"));
                book.setState(BookState.findBookStateByText(resultSet.getString("state")));
                return book.getState();
            }
        } catch (SQLException throwAbles) {
            throwAbles.printStackTrace();
        }
        return null;
    }


    public   void setBookState(String fileId, String chatId, BookState state) {
        String sql = "select * from receivedBooks  where receivedBooks.fileId like ?  and  receivedBooks.chatId like ? ";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, fileId);
            preparedStatement.setString(2, "%" + chatId + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ReceivedBook book = new ReceivedBook();
                book.setFileId(resultSet.getString("fileId"));
                book.setOwnerId(resultSet.getString("chatId"));
                book.setState(BookState.findBookStateByText(resultSet.getString("state")));
                book.setState(state);
            }
        } catch (SQLException throwAbles) {
            throwAbles.printStackTrace();
        }


    }

        public  void updateState( String ownerId,BookState state) {
            String sql="select * from receivedBooks  where  receivedBooks.chatId=? ";
            try (Connection connection= getConnection();
                 PreparedStatement preparedStatement=connection.prepareStatement(sql)){

                preparedStatement.setString(1,"%"+ownerId+"%");
                ResultSet resultSet=preparedStatement.executeQuery();
                while(resultSet.next()){
                    ReceivedBook book=new ReceivedBook();
                    book.setId(resultSet.getString("fileId"));
                    book.setOwnerId(resultSet.getString("chatId"));
                    book.setState(BookState.findBookStateByText(resultSet.getString("state")));
                    book.setState(state);
                }
            } catch (SQLException throwAbles) {
                throwAbles.printStackTrace();
            }

        }


        public void deleteData(String chatId){
        String sql=PConfig.get("delete.receivedBooks.data").formatted(chatId);
        Connection connection=getConnection();
            try {
               Statement statement=connection.createStatement();
               statement.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    public void updateBookState(String fileId,BookState state){
        String sql=PConfig.get("update.receivedBooks.state").formatted(state,fileId);
        Connection connection=getConnection();
        try {
            Statement statement=connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateBookStateByChatId(String chatId,BookState state){
        String sql=PConfig.get("update.receivedBooks.state.with.chatId").formatted(state,chatId);
        Connection connection=getConnection();
        try {
            Statement statement=connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ReceivedBookRepository getInstance() {
        return instance;
    }

}
