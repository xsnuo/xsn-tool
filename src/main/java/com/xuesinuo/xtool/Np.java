package com.xuesinuo.xtool;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 一个 Java8 Optional 类的简易版本。 推荐用在业务代码，而非框架代码中。
 * <p>
 * 本类视为Optional的一个语法糖，并引入一些常用的小功能。意在简化空指针判断的同时，尽可能让取值、判断、比较等功能在一行代码中完成。使用简写作为方法名也是为了缩短代码长度，在连续调用时尽可能不让代码冗长。
 * </p>
 * 
 * <p>
 * 一个最简单的例子：忽略空指针连续调用多级Getter获取嵌套的属性值：
 * </p>
 * 
 * <pre>
 * // e.g.0 基本用法
 * Np.i(MainPojo).x(x -> x.getChildPojo()).x(x -> x.getName()).o("Default Name");
 * </pre>
 * 
 * <p>
 * .eq()和.gt()、.lt()、.ge()、.le()。 .eq()是判断两个数值大小是否相等，不是简单的.equals()。.gt()等和.eq()使用了相同的判断规则：
 * </p>
 * 
 * <pre>
 * // e.g.1 数值比较
 * // 例如BigDecimal对象中不仅记录了数值，还记录了精度。.eq()只比较数值！
 * BigDecimal x = new BigDecimal("12");
 * BigDecimal y = new BigDecimal("12.0");
 * x.equals(y);// False
 * x.compareTo(y);// 0（表示数值相等）
 * Np.i(x).eq(y);// True，表示数值相等
 * </pre>
 * 
 * <p>
 * .and()和.or()构造一些简单的条件语句（复杂语句也可以构建，但可读性较差），单层and、or可以覆盖大多数场景。
 * </p>
 * 
 * <pre>
 * // e.g.2 and or
 * // 简单的并列条件
 * Double num = 10D;
 * Np.i(num).and(np -> np.ge(0D), np -> np.lt(100D));// 0（含）~100（不含）之间：True
 * Np.i(num).or(np -> np.lt(0D), np -> np.le(100D));// 0~100之外：False。当然，也可以用上句话取反
 * // 复杂的并列条件不建议用Np的.and()、.or()。下面是反例：
 * Np.i(num).or(np -> np.and(np2 -> np2.ge(0D), np2 -> np2.lt(10D)), np.and(np2 -> np2.ge(90D), np2 -> np2.lt(100D)));// 这么写可读性已经很差了：0~10或90~100
 * // 改进方案：
 * Np<Double> np = Np.i(num);// 这里可能经过了连续的取值调用，最终得到一个Np对象
 * if (np.ge(0D) && np.lt(10D) || np.ge(90D) && np.lt(100D)) {
 *     // ...
 * }
 * </pre>
 * 
 * <p>
 * 关于遇到null时比较的特殊说明。
 * </p>
 * 
 * <pre>
 * // e.g.3 空
 * // null与null是相等的
 * Integer x = null;
 * Integer y = null;
 * Np.i(x).eq(y);// True
 * Np.i(x).gt(y);// False
 * Np.i(x).lt(y);// False
 * Np.i(x).ge(y);// True
 * Np.i(x).le(y);// True
 * </pre>
 * 
 * <p>
 * 非Comparable实现不要使用大小比较，可以使用.eq()。
 * </p>
 * 
 * <pre>
 * // e.g.4 对象比较
 * // 非实现Comparable接口的对象无法比较大小，建议不要调用gt、lt、ge、le
 * Pojo x = new Pojo();
 * Pojo y = new Pojo();
 * Np.i(x).eq(y);// 同x.equals(y)
 * Np.i(x).gt(y);// False
 * Np.i(x).lt(y);// False
 * Np.i(x).ge(y);// False：此时即使相等也认定为False，因为此类型不可比较
 * Np.i(x).le(y);// False：此时即使相等也认定为False，因为此类型不可比较
 * // 特殊情况：两个null依然为True
 * x = null;
 * y = null;
 * Np.i(x).ge(y);// True
 * Np.i(x).le(y);// True
 * </pre>
 * 
 * <p>
 * in 与 not in，范围取值。对每个值的判断标准依然遵循.eq()的判断方式。
 * </p>
 * <p>
 * 范围判断没有给Collection参数的，如果判断是否在Collection中，建议使用collection.stream()。这里只是提供一种固定的列举，而不是变量集合的判断。
 * </p>
 * 
 * <pre>
 * // e.g.5 范围判断
 * Integer state = service.getStateFromDatabase();
 * if (Np.i(state).in(null, 1, 3)) {
 *     // state值在空、1、3其中之一
 * }
 * if (Np.i(state).notIn(2, 3)) {
 *     // state值不在2、3其中
 * }
 * </pre>
 * 
 * @since 0.1.0
 * @version 1
 * @author xuesinuo
 */
