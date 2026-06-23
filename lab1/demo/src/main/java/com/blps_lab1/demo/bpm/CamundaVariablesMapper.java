package com.blps_lab1.demo.bpm;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class CamundaVariablesMapper {
    private CamundaVariablesMapper() {
    }

    public static Map<String, Object> toCamundaVariables(Map<String, Object> variables) {
        Map<String, Object> result = new HashMap<>();
        if (variables == null) {
            return result;
        }
        variables.forEach((key, value) -> result.put(key, Map.of(
                "value", normalizeValue(value),
                "type", camundaType(value)
        )));
        return result;
    }

    public static Map<String, Object> fromCamundaVariables(Object rawVariables) {
        Map<String, Object> result = new HashMap<>();
        if (!(rawVariables instanceof Map<?, ?> variables)) {
            return result;
        }
        variables.forEach((key, wrapped) -> {
            if (key != null && wrapped instanceof Map<?, ?> wrappedMap && wrappedMap.containsKey("value")) {
                result.put(String.valueOf(key), wrappedMap.get("value"));
            }
        });
        return result;
    }

    public static Map<String, Object> fromJsonVariables(JsonNode variablesNode) {
        Map<String, Object> result = new HashMap<>();
        if (variablesNode == null || !variablesNode.isObject()) {
            return result;
        }
        Iterator<Map.Entry<String, JsonNode>> fields = variablesNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            JsonNode valueNode = field.getValue().get("value");
            if (valueNode == null || valueNode.isNull()) {
                result.put(field.getKey(), null);
            } else if (valueNode.isNumber()) {
                result.put(field.getKey(), valueNode.numberValue());
            } else if (valueNode.isBoolean()) {
                result.put(field.getKey(), valueNode.booleanValue());
            } else {
                result.put(field.getKey(), valueNode.asText());
            }
        }
        return result;
    }

    private static Object normalizeValue(Object value) {
        if (value instanceof LocalDate localDate) {
            return localDate.toString();
        }
        return value;
    }

    private static String camundaType(Object value) {
        if (value instanceof Boolean) {
            return "Boolean";
        }
        if (value instanceof Integer || value instanceof Long) {
            return "Long";
        }
        if (value instanceof Number) {
            return "Double";
        }
        return "String";
    }
}
