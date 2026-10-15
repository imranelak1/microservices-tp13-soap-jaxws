package com.example.demo.config;
import com.example.demo.ws.CompteSoapService;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.*;
@Configuration
public class CxfConfig {
    @Bean EndpointImpl androidEndpoint(Bus bus,com.example.demo.ws.AndroidCompteSoapService service) {
        var endpoint=new EndpointImpl(bus,service);
        endpoint.publish("/services/ws");
        return endpoint;
    }
    @Bean EndpointImpl endpoint(Bus bus,CompteSoapService service) {
        var endpoint=new EndpointImpl(bus,service);
        endpoint.publish("/ws");
        return endpoint;
    }
}
