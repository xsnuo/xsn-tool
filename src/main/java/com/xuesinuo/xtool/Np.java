package com.xuesinuo.xtool;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 一个 Java8 Optional 类的简易版本。
 * <p>
 * A simplified version of Java8 Optional class.
 * <p>
 * 相比Optional，只保留了高频使用的赋值、取值、函数执行。
 * <p>
 * Compared with Optional, only high-frequency used assignment, value acquisition, function execution are retained.
 * <p>
 * 还加入了{@link Comparable}的大小比较，字符的isBlank()等方法。
 * <p>
 * Also added {@link Comparable} size comparison, character isBlank() methods, etc.
 * <p>
 * Np尽可能压缩方法名长度，以减少代码行占用。
 * <p>
 * Np tries to compress the method name length to reduce code line usage.
 * 
 * @author xuesinuo
 * @since 1.0.0
 */
public class Np<T> {
    private Np() {}

    private T t;

    /**
     * 输入一个值。
     * <p>
     * Input a value.
     */
    public static <T> Np<T> i(T t) {
        Np<T> np = new Np<T>();
        np.t = t;
        return np;
    }

    /**
     * 执行一个函数。
     * <p>
     * Execute a function.
     */
    public Np<T> doo(Consumer<T> consumer) {
        if (this.t != null) {
            consumer.accept(this.t);
        }
        return this;
    }

    /**
     * 多次重复执行一个函数。
     * <p>
     * Repeat execution of a function.
     */
    public Np<T> doo(Consumer<T> consumer, int repeat) {
        if (this.t != null) {
            for (int i = 0; i < repeat; i++) {
                consumer.accept(this.t);
            }
        }
        return this;
    }

    /**
     * 执行函数，并获得结果（连续调用）。
     * <p>
     * Execute a function and get the result (continuous call).
     */
    public <R> Np<R> x(Function<T, R> function) {
        Np<R> np = new Np<R>();
        if (this.t != null) {
            R r = function.apply(this.t);
            np.t = r;
        }
        return np;
    }

    /**
     * 满足条件，则修改值，并继续Np操作。
     * <p>
     * If the condition is met, modify the value and continue Np operation.
     * 
     * <pre>
     * // Ex:
     * if (function_result == true) {
     *     return els;
     * } else {
     *     return this;
     * }
     * </pre>
     */
    public Np<T> els(Function<T, Boolean> function, T els) {
        if (this.t != null) {
            Boolean r = function.apply(this.t);
            if (r != null && r == true) {
                Np<T> np = new Np<T>();
                np.t = els;
                return np;
            }
        }
        return this;
    }

    /**
     * 如果为null，则设置一个默认值，并继续Np操作。
     * <p>
     * If it is null, set a default value and continue Np operation.
     */
    public Np<T> def(T def) {
        if (this.t == null) {
            Np<T> np = new Np<T>();
            np.t = def;
            return np;
        }
        return this;
    }

    /**
     * 如果字符串内容是空，则设置一个默认值，并继续Np操作。
     * <p>
     * If the string content is empty, set a default value and continue Np operation.
     */
    public Np<T> ifBlank(T def) {
        if (this.t == null || this.t.toString() == null || this.t.toString().trim().length() == 0) {
            Np<T> np = new Np<T>();
            np.t = def;
            return np;
        }
        return this;
    }

    /**
     * 输出当前值。
     * <p>
     * Output the current value.
     */
    public T o() {
        return t;
    }

    /**
     * 输出，如果是null，则设置一个默认值。
     * <p>
     * Output, if it is null, set a default value.
     */
    public T o(T def) {
        if (t == null) {
            return def;
        }
        return t;
    }

    /**
     * 是否为null。
     * <p>
     * Whether it is null.
     */
    public boolean isNull() {
        return t == null;
    }

    /**
     * 是否不为null。
     * <p>
     * Whether it is not null.
     */
    public boolean notNull() {
        return t != null;
    }

