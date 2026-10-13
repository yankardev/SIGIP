package pe.edu.cibertec.sigip.productionservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ProductionServiceApplication {
 public static void main(String[] args){SpringApplication.run(ProductionServiceApplication.class,args);}
}