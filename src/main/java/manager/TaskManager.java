package manager;

import db.DBConnectionProvider;
import model.Task;
import model.TaskStatus;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Connection connection = DBConnectionProvider.getInstance().getConnection();
    private UserManager userManager = new UserManager();

    public boolean addTask(Task task) {
        String sql = "INSERT INTO task (name ,description ,deadline ,status ,user_id) VALUES(?,?,?,?,?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, task.getName());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setString(3, sdf.format(task.getDeadline()));
            preparedStatement.setString(4, task.getTaskStatus().name());
            preparedStatement.setInt(5, task.getUserId());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                task.setId(resultSet.getInt(1));
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task getTaskFromResultSet(ResultSet resultSet) {
        try {
            return Task.builder()
                    .id(resultSet.getInt(1))
                    .name(resultSet.getString(2))
                    .description(resultSet.getString(3))
                    .deadline(resultSet.getDate(4))
                    .taskStatus(TaskStatus.valueOf(resultSet.getString(5)))
                    .userId(resultSet.getInt(6))
                    .user(userManager.getById(resultSet.getInt(6)))
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Task> getAllTaskByUserId(int userId) {

        String sql = "SELECT * FROM task WHERE user_id = " + userId;

        List<Task> list = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                list.add(getTaskFromResultSet(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Task> getAllTasks() {

        String sql = "select * from task";

        List<Task> list = new ArrayList<>();

        try {

            PreparedStatement prepareStatement = connection.prepareStatement(sql);
            ResultSet resultSet = prepareStatement.executeQuery();

            while (resultSet.next()) {
                Task task = new Task();

                task.setId(resultSet.getInt(1));
                task.setName(resultSet.getString(2));
                task.setDescription(resultSet.getString(3));
                try {
                    task.setDeadline(sdf.parse(resultSet.getString(4)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                task.setTaskStatus(TaskStatus.valueOf(resultSet.getString(5)));
                task.setUserId(resultSet.getInt(6));
                task.setUser(userManager.getById(resultSet.getInt(6)));

                list.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Task getTaskById(int id) {

        String sql = "SELECT * FROM task WHERE id = " + id;

        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                return getTaskFromResultSet(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateTaskStatus(int taskId,String newStatus) {

        String sql = "UPDATE task SET status = ? WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1,newStatus);
            preparedStatement.setInt(2,taskId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Task task) {

        String sql = "DELETE FROM task WHERE id = " + task.getId();

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}