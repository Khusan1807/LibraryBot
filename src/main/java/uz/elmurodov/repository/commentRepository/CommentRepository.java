package uz.elmurodov.repository.commentRepository;

import uz.elmurodov.entity.Book;
import uz.elmurodov.entity.Comment;
import uz.elmurodov.repository.AbstractRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Narzullayev Husan, вс 14:31. 02.01.2022
 */
public class CommentRepository extends AbstractRepository {

    public static void save(Comment comment) {
        String  sql="insert into comment (fullName,phoneNumber,userName,comment) values(?,?,?,?)";
        try (Connection connection= getConnection();
             PreparedStatement preparedStatement=connection.prepareStatement(sql)){
            preparedStatement.setString(1,comment.getFullName());
            preparedStatement.setString(2, comment.getPhoneNumber());
            preparedStatement.setString(3, comment.getUserName());
            preparedStatement.setString(4, comment.getComment());

            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

//    public static List<Comment> get(String message, Integer n) {
//        List<Book>books=new ArrayList<>();
//        String sql="select * from comment  where comment. like ?  limit 10 offset ? ";
//        try (Connection connection= getConnection();
//             PreparedStatement preparedStatement=connection.prepareStatement(sql)){
//
//            preparedStatement.setString(1,message);
//            preparedStatement.setInt(2,n);
//            ResultSet resultSet=preparedStatement.executeQuery();
//
//            while(resultSet.next()){
//                Book book=new Book();
//                book.setAuthor(resultSet.getString("author"));
//                book.setCategory(resultSet.getString("category"));
//                book.setId(resultSet.getString("bookId"));
//                book.setDescription(resultSet.getString("description"));
//                book.setBookName(resultSet.getString("bookName"));
//                books.add(book);
//            }
//        } catch (SQLException throwAbles) {
//            throwAbles.printStackTrace();
//        }
//        return books;
//    }
}
