package com.trafficcontrol.controller;

import com.trafficcontrol.entity.Person;
import com.trafficcontrol.entity.User;
import com.trafficcontrol.entity.SearchLog;
import com.trafficcontrol.exception.PersonNotFoundException;
import com.trafficcontrol.exception.UnauthorizedException;
import com.trafficcontrol.service.PersonService;
import com.trafficcontrol.service.UserService;
import com.trafficcontrol.service.SearchLogService;
import com.trafficcontrol.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;
    private final UserService userService;
    private final SearchLogService searchLogService;
    private final JwtUtil jwtUtil;

    @Autowired
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

    private void checkAdmin(HttpServletRequest request) {
        if (!isAdmin(request)) throw new UnauthorizedException("Yetkisiz erişim");
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
        checkAdmin(request);
        personService.createPerson(person);
        return ResponseEntity.ok("Person eklenmiştir");
    }

    @GetMapping("/{tcKimlik}")
    public ResponseEntity<?> getPersonByTc(@PathVariable String tcKimlik, HttpServletRequest request) {
        if (!isKolluk(request) && !isAdmin(request)) throw new UnauthorizedException("Yetkisiz erişim");

        Person person = personService.getPersonByTc(tcKimlik);
        if (person == null) throw new PersonNotFoundException("Kişi bulunamadı: " + tcKimlik);

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

    @PutMapping
    public ResponseEntity<?> updatePerson(@RequestBody Person person, HttpServletRequest request) {
        checkAdmin(request);
        Person existingPerson = personService.getPersonByTc(person.getTcKimlik());
        if (existingPerson == null) throw new PersonNotFoundException("Kişi bulunamadı: " + person.getTcKimlik());

        personService.updatePersonByTc(person);
        return ResponseEntity.ok("Person güncellendi");
    }

    @PatchMapping("/{tcKimlik}/wanted")
    public ResponseEntity<?> updatePersonWanted(@PathVariable String tcKimlik,
                                                @RequestBody Map<String, Boolean> updates,
                                                HttpServletRequest request) {
        checkAdmin(request);

        Person person = personService.getPersonByTc(tcKimlik);
        if (person == null) throw new PersonNotFoundException("Kişi bulunamadı: " + tcKimlik);

        if (!updates.containsKey("isWanted")) {
            return ResponseEntity.badRequest().body("isWanted alanı gönderilmedi");
        }

        person.setIsWanted(updates.get("isWanted"));
        personService.updatePersonByTc(person);

        return ResponseEntity.ok(Map.of(
            "tcKimlik", person.getTcKimlik(),
            "isWanted", person.getIsWanted()
        ));
    }

    @DeleteMapping("/{tcKimlik}")
    public ResponseEntity<?> deletePerson(@PathVariable String tcKimlik, HttpServletRequest request) {
        checkAdmin(request);

        Person person = personService.getPersonByTc(tcKimlik);
        if (person == null) throw new PersonNotFoundException("Kişi bulunamadı: " + tcKimlik);

        personService.deletePerson(person);
        return ResponseEntity.ok("Person silindi");
    }
}