    /**
     * 判断两值是否相等，不仅是.equals()。
     * <p>
     * Judge whether the two values are equal, not only .equals().
     * <p>
     * NULL与NULL相等。
     * <p>
     * NULL is equals NULL.
     * <p>
     * 按.equals()判断对象是否相等。
     * <p>
     * Compare objects for equality by .equals().
     * <p>
     * 可比较类型（{@link Comparable}），按.compareTo()比较。
     * <p>
     * Comparable type ({@link Comparable}), compare by .compareTo().
     */
    public boolean eq(T target) {
        if (t == null && target == null) {
            return true;
        }
        if (t == null) {
            return false;
        }
        if (target == null) {
            return false;
        }
        if (t.equals(target)) {
            return true;
        } else {
            Integer compareToResult = Np.compareTo(t, target);
            if (compareToResult != null && compareToResult == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否不相等，和.eq()相反。
     * <p>
     * Whether it is not equal, opposite to .eq().
     */
    public boolean notEq(T target) {
        return !this.eq(target);
    }

    /**
     * 是否在目标集合中，和.eq()相同判断规则。
     * <p>
     * Whether it is in the target collection, the same judgment rule as .eq().
     */
    public boolean in(@SuppressWarnings("unchecked") T... target) {
        if (target == null) {
            return false;
        }
        for (T itemObject : target) {
            if (this.eq(itemObject)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否在目标集合中，和.eq()相同判断规则。
     * <p>
     * Whether it is in the target collection, the same judgment rule as .eq().
     */
    public boolean in(Collection<T> target) {
        if (target == null) {
            return false;
        }
        for (T itemObject : target) {
            if (this.eq(itemObject)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否不在目标集合中，和.in()相反。
     * <p>
     * Whether it is not in the target collection, opposite to .in().
     */
    public boolean notIn(@SuppressWarnings("unchecked") T... target) {
        return !this.in(target);
    }

    /**
     * 是否在目标集合中，和.eq()相同判断规则。
     * <p>
     * Whether it is in the target collection, the same judgment rule as .eq().
     */
    public boolean notIn(Collection<T> target) {
        return !this.in(target);
    }

    /**
     * 是否大于（仅限可比较的类型）。
     * <p>
     * Whether it is greater than (only for comparable types).
     */
    public boolean gt(T target) {
        if (t == null) {
            return false;
        }
        if (target == null) {
            return false;
        }
        Integer compareToResult = Np.compareTo(t, target);
        if (compareToResult != null && compareToResult > 0) {
            return true;
        }
        return false;
    }

    /**
     * 是否大于等于（仅限可比较的类型）。
     * <p>
     * Whether it is greater than or equal to (only for comparable types).
     */
    public boolean ge(T target) {
        if (t == null && target == null) {
            return true;
        }
        if (t == null) {
            return false;
        }
        if (target == null) {
            return false;
        }
        Integer compareToResult = Np.compareTo(t, target);
        if (compareToResult != null && compareToResult >= 0) {
            return true;
        }
        return false;
    }

    /**
     * 是否小于（仅限可比较的类型）。
     * <p>
     * Whether it is less than (only for comparable types).
     */
    public boolean lt(T target) {
        if (t == null) {
            return false;
        }
        if (target == null) {
            return false;
        }
        Integer compareToResult = Np.compareTo(t, target);
        if (compareToResult != null && compareToResult < 0) {
            return true;
        }
        return false;
    }

    /**
     * 是否小于等于（仅限可比较的类型）。
     * <p>
     * Whether it is less than or equal to (only for comparable types).
     */
    public boolean le(T target) {
        if (t == null && target == null) {
            return true;
        }
        if (t == null) {
            return false;
        }
        if (target == null) {
            return false;
        }
        Integer compareToResult = Np.compareTo(t, target);
        if (compareToResult != null && compareToResult <= 0) {
            return true;
        }
        return false;
    }

    /**
     * 开关串行判断。
     * <p>
     * Switch serial judgment.
     * <p>
     * <b>注意：和&&一样会熔断</b>
     * <p>
     * <b>Note: It will be interrupted like &&.</b>
     */
    public boolean and(Function<Np<T>, Boolean> functionA, Function<Np<T>, Boolean> functionB) {
        Boolean a = functionA.apply(this);
        if (a == null || a == false) {
            return false;
        }
        Boolean b = functionB.apply(this);
        if (b == null || b == false) {
            return false;
        }
        return true;
    }

    /**
     * 开关并行判断。
     * <p>
     * Switch parallel judgment.
     * <p>
     * <b>注意：和||一样会熔断</b>
     * <p>
     * <b>Note: It will be interrupted like ||.</b>
     */
    public boolean or(Function<Np<T>, Boolean> functionA, Function<Np<T>, Boolean> functionB) {
        Boolean a = functionA.apply(this);
        if (a != null && a == true) {
            return true;
        }
        Boolean b = functionB.apply(this);
        if (b != null && b == true) {
            return true;
        }
        return false;
    }

    /**
     * 将Np内容进行toString；如果是NULL，则直接返回NULL。
     * <p>
     * The Np content is converted to toString; if it is NULL, it is directly returned NULL.
     */
    public String toString() {
        if (t == null) {
            return null;
        }
        return t.toString();
    }

    /**
     * 将Np内容进行toString；如果内容是NULL，则设置默认值。
     * <p>
     * The Np content is converted to toString; if the content is NULL, set the default value.
     */
    public String toString(String def) {
        if (t == null) {
            return def;
        }
        return t.toString();
    }

    private static Integer compareTo(Object obj1, Object obj2) {
        if (obj1 instanceof Comparable<?> && obj2 instanceof Comparable<?>) {
            @SuppressWarnings("unchecked")
            Comparable<Object> objComparable = (Comparable<Object>) obj1;
            return objComparable.compareTo(obj2);
        }
        return null;
    }

}
