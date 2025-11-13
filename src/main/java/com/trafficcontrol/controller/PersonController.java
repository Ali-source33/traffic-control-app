package com.trafficcontrol.controller;

import com.trafficcontrol.entity.Person;
import com.trafficcontrol.entity.User;
import com.trafficcontrol.entity.SearchLog;
import com.trafficcontrol.service.PersonService;
import com.trafficcontrol.service.UserService;
import com.trafficcontrol.service.SearchLogService;
import com.trafficcontrol.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;
    private final UserService userService;
    private final SearchLogService searchLogService;
    private final JwtUtil jwtUtil;

    public PersonController(PersonService personService, UserService userService,
                            SearchLogService searchLogService, JwtUtil jwtUtil) {
        this.personService = personService;
        this.userService = userService;
        this.searchLogService = searchLogService;
        this.jwtUtil = jwtUtil;
    }

    private User getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtUtil.extractUsername(token);
        return userService.getUserByUsername(username);
    }

    private boolean isAdmin(HttpServletRequest request) {
        User user = getCurrentUser(request);
        return user != null && user.getRole().getName().equalsIgnoreCase("ADMIN");
    }

    private boolean isKolluk(HttpServletRequest request) {
        User user = getCurrentUser(request);
        return user != null && user.getRole().getName().equalsIgnoreCase("KOLLUK");
    }

    @PostMapping
    public ResponseEntity<?> addPerson(@RequestBody Person person, HttpServletRequest request) {
        if (!isAdmin(request)) return ResponseEntity.status(403).body("Yetkisiz erişim");
        personService.createPerson(person);
        return ResponseEntity.ok("Person eklenmiştir");
    }

    @PutMapping
    public ResponseEntity<?> updatePerson(@RequestBody Person person, HttpServletRequest request) {
        if (!isAdmin(request)) return ResponseEntity.status(403).body("Yetkisiz erişim");
        try {
            personService.updatePersonByTc(person);
            return ResponseEntity.ok("Person güncellendi");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/{tcKimlik}")
    public ResponseEntity<?> deletePerson(@PathVariable String tcKimlik, HttpServletRequest request) {
        if (!isAdmin(request)) return ResponseEntity.status(403).body("Yetkisiz erişim");
        Person person = personService.getPersonByTc(tcKimlik);
        if (person == null) return ResponseEntity.notFound().build();
        personService.deletePerson(person);
        return ResponseEntity.ok("Person silindi");
    }

    @GetMapping("/{tcKimlik}")
    public ResponseEntity<?> getPersonByTc(@PathVariable String tcKimlik, HttpServletRequest request) {
        if (!isKolluk(request) && !isAdmin(request))
            return ResponseEntity.status(403).body("Yetkisiz erişim");

        Person person = personService.getPersonByTc(tcKimlik);
        if (person == null) return ResponseEntity.notFound().build();

        User currentUser = getCurrentUser(request);
        SearchLog log = new SearchLog();
        log.setPerformedBy(currentUser);
        log.setPerson(person);
        log.setSearchType("TC");
        log.setSearchValue(tcKimlik);
        log.setResultSummary("Kayıt bulundu");
        searchLogService.logSearch(log);

        return ResponseEntity.ok(person);
    }


    @GetMapping
    public ResponseEntity<?> getAllPersons(HttpServletRequest request) {
        if (!isKolluk(request) && !isAdmin(request)) return ResponseEntity.status(403).body("Yetkisiz erişim");
        List<Person> persons = personService.getAllPersons();

        User currentUser = getCurrentUser(request);
        SearchLog log = new SearchLog();
        log.setPerformedBy(currentUser);
        log.setSearchType("TÜM KİŞİLER");
        log.setSearchValue("N/A");
        log.setResultSummary("Toplam kayıt sayısı: " + persons.size());
        searchLogService.logSearch(log);

        return ResponseEntity.ok(persons);
    }
}
