package com.vecizervi.backend.controller; // Reemplaza "tu_paquete" con el nombre real de tu proyecto

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String healthCheck() {
        return "¡La API de VeciZervi está funcionando correctamente en la nube!";
    }
}