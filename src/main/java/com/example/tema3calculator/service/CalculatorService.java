package com.example.tema3calculator.service;

import com.example.tema3calculator.model.Calculator;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class CalculatorService {
    private Map<String, List<Boolean>> statusMap = new HashMap<>();

    public Double calculate(Calculator calculator) {
        simulateDelay();

        if (calculator == null) {
            throw new IllegalArgumentException("object null");
        }

        String operator = calculator.getOperator();
        if (operator == null) {
            throw new IllegalArgumentException("operator null");
        }

        List<Double> operands = calculator.getOperands();
        if (operands == null || operands.size() < 2) {
            throw new IllegalArgumentException("more operands needed");
        }

        switch (operator) {
            case "sum":
                return operands.get(0) + operands.get(1);
            case "sub":
                return operands.get(0) - operands.get(1);
            case "mul":
                return operands.get(0) * operands.get(1);
            case "div":
                if (operands.get(1) == 0) {
                    throw new ArithmeticException("error div by zero");
                }
                return operands.get(0) / operands.get(1);
            default:
                throw new IllegalArgumentException("error");
        }

    }

    public void simulateDelay(){
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void saveToFile(String filename, List<Double> results) throws IOException {
        Path path = Paths.get(filename);

        List<String> stringResults = new ArrayList<>();
        for (Double result : results) {
            stringResults.add(String.valueOf(result));
        }
        Files.write(path, stringResults);
    }


    public List<Double> readFile(String filename) throws IOException {
        Path path = Paths.get(filename);

        if (!Files.exists(path)) {
            return null;
        }

        List<String> lines = Files.readAllLines(path);

        List<Double> results = new ArrayList<>();
        for (String line : lines) {
            results.add(Double.valueOf(line));
        }

        return results;
    }


    public void setStatusList(String filename, int size) {
        List<Boolean> statusList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            statusList.add(false);
        }
        statusMap.put(filename, statusList);
    }

    public boolean checkFinished(String filename) {
        List<Boolean> statusList = statusMap.get(filename);
        if (statusList == null) {
            return false;
        }
        return !statusList.contains(false);
    }

    public void updateStatus(String filename, int index, boolean status) {
        List<Boolean> statusList = statusMap.get(filename);
        if (statusList != null && index >= 0 && index < statusList.size()) {
            statusList.set(index, status);
        }
    }

}
