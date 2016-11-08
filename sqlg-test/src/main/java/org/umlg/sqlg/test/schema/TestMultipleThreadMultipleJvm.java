package org.umlg.sqlg.test.schema;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.umlg.sqlg.sql.dialect.SqlSchemaChangeDialect;
import org.umlg.sqlg.structure.SchemaTable;
import org.umlg.sqlg.structure.SqlgGraph;
import org.umlg.sqlg.test.BaseTest;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

/**
 * Date: 2016/10/29
 * Time: 7:24 PM
 */
public class TestMultipleThreadMultipleJvm extends BaseTest {

    private static Logger logger = LoggerFactory.getLogger(TestMultipleThreadMultipleJvm.class.getName());

    @BeforeClass
    public static void beforeClass() throws ClassNotFoundException, IOException, PropertyVetoException {
        URL sqlProperties = Thread.currentThread().getContextClassLoader().getResource("sqlg.properties");
        try {
            configuration = new PropertiesConfiguration(sqlProperties);
            Assume.assumeTrue(configuration.getString("jdbc.url").contains("postgresql"));
            configuration.addProperty("distributed", true);
            if (!configuration.containsKey("jdbc.url"))
                throw new IllegalArgumentException(String.format("SqlGraph configuration requires that the %s be set", "jdbc.url"));

        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testMultiThreadedLocking() throws Exception {
        //number graphs, pretending its a separate jvm
        int NUMBER_OF_GRAPHS = 10;
        ExecutorService sqlgGraphsExecutorService = Executors.newFixedThreadPool(100);
        CompletionService<Boolean> sqlgGraphsExecutorCompletionService = new ExecutorCompletionService<>(sqlgGraphsExecutorService);
        List<SqlgGraph> graphs = new ArrayList<>();
        try {
            //Pre-create all the graphs
            for (int i = 0; i < NUMBER_OF_GRAPHS; i++) {
                graphs.add(SqlgGraph.open(configuration));
            }
            List<Future<Boolean>> results = new ArrayList<>();
            for (SqlgGraph sqlgGraphAsync : graphs) {
                results.add(sqlgGraphsExecutorCompletionService.submit(() -> {
                    ((SqlSchemaChangeDialect) sqlgGraphAsync.getSqlDialect()).lock(sqlgGraphAsync);
                    sqlgGraphAsync.tx().rollback();
                    return true;
                }));
            }
            sqlgGraphsExecutorService.shutdown();
            for (Future<Boolean> result : results) {
                result.get(10, TimeUnit.SECONDS);
            }
        } finally {
            for (SqlgGraph graph : graphs) {
                graph.close();
            }
        }
    }

    @Test
    public void testMultiThreadedSchemaCreation() throws Exception {
        //number graphs, pretending its a separate jvm
        int NUMBER_OF_GRAPHS = 19;
        int NUMBER_OF_SCHEMAS = 1000;
        //Pre-create all the graphs
        List<SqlgGraph> graphs = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_GRAPHS; i++) {
            graphs.add(SqlgGraph.open(configuration));
        }
        logger.info(String.format("Done firing up %d graphs", NUMBER_OF_GRAPHS));

        ExecutorService poolPerGraph = Executors.newFixedThreadPool(NUMBER_OF_GRAPHS);
        CompletionService<SqlgGraph> poolPerGraphsExecutorCompletionService = new ExecutorCompletionService<>(poolPerGraph);
        try {

            List<Future<SqlgGraph>> results = new ArrayList<>();
            for (final SqlgGraph sqlgGraphAsync : graphs) {

                results.add(
                        poolPerGraphsExecutorCompletionService.submit(() -> {
                                    for (int i = 0; i < NUMBER_OF_SCHEMAS; i++) {
                                        //noinspection Duplicates
                                        try {
                                            sqlgGraphAsync.getTopology().ensureSchemaExist("schema_" + i);
                                            final Random random = new Random();
                                            if (random.nextBoolean()) {
                                                sqlgGraphAsync.tx().commit();
                                            } else {
                                                sqlgGraphAsync.tx().rollback();
                                            }
                                        } catch (Exception e) {
                                            sqlgGraphAsync.tx().rollback();
                                            throw new RuntimeException(e);
                                        }
                                    }
                                    return sqlgGraphAsync;
                                }
                        )
                );
            }
            poolPerGraph.shutdown();

            for (Future<SqlgGraph> result : results) {
                result.get(100, TimeUnit.SECONDS);
            }
            Thread.sleep(1000);
            for (SqlgGraph graph : graphs) {
                assertEquals(this.sqlgGraph.getTopology(), graph.getTopology());
            }
        } finally {
            for (SqlgGraph graph : graphs) {
                graph.close();
            }
        }
    }

    @Test
    public void testMultiThreadedSchemaCreation2() throws Exception {
        //number graphs, pretending its a separate jvm
        int NUMBER_OF_GRAPHS = 19;
        int NUMBER_OF_SCHEMAS = 1000;
        //Pre-create all the graphs
        List<SqlgGraph> graphs = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_GRAPHS; i++) {
            graphs.add(SqlgGraph.open(configuration));
        }
        logger.info(String.format("Done firing up %d graphs", NUMBER_OF_GRAPHS));

        ExecutorService poolPerGraph = Executors.newFixedThreadPool(NUMBER_OF_GRAPHS);
        CompletionService<SqlgGraph> poolPerGraphsExecutorCompletionService = new ExecutorCompletionService<>(poolPerGraph);
        try {

            List<Future<SqlgGraph>> results = new ArrayList<>();
            for (final SqlgGraph sqlgGraphAsync : graphs) {

                for (int i = 0; i < NUMBER_OF_SCHEMAS; i++) {
                    final int count = i;
                    results.add(
                            poolPerGraphsExecutorCompletionService.submit(() -> {
                                        //noinspection Duplicates
                                        try {
                                            sqlgGraphAsync.getTopology().ensureSchemaExist("schema_" + count);
                                            final Random random = new Random();
                                            if (random.nextBoolean()) {
                                                sqlgGraphAsync.tx().commit();
                                            } else {
                                                sqlgGraphAsync.tx().rollback();
                                            }
                                        } catch (Exception e) {
                                            sqlgGraphAsync.tx().rollback();
                                            throw new RuntimeException(e);
                                        }
                                        return sqlgGraphAsync;
                                    }
                            )
                    );
                }
            }
            poolPerGraph.shutdown();

            for (Future<SqlgGraph> result : results) {
                result.get(100, TimeUnit.SECONDS);
            }
            Thread.sleep(1000);
            for (SqlgGraph graph : graphs) {
                assertEquals(this.sqlgGraph.getTopology(), graph.getTopology());
            }
        } finally {
            for (SqlgGraph graph : graphs) {
                graph.close();
            }
        }
    }

