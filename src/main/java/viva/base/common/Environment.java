package viva.base.common;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public class Environment {
    private Map<String, Object> variables;
    
    @Getter
    private Environment parent;

    public Environment(Environment parent) {
        this.variables = new HashMap<>();
        
        this.parent = parent;
    }

    public void set(String k, Object v) {
        variables.computeIfAbsent(k, f -> v);
    }

    public Object get(String k) {
        Environment env = this;
        
        while (env != null) {
            if (env.variables.containsKey(k)) {
                return env.variables.get(k);
            } else {
                env = env.parent;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return variables.toString();
    }
}
