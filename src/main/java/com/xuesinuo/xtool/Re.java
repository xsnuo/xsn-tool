package com.xuesinuo.xtool;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Re: Return and Runtime Exception。调用一个函数时，这个函数可能可能会有return和Exception两种可能，若需要对两种可能都加以判断，本类可以简化代码。
 * 
 * @author xuesinuo
 */
public class Re<R> {
    private Re() {}

    private R r;

    private RuntimeException e;

    /**
     * 返回方式
     * 
     * @author xuesinuo
     */
    public enum Mode {
        /** Return */
        R,
        /** Exception */
        E;
    }

    public static <R> Re<R> i(R r, RuntimeException e) {
        Re<R> re = new Re<>();
        re.r = r;
        re.e = e;
        return re;
    }

    public static <R> Re<R> x(Supplier<R> supplier) {
        Re<R> re = new Re<>();
        try {
            re.r = supplier.get();
        } catch (RuntimeException e) {
            re.e = e;
        }
        return re;
    }

    public R o(Mode m) {
        if (m == Mode.E) {
            throw this.e;
        }
        return this.r;
    }

    public R r() {
        return r;
    }

    public Re<R> r(Consumer<R> consumer) {
        if (e == null) {
            consumer.accept(r);
        }
        return this;
    }

    public RuntimeException e() {
        return e;
    }

    public Re<R> e(Consumer<RuntimeException> consumer) {
        if (e != null) {
            consumer.accept(e);
        }
        return this;
    }
}