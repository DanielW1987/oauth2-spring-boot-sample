package rocks.danielw.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class DemoRestController {

    @GetMapping(path = "/api/v1/endpoint-one")
    @ResponseStatus(OK)
    @PreAuthorize("hasAuthority('SCOPE_endpoint-one')")
    public String endpointOne() {
        return "Hello from Endpoint 1";
    }

    @GetMapping(path = "/api/v1/endpoint-two")
    @ResponseStatus(OK)
    @PreAuthorize("hasAuthority('SCOPE_endpoint-two')")
    public String endpointTwo() {
        return "Hello from Endpoint 2";
    }
}
