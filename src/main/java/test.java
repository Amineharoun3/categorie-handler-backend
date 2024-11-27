import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class test {
    @Autowired
    private javax.sql.DataSource dataSource;

    @GetMapping("/test")
    public String test() {
        try {
            dataSource.getConnection();
            return "Connection successful!";
        } catch (Exception e) {
            return "Connection failed: " + e.getMessage();
        }
    }
}
