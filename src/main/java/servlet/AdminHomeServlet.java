package servlet;

import manager.TaskManager;
import manager.UserManager;
import model.Task;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/adminHome")
public class AdminHomeServlet extends HttpServlet {

    private TaskManager taskManager = new TaskManager();
    private UserManager userManager = new UserManager();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


            List<Task> allTasks = taskManager.getAllTasks();
            List<User> allUsers = userManager.getAllUsers();

            req.setAttribute("tasks", allTasks);
            req.setAttribute("users", allUsers);

            req.getRequestDispatcher("/WEB-INF/admin.jsp").forward(req, resp);

    }
}
