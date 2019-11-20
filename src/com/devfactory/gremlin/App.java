/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.devfactory.gremlin;

import java.util.HashMap;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class App {

	public static void main(String[] args) {
		// create a graph with two vertices and a connecting edge
		final Graph graph = TinkerGraph.open();
		final Vertex marko = graph.addVertex(T.id, 1, T.label, "person", "name", "marko", "age", 29);
		final Vertex vadas = graph.addVertex(T.id, 2, T.label, "person", "name", "vadas", "age", 27);
		marko.addEdge("knows", vadas, T.id, 7, "weight", 0.5d);

		final GraphTraversalSource g = graph.traversal();

		// print the id, label and properties of a vertex
		dump(g.V().has("name", "marko").next());

		// print the id, label and properties of an edge
		dump(g.E().hasLabel("knows").next());

		// print all the elements of a path
		dump(g.V().has("name", "marko").outE("knows").inV().path().next());

		// print the results of valueMap() on a vertex
		dump(g.V().has("name", "marko").valueMap().next());

		// print the results of valueMap() on an edge
		dump(g.E().hasLabel("knows").valueMap().next());
	}

	private static void dump(Object results) {
		final Class cls = results.getClass();
		if (Map.class.isAssignableFrom(cls)) {
			System.out.println("Map: " + results);
		}
		else if (Path.class.isAssignableFrom(cls)) {
			// dump each element in the path
			System.out.println("Path:");
			((Path)results).forEach(element -> {
				System.out.print("  ");
				dump(element);
			});
		}
		else if (Element.class.isAssignableFrom(cls)) {
			System.out.println((Vertex.class.isAssignableFrom(cls) ? "Vertex: " : "Edge: ") + getElement((Element)results));
		}
		else {
			throw new IllegalArgumentException("Invalid result type: " + cls);
		}
	}

	// get the id, label and properties of an element
	private static Map<String, Object> getElement(Element element, String... keys) {
		final HashMap<String, Object> map = new HashMap<>();
		map.put("id", element.id());
		map.put("label", element.label());
		element.properties(keys).forEachRemaining(prop -> {
			map.put(prop.key(), prop.value());
		});
		return map;
	}
}