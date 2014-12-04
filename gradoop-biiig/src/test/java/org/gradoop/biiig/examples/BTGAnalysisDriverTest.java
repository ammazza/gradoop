package org.gradoop.biiig.examples;

import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.gradoop.MapReduceClusterTest;
import org.gradoop.model.Vertex;
import org.gradoop.storage.GraphStore;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Tests the pipeline described in
 * {@link org.gradoop.biiig.examples.BTGAnalysisDriver}.
 */
public class BTGAnalysisDriverTest extends MapReduceClusterTest {

  @Test
  public void driverTest()
    throws Exception {
    Configuration conf = utility.getConfiguration();
    String graphFile = "btg.graph";
    String outputDir = "/output";
    String workerCount = Integer.toString(1);

    prepareInput(graphFile);

    BTGAnalysisDriver btgAnalysisDriver = new BTGAnalysisDriver();
    btgAnalysisDriver.setConf(conf);

    // run the pipeline
    int exitCode = btgAnalysisDriver.run(new String[] {
      graphFile,
      outputDir,
      workerCount
    });

    // tests
    assertThat(exitCode, is(0));
    GraphStore graphStore = openGraphStore();
    // BTG results
    validateBTGComputation(graphStore);
    graphStore.close();
  }

  private void validateBTGComputation(GraphStore graphStore) {
    validateBTGs(graphStore.readVertex(0L), 4L, 9L);
    validateBTGs(graphStore.readVertex(1L), 4L, 9L);
    validateBTGs(graphStore.readVertex(2L), 4L, 9L);
    validateBTGs(graphStore.readVertex(3L), 4L, 9L);
    validateBTGs(graphStore.readVertex(4L), 4L);
    validateBTGs(graphStore.readVertex(5L), 4L);
    validateBTGs(graphStore.readVertex(6L), 4L);
    validateBTGs(graphStore.readVertex(7L), 4L);
    validateBTGs(graphStore.readVertex(8L), 4L);
    validateBTGs(graphStore.readVertex(9L), 9L);
    validateBTGs(graphStore.readVertex(10L), 9L);
    validateBTGs(graphStore.readVertex(11L), 9L);
    validateBTGs(graphStore.readVertex(12L), 9L);
    validateBTGs(graphStore.readVertex(13L), 9L);
    validateBTGs(graphStore.readVertex(14L), 9L);
    validateBTGs(graphStore.readVertex(15L), 9L);
  }

  private void validateBTGs(Vertex vertex, long... expectedBTGs) {
    assertEquals(expectedBTGs.length, vertex.getGraphCount());
    List<Long> graphIDs = Lists.newArrayList(vertex.getGraphs());
    for (long expectedBTG : expectedBTGs) {
      assertTrue(graphIDs.contains(expectedBTG));
    }

  }

  private void prepareInput(String inputFile)
    throws IOException {
    URL tmpUrl = Thread.currentThread().getContextClassLoader().getResource
      (inputFile);
    assertNotNull(tmpUrl);
    String graphFileResource = tmpUrl.getPath();
    // copy input graph to DFS
    FileSystem fs = utility.getTestFileSystem();
    Path graphFileLocalPath = new Path(graphFileResource);
    Path graphFileDFSPath = new Path(inputFile);
    fs.copyFromLocalFile(graphFileLocalPath, graphFileDFSPath);
  }
}