import javax.xml.ws.Endpoint;
import com.example.soapapi.*;

public class App {
    public static void main(String[] args) {
        // Adresse URL pour accéder à votre API SOAP
        String url = "http://localhost:8080/admin-api";

        // Créer une instance de votre implémentation de service
        AdminServiceImpl adminService = new AdminServiceImpl();

        // Publier le service à l'URL spécifiée
        Endpoint.publish(url, adminService);

        System.out.println("API SOAP démarrée à l'adresse : " + url);
    }
}