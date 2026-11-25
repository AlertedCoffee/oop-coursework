package coursework.lab2;

import coursework.io.LineWriter;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public final class MethodInvoker {

    private MethodInvoker() {
    }

    public static <T> T create(Class<T> type, SampleValues samples, LineWriter out) {
        if (type == null) {
            throw new IllegalArgumentException("Нужен класс");
        }
        if (samples == null) {
            throw new IllegalArgumentException("Нужны образцы аргументов");
        }
        if (out == null) {
            throw new IllegalArgumentException("Вывод не задан");
        }
        if (type.isPrimitive()) {
            throw new IllegalArgumentException("Примитив не создаётся конструктором: " + type.getName());
        }
        return type.cast(argumentFor(type, samples, out, new HashSet<Class<?>>()));
    }

    public static void invokeAnnotatedHidden(Object target, LineWriter out, SampleValues samples) {
        if (target == null) {
            throw new IllegalArgumentException("Нужен объект, у которого вызываются методы");
        }
        if (out == null) {
            throw new IllegalArgumentException("Вывод не задан");
        }
        if (samples == null) {
            throw new IllegalArgumentException("Нужны образцы аргументов");
        }

        Method[] methods = target.getClass().getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName));

        for (Method method : methods) {
            Repeat repeat = method.getAnnotation(Repeat.class);
            if (repeat == null) {
                continue;
            }

            int modifiers = method.getModifiers();
            if (!Modifier.isProtected(modifiers) && !Modifier.isPrivate(modifiers)) {
                out.write(String.format(
                        "Пропуск %s: аннотация есть, видимость %s",
                        method.getName(),
                        visibility(modifiers)));
                continue;
            }
            if (Modifier.isStatic(modifiers)) {
                throw new IllegalStateException("Статические методы не вызываются: " + method.getName());
            }

            int times = repeat.value();
            if (times < 0) {
                throw new IllegalArgumentException("Отрицательное число повторов у " + method.getName());
            }

            method.setAccessible(true);
            out.write(String.format(
                    "%s %s — %d раз(а)",
                    visibility(modifiers),
                    method.getName(),
                    times));

            for (int i = 0; i < times; i++) {
                Object[] args = argumentsFor(method.getParameterTypes(), samples, out, new HashSet<Class<?>>());
                try {
                    method.invoke(target, args);
                } catch (Exception e) {
                    Throwable cause = e.getCause() == null ? e : e.getCause();
                    String detail = cause.getMessage() == null
                            ? cause.getClass().getSimpleName()
                            : cause.getMessage();
                    throw new IllegalStateException("Ошибка в " + method.getName() + ": " + detail, cause);
                }
            }
        }
    }

    private static String visibility(int modifiers) {
        if (Modifier.isPublic(modifiers)) {
            return "public";
        }
        if (Modifier.isProtected(modifiers)) {
            return "protected";
        }
        if (Modifier.isPrivate(modifiers)) {
            return "private";
        }
        return "package";
    }

    private static Object[] argumentsFor(
            Class<?>[] types,
            SampleValues samples,
            LineWriter out,
            Set<Class<?>> stack) {
        Object[] args = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            args[i] = argumentFor(types[i], samples, out, stack);
        }
        return args;
    }

    private static Object argumentFor(
            Class<?> type,
            SampleValues samples,
            LineWriter out,
            Set<Class<?>> stack) {
        if (type == boolean.class || type == Boolean.class) {
            return samples.flag();
        }
        if (type == byte.class || type == Byte.class) {
            return (byte) samples.number();
        }
        if (type == short.class || type == Short.class) {
            return (short) samples.number();
        }
        if (type == int.class || type == Integer.class) {
            return samples.number();
        }
        if (type == long.class || type == Long.class) {
            return (long) samples.number();
        }
        if (type == float.class || type == Float.class) {
            return (float) samples.real();
        }
        if (type == double.class || type == Double.class) {
            return samples.real();
        }
        if (type == char.class || type == Character.class) {
            return 'A';
        }
        if (type == String.class) {
            return samples.text();
        }
        if (type == Fuel.class) {
            return samples.fuel();
        }
        if (type.isEnum()) {
            Object[] constants = type.getEnumConstants();
            if (constants.length == 0) {
                throw new IllegalArgumentException("У перечисления нет констант: " + type.getName());
            }
            return constants[0];
        }
        if (type.isArray()) {
            return Array.newInstance(type.getComponentType(), 0);
        }
        return instantiate(type, samples, out, stack);
    }

    private static Object instantiate(
            Class<?> type,
            SampleValues samples,
            LineWriter out,
            Set<Class<?>> stack) {
        if (stack.contains(type)) {
            throw new IllegalArgumentException("Циклический конструктор: " + type.getName());
        }
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            throw new IllegalArgumentException("Нельзя создать экземпляр " + type.getName());
        }

        Constructor<?>[] constructors = type.getDeclaredConstructors();
        Arrays.sort(constructors, Comparator.comparingInt(Constructor::getParameterCount));

        ReflectiveOperationException failure = null;
        IllegalArgumentException rejected = null;
        for (Constructor<?> constructor : constructors) {
            stack.add(type);
            try {
                constructor.setAccessible(true);
                Class<?>[] params = constructor.getParameterTypes();
                Object[] args = argumentsFor(params, samples, out, stack);
                Object instance = constructor.newInstance(args);
                out.write(String.format(
                        "Вызов конструктора %s, параметров: %d",
                        type.getSimpleName(),
                        params.length));
                return instance;
            } catch (IllegalArgumentException e) {
                rejected = e;
            } catch (ReflectiveOperationException e) {
                failure = e;
            } finally {
                stack.remove(type);
            }
        }

        if (failure != null) {
            throw new IllegalArgumentException(
                    "Нельзя создать " + type.getName() + " без null",
                    failure);
        }
        if (rejected != null) {
            throw rejected;
        }
        throw new IllegalArgumentException("Нет конструктора у " + type.getName());
    }
}
