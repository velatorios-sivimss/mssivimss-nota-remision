package com.imss.sivimss.notasremision.util;

public final class UtUtils {
    public static class CapturedArgument {
        private Class<?> type;
        private Object value;

        public CapturedArgument(Class<?> type, Object value) {
            this.type = type;
            this.value = value;
        }
    }

    public static Object getUnsafeInstance() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        java.lang.reflect.Field f = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return f.get(null);
    }

    public static Object createInstance(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        return Class.forName("sun.misc.Unsafe").getDeclaredMethod("allocateInstance", Class.class)
                .invoke(getUnsafeInstance(), clazz);
    }

    public static Object[] createArray(String className, int length, Object... values) throws ClassNotFoundException {
        Object array = java.lang.reflect.Array.newInstance(Class.forName(className), length);

        for (int i = 0; i < values.length; i++) {
            java.lang.reflect.Array.set(array, i, values[i]);
        }

        return (Object[]) array;
    }

    public static void setField(Object object, String fieldClassName, String fieldName, Object fieldValue) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = Class.forName(fieldClassName);
        java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);

        java.lang.reflect.Field modifiersField = java.lang.reflect.Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);

        field.setAccessible(true);
        field.set(object, fieldValue);
    }

    public static void setStaticField(Class<?> clazz, String fieldName, Object fieldValue) throws NoSuchFieldException, IllegalAccessException {
        java.lang.reflect.Field field;

        do {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (Exception e) {
                clazz = clazz.getSuperclass();
                field = null;
            }
        } while (field == null);

        java.lang.reflect.Field modifiersField = java.lang.reflect.Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);

        field.setAccessible(true);
        field.set(null, fieldValue);
    }

    public static Object getFieldValue(Object obj, String fieldClassName, String fieldName) throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        Class<?> clazz = Class.forName(fieldClassName);
        java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);

        field.setAccessible(true);
        java.lang.reflect.Field modifiersField = java.lang.reflect.Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);

        return field.get(obj);
    }

    public static Object getStaticFieldValue(Class<?> clazz, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        java.lang.reflect.Field field;
        Class<?> originClass = clazz;
        do {
            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                java.lang.reflect.Field modifiersField = java.lang.reflect.Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);

                return field.get(null);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        } while (clazz != null);

        throw new NoSuchFieldException("Field '" + fieldName + "' not found on class " + originClass);
    }

    public static Object getEnumConstantByName(Class<?> enumClass, String name) throws IllegalAccessException {
        java.lang.reflect.Field[] fields = enumClass.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            String fieldName = field.getName();
            if (field.isEnumConstant() && fieldName.equals(name)) {
                field.setAccessible(true);

                return field.get(null);
            }
        }

        return null;
    }

    static class FieldsPair {
        final Object o1;
        final Object o2;

        public FieldsPair(Object o1, Object o2) {
            this.o1 = o1;
            this.o2 = o2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FieldsPair that = (FieldsPair) o;
            return java.util.Objects.equals(o1, that.o1) && java.util.Objects.equals(o2, that.o2);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(o1, o2);
        }
    }

    public static boolean deepEquals(Object o1, Object o2) {
        return deepEquals(o1, o2, new java.util.HashSet<>());
    }

    private static boolean deepEquals(Object o1, Object o2, java.util.Set<FieldsPair> visited) {
        visited.add(new FieldsPair(o1, o2));

        if (o1 == o2) {
            return true;
        }

        if (o1 == null || o2 == null) {
            return false;
        }

        if (o1 instanceof Iterable) {
            if (!(o2 instanceof Iterable)) {
                return false;
            }

            return iterablesDeepEquals((Iterable<?>) o1, (Iterable<?>) o2, visited);
        }

        if (o2 instanceof Iterable) {
            return false;
        }

        if (o1 instanceof java.util.stream.BaseStream) {
            if (!(o2 instanceof java.util.stream.BaseStream)) {
                return false;
            }

            return streamsDeepEquals((java.util.stream.BaseStream<?, ?>) o1, (java.util.stream.BaseStream<?, ?>) o2, visited);
        }

        if (o2 instanceof java.util.stream.BaseStream) {
            return false;
        }

        if (o1 instanceof java.util.Map) {
            if (!(o2 instanceof java.util.Map)) {
                return false;
            }

            return mapsDeepEquals((java.util.Map<?, ?>) o1, (java.util.Map<?, ?>) o2, visited);
        }

        if (o2 instanceof java.util.Map) {
            return false;
        }

        Class<?> firstClass = o1.getClass();
        if (firstClass.isArray()) {
            if (!o2.getClass().isArray()) {
                return false;
            }

            // Primitive arrays should not appear here
            return arraysDeepEquals(o1, o2, visited);
        }

        // common classes

        // check if class has custom equals method (including wrappers and strings)
        // It is very important to check it here but not earlier because iterables and maps also have custom equals 
        // based on elements equals 
        if (hasCustomEquals(firstClass)) {
            return o1.equals(o2);
        }

        // common classes without custom equals, use comparison by fields
        final java.util.List<java.lang.reflect.Field> fields = new java.util.ArrayList<>();
        while (firstClass != Object.class) {
            fields.addAll(java.util.Arrays.asList(firstClass.getDeclaredFields()));
            // Interface should not appear here
            firstClass = firstClass.getSuperclass();
        }

        for (java.lang.reflect.Field field : fields) {
            field.setAccessible(true);
            try {
                final Object field1 = field.get(o1);
                final Object field2 = field.get(o2);
                if (!visited.contains(new FieldsPair(field1, field2)) && !deepEquals(field1, field2, visited)) {
                    return false;
                }
            } catch (IllegalArgumentException e) {
                return false;
            } catch (IllegalAccessException e) {
                // should never occur because field was set accessible
                return false;
            }
        }

        return true;
    }

    public static boolean arraysDeepEquals(Object arr1, Object arr2, java.util.Set<FieldsPair> visited) {
        final int length = java.lang.reflect.Array.getLength(arr1);
        if (length != java.lang.reflect.Array.getLength(arr2)) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (!deepEquals(java.lang.reflect.Array.get(arr1, i), java.lang.reflect.Array.get(arr2, i), visited)) {
                return false;
            }
        }

        return true;
    }

    public static boolean iterablesDeepEquals(Iterable<?> i1, Iterable<?> i2, java.util.Set<FieldsPair> visited) {
        final java.util.Iterator<?> firstIterator = i1.iterator();
        final java.util.Iterator<?> secondIterator = i2.iterator();
        while (firstIterator.hasNext() && secondIterator.hasNext()) {
            if (!deepEquals(firstIterator.next(), secondIterator.next(), visited)) {
                return false;
            }
        }

        if (firstIterator.hasNext()) {
            return false;
        }

        return !secondIterator.hasNext();
    }

    public static boolean streamsDeepEquals(
            java.util.stream.BaseStream<?, ?> s1,
            java.util.stream.BaseStream<?, ?> s2,
            java.util.Set<FieldsPair> visited
    ) {
        final java.util.Iterator<?> firstIterator = s1.iterator();
        final java.util.Iterator<?> secondIterator = s2.iterator();
        while (firstIterator.hasNext() && secondIterator.hasNext()) {
            if (!deepEquals(firstIterator.next(), secondIterator.next(), visited)) {
                return false;
            }
        }

        if (firstIterator.hasNext()) {
            return false;
        }

        return !secondIterator.hasNext();
    }

    public static boolean mapsDeepEquals(
            java.util.Map<?, ?> m1,
            java.util.Map<?, ?> m2,
            java.util.Set<FieldsPair> visited
    ) {
        final java.util.Iterator<? extends java.util.Map.Entry<?, ?>> firstIterator = m1.entrySet().iterator();
        final java.util.Iterator<? extends java.util.Map.Entry<?, ?>> secondIterator = m2.entrySet().iterator();
        while (firstIterator.hasNext() && secondIterator.hasNext()) {
            final java.util.Map.Entry<?, ?> firstEntry = firstIterator.next();
            final java.util.Map.Entry<?, ?> secondEntry = secondIterator.next();

            if (!deepEquals(firstEntry.getKey(), secondEntry.getKey(), visited)) {
                return false;
            }

            if (!deepEquals(firstEntry.getValue(), secondEntry.getValue(), visited)) {
                return false;
            }
        }

        if (firstIterator.hasNext()) {
            return false;
        }

        return !secondIterator.hasNext();
    }

    public static boolean hasCustomEquals(Class<?> clazz) {
        while (!Object.class.equals(clazz)) {
            try {
                clazz.getDeclaredMethod("equals", Object.class);
                return true;
            } catch (Exception e) {
                // Interface should not appear here
                clazz = clazz.getSuperclass();
            }
        }

        return false;
    }

    public static int getArrayLength(Object arr) {
        return java.lang.reflect.Array.getLength(arr);
    }

    public static void consumeBaseStream(java.util.stream.BaseStream stream) {
        stream.iterator().forEachRemaining(value -> {
        });
    }

    public static Object buildStaticLambda(
            Class<?> samType,
            Class<?> declaringClass,
            String lambdaName,
            CapturedArgument... capturedArguments
    ) throws Throwable {
        // Create lookup for class where the lambda is declared in.
        java.lang.invoke.MethodHandles.Lookup caller = getLookupIn(declaringClass);

        // Obtain the single abstract method of a functional interface whose instance we are building.
        // For example, for `java.util.function.Predicate` it will be method `test`.
        java.lang.reflect.Method singleAbstractMethod = getSingleAbstractMethod(samType);
        String invokedName = singleAbstractMethod.getName();
        // Method type of single abstract method of the target functional interface.
        java.lang.invoke.MethodType samMethodType = java.lang.invoke.MethodType.methodType(singleAbstractMethod.getReturnType(), singleAbstractMethod.getParameterTypes());

        java.lang.reflect.Method lambdaMethod = getLambdaMethod(declaringClass, lambdaName);
        lambdaMethod.setAccessible(true);
        java.lang.invoke.MethodType lambdaMethodType = java.lang.invoke.MethodType.methodType(lambdaMethod.getReturnType(), lambdaMethod.getParameterTypes());
        java.lang.invoke.MethodHandle lambdaMethodHandle = caller.findStatic(declaringClass, lambdaName, lambdaMethodType);

        Class<?>[] capturedArgumentTypes = getLambdaCapturedArgumentTypes(capturedArguments);
        java.lang.invoke.MethodType invokedType = java.lang.invoke.MethodType.methodType(samType, capturedArgumentTypes);
        java.lang.invoke.MethodType instantiatedMethodType = getInstantiatedMethodType(lambdaMethod, capturedArgumentTypes);

        // Create a CallSite for the given lambda.
        java.lang.invoke.CallSite site = java.lang.invoke.LambdaMetafactory.metafactory(
                caller,
                invokedName,
                invokedType,
                samMethodType,
                lambdaMethodHandle,
                instantiatedMethodType);

        Object[] capturedValues = getLambdaCapturedArgumentValues(capturedArguments);

        // Get MethodHandle and pass captured values to it to obtain an object
        // that represents the target functional interface instance.
        java.lang.invoke.MethodHandle handle = site.getTarget();
        return handle.invokeWithArguments(capturedValues);
    }

    public static Object buildLambda(
            Class<?> samType,
            Class<?> declaringClass,
            String lambdaName,
            Object capturedReceiver,
            CapturedArgument... capturedArguments
    ) throws Throwable {
        // Create lookup for class where the lambda is declared in.
        java.lang.invoke.MethodHandles.Lookup caller = getLookupIn(declaringClass);

        // Obtain the single abstract method of a functional interface whose instance we are building.
        // For example, for `java.util.function.Predicate` it will be method `test`.
        java.lang.reflect.Method singleAbstractMethod = getSingleAbstractMethod(samType);
        String invokedName = singleAbstractMethod.getName();
        // Method type of single abstract method of the target functional interface.
        java.lang.invoke.MethodType samMethodType = java.lang.invoke.MethodType.methodType(singleAbstractMethod.getReturnType(), singleAbstractMethod.getParameterTypes());

        java.lang.reflect.Method lambdaMethod = getLambdaMethod(declaringClass, lambdaName);
        lambdaMethod.setAccessible(true);
        java.lang.invoke.MethodType lambdaMethodType = java.lang.invoke.MethodType.methodType(lambdaMethod.getReturnType(), lambdaMethod.getParameterTypes());
        java.lang.invoke.MethodHandle lambdaMethodHandle = caller.findVirtual(declaringClass, lambdaName, lambdaMethodType);

        Class<?>[] capturedArgumentTypes = getLambdaCapturedArgumentTypes(capturedArguments);
        java.lang.invoke.MethodType invokedType = java.lang.invoke.MethodType.methodType(samType, capturedReceiver.getClass(), capturedArgumentTypes);
        java.lang.invoke.MethodType instantiatedMethodType = getInstantiatedMethodType(lambdaMethod, capturedArgumentTypes);

        // Create a CallSite for the given lambda.
        java.lang.invoke.CallSite site = java.lang.invoke.LambdaMetafactory.metafactory(
                caller,
                invokedName,
                invokedType,
                samMethodType,
                lambdaMethodHandle,
                instantiatedMethodType);

        Object[] capturedArgumentValues = getLambdaCapturedArgumentValues(capturedArguments);

        // This array will contain the value of captured receiver
        // (`this` instance of class where the lambda is declared)
        // and the values of captured arguments.
        Object[] capturedValues = new Object[capturedArguments.length + 1];

        // Setting the captured receiver value.
        capturedValues[0] = capturedReceiver;

        // Setting the captured argument values.
        System.arraycopy(capturedArgumentValues, 0, capturedValues, 1, capturedArgumentValues.length);

        // Get MethodHandle and pass captured values to it to obtain an object
        // that represents the target functional interface instance.
        java.lang.invoke.MethodHandle handle = site.getTarget();
        return handle.invokeWithArguments(capturedValues);
    }

    private static java.lang.invoke.MethodHandles.Lookup getLookupIn(Class<?> clazz) throws IllegalAccessException, NoSuchFieldException {
        java.lang.invoke.MethodHandles.Lookup lookup = java.lang.invoke.MethodHandles.lookup().in(clazz);

        // Allow lookup to access all members of declaringClass, including the private ones.
        // For example, it is useful to access private synthetic methods representing lambdas.
        java.lang.reflect.Field allowedModes = java.lang.invoke.MethodHandles.Lookup.class.getDeclaredField("allowedModes");
        allowedModes.setAccessible(true);
        allowedModes.setInt(lookup, java.lang.reflect.Modifier.PUBLIC | java.lang.reflect.Modifier.PROTECTED | java.lang.reflect.Modifier.PRIVATE | java.lang.reflect.Modifier.STATIC);

        return lookup;
    }

    private static Class<?>[] getLambdaCapturedArgumentTypes(CapturedArgument... capturedArguments) {
        Class<?>[] capturedArgumentTypes = new Class<?>[capturedArguments.length];
        for (int i = 0; i < capturedArguments.length; i++) {
            capturedArgumentTypes[i] = capturedArguments[i].type;
        }
        return capturedArgumentTypes;
    }


    private static Object[] getLambdaCapturedArgumentValues(CapturedArgument... capturedArguments) {
        return java.util.Arrays.stream(capturedArguments)
                .map(argument -> argument.value)
                .toArray();
    }

    private static java.lang.invoke.MethodType getInstantiatedMethodType(java.lang.reflect.Method lambdaMethod, Class<?>[] capturedArgumentTypes) {
        // Types of arguments of synthetic method (representing lambda) without the types of captured values.
        java.util.List<Class<?>> instantiatedMethodParamTypeList = java.util.Arrays.stream(lambdaMethod.getParameterTypes())
                .skip(capturedArgumentTypes.length)
                .collect(java.util.stream.Collectors.toList());

        // The same types, but stored in an array.
        Class<?>[] instantiatedMethodParamTypes = new Class<?>[instantiatedMethodParamTypeList.size()];
        for (int i = 0; i < instantiatedMethodParamTypeList.size(); i++) {
            instantiatedMethodParamTypes[i] = instantiatedMethodParamTypeList.get(i);
        }

        return java.lang.invoke.MethodType.methodType(lambdaMethod.getReturnType(), instantiatedMethodParamTypes);
    }

    private static java.lang.reflect.Method getLambdaMethod(Class<?> declaringClass, String lambdaName) {
        return java.util.Arrays.stream(declaringClass.getDeclaredMethods())
                .filter(method -> method.getName().equals(lambdaName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No lambda method named " + lambdaName + " was found in class: " + declaringClass.getCanonicalName()));
    }

    private static java.lang.reflect.Method getSingleAbstractMethod(Class<?> clazz) {
        java.util.List<java.lang.reflect.Method> abstractMethods = java.util.Arrays.stream(clazz.getMethods())
                .filter(method -> java.lang.reflect.Modifier.isAbstract(method.getModifiers()))
                .collect(java.util.stream.Collectors.toList());

        if (abstractMethods.isEmpty()) {
            throw new IllegalArgumentException("No abstract methods found in class: " + clazz.getCanonicalName());
        }
        if (abstractMethods.size() > 1) {
            throw new IllegalArgumentException("More than one abstract method found in class: " + clazz.getCanonicalName());
        }

        return abstractMethods.get(0);
    }
}