public class Np<T> {
    private Np() {}

    private T t;

    /**
     * 输入一个值。
     */
    public static <T> Np<T> i(T t) {
        Np<T> np = new Np<T>();
        np.t = t;
        return np;
    }

    /**
     * 执行一个函数。
     */
    public Np<T> doo(Consumer<T> consumer) {
        if (this.t != null) {
            consumer.accept(this.t);
        }
        return this;
    }

    /**
     * 多次重复执行一个函数。
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
     * 判断语句。T：保持原值；F：设置新值。
     * 
     * <pre>
     * // 模拟如下代码
     * if (function == true) {
     *     return this;
     * } else {
     *     return other;
     * }
     * </pre>
     */
    public Np<T> els(Function<T, Boolean> function, T els) {
        if (this.t != null) {
            Boolean r = function.apply(this.t);
            if (r != null && r == true) {
                return this;
            }
        }
        Np<T> np = new Np<T>();
        np.t = els;
        return np;
    }

    /**
     * 如果为null，则设置一个默认值。
     * 
     * <pre>
     * // 模拟如下代码
     * if (this != null) {
     *     return this;
     * } else {
     *     return def;
     * }
     * </pre>
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
     * 调用toString()后如果是null或空内容的字符串，则设置一个默认值。
     */
    public Np<T> ifBlank(T def) {
        if (this.t == null || this.t.toString() == null || this.t.toString().trim().equals("")) {
            Np<T> np = new Np<T>();
            np.t = def;
            return np;
        }
        return this;
    }

    /**
     * 输出
     */
    public T o() {
        return t;
    }

    /**
     * 输出，如果是null，则设置一个默认值。
     */
    public T o(T def) {
        if (t == null) {
            return def;
        }
        return t;
    }

    /**
     * 是否为null。
     */
    public boolean isNull() {
        return t == null;
    }

    /**
     * 是否不为null。
     */
    public boolean notNull() {
        return t != null;
    }

    /**
     * 【注意！不是.equals()】看两个对象（x，y）“数值”是否相等！
     * <p>
     * 相比x.equals(y)增加了：两空值相等；x.compareTo(y)==0相等。
     * </p>
     * 
     * <pre>
     * // e.g.
     * // BigDecimal对象中不仅记录了数值，还记录了精度。eq只比较数值
     * BigDecimal x = new BigDecimal("12");
     * BigDecimal y = new BigDecimal("12.0");
     * x.equals(y); // F
     * x.compareTo(y); // 0
     * Np.i(x).eq(y); // T
     * </pre>
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
     */
    public boolean notEq(T target) {
        return !this.eq(target);
    }

    /**
     * 是否在目标集合中，和.eq()相同判断规则。
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
     * 是否不在目标集合中，和.in()相反。
     */
    public boolean notIn(@SuppressWarnings("unchecked") T... target) {
        return !this.in(target);
    }

    /**
     * 是否大于（仅限可比较的类型）。
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
     * 开关串行判断。 <b>注意：和&&一样会熔断</b>
     * 
     * <p>
     * 此处没有使用动态数组目的：1.消除泛型动态数组引起的警告；2.本意组织一些简单的判断条件，如果太复杂，不建议使用。
     * </p>
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
     * 开关并行判断。 <b>注意：和||一样会熔断</b>
     * 
     * <p>
     * 此处没有使用动态数组目的：1.消除泛型动态数组引起的警告；2.意组织一些简单的判断条件，如果太复杂，不建议使用。
     * </p>
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
     * 将Np内容进行toString；如果内容是null，则直接返回null。
     */
    public String toString() {
        if (t == null) {
            return null;
        }
        return t.toString();
    }

    /**
     * 将Np内容进行toString；如果内容是null，则设置默认值。
     */
    public String toString(String def) {
        if (t == null) {
            return def;
        }
        return t.toString();
    }

    /**
     * 试图对Object调用Comparable的compareTo方法
     */
    private static Integer compareTo(Object obj1, Object obj2) {
        if (obj1 instanceof Comparable<?> && obj2 instanceof Comparable<?>) {
            @SuppressWarnings("unchecked")
            Comparable<Object> objComparable = (Comparable<Object>) obj1;
            return objComparable.compareTo(obj2);
        }
        return null;
    }

}