    @Test
    public void testMultiThreadedVertexLabelCreation() throws Exception {
        //number graphs, pretending its a separate jvm
        int NUMBER_OF_GRAPHS = 19;
        int NUMBER_OF_SCHEMAS = 1000;
        //Pre-create all the graphs
        List<SqlgGraph> graphs = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_GRAPHS; i++) {
            graphs.add(SqlgGraph.open(configuration));
        }
        logger.info(String.format("Done firing up %d graphs", NUMBER_OF_GRAPHS));

        ExecutorService poolPerGraph = Executors.newFixedThreadPool(NUMBER_OF_GRAPHS);
        CompletionService<SqlgGraph> poolPerGraphsExecutorCompletionService = new ExecutorCompletionService<>(poolPerGraph);
        try {

            List<Future<SqlgGraph>> results = new ArrayList<>();
            for (final SqlgGraph sqlgGraphAsync : graphs) {

                for (int i = 0; i < NUMBER_OF_SCHEMAS; i++) {
                    final int count = i;
                    results.add(
                            poolPerGraphsExecutorCompletionService.submit(() -> {
                                        //noinspection Duplicates
                                        try {
                                            if (count % 2 == 0) {
                                            } else {
                                                sqlgGraphAsync.getTopology().ensureVertexTableExist("schema_" + count, "tableOut_" + count, Collections.emptyMap());
                                                sqlgGraphAsync.getTopology().ensureVertexTableExist("schema_" + count, "tableIn_" + count, Collections.emptyMap());
                                                SchemaTable foreignKeyOut = SchemaTable.of("schema_" + count, "tableOut_" + count);
                                                SchemaTable foreignKeyIn = SchemaTable.of("schema_" + count, "tableIn_" + count);
                                                sqlgGraphAsync.getTopology().ensureEdgeTableExist("schema_" + count, foreignKeyOut, foreignKeyIn, Collections.emptyMap());
                                            }
                                            final Random random = new Random();
                                            if (random.nextBoolean()) {
                                                sqlgGraphAsync.tx().commit();
                                            } else {
                                                sqlgGraphAsync.tx().rollback();
                                            }
                                        } catch (Exception e) {
                                            sqlgGraphAsync.tx().rollback();
                                            throw new RuntimeException(e);
                                        }
                                        return sqlgGraphAsync;
                                    }
                            )
                    );
                }
            }
            poolPerGraph.shutdown();

            for (Future<SqlgGraph> result : results) {
                result.get(100, TimeUnit.SECONDS);
            }
            Thread.sleep(1000);
            for (SqlgGraph graph : graphs) {
                assertEquals(this.sqlgGraph.getTopology(), graph.getTopology());
                assertEquals(this.sqlgGraph.getTopology().toJson(), graph.getTopology().toJson());
            }
        } finally {
            for (SqlgGraph graph : graphs) {
                graph.close();
            }
        }

    }

}
