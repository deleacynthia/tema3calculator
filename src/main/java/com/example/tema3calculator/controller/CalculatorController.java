package com.example.tema3calculator.controller;

import com.example.tema3calculator.model.Calculator;
import com.example.tema3calculator.service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api")
public class CalculatorController {

    @Autowired
    private CalculatorService calculatorService;

    @PostMapping("/do-math")
    public ResponseEntity<String> doMath(@RequestBody List<Calculator> calculators) {
        String filename = UUID.randomUUID().toString() + ".txt";
        calculatorService.setStatusList(filename, calculators.size());

        List<Double> results = initResults(calculators.size());

        for (int i = 0; i < calculators.size(); i++) {
            Calculator calculator = calculators.get(i);
            int aux = i;
            Thread t = new Thread(() -> {
                Double result = calculatorService.calculate(calculator);
                results.set(aux, result);
                calculatorService.updateStatus(filename, aux, true);

                if (!results.contains(null)) {
                    try {
                        calculatorService.saveToFile(filename, results);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }

        return ResponseEntity.ok(filename);
    }
    private List<Double> initResults(int size) {
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(null);
        }
        return list;
    }
    @GetMapping("/check-finished/{filename}")
    public ResponseEntity<Boolean> checkFinished(@PathVariable String filename) {
        return ResponseEntity.ok(calculatorService.checkFinished(filename));
    }

    @GetMapping("/results/{filename}")
    public ResponseEntity<Object> getResults(@PathVariable String filename) {

        if(!calculatorService.checkFinished(filename)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("calculator in process");
        }

        try {
            List<Double> results = calculatorService.readFile(filename);
            if (results != null) {
                return ResponseEntity.ok(results);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("res not found");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
        }
    }


}

