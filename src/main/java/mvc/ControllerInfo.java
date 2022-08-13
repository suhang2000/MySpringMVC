package mvc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControllerInfo {
    private Class<?> controllerClass;

    private Method invokeMethod;

    // method parameter -> parameter type
    private Map<String, Class<?>> methodParameter;
}
