package com.rpicloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.*;

@SpringBootApplication
@RestController
@EnableRedisHttpSession
public class CasestudyResourceServiceApplication extends WebSecurityConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(CasestudyResourceServiceApplication.class, args);
	}

	private String message = "Hello World";

    public ArrayList getChanges() {
        return changes;
    }

    public void setChanges(ArrayList changes) {
        this.changes = changes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private ArrayList changes = new ArrayList<>();

	@RequestMapping(value="/", method= RequestMethod.GET)
	public Map<String,Object> home() {
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("id", UUID.randomUUID().toString());
		model.put("content", message);
		return model;
	}

	@RequestMapping(value="/changes", method=RequestMethod.GET)
	public ArrayList changes() {
		return getChanges();
	}

	@RequestMapping(value="/", method=RequestMethod.POST)
	public Map<String, Object> update(@RequestBody Map<String,String> map, Principal principal) {
		if (map.containsKey("content")) {
			message = map.get("content");
            Map<String, Object> model = new HashMap<>();
            model.put("timestamp", new Date());
            model.put("user", principal.getName());
            model.put("content", message);
            changes.add(model);
		}
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", message);
        return model;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// We need this to prevent the browser from popping up a dialog on a 401
		http.httpBasic().disable();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/**").hasRole("WRITER").anyRequest().authenticated();
	}

	@Autowired
	public void setEnvironment(Environment e){
		System.out.println(e.getProperty("configuration.projectName"));
	}
}

@RestController
@RefreshScope
class ProjectNameRestController {
	@Value("${configuration.projectName}")
	String projectName;

	@RequestMapping("/project-name")
	String projectName(){
		return this.projectName;
	}
}