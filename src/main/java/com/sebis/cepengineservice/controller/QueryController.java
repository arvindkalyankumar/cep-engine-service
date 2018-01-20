package com.sebis.cepengineservice.controller;

import com.sebis.cepengineservice.dto.QueryDto;
import com.sebis.cepengineservice.service.MappedSpanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/query")
public class QueryController {
    private MappedSpanService service;

    @Autowired
    public QueryController(MappedSpanService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> query(@Valid @RequestBody QueryDto query) {
        return service.query(query);
    }
}
