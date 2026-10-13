package pe.edu.cibertec.sigip.notificationservice.config;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
@Configuration
public class RabbitConfig{@Bean Queue sigipNotificationsQueue(@Value("${sigip.rabbit.queue}")String name){return new Queue(name,true);}}