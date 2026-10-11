# Coding style (User Rules)

Copy everything below the line into Cursor Settings → Rules → User Rules.

---

## Java class member order

Keep class members in this order:

1. Logger field (if present) — always the first field, even before constants.
2. Static constant fields (`static final`, uppercase constants).
3. Other static fields.
4. Static blocks.
5. Nested classes (static or inner).
6. Instance fields.
7. Constructors.
8. Methods.
9. Special methods last (`equals`, `hashCode`, `toString`, `compareTo`, `clone`, and similar).

Apply when creating or substantially editing a Java class. Do not rearrange unrelated files solely for member order unless asked.

Example shape:

    private static Logger logger = LogManager.getLogger(Example.class);

    private static final String CONST = "x";

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // static init
    }

    private static class Helper {
    }

    private String name;

    public Example() {
    }

    public void doWork() {
    }

    @Override
    public boolean equals(Object obj) {
        // ...
    }

## Imports and type names

Prefer an import and the simple type name. Do not use fully qualified names (FQN) in type references (implements, extends, fields, parameters, casts, new, etc.) unless there is a name conflict.

Use FQN only when two types share the same simple name and both are needed in the same file. Import one and FQN the other, or FQN both for clarity.

Bad:

    public class Foo implements com.example.api.IBar

Good:

    import com.example.api.IBar;
    public class Foo implements IBar

## Fixed collaborators as final fields

When a class instance is bound to a fixed context or service for its lifetime (the dependency will not change over time), resolve it once in the constructor and store it as a final instance field. Prefer that field over repeated static lookups of the same instance inside the class.
