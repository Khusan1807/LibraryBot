package uz.elmurodov.repository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import uz.elmurodov.entity.User;
import uz.elmurodov.enums.state.AllState;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import static java.lang.Integer.parseInt;

/**
 * @author Narzullayev Husan, Sat 10:22 AM. 12/18/2021
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogRepository extends AbstractRepository {
    private static final LogRepository instance = new LogRepository();


    public void save(String data) {
        //query.append("drop table log (log);") ;
        query.append("insert into log (log) values (?);");
        getPreparedStatement(data);
        executeWithout();
        close();
    }
    @SneakyThrows
    public void save(User user, String chatID, String name) {
        try (Connection connection= getConnection();
             Statement statement=connection.createStatement()){
            String  sql=("insert into users (chatId,fullName,age,phoneNumber,language,gender,userName,state)" +
                    " values('%s','%s','%s','%s','%s','%s','%s','%s');").formatted(chatID,
                    user.getFullName(),user.getAge(),user.getPhoneNumber(),user.getLanguage(),user.getGender(),user.getUserName(),name);
            statement.execute(sql);
        }
    }

    public void updateUser( String chatID, AllState state){
        String sql="update users set state = ? where chatId = ? ";
        try (Connection connection= getConnection();
        ){
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,state.toString());
            preparedStatement.setInt(2,parseInt(chatID));
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateUserName( String chatID, String name){
        String sql="update users set fullName = ? where chatId = ? ";
        try (Connection connection= getConnection();
        ){
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,name);
            preparedStatement.setInt(2,parseInt(chatID));
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateUserAge( String chatID, Integer age){
        String sql="update users set age = ? where chatId = ? ";
        try (Connection connection= getConnection();
        ){
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setInt(1,age);
            preparedStatement.setInt(2,parseInt(chatID));
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateUserGender( String chatID, String gender){
        String sql="update users set gender = ? where chatId = ? ";
        try (Connection connection= getConnection();
        ){
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,gender);
            preparedStatement.setInt(2,parseInt(chatID));
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateUserPhoneNumber( String chatID, String phoneNumber){
        String sql="update users set phoneNumber = ? where chatId = ? ";
        try (Connection connection= getConnection();
        ){
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,phoneNumber);
            preparedStatement.setInt(2,parseInt(chatID));
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void updateUsername( String chatID, String username){
        String sql="update users set username = ? where chatId = ? ";
        try (Connection connection= getConnection();
        ){
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,username);
            preparedStatement.setInt(2,parseInt(chatID));
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static LogRepository getInstance() {
        return instance;
    }
}
