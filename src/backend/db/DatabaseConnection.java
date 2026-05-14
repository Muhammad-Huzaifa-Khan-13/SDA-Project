package backend.db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://trolley.proxy.rlwy.net:54135/quizdb?useSSL=false&autoReconnect=true";

    private static final String USER = "root";
    private static final String PASSWORD = "gwIdHgdnnmBsrmLxLisJXmqyeFbqZsFn";

    // Simple connection pool
    private static final int MAX_POOL = 10;
    private static final int INIT_POOL = 2;
    private static final BlockingQueue<Connection> pool = new ArrayBlockingQueue<>(MAX_POOL);
    private static volatile boolean initialized = false;
    private static final Object initLock = new Object();

    private static void initPool() {
        if (initialized) return;
        synchronized (initLock) {
            if (initialized) return;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.out.println("MySQL Driver not found: " + e.getMessage());
            }
            for (int i = 0; i < INIT_POOL; i++) {
                try {
                    Connection phys = DriverManager.getConnection(URL, USER, PASSWORD);
                    pool.offer(phys);
                } catch (SQLException e) {
                    System.out.println("Connection creation failed during pool init: " + e.getMessage());
                }
            }
            initialized = true;
        }
    }

    private static Connection createPhysicalConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static Connection getConnection() {
        initPool();
        Connection phys = null;
        try {
            // try to get an existing connection quickly
            phys = pool.poll(200, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (phys == null) {
            try {
                phys = createPhysicalConnection();
            } catch (SQLException e) {
                System.out.println("Connection failed: " + e.getMessage());
                return null;
            }
        }

        // Wrap connection in a proxy that returns it to pool on close()
        Connection proxy = (Connection) Proxy.newProxyInstance(
                DatabaseConnection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                new PooledConnectionHandler(phys)
        );
        return proxy;
    }

    private static class PooledConnectionHandler implements InvocationHandler {
        private final Connection physical;
        private volatile boolean closed = false;

        PooledConnectionHandler(Connection physical) {
            this.physical = physical;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if ("close".equals(name)) {
                // Instead of closing, return to pool if possible
                if (!closed) {
                    closed = true;
                    try {
                        if (!physical.isClosed()) {
                            // reset to default state if needed
                            try { physical.setAutoCommit(true); } catch (Exception ignore) {}
                            // try to return to pool, if pool full then actually close
                            boolean offered = pool.offer(physical);
                            if (!offered) {
                                try { physical.close(); } catch (Exception ignore) {}
                            }
                        }
                    } catch (Exception ignore) {}
                }
                return null;
            }

            // Delegate other methods to the underlying connection
            try {
                return method.invoke(physical, args);
            } catch (Throwable t) {
                throw t.getCause() != null ? t.getCause() : t;
            }
        }
    }

    // Optionally allow shutting down the pool at application exit
    public static void shutdown() {
        synchronized (initLock) {
            while (!pool.isEmpty()) {
                try {
                    Connection c = pool.poll();
                    if (c != null) {
                        try { c.close(); } catch (Exception ignore) {}
                    }
                } catch (Exception ignore) {}
            }
            initialized = false;
        }
    }
}