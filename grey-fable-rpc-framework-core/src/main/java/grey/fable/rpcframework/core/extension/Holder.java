package grey.fable.rpcframework.core.extension;

import java.util.concurrent.atomic.AtomicReference;

public class Holder<T> {

    private final AtomicReference<T> value = new AtomicReference<>();

    public void set(T value) {
        this.value.set(value);
    }

    public T get() {
        return this.value.get();
    }
}
