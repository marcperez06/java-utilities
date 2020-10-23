/*
 * @author Aleix Marques Casanovas
 */
package io.github.marcperez06.java_utilities.api.request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ResponseTypeHolder<T> implements Comparable<ResponseTypeHolder<T>> {
    protected final Type type;

    public ResponseTypeHolder() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class) {
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        } else {
            this.type = ((ParameterizedType)superClass).getActualTypeArguments()[0];
        }
    }

    public Type getType() {
        return this.type;
    }

    public int compareTo(ResponseTypeHolder<T> o) {
        return 0;
    }
    
}