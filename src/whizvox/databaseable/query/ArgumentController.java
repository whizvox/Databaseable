package whizvox.databaseable.query;

import whizvox.databaseable.Database;
import whizvox.databaseable.Databaseable;
import whizvox.databaseable.Row;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public interface ArgumentController {

    Object[] parseParams(String[] args) throws IllegalArgumentException;

    <T extends Row> void control(ObjectOutput<T> out, Database<T> db, Object[] args) throws IllegalArgumentException;

    ArgumentController LIMIT = new ArgumentController() {
        public static final int LIMIT_MAX = 65536;
        @Override public Object[] parseParams(String[] args) throws IllegalArgumentException {
            if (args.length > 0) {
                try {
                    int n = Integer.parseInt(args[0]);
                    if (n > 0 && n <= LIMIT_MAX) {
                        return new Object[] {n};
                    } else {
                        throw new IllegalArgumentException("Limit exceeds bounds of 1," + LIMIT_MAX);
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid int: " + args[0]);
                }
            }
            throw new IllegalArgumentException("Requires 1 argument: (limit [int, bounds are 1," + LIMIT_MAX + "])");
        }
        @Override public <T extends Row> void control(ObjectOutput<T> out, Database<T> db, Object[] args) {
            out.limit((int) args[0]);
        }
    };

    ArgumentController ORDER = new ArgumentController() {
        @Override public Object[] parseParams(String[] args) throws IllegalArgumentException {
            if (args.length > 0) {
                Object[] out = new Object[2];
                out[0] = args[0].toLowerCase();
                if (args.length == 1) {
                    out[1] = false;
                } else {
                    try {
                        out[1] = Databaseable.parseBool(args[1]);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                return out;
            }
            throw new IllegalArgumentException("Requires 2 arguments: (column name [str2], ascending order [bool])");
        }
        @Override public <T extends Row> void control(ObjectOutput<T> out, Database<T> db, Object[] args) throws IllegalArgumentException {
            String name = (String) args[0];
            if (!db.hasColumn(name)) {
                throw new IllegalArgumentException("Database does not have column: " + name);
            }
            int columnIndex = db.getColumnIndex(name);
            boolean ascendingOrder = (boolean) args[1];
            try {
                out.sort((o1, o2) -> {
                    Number n1 = (Number) o1.get(columnIndex);
                    Number n2 = (Number) o2.get(columnIndex);
                    return (int) (n1.doubleValue() - n2.doubleValue()) * (ascendingOrder ? 1 : -1);
                });
                return;
            } catch (Exception e) {}
            try {
                out.sort((o1, o2) -> {
                    String s1 = (String) o1.get(columnIndex);
                    String s2 = (String) o2.get(columnIndex);
                    return s1.compareToIgnoreCase(s2) * (ascendingOrder ? 1 : -1);
                });
                return;
            } catch (Exception e) {}
            try {
                out.sort((o1, o2) -> {
                    Comparable c1 = (Comparable) o1.get(columnIndex);
                    Comparable c2 = (Comparable) o2.get(columnIndex);
                    return c1.compareTo(c2) * (ascendingOrder ? 1 : -1);
                });
                return;
            } catch (Exception e) {}
            throw new IllegalArgumentException("Column type cannot be compared. Can only be a number or a string or an instance of " + Comparable.class.getName());
        }
    };

    ArgumentController IF = new ArgumentController() {
        @Override
        public Object[] parseParams(String[] args) throws IllegalArgumentException {
            if (args.length > 0) {
                Scanner scanner = new Scanner(args[0]);
                try {
                    String o1 = scanner.next();
                    String o2 = scanner.next();
                    String op = scanner.next();
                    Object[] out = new Object[3];
                    try {
                        out[0] = Double.parseDouble(o1);
                    } catch (NumberFormatException e) {
                        out[0] = o1;
                    }
                    try {
                        out[1] = Double.parseDouble(o2);
                    } catch (NumberFormatException e) {
                        out[1] = o2;
                    }
                    out[2] = op;
                    return out;
                } catch (NoSuchElementException e) {
                    throw new IllegalArgumentException("Invalid expression: " + args[0]);
                }
            }
            throw new IllegalArgumentException("Requires 1 argument: (expression [str2]) (Ex: if(year > 2000)");
        }
        @Override
        public <T extends Row> void control(ObjectOutput<T> out, Database<T> db, Object[] args) throws IllegalArgumentException {
            double[] vars1 = new double[2];
            double[] vars2 = new double[2];
            int var1Index, var2Index;
            if (args[0].getClass().isAssignableFrom(String.class)) {
                var1Index = db.getColumnIndex((String) args[0]);
                if (var1Index == -1) {
                    throw new IllegalArgumentException("Database does not have column: " + args[0]);
                }
            } else {
                var1Index = -1;
                vars1[0] = (double) args[0];
                vars2[0] = (double) args[0];
            }
            if (args[1].getClass().isAssignableFrom(String.class)) {
                var2Index = db.getColumnIndex((String) args[0]);
                if (var2Index == -1) {
                    throw new IllegalArgumentException("Database column does not exist: " + args[0]);
                }
            } else {
                var2Index = -1;
                vars1[1] = (double) args[1];
                vars2[1] = (double) args[1];
            }
            if (var1Index != -1 && !db.getCodec(var1Index).getClass().getGenericSuperclass().getClass().isAssignableFrom(Number.class)) {
                throw new IllegalArgumentException("Column is not a valid number: " + args[0]);
            }
            if (var2Index != -1 && !db.getCodec(var2Index).getClass().getGenericSuperclass().getClass().isAssignableFrom(Number.class)) {
                throw new IllegalArgumentException("Column is not a valid number: " + args[1]);
            }
            AtomicInteger i = new AtomicInteger(0);
            out.sort((o1, o2) -> {
                updateVars(vars1, o1, var1Index, var2Index);
                updateVars(vars2, o2, var1Index, var2Index);
                boolean b1, b2;
                switch ((String) args[2]) {
                    case "<":
                        b1 = vars1[0] < vars1[1];
                        b2 = vars2[0] < vars2[1];
                        break;
                    case ">":
                        b1 = vars1[0] > vars1[1];
                        b2 = vars2[0] > vars2[1];
                        break;
                    case "<=":
                        b1 = vars1[0] <= vars1[1];
                        b2 = vars2[0] <= vars2[1];
                        break;
                    case ">=":
                        b1 = vars1[0] >= vars1[1];
                        b2 = vars2[0] >= vars2[1];
                        break;
                    case "==":
                        b1 = vars1[0] == vars1[1];
                        b2 = vars2[0] == vars2[1];
                        break;
                    case "!=":
                        b1 = vars1[0] != vars1[1];
                        b2 = vars2[0] != vars2[1];
                        break;
                    default:
                        throw new IllegalArgumentException("Not a valid operator: " + args[2]);
                }
                if (b1) {
                    if (b2) {
                        i.addAndGet(2);
                        return 0;
                    } else {
                        i.addAndGet(1);
                        return 1;
                    }
                }
                i.addAndGet(1);
                return -1;
            });
        }
        void updateVars(double[] vars, Row row, int var1Index, int var2Index) {
            if (var1Index != -1) {
                vars[0] = ((Number) row.get(var1Index)).doubleValue();
            }
            if (var2Index != -1) {
                vars[1] = ((Number) row.get(var2Index)).doubleValue();
            }
        }
    };

}
