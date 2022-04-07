package com.example.CourseRegistration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @Autowired
    UserRepo repo;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    MyUserDetailsService userService;

    @Autowired
    JwtToken jwtToken;

    PasswordEncoder passwordEncoder;

    @RequestMapping("/")
    public String homePage() {
        return "home";
    }

    @RequestMapping("/signin")
    public String signin() {
        return "signin";
    }

    @RequestMapping("/signup")
    public String signup() {
        return "signup";
    }

    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello";
    }

    @RequestMapping("/addUser")
    @ResponseBody
    public String addUser(ModelMap map, @RequestBody Users user) {
       
        this.passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(user.getEmail());
       
            String encoded = this.passwordEncoder.encode(user.getPassword());
            user.setPassword(encoded);
            repo.save(user);
            return "success";
        

    }

    @RequestMapping("/authuser")
    public String authUser(Model md, ModelMap model, @RequestParam("username") String username,
            @RequestParam("password") String password) {

        Users auth = repo.findByEmailAndPassword(username, password);
        System.out.println(auth);
        if (auth == null) {
            model.put("error", "Please provide correct username and password");
            return "signin";
        } else {
            System.out.println("Found");
            md.addAttribute("user", auth.getFirstname());
            return "auth";

        }
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createToken(@RequestBody JwtRequest user) throws Exception {
        System.out.println("auth"+ user.getPassword());
        authenticate(user.getUsername(), user.getPassword());
        UserDetails userDetail = userService.loadUserByUsername(user.getUsername());
        String token = jwtToken.generateToken(userDetail);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            throw new Exception("Disabled", e);
        }
    }

    // @RequestMapping("/getUser")
    // public String getUser(@RequestParam("username") String name, Model m) {
    // m.addAttribute("result", repo.findByFirstname(name));
    // return "display";
    // }
}
