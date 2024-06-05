package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.servicios.IOPServicio;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
public class AdminCotrolador {


    @Autowired
    private IOPServicio iOPServicio;

    @GetMapping("/dashboard")
    public String panelAdmin() {
        return "panel_admin.html";
    }

    @PostMapping("/importIOP")
    public String importarIOP(@RequestParam("fileExcel") MultipartFile fileExcel) throws IOException {
        iOPServicio.importarIOP(fileExcel.getInputStream());
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/exportIOP")
    public ResponseEntity<InputStreamResource> exportIOP() throws Exception {
        ByteArrayInputStream stream = iOPServicio.exportarIOP();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=IOP-Cba.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }
}
