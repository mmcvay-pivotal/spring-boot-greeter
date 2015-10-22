package io.pivotal.greeter;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class GreetingController {

	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${pivotal.stringReverserService.name}")
	private String reverserServiceName;
	
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    @HystrixCommand(fallbackMethod = "fallbackGreeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        	
    	String reversed = restTemplate.getForObject("http://" + reverserServiceName + "/reverse/{name}", String.class, name);
		
    	return new Greeting(counter.incrementAndGet(),
                            String.format(template, reversed));
    }
    
    private Greeting fallbackGreeting(String name) {
    	
    	return new Greeting(counter.incrementAndGet(),
                String.format(template, name + "-fallback"));
    	
    }
}
