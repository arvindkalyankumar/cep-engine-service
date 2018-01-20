package com.sebis.cepengineservice.controller;

import com.sebis.cepengineservice.dto.StoredQueryDto;
import com.sebis.cepengineservice.service.StoredQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@CrossOrigin
@RestController
@RequestMapping(
        value = "/api/stored-query",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class StoredQueryController {
    private StoredQueryService storedQueryService;

    @Autowired
    public StoredQueryController(StoredQueryService storedQueryService) {
        this.storedQueryService = storedQueryService;
    }

    @GetMapping(value = "/{id}")
    public StoredQueryDto findOne(@PathVariable String id) {
        return storedQueryService.findOne(id);
    }

    @GetMapping
    public Collection<StoredQueryDto> findAll() {
        return storedQueryService.findAll();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String save(@RequestBody @Valid StoredQueryDto dto) {
        return storedQueryService.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        storedQueryService.delete(id);
    }
}
