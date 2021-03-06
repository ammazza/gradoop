/**
 * Copyright © 2014 - 2017 Leipzig University (Database Research Group)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradoop.flink.model.impl.operators.aggregation.functions;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.gradoop.common.model.impl.pojo.GraphElement;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.common.model.impl.properties.PropertyValue;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Takes a graph element and a property key as input and builds one tuple of
 * graph id and property value per graph the vertex is contained in.
 * @param <GE> epgm graph element
 */
public class GraphIdsWithPropertyValue<GE extends GraphElement>
  implements FlatMapFunction<GE, Tuple2<GradoopId, PropertyValue>> {


  /**
   * Property key to retrieve property value
   */
  private final String propertyKey;

  /**
   * Constructor
   *
   * @param propertyKey property key to retrieve values for
   */
  public GraphIdsWithPropertyValue(String propertyKey) {
    this.propertyKey = checkNotNull(propertyKey);
  }

  @Override
  public void flatMap(GE ge,
    Collector<Tuple2<GradoopId, PropertyValue>> collector) throws Exception {
    if (ge.hasProperty(propertyKey)) {
      for (GradoopId gradoopId : ge.getGraphIds()) {
        collector.collect(new Tuple2<>(
            gradoopId,
            ge.getPropertyValue(propertyKey))
        );
      }
    }
  }
}
