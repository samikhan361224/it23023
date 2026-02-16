import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String departmentName = request.getParameter("departmentName");
        String numberOfStudentsStr = request.getParameter("numberOfStudents");
        String action = request.getParameter("action");

        int numberOfStudents = 0;
        if (numberOfStudentsStr != null && !numberOfStudentsStr.isEmpty()) {
            numberOfStudents = Integer.parseInt(numberOfStudentsStr);
        }

        ServiceClass service = new ServiceClass();
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Date and Time
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        out.println("<html><head><style>");
        out.println("table { border-collapse: collapse; width: 50%; }");
        out.println("th, td { border: 1px solid #333; padding: 8px; text-align: left; }");
        out.println("th { background-color: #f2f2f2; }");
        out.println("</style></head><body>");

        out.println("<h3>Date & Time: " + dateTime + "</h3>");
        out.println("<h2>Action: " + action + "</h2>");

        switch (action) {
            case "Insert":
                if (service.insertDB(departmentName, numberOfStudents)) {
                    out.println("<p>Inserted successfully.</p>");
                } else {
                    out.println("<p>Insertion failed.</p>");
                }
                break;

            case "View":
                List<String> departments = service.viewDB();
                if (departments.isEmpty()) {
                    out.println("<p>No departments found.</p>");
                } else {
                    out.println("<table>");
                    out.println("<tr><th>Department Name</th><th>Number of Students</th></tr>");
                    for (String dept : departments) {
                        String[] parts = dept.split(", Students: ");
                        String deptName = parts[0].replace("Department: ", "");
                        String numStudents = parts.length > 1 ? parts[1] : "";
                        out.println("<tr><td>" + deptName + "</td><td>" + numStudents + "</td></tr>");
                    }
                    out.println("</table>");
                }
                break;

            case "Update":
                if (service.updateDB(departmentName, numberOfStudents)) {
                    out.println("<p>Updated successfully.</p>");
                } else {
                    out.println("<p>Update failed. Department not found?</p>");
                }
                break;

            case "Delete":
                if (service.deleteDB(departmentName)) {
                    out.println("<p>Deleted successfully.</p>");
                } else {
                    out.println("<p>Delete failed. Department not found?</p>");
                }
                break;

            default:
                out.println("<p>Unknown action.</p>");
        }

        out.println("</body></html>");
    }
}